package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.core.PermissionSet;
import net.myplayplanet.permission.core.PermissionValue;
import net.myplayplanet.permission.core.dto.EffectiveUserModelDto;
import net.myplayplanet.permission.core.dto.PermissionDto;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    public User findUserOrThrow(UUID uuid){
        return this.userRepository.findByUuid(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public EffectiveUserModelDto getEffectiveUserModel(User user){
        EffectiveUserModelDto effectiveUserModelDto = new EffectiveUserModelDto();
        effectiveUserModelDto.setUser(user.getUuid());
        effectiveUserModelDto.setPermissions(calcPermissionSet(user));

        return effectiveUserModelDto;
    }

    private PermissionSet calcPermissionSet(User user){
        final PermissionSet userPermissions = new PermissionSet();


        for (Permission permission : user.getGranted()) {
            userPermissions.add(permission.getKey(), PermissionValue.GRANTED);
        }

        for (Permission permission : user.getDenied()) {
            userPermissions.add(permission.getKey(), PermissionValue.DENIED);
        }

        final Set<PermissionDto> rolePermissions = roleService.getEffectivePermissions(user.getRole());

        userPermissions.addAll(rolePermissions);
        return userPermissions;
    }

}
