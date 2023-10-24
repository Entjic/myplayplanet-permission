package net.myplayplanet.permission.core.dto.effective;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.myplayplanet.permission.core.dto.RoleDto;
import net.myplayplanet.permission.core.model.ExtensivePermissionSet;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ExtensiveEffectiveUserModelDto {

    private UUID user;
    private Set<RoleDto> roles;
    private ExtensivePermissionSet permissionDtos;


}
