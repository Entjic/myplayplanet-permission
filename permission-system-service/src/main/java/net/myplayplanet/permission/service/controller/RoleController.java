package net.myplayplanet.permission.service.controller;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.core.dto.RoleDto;
import net.myplayplanet.permission.core.enums.PermissionValue;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.service.PermissionService;
import net.myplayplanet.permission.service.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/permission/role/")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final EntityMapper entityMapper;

    @PostMapping("create")
    public RoleDto createRole(@RequestBody RoleDto roleDto){
        return entityMapper.roleToRoleDto(this.roleService.save(entityMapper.roleDtoToRole(roleDto)));
    }

    @DeleteMapping("delete/{id}")
    public Long deleteRole(@PathVariable Long id){
        return this.roleService.delete(id);
    }

    @PostMapping("{id}/weight/{weight}")
    public RoleDto changeWeight(@PathVariable Long id, @PathVariable Integer weight){
        return entityMapper.roleToRoleDto(this.roleService.changeWeight(id, weight));
    }

    @PostMapping("{id}/permission/{uuid}/{value}")
    public RoleDto setPermission(@PathVariable Long id, @PathVariable UUID uuid, @PathVariable PermissionValue value){
        Permission permission = this.permissionService.findPermissionOrThrow(uuid);
        return entityMapper.roleToRoleDto(this.roleService.setPermission(id, permission, value));
    }

    @GetMapping("all/")
    public Set<Long> getAllRoleIds(){
        return this.roleService.getAll();
    }

}
