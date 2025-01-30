package net.myplayplanet.permission.core.dto.effective;

import com.google.common.base.MoreObjects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.myplayplanet.permission.core.dto.PermissionDto;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EffectiveUserModelDto {

    private UUID user;
    private Set<PermissionDto> permissions;


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("user", user)
                .add("permissions", permissions)
                .toString();
    }
}
