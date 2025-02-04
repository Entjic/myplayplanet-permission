package net.myplayplanet.permission.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.dto.ScopeDto;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import net.myplayplanet.permission.service.service.ScopeService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/permission/scope/")
@Tag(name = "Scope")
public class ScopeController {

    private final ScopeService scopeService;

    private final EntityMapper entityMapper;

    @Operation(operationId = "getScopeById")
    @GetMapping("{id}/")
    public ScopeDto getById(@PathVariable Long id) {
        return this.entityMapper.mapScopeToScopeDto(this.scopeService.findScopeOrThrow(id));
    }

    @PostMapping("getOrCreate/")
    public ScopeDto getOrCreateScope(@RequestBody ScopeDto scopeDto) {
        return this.entityMapper.mapScopeToScopeDto(this.scopeService.createOrFind(scopeDto.getName()));
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

    @Operation(operationId = "getAllScopeIds")
    @GetMapping("all/id/")
    public Set<Long> getAllScopeIds() {
        return this.scopeService.getAllIds();
    }

    @Operation(operationId = "getAllScopes")
    @GetMapping("all/")
    public Set<ScopeDto> getAll() {
        return this.entityMapper.mapScopesToScopeDtos(this.scopeService.getAll());
    }

}
