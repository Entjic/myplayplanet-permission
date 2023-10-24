package net.myplayplanet.permission.core.dto;

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
    private String description;

    private UUID parent; // nullable

    public PermissionInfoDto(UUID key, String name, String description){
        this(key, name, description, null);
    }

    public PermissionInfoDto(UUID key){
        this(key, null, null);
    }

}
