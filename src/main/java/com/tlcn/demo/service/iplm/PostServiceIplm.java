package com.tlcn.demo.service.iplm;

import com.cloudinary.utils.ObjectUtils;
import com.tlcn.demo.dto.PostDTO;
import com.tlcn.demo.dto.PostReq;
import com.tlcn.demo.dto.PostShareDTO;
import com.tlcn.demo.dto.UserDTO;
import com.tlcn.demo.model.ImagePost;
import com.tlcn.demo.model.Post;
import com.tlcn.demo.model.PostLike;
import com.tlcn.demo.model.Users;
import com.tlcn.demo.repository.ImagePostRepo;
import com.tlcn.demo.repository.PostLikeRepo;
import com.tlcn.demo.repository.PostRepo;
import com.tlcn.demo.repository.UserRepo;
import com.tlcn.demo.service.Cloudinary.CloudinaryUpload;
import com.tlcn.demo.service.PostService;
import com.tlcn.demo.service.UserFollowingService;
import com.tlcn.demo.service.UserService;
import com.tlcn.demo.util.Convert;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceIplm implements PostService {

    private final PostRepo postRepo;
    private final ImagePostRepo imagePostRepo;
    private final CloudinaryUpload cloudinaryUpload;
    private final UserService userService;
    private final PostLikeRepo postLikeRepo;
    private final UserRepo userRepo;
    private final UserFollowingService userFollowingService;

    @Override
    public Post save(Post post) {
        return postRepo.save(post);
    }

    @Override
    public Post findPostById(Long id) {
        Optional<Post> post = postRepo.findById(id);
        return post.orElse(null);
    }

    @Override
    public void deletePostById(Long id) throws IOException {
        Post postProxy = postRepo.getById(id);
        List<ImagePost> images = imagePostRepo.findAllByPost(postProxy);
        if (images.size() > 0) {
            images.forEach(image -> {
                try {
                    cloudinaryUpload.cloudinary().uploader().destroy("postImages/" + cloudinaryUpload.getPublicId(image.getUrlImage()),
                            ObjectUtils.asMap("resource_type", "image"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        postRepo.deleteById(id);
    }

    @Override
    public PostDTO findPostDTOById(Long id) {
        Post post = findPostById(id);
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userService.findById(Long.valueOf(principal));
        return convertPostToPostDTO(post, users);
    }

    @Override
    public Post saveNewPost(PostReq postReq, Long userId) {
        Users usersProxy = userRepo.getById(userId);

        Post post = null;
        if (usersProxy.isEnable()) {
            post = new Post();
            post.setContent(postReq.getContent());
            post.setUsers(usersProxy);
            post.setPostShared(null);
            postRepo.save(post);
        }
        return post;
    }

    @Override
    public String upImagePost(MultipartFile file, Long postId) throws IOException {
        if (!postRepo.existsById(postId)) {
            return null;
        }
        Post postProxy = postRepo.getById(postId);
        if (!file.isEmpty()) {
            Map params = ObjectUtils.asMap(
                    "resource_type", "auto",
                    "folder", "postImages"
            );
            Map map = cloudinaryUpload.cloudinary().uploader().upload(Convert.convertMultiPartToFile(file), params);
            ImagePost imagePost = new ImagePost();
            imagePost.setPost(postProxy);
            imagePost.setUrlImage((String) map.get("secure_url"));

            imagePostRepo.save(imagePost);
            return map.get("secure_url").toString();
        }
        return null;
    }

    @Override
    public List<PostDTO> findPostOfUser(Long userId, Long guestId, Integer page, Integer size) {
        List<Post> posts;
        Pageable pageable = PageRequest.of(page, size);
        Users users;
        if (guestId.equals(-1L)) {
            users = userService.findById(userId);
            posts = postRepo.findAllByUsersOrderByCreateTimeDesc(users, pageable);
            return convertPostsToPostDTOs(posts, users);
        } else {
            users = userService.findById(userId);

            Users guest = userService.findById(guestId);

            posts = postRepo.findAllByUsersOrderByCreateTimeDesc(users, pageable);
            return convertPostsToPostDTOs(posts, guest);
        }
    }

    @Override
    public List<PostDTO> findPostHomePage(Long userId, Integer page, Integer size) {
        List<Users> usersFollowingId = userFollowingService.findAllIdFollowingUser(userId);
        Users users = userService.findById(userId);
        usersFollowingId.add(users);
        Pageable pageable = PageRequest.of(page, size);
        List<Post> posts = postRepo.findAllByUsersInOrderByCreateTimeDesc(usersFollowingId, pageable);

        return convertPostsToPostDTOs(posts, users);
    }

    @Override
    public boolean likePost(Long userId, Long postId) {
        Post post = findPostById(postId);

        Users usersProxy = userRepo.getById(userId);

        if (post != null) {
            boolean check = postLikeRepo.findByPostIdAndUserId(post, usersProxy) != null;
            if (!check) {
                PostLike postLike = new PostLike(post, usersProxy);
                post.increaseLike();
                postLikeRepo.save(postLike);
                save(post);
                if (!post.getUsers().getId().equals(userId)) {
                    String content = String.format("%s %s liked your post.", usersProxy.getLastName(), usersProxy.getFirstName());
//                    notificationPayload =
//                            Convert.convertNotificationToNotifiPayload(
//                                    notificationService.sendNotificationPost(post, usersProxy, content));
//                    simpMessagingTemplate.convertAndSendToUser(notificationPayload.getUserReceiverId().toString(),
//                            "/notificationPopUp",notificationPayload);

                }
            } else {
                PostLike postLike = new PostLike(post, usersProxy);
                post.decreaseLike();
                postLikeRepo.delete(postLike);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Post> findPostReported() {
        List<Post> posts = postRepo.findAllByCountReportedGreaterThan(20);
        return posts;
    }

    @Override
    public JSONArray getPostAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        var pagePost = postRepo.findAll(pageable);
        JSONArray jsonArray = new JSONArray();
        pagePost.forEach(post -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userCreate", post.getUsers().getId());
            jsonObject.put("post", post);
            jsonArray.add(jsonObject);
        });
        return jsonArray;
    }

    private PostDTO convertPostToPostDTO(Post post, Users user) {
        PostDTO postDTO = new PostDTO(post.getId(), post.getContent(), post.getUsers().getId(),
                post.getCountLiked(), post.getCountCmted(), post.getCountShated(), post.getCountReported(),
                post.getCreateTime(), post.getUpdateTime(), post.isPostShare());
        if (post.isPostShare() && post.getPostShared() != null) {
            PostShareDTO postShareDTO = new PostShareDTO();
            Post postShare = post.getPostShared();
            Users userCreate = postShare.getUsers();
            postShareDTO.setPostShared(postShare);
            postShareDTO.setUserCreateId(userCreate.getId());
            postShareDTO.setFirstName(userCreate.getFirstName());
            postShareDTO.setLastName(userCreate.getLastName());
            postShareDTO.setAvatar(userCreate.getImageUrl());
            postDTO.setPostShared(postShareDTO);
        }
        postDTO.setImages(post.getImages());
        UserDTO userDTO = new UserDTO();
        userDTO.setId(post.getUsers().getId());
        userDTO.setFirstName(post.getUsers().getFirstName());
        userDTO.setLastName(post.getUsers().getLastName());
        userDTO.setEmail(post.getUsers().getEmail());
        userDTO.setImageUrl(post.getUsers().getImageUrl());
        if (user != null) {
            boolean check = postLikeRepo.findByPostIdAndUserId(post, user) != null;
            postDTO.setLiked(check);
        }
        postDTO.setUserCreate(userDTO);
        return postDTO;
    }

    private List<PostDTO> convertPostsToPostDTOs(List<Post> posts, Users user) {
        List<PostDTO> postDTOS = new ArrayList<>();
        posts.forEach(post -> {
            postDTOS.add(convertPostToPostDTO(post, user));
        });
        return postDTOS;
    }
}
