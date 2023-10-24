package net.myplayplanet.permission.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private Long key;
    private Integer weight;
    private Set<PermissionDto> permissions;

}
