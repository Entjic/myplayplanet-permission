package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.core.model.ExtensivePermissionSet;
import net.myplayplanet.permission.core.enums.PermissionOrigin;
import net.myplayplanet.permission.core.model.PermissionSet;
import net.myplayplanet.permission.core.enums.PermissionValue;
import net.myplayplanet.permission.core.dto.effective.EffectiveUserModelDto;
import net.myplayplanet.permission.core.dto.effective.ExtensiveEffectiveUserModelDto;
import net.myplayplanet.permission.core.dto.effective.ExtensivePermissionDto;
import net.myplayplanet.permission.core.dto.PermissionDto;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    private final RoleService roleService;

    public User findUserOrThrow(UUID uuid){
        return this.userRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Set<User> getAll(){
        return new HashSet<>(this.userRepository.findAll());
    }

    public User addRole(User user, Role role){
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User removeRole(User user, Role role){
        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    public ExtensiveEffectiveUserModelDto getExtensiveEffectiveUserModel(User user){
        final ExtensiveEffectiveUserModelDto extensiveEffectiveUserModelDto = new ExtensiveEffectiveUserModelDto();

        extensiveEffectiveUserModelDto.setUser(user.getUuid());
        extensiveEffectiveUserModelDto.setRoles(entityMapper.rolesToRoleDtos(user.getRoles()));
        extensiveEffectiveUserModelDto.setPermissionDtos(calcExtensivePermissionSet(user));

        return extensiveEffectiveUserModelDto;
    }

    public EffectiveUserModelDto getEffectiveUserModel(User user){
        final EffectiveUserModelDto effectiveUserModelDto = new EffectiveUserModelDto();
        effectiveUserModelDto.setUser(user.getUuid());
        effectiveUserModelDto.setPermissions(calcPermissionSet(user));

        return effectiveUserModelDto;
    }

    private ExtensivePermissionSet calcExtensivePermissionSet(User user){
        final ExtensivePermissionSet set = roleService.getExtensivEffectivePermissions(user.getRoles());

        final Map<UUID, ExtensivePermissionDto> map = set.toMap();

        for (Permission permission : user.getGranted()) {

            if(map.containsKey(permission.getUuid())){
                PermissionValue permissionValue = map.get(permission.getUuid()).getPermissionDto().getPermissionValue();
                if(!permissionValue.equals(PermissionValue.GRANTED)){
                    map.put(permission.getUuid(), generateExtensivePermissionDto(permission.getUuid(),
                            PermissionValue.GRANTED));
                }
            }
        }

        for (Permission permission : user.getDenied()) {

            if(map.containsKey(permission.getUuid())){
                PermissionValue permissionValue = map.get(permission.getUuid()).getPermissionDto().getPermissionValue();
                if(!permissionValue.equals(PermissionValue.DENIED)){
                    map.put(permission.getUuid(), generateExtensivePermissionDto(permission.getUuid(),
                            PermissionValue.DENIED));
                }
            }
        }

        return new ExtensivePermissionSet(map);

    }

    private ExtensivePermissionDto generateExtensivePermissionDto(UUID uuid, PermissionValue permissionValue){
        return new ExtensivePermissionDto(new PermissionDto(uuid, permissionValue), PermissionOrigin.SPECIFIC, null);
    }

    public PermissionSet calcPermissionSet(User user){
        final PermissionSet rolePermissionDtos = roleService.getEffectivePermissions(user.getRoles());

        final Map<UUID, PermissionValue> map = rolePermissionDtos.toMap();

        for (Permission permission : user.getGranted()) {

            if(map.containsKey(permission.getUuid())){
                PermissionValue permissionValue = map.get(permission.getUuid());
                if(!permissionValue.equals(PermissionValue.GRANTED)){
                    map.put(permission.getUuid(), PermissionValue.GRANTED);
                }
            }
        }

        for (Permission permission : user.getDenied()) {

            if(map.containsKey(permission.getUuid())){
                PermissionValue permissionValue = map.get(permission.getUuid());
                if(!permissionValue.equals(PermissionValue.DENIED)){
                    map.put(permission.getUuid(), PermissionValue.DENIED);
                }
            }
        }

        return new PermissionSet(map);
    }

}
