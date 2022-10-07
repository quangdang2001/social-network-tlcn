package com.tlcn.demo.service;


import com.tlcn.demo.dto.PostDTO;
import com.tlcn.demo.dto.PostReq;
import com.tlcn.demo.model.Post;
import org.cloudinary.json.JSONArray;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {

    Post save(Post post);
    Post findPostById(Long id);
    void deletePostById(Long id) throws IOException;
    PostDTO findPostDTOById(Long id);
    Post saveNewPost(PostReq postReq, Long userId);
    String upImagePost(MultipartFile file, Long postId) throws IOException;
    List<PostDTO> findPostOfUser(Long userId,Long guestId ,Integer page, Integer size);
    List<PostDTO> findPostHomePage(Long userId, Integer page, Integer size);
    boolean likePost(Long userId, Long postId);
}
