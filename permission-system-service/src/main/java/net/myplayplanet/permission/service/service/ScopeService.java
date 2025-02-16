package net.myplayplanet.permission.service.service;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.dto.ScopeDto;
import net.myplayplanet.permission.service.model.Scope;
import net.myplayplanet.permission.service.repository.ScopeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScopeService {

    private final ScopeRepository scopeRepository;

    public Scope findScopeOrThrow(Long id) {
        return this.scopeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No scope with id " + id + " found."));
    }

    public Set<Long> getAllIds() {
        return this.scopeRepository.findAll().stream()
                .map(Scope::getId)
                .collect(Collectors.toSet());
    }

    public Collection<Scope> getAll() {
        return this.scopeRepository.findAll();
    }

    public Scope create(String name) {
        return this.scopeRepository.save(new Scope(null, name));
    }

    public Scope rename(Long id, String name) {
        Scope scope = this.findScopeOrThrow(id);
        scope.setName(name);
        return this.scopeRepository.save(scope);
    }

    public void delete(Long id) {
        this.scopeRepository.deleteById(id);
    }

    public Scope createOrFind(final ScopeDto scopeDto) {
        Optional<Scope> existing = Optional.empty();

        if (scopeDto.getId() != null) {
            existing = this.scopeRepository.findById(scopeDto.getId());
        }

        return existing.orElse(this.create(scopeDto.getName()));

    }
}
