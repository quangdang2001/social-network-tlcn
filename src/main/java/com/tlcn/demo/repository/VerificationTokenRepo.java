package com.tlcn.demo.repository;

import com.tlcn.demo.model.Users;
import com.tlcn.demo.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepo extends JpaRepository<VerificationToken,Long> {
    VerificationToken findVerificationTokenByUserEmail(String email);
    VerificationToken findVerificationTokenByToken(String token);
}
