package net.myplayplanet.permission.service.dto.model;

import net.myplayplanet.permission.service.dto.effective.ExtensivePermissionDto;

import java.util.*;

public class ExtensivePermissionSet extends HashSet<ExtensivePermissionDto> {

    public ExtensivePermissionSet(){
        super();
    }

    public ExtensivePermissionSet(Map<String, ExtensivePermissionDto> map){
        super(new HashSet<>(map.values()));
    }

    @Override
    public boolean add(ExtensivePermissionDto extensivePermissionDto) {

        for (ExtensivePermissionDto dto : this) {
            if(dto.getPermissionDto().getKey().equals(extensivePermissionDto.getPermissionDto().getKey())) return false;
        }

        return super.add(extensivePermissionDto);
    }


    public Map<String, ExtensivePermissionDto> toMap(){
        Map<String, ExtensivePermissionDto> map = new HashMap<>();
        for (ExtensivePermissionDto permissionDto : this) {
            map.put(permissionDto.getPermissionDto().getKey(), permissionDto);
        }
        return map;
    }

}
