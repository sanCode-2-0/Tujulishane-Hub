package com.tujulishanehub.backend.repositories;

import com.tujulishanehub.backend.models.ApprovalStatus;
import com.tujulishanehub.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByApprovalStatus(ApprovalStatus approvalStatus);
    List<User> findByRole(User.Role role);
    List<User> findByParentDonorId(Long parentDonorId);
    List<User> findByRoleAndParentDonorIsNull(User.Role role);
}
