package net.myplayplanet.permission.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionInfoDto {

    private UUID key;
    private String name;

    private UUID parent; // nullable
}
