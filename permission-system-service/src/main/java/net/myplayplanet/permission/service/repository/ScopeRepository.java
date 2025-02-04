package net.myplayplanet.permission.service.repository;

import net.myplayplanet.permission.service.model.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScopeRepository extends JpaRepository<Scope, Long> {

    Scope findByName(String name);
}
