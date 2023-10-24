package net.myplayplanet.permission.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID uuid;
    private Set<RoleDto> roles;

    private Set<PermissionDto> explicitPermissions;

}
