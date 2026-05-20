package com.avocadogroup.recipy.user;

import com.avocadogroup.recipy.user.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Dto to expose the user object
    @Mapping(target = "verified", source = "emailVerified")
    UserDto toDto(User user);
}
