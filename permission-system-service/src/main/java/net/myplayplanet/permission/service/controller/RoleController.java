package net.myplayplanet.permission.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.dto.PermissionDto;
import net.myplayplanet.permission.service.dto.RoleDisplayDto;
import net.myplayplanet.permission.service.dto.RoleDto;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.service.PermissionService;
import net.myplayplanet.permission.service.service.RoleService;
import net.myplayplanet.permission.service.service.ScopeService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/role/")
@Tag(name = "Role")
public class RoleController {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final ScopeService scopeService;
    private final EntityMapper entityMapper;

    @Operation(operationId = "getRoleById")
    @GetMapping("{id}")
    public RoleDisplayDto getById(@PathVariable Long id) {
        return this.entityMapper.roleToRoleDisplayDto(this.roleService.findOrThrow(id));
    }

    @PostMapping("{scope}")
    public RoleDto createRole(@PathVariable Long scope, @RequestBody RoleDto roleDto) {
        Role role = entityMapper.roleDtoToRole(roleDto, this.scopeService.findScopeOrThrow(scope));
        return entityMapper.roleToRoleDto(this.roleService.save(role));
    }

    @PostMapping("copy/{id}")
    public RoleDto createFromExisting(@PathVariable Long id, @RequestBody RoleDisplayDto roleDto) {
        Role template = this.roleService.findOrThrow(id);
        return this.entityMapper.roleToRoleDto(this.roleService.createFromExisting(roleDto.getName(),
                roleDto.getDescription(),
                roleDto.getWeight(),
                template));
    }

    // if applicable specific endpoint use is preferred, especially for granting / revoking permissions after initial creation!
    @PutMapping("{scope}")
    @Operation(summary = "Update or create an existing role of a scope.", description = "This endpoint is used to update an already existing role inside a scope. If the role does not exist it will be created.")
    public RoleDto updateRole(@PathVariable Long scope, @RequestBody RoleDto roleDto) {
        Role role = entityMapper.roleDtoToRole(roleDto, this.scopeService.findScopeOrThrow(scope));
        return entityMapper.roleToRoleDto(this.roleService.alterOrCreate(role));
    }

    @DeleteMapping("delete/{id}")
    public Long deleteRole(@PathVariable Long id) {
        return this.roleService.delete(id);
    }

    @PostMapping("{id}/weight")
    public RoleDto changeWeight(@PathVariable Long id, @RequestBody Integer weight) {
        return entityMapper.roleToRoleDto(this.roleService.changeWeight(id, weight));
    }

    @PostMapping("{id}/permission")
    public RoleDto setPermission(@PathVariable Long id, @RequestBody PermissionDto permissionDto) {
        Permission permission = this.permissionService.findPermissionOrThrow(permissionDto.getKey());
        return entityMapper.roleToRoleDto(this.roleService.setPermission(id, permission, permissionDto.getPermissionValue()));
    }

    @PostMapping("{id}/permissions")
    public RoleDto setPermissions(@PathVariable Long id, @RequestBody Collection<PermissionDto> permissionDtos) {
        for (final PermissionDto permissionDto : permissionDtos) {
            Permission permission = this.permissionService.findPermissionOrThrow(permissionDto.getKey());
            this.roleService.setPermission(id, permission, permissionDto.getPermissionValue());
        }
        return this.entityMapper.roleToRoleDto(this.roleService.findOrThrow(id));
    }

    @Operation(operationId = "getAllRoleIdsByScope")
    @GetMapping("{scope}/all/id")
    public Set<Long> getAllRoleIdsByScope(@PathVariable Long scope) {
        return this.roleService.getAllIds(this.scopeService.findScopeOrThrow(scope));
    }

    @GetMapping("{scope}/all")
    public Set<RoleDto> getAllRolesByScope(@PathVariable Long scope) {
        return this.roleService.getAllRoles(this.scopeService.findScopeOrThrow(scope)).stream()
                .map(this.entityMapper::roleToRoleDto).collect(Collectors.toSet());
    }

    @Operation(operationId = "getByName")
    @GetMapping("{scope}/name")
    public RoleDto getByName(@PathVariable Long scope, @RequestParam String name) {
        Role role = this.roleService.getByName(this.scopeService.findScopeOrThrow(scope), name);
        return entityMapper.roleToRoleDto(role);
    }

    @GetMapping("all/id")
    public Collection<Long> getAllRoleIds() {
        return this.roleService.getAllIds().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
    }

}
