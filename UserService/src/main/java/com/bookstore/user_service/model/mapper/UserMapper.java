package com.bookstore.user_service.model.mapper;

import com.bookstore.user_service.model.dto.UserDto;
import com.bookstore.user_service.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Class to map UserDto to User and User to UserDto.
 *
 * @author Filipa Simões
 */
@Mapper
public interface UserMapper {

    UserDto userToUserDto(User user);

    @Mapping(target="password", ignore = true)
    User userDtoToUser(UserDto userDto);

    List<UserDto> userListToUserDtoList(List<User> userList);

    List<User> userDtoListToUserList(List<UserDto> userDtoList);
}
