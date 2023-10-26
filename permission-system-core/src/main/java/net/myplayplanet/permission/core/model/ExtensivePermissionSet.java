package net.myplayplanet.permission.core.model;

import net.myplayplanet.permission.core.dto.effective.ExtensivePermissionDto;

import java.util.*;

public class ExtensivePermissionSet extends HashSet<ExtensivePermissionDto> {

    public ExtensivePermissionSet(){
        super();
    }

    public ExtensivePermissionSet(Map<UUID, ExtensivePermissionDto> map){
        super(new HashSet<>(map.values()));
    }

    @Override
    public boolean add(ExtensivePermissionDto extensivePermissionDto) {

        for (ExtensivePermissionDto dto : this) {
            if(dto.getPermissionDto().getUuid().equals(extensivePermissionDto.getPermissionDto().getUuid())) return false;
        }

        return super.add(extensivePermissionDto);
    }


    public Map<UUID, ExtensivePermissionDto> toMap(){
        Map<UUID, ExtensivePermissionDto> map = new HashMap<>();
        for (ExtensivePermissionDto permissionDto : this) {
            map.put(permissionDto.getPermissionDto().getUuid(), permissionDto);
        }
        return map;
    }

}
