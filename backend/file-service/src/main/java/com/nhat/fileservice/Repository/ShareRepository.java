package com.nhat.fileservice.Repository;

import com.nhat.fileservice.Model.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    Share findByToken(String token);
}
