package com.nishikatakagi.ProductDigital.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nishikatakagi.ProductDigital.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // NghiemNgoc
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Page<User> findAll(Specification spec,Pageable pageable);
    @SuppressWarnings({ "null", "unchecked" })
    User save(User user);

    // Lacel
    User findUserByUsername(String username);

    User findById(int id);

    // Tram-Anh

    User findByEmail(String email);
}
