package com.cloudwatt.apis.bss.spec.commonapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.domain.IdEmail;
import com.cloudwatt.apis.bss.spec.domain.Identity;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

public interface FindUserApi {

    public interface FindUserQuery extends IdEmail {

        /**
         * Get the Last name of identity
         * 
         * @return the last name if set
         */
        public String getLastName();

        /**
         * Get the first name of identity if set in the query
         * 
         * @return the first name
         */
        public String getFirstName();
    }

    public interface FindUserQueryBuilder {

        public FindUserQuery build();

        public FindUserQueryBuilder setLastName(String lastName);

        public FindUserQueryBuilder setFirstName(String firstName);

    }

    /**
     * Create a Query Builder
     * 
     * @param email the email to search for
     * @return the Builder to query API
     */
    public FindUserQueryBuilder builder(String email);

    /**
     * Find User by its identity
     * 
     * @param query the query
     * @return the Identity
     */
    public Identity findUser(FindUserQuery query) throws IOException, TooManyRequestsException;

}
