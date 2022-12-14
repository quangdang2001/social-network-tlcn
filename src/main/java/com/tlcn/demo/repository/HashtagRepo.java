package com.tlcn.demo.repository;

import com.tlcn.demo.model.Hashtag;
import com.tlcn.demo.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashtagRepo extends JpaRepository<Hashtag,Long> {
    boolean existsByHashtag(String hashtag);
    Hashtag findHashtagByHashtag(String hashtag);
}
