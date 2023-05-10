package com.phrase.assignment.model;

import com.phrase.assignment.dto.LoginResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "account_configurations"
)
public class AccountConfiguration {
    @Id
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    //todo
    private String role;
    private String id;
    private String uid;

    private String token;
    private Timestamp expires;
    private Timestamp lastInvalidateAllSessionsPerformed;

    public AccountConfiguration(LoginResponseDto loginResponse, String password) {
        this.userName = loginResponse.getUser().getUserName();
        this.password = password;
        this.firstName = loginResponse.getUser().getFirstName();
        this.lastName = loginResponse.getUser().getLastName();
        this.email = loginResponse.getUser().getEmail();
        this.role = loginResponse.getUser().getRole();
        this.id = loginResponse.getUser().getId();
        this.uid = loginResponse.getUser().getUid();
        this.token = loginResponse.getToken();
        this.expires = loginResponse.getExpires();
        this.lastInvalidateAllSessionsPerformed = loginResponse.getLastInvalidateAllSessionsPerformed();
    }
}
