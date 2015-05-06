/**
 * $URL$
 *
 * $LastChangedBy$ - $LastChangedDate$
 */
package com.cloudwatt.apis.bss.impl.contactapi;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Pierre Souchay <pierre@souchay.net>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactInformationWithRoles {

    /**
     * Constructor
     * 
     * @param id
     * @param contact
     * @param accounts
     */
    @JsonCreator
    public ContactInformationWithRoles(@JsonProperty("id") UUID id, @JsonProperty("contact") Contact contact,
            @JsonProperty("accounts") Iterable<AccountRoles> accounts) {
        super();
        this.id = id;
        this.contact = contact;
        this.accounts = accounts;
    }

    /**
     * get the id
     * 
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * get the contact
     * 
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * get the accounts
     * 
     * @return the accounts
     */
    public Iterable<AccountRoles> getAccounts() {
        return accounts;
    }

    private final UUID id;

    private final Contact contact;

    private final Iterable<AccountRoles> accounts;

}
