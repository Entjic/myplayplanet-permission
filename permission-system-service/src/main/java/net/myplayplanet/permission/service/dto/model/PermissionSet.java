package net.myplayplanet.permission.service.dto.model;

import net.myplayplanet.permission.service.dto.PermissionDto;
import net.myplayplanet.permission.service.dto.enums.PermissionValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class PermissionSet extends HashSet<PermissionDto> {


    public PermissionSet() {
        super();
    }

    public PermissionSet(Map<UUID, PermissionValue> map) {
        super();
        for (Map.Entry<UUID, PermissionValue> uuidPermissionValueEntry : map.entrySet()) {
            this.add(uuidPermissionValueEntry.getKey(), uuidPermissionValueEntry.getValue());
        }
    }

    public boolean add(UUID uuid, PermissionValue permissionValue) {
        return this.add(new PermissionDto(uuid, permissionValue));
    }

    public boolean contains(UUID uuid) {
        for (final PermissionDto permissionDto : this) {
            if (permissionDto.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Map<UUID, PermissionValue> toMap() {
        Map<UUID, PermissionValue> map = new HashMap<>();
        for (PermissionDto permissionDto : this) {
            map.put(permissionDto.getUuid(), permissionDto.getPermissionValue());
        }
        return map;
    }
}
