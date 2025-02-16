package net.myplayplanet.permission.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.dto.PermissionDisplayDto;
import net.myplayplanet.permission.service.dto.PermissionInfoDto;
import net.myplayplanet.permission.service.dto.enums.DeletionMode;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.service.PermissionService;
import net.myplayplanet.permission.service.service.ScopeService;
import net.myplayplanet.permission.service.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/permission")
@Tag(name = "Permission")
public class PermissionController {

    private final PermissionService permissionService;
    private final UserService userService;
    private final ScopeService scopeService;

    private final EntityMapper entityMapper;

    @PostMapping
    @Operation(summary = "Create a new permission.", description = "This endpoint is used to create a new permission.")
    @ApiResponses({
            @ApiResponse(content = @Content(schema = @Schema(implementation = PermissionInfoDto.class)),
                    responseCode = "200", description = "Permission created successfully."),
            @ApiResponse(content = @Content(schema = @Schema(implementation = ResponseStatusException.class)),
                    responseCode = "409", description = "The ID is not unique."),
            @ApiResponse(content = @Content(schema = @Schema(implementation = ResponseStatusException.class)),
                    responseCode = "404", description = "The referenced parent does not exist."),
    })
    public PermissionInfoDto createPermission(@RequestBody PermissionInfoDto permissionInfoDto) {
        Permission permission = this.permissionService.saveNewPermission(permissionInfoDto.getKey(),
                permissionInfoDto.getParent());

        return entityMapper.permissionToPermissionInfoDto(permission);
    }

    @PutMapping
    @Operation(summary = "Update an existing permission.", description = "This endpoint is used to update an already existing permission.")
    @ApiResponses({
            @ApiResponse(content = @Content(schema = @Schema(implementation = PermissionInfoDto.class)),
                    responseCode = "200", description = "Permission updated successfully."),
            @ApiResponse(content = @Content(schema = @Schema(implementation = ResponseStatusException.class)),
                    responseCode = "404", description = "No permission associated with key found. Did you mean to use /save instead?"),
            @ApiResponse(content = @Content(schema = @Schema(implementation = ResponseStatusException.class)),
                    responseCode = "404", description = "The referenced parent does not exist.")
    })
    public PermissionInfoDto updatePermission(@RequestBody PermissionInfoDto permissionInfoDto) {
        Permission permission = this.permissionService.updateExistingPermission(permissionInfoDto.getKey(),
                permissionInfoDto.getParent());

        return entityMapper.permissionToPermissionInfoDto(permission);
    }


    @DeleteMapping("delete/{key}/mode/{mode}")
    @Operation(summary = "Delete permission.", description = "This endpoint is used to delete a permission. " +
            "There are 3 different modes: Shallow simply deletes the permission, at the risk of ripping a hole in the permission tree. " +
            "Intelligent fixes that hole, by connecting all children of the deleted permission to the deleted permissions parent. " +
            "Recursive also deletes all sub-permissions.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Permission deleted successfully."),
            @ApiResponse(content = @Content(schema = @Schema(implementation = ResponseStatusException.class)),
                    responseCode = "404", description = "The referenced permission does not exist.")
    })
    public Set<String> deletePermission(@Parameter(description = "The key of the permission", required = true)
                                        @PathVariable String key,
                                        @Parameter(description = "The mode used for deleting the permission.", required = true)
                                        @PathVariable DeletionMode mode) {
        return this.permissionService.delete(Set.of(key), mode);
    }

    @Operation(summary = "Check if user has permission.", description = "This endpoint checks if a specified user has a certain permission.")
    @GetMapping("{scope}/has/{user}/permission/{permission}")
    public Boolean hasPermission(@Parameter(description = "ID of the scope")
                                 @PathVariable Long scope,
                                 @Parameter(description = "The UUID of the user")
                                 @PathVariable UUID user,
                                 @Parameter(description = "The UUID of the permission")
                                 @PathVariable String permission) {
        return this.permissionService.hasPermission(this.userService.findUserOrThrow(
                        this.scopeService.findScopeOrThrow(scope),
                        user),
                this.permissionService.findPermissionOrThrow(permission));
    }


    @Operation(summary = "Fetches all existing permissions in a scope.", operationId = "getAllPermissionsByScope")
    @GetMapping("scope/{scope}")
    public Set<String> getAllPermissionsByScope(@PathVariable Long scope) {
        return this.permissionService.getAllByScope(scope).stream().map(Permission::getKey).collect(Collectors.toSet());
    }

    @Operation(summary = "Fetches a permissions by its key.", operationId = "getPermissionByKey")
    @GetMapping("{key}")
    public PermissionDisplayDto getByKey(@PathVariable String key) {
        return this.entityMapper.permissionToPermissionDisplayDto(
                this.permissionService.findPermissionOrThrow(key));
    }

    @PostMapping("{key}/scope/{scope}")
    public void addPermissionToScope(@PathVariable String key, @PathVariable Long scope) {
        this.permissionService.addToScope(key, scope);
    }


}
