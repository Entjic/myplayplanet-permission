package net.myplayplanet.permission.core.dto.effective;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.myplayplanet.permission.core.dto.PermissionDto;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EffectiveUserModelDto {

    private UUID user;
    private Set<PermissionDto> permissions;




}
