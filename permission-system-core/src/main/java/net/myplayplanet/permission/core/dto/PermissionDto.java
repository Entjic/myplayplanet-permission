package net.myplayplanet.permission.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.myplayplanet.permission.core.PermissionValue;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {

    private UUID key;
    private PermissionValue permissionValue;



}
