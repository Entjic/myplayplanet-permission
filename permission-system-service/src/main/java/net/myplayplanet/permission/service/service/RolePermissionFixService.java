package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionFixService {
    private static final Logger log = LoggerFactory.getLogger(RolePermissionFixService.class);
    // TODO: 17.02.2025 Class necessary to avoid circular dependencies, make prettier eventually
    private final PermissionRepository permissionRepository;

    public void fixPermissions(Role role) {
        role.setGranted(role.getGranted().stream().map(permission -> findPermissionOrThrow(permission.getKey())).collect(Collectors.toSet()));
        role.setDenied(role.getDenied().stream().map(permission -> findPermissionOrThrow(permission.getKey())).collect(Collectors.toSet()));
    }

    private Permission findPermissionOrThrow(String key) {
        return this.permissionRepository.findByKey(key).orElseThrow(() -> {
            log.warn("Couldn't find permission with key {}, throwing 404", key);
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });
    }


}
