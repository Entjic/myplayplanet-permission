package net.myplayplanet.permission.service.repository;

import net.myplayplanet.permission.service.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
