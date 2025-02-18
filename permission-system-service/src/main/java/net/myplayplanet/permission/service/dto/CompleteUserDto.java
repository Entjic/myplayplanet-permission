package net.myplayplanet.permission.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.myplayplanet.permission.service.dto.model.PermissionSet;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompleteUserDto {
    private UUID uuid;
    private PermissionSet permissions; // user specific permissions

}
