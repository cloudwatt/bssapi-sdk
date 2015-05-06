/**
 * 
 */
package com.cloudwatt.apis.bss.impl.accountapi;

import java.util.Set;
import com.cloudwatt.apis.bss.spec.domain.AccountWithRoles;

/**
 * @author pierre
 *
 */
public class AccountWithRolesAndCaps implements AccountWithRoles {

    private final String customerId;

    private final Set<String> namedRoles;

    private final Set<String> caps;

    @Override
    public String getCustomerId() {
        return customerId;
    }

    @Override
    public Set<String> getNamedRoles() {
        return namedRoles;
    }

    public AccountWithRolesAndCaps(String customerId, Set<String> namedRoles, Set<String> caps) {
        super();
        this.customerId = customerId;
        this.namedRoles = namedRoles;
        this.caps = caps;
    }

    @Override
    public Set<String> getCaps() {
        return caps;
    }

}
