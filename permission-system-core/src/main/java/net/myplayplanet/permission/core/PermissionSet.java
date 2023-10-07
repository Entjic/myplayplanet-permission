package net.myplayplanet.permission.core;

import net.myplayplanet.permission.core.dto.PermissionDto;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class PermissionSet extends HashSet<PermissionDto> {


    public boolean add(UUID uuid, PermissionValue permissionValue){
        return this.add(new PermissionDto(uuid, permissionValue));
    }

    @Override
    public boolean add(PermissionDto permissionDto) {

        for (PermissionDto dto : this) {
            if(dto.getKey().equals(permissionDto.getKey())) return false;
        }

        return super.add(permissionDto);
    }

    @Override
    public boolean addAll(Collection<? extends PermissionDto> c) {

        for (PermissionDto permissionDto : c) {
            this.add(permissionDto);
        }

        return true;
    }
}
