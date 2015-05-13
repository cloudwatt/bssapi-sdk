package com.cloudwatt.apis.bss.spec.domain.account.billing;

import java.net.URI;
import java.util.Date;
import java.util.Map;

public interface Invoice {

    /**
     * The ID of invoice
     * 
     * @return the invoice ID
     */
    public int getId();

    /**
     * Get the creation date of Invoice
     * 
     * @return the UTC creation time
     */
    public Date getCreateDate();

    /**
     * Get the due date of Invoice
     * 
     * @return the UTC due time
     */
    public Date getDueDate();

    /**
     * Get the amount in Euros of invoice
     * 
     * @return the invoice amount
     */
    public double getTotalInEuros();

    /**
     * Get the current balance
     * 
     * @return the balance
     */
    public double getBalance();

    public Map<String, URI> getInvoicesURI();

    public Iterable<Payment> getPayments();
}
