package net.myplayplanet.permission.core.model;

import net.myplayplanet.permission.core.enums.PermissionValue;
import net.myplayplanet.permission.core.dto.PermissionDto;

import java.util.*;

public class PermissionSet extends HashSet<PermissionDto> {


    public PermissionSet(){
        super();
    }

    public PermissionSet(Map<UUID, PermissionValue> map){
        super();
        for (Map.Entry<UUID, PermissionValue> uuidPermissionValueEntry : map.entrySet()) {
            this.add(uuidPermissionValueEntry.getKey(), uuidPermissionValueEntry.getValue());
        }
    }

    public boolean add(UUID uuid, PermissionValue permissionValue){
        return this.add(new PermissionDto(uuid, permissionValue));
    }

    @Override
    public boolean add(PermissionDto permissionDto) {

        for (PermissionDto dto : this) {
            if(dto.getUuid().equals(permissionDto.getUuid())) return false;
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

    public Map<UUID, PermissionValue> toMap(){
        Map<UUID, PermissionValue> map = new HashMap<>();
        for (PermissionDto permissionDto : this) {
            map.put(permissionDto.getUuid(), permissionDto.getPermissionValue());
        }
        return map;
    }
}
