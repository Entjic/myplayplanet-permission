package net.myplayplanet.permission.service.controller;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.core.dto.effective.EffectiveUserModelDto;
import net.myplayplanet.permission.core.dto.effective.ExtensiveEffectiveUserModelDto;
import net.myplayplanet.permission.core.dto.UserDto;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.User;
import net.myplayplanet.permission.service.service.RoleService;
import net.myplayplanet.permission.service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/permission/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final EntityMapper entityMapper;

    @GetMapping("effective/extensive/{uuid}")
    public ExtensiveEffectiveUserModelDto getExtensiveEffectiveUserModelDto(@PathVariable UUID uuid) {
        return userService.getExtensiveEffectiveUserModel(userService.findUserOrThrow(uuid));
    }

    @GetMapping("effective/{uuid}")
    public EffectiveUserModelDto getEffectiveUserModelDto(@PathVariable UUID uuid) {
        return userService.getEffectiveUserModel(userService.findUserOrThrow(uuid));
    }

    @GetMapping("all/")
    public Set<UserDto> getAll() {
        return this.entityMapper.usersToUserDtos(this.userService.getAll());
    }

    @PostMapping("role/add/{roleId}/user/{uuid}")
    public UserDto addRole(@PathVariable UUID uuid, @PathVariable Long roleId) {
        User user = this.userService.findUserOrThrow(uuid);
        Role role = this.roleService.findOrThrow(roleId);
        return this.entityMapper.userToUserDto(this.userService.addRole(user, role));
    }

    @PostMapping("role/remove/{roleId}/user/{uuid}")
    public UserDto removeRole(@PathVariable UUID uuid, @PathVariable Long roleId) {
        User user = this.userService.findUserOrThrow(uuid);
        Role role = this.roleService.findOrThrow(roleId);
        return this.entityMapper.userToUserDto(this.userService.removeRole(user, role));
    }


}
