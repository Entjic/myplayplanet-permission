package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.dto.PermissionDto;
import net.myplayplanet.permission.service.dto.effective.EffectiveUserModelDto;
import net.myplayplanet.permission.service.dto.effective.ExtensiveEffectiveUserModelDto;
import net.myplayplanet.permission.service.dto.effective.ExtensivePermissionDto;
import net.myplayplanet.permission.service.dto.enums.PermissionOrigin;
import net.myplayplanet.permission.service.dto.enums.PermissionValue;
import net.myplayplanet.permission.service.dto.model.ExtensivePermissionSet;
import net.myplayplanet.permission.service.dto.model.PermissionSet;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.Scope;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    private final RoleService roleService;

    public User findUserOrThrow(Scope scope, UUID uuid) {
        return this.userRepository.findByScopeAndUuid(scope, uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Optional<User> findUser(Scope scope, UUID uuid) {
        return this.userRepository.findByScopeAndUuid(scope, uuid);
    }

    public User findOrCreateUser(Scope scope, UUID uuid) {
        return this.userRepository.findByScopeAndUuid(scope, uuid).orElse(new User(uuid, scope));
    }

    public Set<User> getAll(Scope scope) {
        return new HashSet<>(this.userRepository.findAllByScope(scope));
    }

    public User addRole(User user, Role role) {
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User removeRole(User user, Role role) {
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    public User setUserSpecificPermission(User user, Permission permission, PermissionValue permissionValue) {
        if (permissionValue.equals(PermissionValue.GRANTED)) {
            user.getGranted().add(permission);
            user.getDenied().remove(permission);
        }
        if (permissionValue.equals(PermissionValue.DENIED)) {
            user.getDenied().add(permission);
            user.getGranted().remove(permission);
        }
        if (permissionValue.equals(PermissionValue.NEUTRAL)) {
            user.getGranted().remove(permission);
            user.getDenied().remove(permission);
        }
        return userRepository.save(user);
    }

    public void delete(User user) {
        this.userRepository.delete(user);
    }

    public User clearUserSpecificPermissions(User user) {
        user.getGranted().clear();
        user.getDenied().clear();
        return userRepository.save(user);
    }

    public ExtensiveEffectiveUserModelDto getExtensiveEffectiveUserModel(User user) {
        final ExtensiveEffectiveUserModelDto extensiveEffectiveUserModelDto = new ExtensiveEffectiveUserModelDto();

        extensiveEffectiveUserModelDto.setUser(user.getUuid());
        extensiveEffectiveUserModelDto.setRoles(entityMapper.rolesToRoleDtos(user.getRoles()));
        extensiveEffectiveUserModelDto.setPermissionDtos(calcExtensivePermissionSet(user));

        return extensiveEffectiveUserModelDto;
    }

    public EffectiveUserModelDto getEffectiveUserModel(User user) {
        final EffectiveUserModelDto effectiveUserModelDto = new EffectiveUserModelDto();
        effectiveUserModelDto.setUser(user.getUuid());
        effectiveUserModelDto.setPermissions(calcPermissionSet(user));

        return effectiveUserModelDto;
    }

    private ExtensivePermissionSet calcExtensivePermissionSet(User user) {
        final ExtensivePermissionSet set = roleService.getExtensivEffectivePermissions(user.getRoles());

        final Map<String, ExtensivePermissionDto> map = set.toMap();

        for (Permission permission : user.getGranted()) {

            if (map.containsKey(permission.getKey())) {
                PermissionValue permissionValue = map.get(permission.getKey()).getPermissionDto().getPermissionValue();
                if (!permissionValue.equals(PermissionValue.GRANTED)) {
                    map.put(permission.getKey(), generateExtensivePermissionDto(permission.getKey(),
                            PermissionValue.GRANTED));
                }
            }
        }

        for (Permission permission : user.getDenied()) {

            if (map.containsKey(permission.getKey())) {
                PermissionValue permissionValue = map.get(permission.getKey()).getPermissionDto().getPermissionValue();
                if (!permissionValue.equals(PermissionValue.DENIED)) {
                    map.put(permission.getKey(), generateExtensivePermissionDto(permission.getKey(),
                            PermissionValue.DENIED));
                }
            }
        }

        return new ExtensivePermissionSet(map);

    }

    private ExtensivePermissionDto generateExtensivePermissionDto(String key, PermissionValue permissionValue) {
        return new ExtensivePermissionDto(new PermissionDto(key, permissionValue), PermissionOrigin.SPECIFIC, null);
    }

    public PermissionSet calcPermissionSet(User user) {
        final PermissionSet rolePermissionDtos = roleService.getEffectivePermissions(user.getRoles(), user.getScope().getId());

        final Map<String, PermissionValue> map = rolePermissionDtos.toMap();

        for (Permission permission : user.getGranted()) {
            map.put(permission.getKey(), PermissionValue.GRANTED);
        }

        for (Permission permission : user.getDenied()) {
            map.put(permission.getKey(), PermissionValue.DENIED);
        }

        return new PermissionSet(map, user.getScope().getId());
    }

    public Collection<User> findUsersByUUID(UUID user) {
        return this.userRepository.findAllByUuid(user);
    }

}
