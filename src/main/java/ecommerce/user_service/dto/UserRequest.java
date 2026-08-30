package ecommerce.user_service.dto;

//only all params constructor present in record
//use userRequest.firstName(), userRequest.phone() etc for getters. No "get" keyword here.
//no setter
public record UserRequest(
        String firstName,
        String lastName,
        String email,
        String phone,
        String password
) {
}
