package net.myplayplanet.permission.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDisplayDto {
    private UUID uuid;
    private String name;
}
