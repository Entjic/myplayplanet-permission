package net.myplayplanet.permission.service.controller;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.core.dto.ScopeDto;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.service.ScopeService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/permission/scope/")
public class ScopeController {

    private final ScopeService scopeService;

    private final EntityMapper entityMapper;

    @GetMapping("{id}/")
    public ScopeDto getById(@PathVariable Long id) {
        return this.entityMapper.mapScopeToScopeDto(this.scopeService.findScopeOrThrow(id));
    }

    @PostMapping("create/{name}/")
    public ScopeDto create(@PathVariable String name) {
        return this.entityMapper.mapScopeToScopeDto(this.scopeService.create(name));
    }

    @PostMapping("rename/{id}/name/{name}/")
    public ScopeDto rename(@PathVariable Long id, @PathVariable String name) {
        return this.entityMapper.mapScopeToScopeDto(this.scopeService.rename(id, name));
    }

    @DeleteMapping("{id}/")
    public ScopeDto delete(@PathVariable Long id) {
        ScopeDto scopeDto = this.entityMapper.
                mapScopeToScopeDto(this.scopeService.findScopeOrThrow(id));
        this.scopeService.delete(id);
        return scopeDto;
    }

    @GetMapping("all/id/")
    public Set<Long> getAllScopeIds() {
        return this.scopeService.getAllIds();
    }

    @GetMapping("all/")
    public Set<ScopeDto> getAll() {
        return this.entityMapper.mapScopesToScopeDtos(this.scopeService.getAll());
    }

}
