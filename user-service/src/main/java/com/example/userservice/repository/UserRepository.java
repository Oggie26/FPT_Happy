package com.example.userservice.repository;

import com.example.userservice.entity.User;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByPhoneAndIsDeletedFalse(String phone);
    Optional<User> findByIdAndIsDeletedFalse(String id);
    Optional<User> findByAuthId(String authId);
    @Query(value = "SELECT * FROM users u WHERE " +
            "(:keyword IS NULL OR :keyword = '' " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT count(*) FROM users u WHERE " +
                    "(:keyword IS NULL OR :keyword = '' " +
                    "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            nativeQuery = true)
    Page<User> searchByKeywordNative(@Param("keyword") String keyword, Pageable pageable);
}
