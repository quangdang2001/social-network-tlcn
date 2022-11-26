package com.tlcn.demo.service.iplm;

import com.cloudinary.utils.ObjectUtils;

import com.tlcn.demo.dto.LoginRequest;
import com.tlcn.demo.dto.UserDTO;
import com.tlcn.demo.exception.AppException;
import com.tlcn.demo.model.UserFollowing;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.model.VerificationToken;
import com.tlcn.demo.repository.UserRepo;
import com.tlcn.demo.repository.VerificationTokenRepo;
import com.tlcn.demo.service.Cloudinary.CloudinaryUpload;
import com.tlcn.demo.service.UserService;
import com.tlcn.demo.service.auth.UserDetailIplm;
import com.tlcn.demo.util.Convert;
import com.tlcn.demo.util.JWTProvider;
import com.tlcn.demo.util.Utils;
import com.tlcn.demo.util.contant.Constant;
import com.tlcn.demo.util.contant.FolderName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceIplm implements UserService {

    private final UserRepo userRepo;
    private final VerificationTokenRepo verificationTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CloudinaryUpload cloudinaryUpload;
    @Override
    public Users findById(Long id) {
        Optional<Users> users = userRepo.findById(id);
        return users.orElse(null);
    }

    @Override
    public Users saveRegister(UserDTO userDTO) {
        Users user;
        Boolean check = userRepo.existsByEmail(userDTO.getEmail());
        if (check){
            throw new AppException(400, "User existed!!!");
        }
        user = new Users();
        user.setFirstName(Convert.formatName(userDTO.getFirstName()));
        user.setLastName(Convert.formatName(userDTO.getLastName()));
        user.setAddress(userDTO.getAddress());
        user.setBio(userDTO.getBio());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setBirthDay(userDTO.getBirthDay());
        return userRepo.save(user);
    }

    @Override
    public Users save(Users users) {
        return userRepo.save(users);
    }

    @Override
    public Users findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    @Override
    public void saveVerificationTokenForUser(String token, Users user) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepo.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String email, String token) {
        VerificationToken verificationToken
                = verificationTokenRepo.findVerificationTokenByToken(token);

        if (verificationToken == null) {
            return "invalid";
        }

        Users user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            verificationTokenRepo.delete(verificationToken);
            return "expired";
        }
        verificationToken.setToken(null);
        verificationTokenRepo.save(verificationToken);
        user.setEnable(true);
        user.setRole(Constant.ROLE_USER);
        userRepo.save(user);
        return "valid";
    }

    @Override
    public VerificationToken SendToken(String email) {
        VerificationToken verificationToken = verificationTokenRepo.findVerificationTokenByUserEmail(email);
        if (verificationToken != null){
            String token = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            verificationToken.setToken(token);
            verificationTokenRepo.save(verificationToken);
            return verificationToken;
        }
        return null;
    }



    @Override
    public Users validatePasswordResetToken(String token) {

        VerificationToken verificationToken
                = verificationTokenRepo.findVerificationTokenByToken(token);

        if (verificationToken == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            verificationTokenRepo.delete(verificationToken);
            return null;
        }
        Users users = verificationToken.getUser();
        verificationToken.setToken(null);
        verificationTokenRepo.save(verificationToken);
        return users;
    }

    @Override
    public void changePassword(Users user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(Users user, String oldPassword) {
        return passwordEncoder.matches(oldPassword,user.getPassword());
    }

    @Override
    public Object login(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            UserDetailIplm user = (UserDetailIplm) authentication.getPrincipal();
            if (user.getUsers().getProvider()!=null){
                throw new AppException(403,"Login method not supported");
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String access_token = JWTProvider.createJWT(user.getUsers(),request);

            Map<String,String> token = new HashMap<>();
            token.put("access_token",access_token);
            token.put("userId",user.getUsers().getId().toString());
            token.put("role", user.getUsers().getRole());
            return token;
        }catch (Exception e){
            throw new AppException(403,e.getMessage());
        }
    }
    @Override
    public Users updateUser(UserDTO userDTO) {
        Long userId = Utils.getIdCurrentUser();
        Users users = findById(userId);
        if (users!=null){
            if (userDTO.getFirstName()!=null && !userDTO.getFirstName().trim().equals("") ){
                users.setFirstName(Convert.formatName(userDTO.getFirstName()));
            }
            if (userDTO.getLastName()!=null && !userDTO.getLastName().trim().equals("") ){
                users.setLastName(Convert.formatName(userDTO.getLastName()));
            }
            if ( userDTO.getAddress()!=null && !userDTO.getAddress().trim().equals("")){
                users.setAddress(Convert.formatName(userDTO.getAddress()));
            }
            if ( userDTO.getBio()!=null && !userDTO.getBio().trim().equals("")){
                users.setBio(Convert.formatName(userDTO.getBio()));
            }
            if ( userDTO.getGender()==1 && userDTO.getGender()==0){
                users.setGender(userDTO.getGender());
            }
            if ( userDTO.getBirthDay()!=null && !userDTO.getBirthDay().equals("")){
                users.setBirthDay(userDTO.getBirthDay());
            }
        }
        save(users);
        return users;
    }
    @Override
    public String upImageProfile(MultipartFile file) throws IOException {
        Long userId = Utils.getIdCurrentUser();
        Users users = findById(userId);
        String imgUrl = users.getImageUrl();

        if (imgUrl!= null) {
            cloudinaryUpload.cloudinary().uploader().destroy("avatars/" + cloudinaryUpload.getPublicId(imgUrl)
                    , ObjectUtils.asMap("resource_type", "image"));
        }
        imgUrl = cloudinaryUpload.upload(file, FolderName.AVATARS);
        users.setImageUrl(imgUrl);
        save(users);
        return imgUrl;
    }

    @Override
    public Page<Users> getUserAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Users> users = userRepo.findAll(pageable);
        return  users;
    }

    @Override
    public boolean deleteUser(Long userId) {
        try {
            Users users = findById(userId);
            users.setRole(null);
            users.setEnable(false);
            userRepo.save(users);
//            userRepo.deleteById(userId);
            return true;
        }catch (Exception e){
            log.error("User delete exception: ",e.getMessage());
            return false;
        }
    }

    @Override
    public List<Users> findUserReported() {
        return userRepo.findAllByCountReportGreaterThan(20);
    }

    @Override
    public boolean enableUser(String email) {
        try {
            Users users = findUserByEmail(email);
            users.setRole(Constant.ROLE_USER);
            users.setEnable(true);
            userRepo.save(users);
            return true;
        } catch (Exception e){
            throw new AppException(400,"Failed");
        }
    }

}
