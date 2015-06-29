package com.cloudwatt.apis.bss.impl.accountapi;

import com.cloudwatt.apis.bss.spec.domain.account.AccountMinimalInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

public class MinimumAccountInformationImpl implements AccountMinimalInformation {

    @JsonCreator
    public MinimumAccountInformationImpl(@JsonProperty(value = "email", required = true) final String email,
            @JsonProperty(value = "name", required = true) final String name,
            @JsonProperty(value = "corporate_name", required = true) final String corporateName) {
        this.name = name;
        this.corporateName = corporateName;
        this.email = email;
    }

    private final String email, name, corporateName;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<String> getCorporateName() {
        return Optional.of(corporateName);
    }

}
