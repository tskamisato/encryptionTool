package com.tfmunir.encryption_tool.repository;

import com.tfmunir.encryption_tool.model.ToolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolUserRepository extends JpaRepository<ToolUser, Integer> {
    Optional<ToolUser> findByUsername(String username);
}
