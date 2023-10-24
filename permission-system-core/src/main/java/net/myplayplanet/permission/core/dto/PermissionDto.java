package net.myplayplanet.permission.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.myplayplanet.permission.core.enums.PermissionValue;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PermissionDto {

    private UUID key;
    private PermissionValue permissionValue;



}
