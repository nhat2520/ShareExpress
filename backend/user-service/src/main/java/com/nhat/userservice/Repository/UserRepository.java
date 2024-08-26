package com.nhat.userservice.Repository;

import com.nhat.userservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("Select u from User u Where u.username = :username")
    User findByUsername(@Param("username") String username);

    boolean existsByUsername(String username);
}
