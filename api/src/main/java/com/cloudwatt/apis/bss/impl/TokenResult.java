/**
 * $URL$
 *
 * $LastChangedBy$ - $LastChangedDate$
 */
package com.cloudwatt.apis.bss.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import com.cloudwatt.apis.bss.spec.domain.Identity;
import com.cloudwatt.apis.bss.spec.exceptions.HttpUnexpectedError;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;
import com.cloudwatt.apis.bss.spec.exceptions.WrongCredentialsException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Optional;

/**
 * @author Pierre Souchay <pierre@souchay.net>
 */
public class TokenResult {

    public static class Auth {

        private final PasswordCredentials passwordCredentials;

        public PasswordCredentials getPasswordCredentials() {
            return passwordCredentials;
        }

        Auth(PasswordCredentials passwordCredentials) {
            this.passwordCredentials = passwordCredentials;
        }

    }

    public static class AuthPayload {

        private final Auth auth;

        public AuthPayload(Auth auth) {
            super();
            this.auth = auth;
        }

        public Auth getAuth() {
            return auth;
        }

    }

    static class PasswordCredentials {

        private final String username;

        private final String password;

        public PasswordCredentials(String username, String password) {
            super();
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static AuthPayload buildCrendentialsPayload(String email, String password) {
        return new AuthPayload(new Auth(new PasswordCredentials(email, password)));

    }

    /**
     * Get a token
     * 
     * @param client
     * @param url
     * @param payload
     * @return the token
     * @throws IOException, TooManyRequestsException
     */
    public static Optional<TokenResult> getToken(final WebClient client, String url, final AuthPayload payload)
            throws IOException, TooManyRequestsException, WrongCredentialsException, HttpUnexpectedError {
        HttpPost post = new HttpPost(url);
        post.setHeader(Constants.HEADER_NAME_CONTENT_TYPE, Constants.HEADER_VALUE_APPLICATION_JSON);
        post.setHeader(Constants.HEADER_NAME_ACCEPT, Constants.HEADER_VALUE_APPLICATION_JSON);
        try {
            ObjectWriter ow = new ObjectMapper().writer();
            final String json = ow.writeValueAsString(payload);
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        } catch (JsonProcessingException shouldNotHappen) {
            throw new IOException("JSONSerialization issue " + shouldNotHappen.getClass() + ": " + shouldNotHappen.getMessage(), shouldNotHappen); //$NON-NLS-1$//$NON-NLS-2$
        }
        try {
            return client.doRequestAndRetrieveResultAsJSON(TokenResult.class, post, Optional.<TokenAccess> absent());
        } catch (HttpUnexpectedError err) {
            if (err.getHttpCode() == 401)
                throw new WrongCredentialsException(post.getURI(), err.getHttpCode(), err.getHttpMessage());
            else
                throw err;
        }
    }

    public static class RolesSet extends HashSet<Role> {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(Include.NON_EMPTY)
    public static class Role {

        private final String id;

        private final String name;

        private final String description;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getServiceId() {
            return serviceId;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getTenantName() {
            return tenantName;
        }

        private final String serviceId;

        private final String tenantId;

        private final String tenantName;

        @JsonCreator
        public Role(@JsonProperty(value = "id", required = false) String id,
                @JsonProperty(value = "name", required = false) String name,
                @JsonProperty(value = "description", required = false) String description,
                @JsonProperty(value = "serviceId", required = false) String serviceId,
                @JsonProperty(value = "tenantId", required = false) String tenantId,
                @JsonProperty(value = "tenantName", required = false) String tenantName) {
            this.id = id;
            this.name = name;
            this.serviceId = serviceId;
            this.tenantId = tenantId;
            this.tenantName = tenantName;
            this.description = description;
        }
    }

    @JsonInclude(Include.NON_EMPTY)
    public static class UserIAM implements Identity {

        public UserIAM() {
        }

        /**
         * get the username
         * 
         * @return the username
         */
        public String getUsername() {
            return username;
        }

        /**
         * Set the username
         * 
         * @param username the username to set
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * get the id
         * 
         * @return the id
         */
        @Override
        public String getId() {
            return id;
        }

        /**
         * Set the id
         * 
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * get the name
         * 
         * @return the name
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * Set the name
         * 
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * get the email
         * 
         * @return the email
         */
        @Override
        public String getEmail() {
            return email;
        }

        /**
         * Set the email
         * 
         * @param email the email to set
         */
        public void setEmail(String email) {
            this.email = email;
        }

        private transient Iterable<String> bssAccounts = new LinkedList<String>();

        /**
         * get the bssAccounts
         * 
         * @return the bssAccounts
         */
        public Iterable<String> getBssAccounts() {
            return bssAccounts;
        }

        /**
         * Set the bssAccounts
         * 
         * @param bssAccounts the bssAccounts to set
         */
        public void setBssAccounts(Iterable<String> bssAccounts) {
            this.bssAccounts = bssAccounts;
        }

        /**
         * get the roles_links
         * 
         * @return the roles_links
         */
        public Iterable<String> getRoles_links() {
            return roles_links;
        }

        /**
         * Set the roles_links
         * 
         * @param roles_links the roles_links to set
         */
        public void setRoles_links(Iterable<String> roles_links) {
            this.roles_links = roles_links;
        }

        /**
         * get the roles
         * 
         * @return the roles
         */
        public Iterable<Role> getRoles() {
            return roles;
        }

        /**
         * Set the roles
         * 
         * @param roles the roles to set
         */
        public void setRoles(Iterable<Role> roles) {
            this.roles = roles;
        }

        private String username, id, name, email;

        private Iterable<String> roles_links;

        private Iterable<Role> roles;

    }

    /**
     * 
     * @author Pierre Souchay <pierre@souchay.net> (last changed by $LastChangedBy$)
     * @version $Revision$
     * 
     */
    public static class TokenAccess {

        @JsonCreator
        public TokenAccess(@JsonProperty(value = "token", required = true) Token token,
                @JsonProperty(value = "user", required = true) UserIAM user) {
            assert (token != null);
            assert (user != null);
            this.token = token;
            this.user = user;
        }

        private final Token token;

        @JsonProperty
        private Iterable<JsonNode> serviceCatalog;

        @JsonProperty
        private UserIAM user;

        @JsonProperty
        private JsonNode metadata;

        /**
         * Set the user
         * 
         * @param user the user to set
         */
        public void setUser(UserIAM user) {
            this.user = user;
        }

        /**
         * get the user
         * 
         * @return the user
         */
        public UserIAM getUser() {
            return user;
        }

        /**
         * get the metadata
         * 
         * @return the metadata
         */
        public JsonNode getMetadata() {
            return metadata;
        }

        /**
         * Set the metadata
         * 
         * @param metadata the metadata to set
         */
        public void setMetadata(JsonNode metadata) {
            this.metadata = metadata;
        }

        /**
         * get the serviceCatalog
         * 
         * @return the serviceCatalog
         */
        public Iterable<JsonNode> getServiceCatalog() {
            return serviceCatalog;
        }

        /**
         * Set the serviceCatalog
         * 
         * @param serviceCatalog the serviceCatalog to set
         */
        public void setServiceCatalog(Iterable<JsonNode> serviceCatalog) {
            this.serviceCatalog = serviceCatalog;
        }

        /**
         * get the token
         * 
         * @return the token
         */
        public Token getToken() {
            return token;
        }

    }

    @JsonCreator
    public TokenResult(@JsonProperty("access") TokenAccess access) {
        this.access = access;
    }

    /**
     * get the access
     * 
     * @return the access
     */
    @JsonDeserialize(as = TokenAccess.class)
    public TokenAccess getAccess() {
        return access;
    }

    private final TokenAccess access;

    @JsonInclude(Include.NON_NULL)
    public static class Token {

        private final String id;

        public String getId() {
            return id;
        }

        public Date getExpires() {
            return expires;
        }

        public Tenant getTenant() {
            return tenant;
        }

        private final Date expires;

        private final Tenant tenant;

        @JsonCreator
        public Token(@JsonProperty("id") String id, @JsonProperty("expires") Date expires,
                @JsonProperty(value = "tenant", required = false) Tenant tenant) {
            super();
            this.id = id;
            this.expires = expires;
            this.tenant = tenant;
        }

    }

    public static class Tenant {

        @JsonCreator
        public Tenant(@JsonProperty("id") String id, @JsonProperty("name") String name,
                @JsonProperty("description") String description, @JsonProperty("enabled") Boolean enabled) {
            super();
            this.id = id;
            this.name = name;
            this.description = description;
            this.enabled = enabled;
        }

        private final String id;

        private final String name;

        private final String description;

        private final Boolean enabled;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Boolean getEnabled() {
            return enabled;
        }
    }

}
