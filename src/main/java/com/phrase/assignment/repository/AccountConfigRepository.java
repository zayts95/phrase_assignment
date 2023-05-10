package com.phrase.assignment.repository;

import com.phrase.assignment.model.AccountConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountConfigRepository extends JpaRepository<AccountConfiguration, String> {

    String findTokenByUserName(String username);
}
