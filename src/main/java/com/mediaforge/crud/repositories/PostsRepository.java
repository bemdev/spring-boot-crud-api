package com.mediaforge.crud.repositories;

import com.mediaforge.crud.entities.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
}
