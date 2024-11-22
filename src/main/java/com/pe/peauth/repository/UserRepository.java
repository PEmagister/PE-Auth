package com.pe.peauth.repository;

import com.pe.peauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  @Query(nativeQuery = true,
          value = "select * from users where role like '%MEMBER%'")
  List<User> findAllWithMemberAccess();

}
