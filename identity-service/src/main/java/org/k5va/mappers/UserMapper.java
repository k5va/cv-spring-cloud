package org.k5va.mappers;

import org.k5va.dto.CreateUserDto;
import org.k5va.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserDto createUserDto) {
        return User.builder()
                .name(createUserDto.username())
                .email(createUserDto.email())
                .password(createUserDto.password())
                .build();
    }

}
