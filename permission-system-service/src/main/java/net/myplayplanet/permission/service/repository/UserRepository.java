package net.myplayplanet.permission.service.repository;

import net.myplayplanet.permission.service.model.Role;
import net.myplayplanet.permission.service.model.Scope;
import net.myplayplanet.permission.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByScopeAndUuid(Scope scope, UUID uuid);

    Collection<User> findAllByScope(Scope scope);

}
