package net.myplayplanet.permission.spring;


import net.myplayplanet.permission.model.PermissionInfoDto;
import net.myplayplanet.permission.model.RoleDto;
import net.myplayplanet.permission.model.ScopeDto;

import java.util.Collection;

public interface PermissionAutoRegister {

    String scopeName();

    Collection<PermissionInfoDto> permissions();

    Collection<RoleDto> defaultRoles();

}
