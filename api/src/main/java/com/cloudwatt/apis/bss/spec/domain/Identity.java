package com.cloudwatt.apis.bss.spec.domain;

/**
 * Information about an Identity aka a physical person that may call the APIs
 * 
 * @author pierre souchay
 *
 */
public interface Identity extends IdEmail {

    /**
     * Identifier of identity
     * 
     * @return a not null identifier
     */
    public String getId();

    /**
     * Get the name of Identity (technical key).
     * 
     * Note that the name is not a real name
     * 
     * @return the name of identity, as returned by keystone
     */
    public String getName();
}
