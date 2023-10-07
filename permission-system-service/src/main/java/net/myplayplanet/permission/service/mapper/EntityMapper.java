package net.myplayplanet.permission.service.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    Integer mapToId(ExampleEntity example);

}
