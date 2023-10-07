package net.myplayplanet.permission.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ExtendedEffectiveUserModelDto {

    private UUID user;
    private Set<RoleDto> roles;
    private Set<ExtensivePermissionDto> permissionDtos;

}
