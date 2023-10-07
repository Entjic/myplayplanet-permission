package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.core.PermissionSet;
import net.myplayplanet.permission.core.PermissionValue;
import net.myplayplanet.permission.core.dto.PermissionDto;
import net.myplayplanet.permission.service.model.Permission;
import net.myplayplanet.permission.service.model.Role;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleService {


    public Set<PermissionDto> getEffectivePermissions(Set<Role> roles){
        List<Role> sorted = this.sortByWeight(roles);

        PermissionSet set = new PermissionSet();

        for (Role role : sorted) {
            for (Permission permission : role.getGranted()) {
                set.add(permission.getKey(), PermissionValue.GRANTED);
            }
            for (Permission permission : role.getDenied()) {
                set.add(permission.getKey(), PermissionValue.DENIED);
            }
        }

        return set;
    }

    private List<Role> sortByWeight(Set<Role> set){
        List<Role> roles = new ArrayList<>(set);

        roles.sort(Comparator.comparingInt(Role::getWeight));

        return roles;
    }

}
