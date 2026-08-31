package ecommerce.user_service.mapper;

import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
//MapStruct generates the implementation for you during compilation and makes it a Spring bean.
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "role", constant = "CUSTOMER")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    //can use mvn compile to generate impl class rather than whole mvn clean install cmd
    // or use in intellij user-service->lifecycle->compile to do it
    List<User> toEntityList(List<UserRequest> requests);
}
