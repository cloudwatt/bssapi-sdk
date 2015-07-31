package com.cloudwatt.apis.bss.impl;

import com.cloudwatt.apis.bss.spec.domain.Identity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
class CWIdentity implements Identity {

    /**
     * Constructor, package protected
     * 
     * @param id
     * @param email
     */
    @JsonCreator
    public CWIdentity(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("email") String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    private final String id;

    private final String name;

    private final String email;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

}
