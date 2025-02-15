package net.myplayplanet.permission.service.dto.model;

import lombok.Getter;
import net.myplayplanet.permission.service.dto.PermissionDto;
import net.myplayplanet.permission.service.dto.enums.PermissionValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Getter
public class PermissionSet extends HashSet<PermissionDto> {
    private final Long scope;

    public PermissionSet(final Long scope) {
        super();
        this.scope = scope;
    }

    public PermissionSet(Map<String, PermissionValue> map, final Long scope) {
        super();
        this.scope = scope;
        for (Map.Entry<String, PermissionValue> keyPermissionValueEntry : map.entrySet()) {
            this.add(keyPermissionValueEntry.getKey(), keyPermissionValueEntry.getValue());
        }
    }

    public boolean add(String key, PermissionValue permissionValue) {
        return this.add(new PermissionDto(key, permissionValue));
    }

    public boolean contains(String key) {
        for (final PermissionDto permissionDto : this) {
            if (permissionDto.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, PermissionValue> toMap() {
        Map<String, PermissionValue> map = new HashMap<>();
        for (PermissionDto permissionDto : this) {
            map.put(permissionDto.getKey(), permissionDto.getPermissionValue());
        }
        return map;
    }
}
