package net.myplayplanet.permission.service.controller;

import lombok.RequiredArgsConstructor;
import net.myplayplanet.permission.service.mapper.EntityMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("api/v1/example/")
public class ExampleController {

    private final ExampleService exampleService;
    private final EntityMapper entityMapper;

    @GetMapping(path = "just/an/example/")
    public Integer example(){
        ExampleEntity example = exampleService.example();
        return entityMapper.mapToId(example);
    }

}
