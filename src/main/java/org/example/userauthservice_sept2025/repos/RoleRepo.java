package org.example.userauthservice_sept2025.repos;

import org.example.userauthservice_sept2025.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
}
