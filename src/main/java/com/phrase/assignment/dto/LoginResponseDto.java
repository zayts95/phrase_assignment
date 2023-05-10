package com.phrase.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponseDto {

    private UserDto user;
    private String token;
    private Timestamp expires;
    private Timestamp lastInvalidateAllSessionsPerformed;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class UserDto{
        private String firstName;
        private String lastName;
        private String userName;
        private String email;
                //todo
        private String role;
        private String id;
        private String uid;
    }

    public UserDto createUserDto(){
        return new UserDto();
    }
}
