package net.myplayplanet.permission.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private Long key;
    private String name;
    private String description;
    private Integer weight;
    private Set<PermissionDto> permissions;
    private Boolean editable;

}
