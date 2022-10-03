package com.tlcn.demo.repository;


import com.tlcn.demo.model.Post;
import com.tlcn.demo.model.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
}
