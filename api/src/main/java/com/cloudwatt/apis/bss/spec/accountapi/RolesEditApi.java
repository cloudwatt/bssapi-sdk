/**
 * 
 */
package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.domain.Identity;
import com.cloudwatt.apis.bss.spec.exceptions.IOExceptionLocalized;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * @author pierre
 *
 */
public interface RolesEditApi {

    /**
     * Exception thrown when a role cannot be used or does not exists
     * 
     * @author Pierre Souchay
     *
     */
    public static class NoSuchRoleException extends IOExceptionLocalized {

        /**
         * 
         */
        private static final long serialVersionUID = 6364994256412535546L;

        /**
         * Constructor
         * 
         * @param roleName
         */
        public NoSuchRoleException(String roleName) {
            super("NoSuchRoleException", roleName); //$NON-NLS-1$
        }

    }

    /**
     * Removes the given Role
     * 
     * @param roleToRemove the role to remove
     */
    public void removeRole(IdentityToAccountRole roleToRemove) throws IOException, TooManyRequestsException;

    /**
     * Add a role to an identity
     * 
     * @param identity the identity to add
     * @param roleToAdd the role to add
     */
    public void addRoleToIdentity(Identity identity, String roleToAdd) throws IOException, TooManyRequestsException,
            NoSuchRoleException;

    /**
     * List the roles you can add to this account
     * 
     * @return the list of roles you may add to this account
     * @throws IOException if something bad happens
     * @throws TooManyRequestsException if too ma,y requests are performed, please slow down
     */
    public Iterable<String> listAllowedRolesForAccount() throws IOException, TooManyRequestsException;
}
