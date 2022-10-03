package com.tlcn.demo.repository;


import com.tlcn.demo.model.ImagePost;
import com.tlcn.demo.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagePostRepo extends JpaRepository<ImagePost,Long> {

}
