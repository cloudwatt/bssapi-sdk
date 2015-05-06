package com.cloudwatt.apis.bss.impl;

import com.cloudwatt.apis.bss.spec.domain.Identity;

class CWIdentity implements Identity {

    /**
     * Constructor, package protected
     * 
     * @param id
     * @param email
     */
    CWIdentity(String id, String name, String email) {
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
