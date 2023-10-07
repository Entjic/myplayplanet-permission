package net.myplayplanet.permission.core.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.myplayplanet.permission.core.PermissionOrigin;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionOriginDto {

    private PermissionDto permissionDto;
    private PermissionOrigin origin;

}
