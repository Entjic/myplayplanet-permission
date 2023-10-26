package net.myplayplanet.permission.service.repository;

import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Collection<Role> findAllByScope(Scope scope);

}
