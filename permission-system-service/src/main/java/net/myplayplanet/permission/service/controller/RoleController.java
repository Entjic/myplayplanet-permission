package net.myplayplanet.permission.service.controller;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.core.dto.RoleDisplayDto;
import net.myplayplanet.permission.core.dto.RoleDto;
import net.myplayplanet.permission.core.enums.PermissionValue;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.service.PermissionService;
import net.myplayplanet.permission.service.service.RoleService;
import net.myplayplanet.permission.service.service.ScopeService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/permission/role/")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final ScopeService scopeService;
    private final EntityMapper entityMapper;

    @GetMapping("{id}/")
    public RoleDisplayDto getById(@PathVariable Long id){
        return this.entityMapper.roleToRoleDisplayDto(this.roleService.findOrThrow(id));
    }
    @PostMapping("{scope}/create/")
    public RoleDto createRole(@PathVariable Long scope, @RequestBody RoleDto roleDto){
        Role role = entityMapper.roleDtoToRole(roleDto, this.scopeService.findScopeOrThrow(scope));
        return entityMapper.roleToRoleDto(this.roleService.save(role));
    }

    @DeleteMapping("delete/{id}/")
    public Long deleteRole(@PathVariable Long id){
        return this.roleService.delete(id);
    }

    @PostMapping("{id}/weight/{weight}/")
    public RoleDto changeWeight(@PathVariable Long id, @PathVariable Integer weight){
        return entityMapper.roleToRoleDto(this.roleService.changeWeight(id, weight));
    }

    @PostMapping("{id}/permission/{uuid}/{value}/")
    public RoleDto setPermission(@PathVariable Long id, @PathVariable UUID uuid, @PathVariable PermissionValue value){
        Permission permission = this.permissionService.findPermissionOrThrow(uuid);
        return entityMapper.roleToRoleDto(this.roleService.setPermission(id, permission, value));
    }

    @GetMapping("{scope}/all/")
    public Set<Long> getAllRoleIdsByScope(@PathVariable Long scope){
        return this.roleService.getAll(this.scopeService.findScopeOrThrow(scope));
    }

    @GetMapping("ids/")
    public Collection<Long> getAllRoleIds(){
        return this.roleService.getAll().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
    }

}
