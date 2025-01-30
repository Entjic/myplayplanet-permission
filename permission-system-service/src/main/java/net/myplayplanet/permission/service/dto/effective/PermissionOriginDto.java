package net.myplayplanet.permission.service.dto.effective;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.myplayplanet.permission.service.dto.PermissionDto;
import net.myplayplanet.permission.service.dto.enums.PermissionOrigin;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionOriginDto {

    private PermissionDto permissionDto;
    private PermissionOrigin origin;

}
