package net.myplayplanet.permission.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.myplayplanet.permission.service.dto.model.PermissionSet;
import net.myplayplanet.permission.service.model.Scope;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteRoleDto {

    private Long id;
    private Scope scope;
    private String name;
    private Integer weight; // Higher weight corresponds to overriding lower value permissions
    private PermissionSet permissions;
    private Boolean editable = false; // If not editable, name is a translation key, otherwise its user input freeform
    private String description;

}
