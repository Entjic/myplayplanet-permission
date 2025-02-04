package net.myplayplanet.permission.service.dto;

import com.google.common.base.MoreObjects;
import lombok.*;
import net.myplayplanet.permission.service.dto.enums.PermissionValue;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {

    private UUID uuid;
    private Long scope;
    private PermissionValue permissionValue;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .add("permissionValue", permissionValue)
                .toString();
    }
}
