package net.myplayplanet.permission.core.dto;

import com.google.common.base.MoreObjects;
import lombok.*;
import net.myplayplanet.permission.core.enums.PermissionValue;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {

    private UUID uuid;
    private PermissionValue permissionValue;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .add("permissionValue", permissionValue)
                .toString();
    }
}
