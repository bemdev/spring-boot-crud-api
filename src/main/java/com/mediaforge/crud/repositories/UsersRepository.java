package com.mediaforge.crud.repositories;

import com.mediaforge.crud.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
