package com.cloudwatt.apis.bss.impl;

import com.cloudwatt.apis.bss.spec.commonapi.FindUserApi.FindUserQuery;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class CWIdentityQuery implements FindUserQuery {

    /**
     * Constructor, package protected
     * 
     * @param email
     * @param firstName
     * @param lastName
     */
    public CWIdentityQuery(String email, Optional<String> firstName, Optional<String> lastName) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("email cannot be null");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    private final Optional<String> lastName;

    private final Optional<String> firstName;

    private final String email;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getLastName() {
        return lastName.orNull();
    }

    @Override
    public String getFirstName() {
        return firstName.orNull();
    }
}
