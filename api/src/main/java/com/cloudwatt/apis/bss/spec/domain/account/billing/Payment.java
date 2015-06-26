package com.cloudwatt.apis.bss.spec.domain.account.billing;

import java.util.Date;

/**
 * Payment information
 * 
 * @author pierre souchay
 *
 */
public interface Payment {

    /**
     * Get the ID of payment
     * 
     * @return the ID of Payment
     */
    public int getId();

    /**
     * Get the amount of payment
     * 
     * @return the amount
     */
    public double getAmountInEuros();

    /**
     * Date of Payment
     * 
     * @return the date of payment
     */
    public Date getCreateDate();

}
