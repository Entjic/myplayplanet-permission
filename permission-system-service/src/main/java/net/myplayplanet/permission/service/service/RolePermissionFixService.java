package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.repository.PermissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionFixService {
    // TODO: 17.02.2025 Class necessary to avoid circular dependencies, make prettier eventually
    private final PermissionRepository permissionRepository;

    public void fixPermissions(Role role){
        role.setGranted(role.getGranted().stream().map(permission ->findPermissionOrThrow(permission.getKey())).collect(Collectors.toSet()));
        role.setDenied(role.getDenied().stream().map(permission -> findPermissionOrThrow(permission.getKey())).collect(Collectors.toSet()));
    }

    private Permission findPermissionOrThrow(String key) {
        return this.permissionRepository.findByKey(key).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }



}
