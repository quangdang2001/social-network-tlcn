package com.tlcn.demo.service;

import com.tlcn.demo.dto.LoginRequest;
import com.tlcn.demo.dto.UserDTO;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.model.VerificationToken;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserService {
    Users findById(Long id);
    Users saveRegister(UserDTO userDTO);
    Users save(Users users);
    Users findUserByEmail(String email);
    void saveVerificationTokenForUser(String token,Users user);
    String validateVerificationToken(String email,String token);
    VerificationToken SendToken(String email);
    Users validatePasswordResetToken(String token);
    void changePassword(Users user, String newPassword);
    boolean checkIfValidOldPassword(Users user, String oldPassword);
    Users updateUser(UserDTO userDTO);
    Object login(LoginRequest loginRequest, HttpServletRequest request);
    String upImageProfile(MultipartFile file) throws IOException;
}
