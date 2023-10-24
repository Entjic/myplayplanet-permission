package net.myplayplanet.permission.core.dto.effective;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.myplayplanet.permission.core.dto.PermissionDto;
import net.myplayplanet.permission.core.enums.PermissionOrigin;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionOriginDto {

    private PermissionDto permissionDto;
    private PermissionOrigin origin;

}
