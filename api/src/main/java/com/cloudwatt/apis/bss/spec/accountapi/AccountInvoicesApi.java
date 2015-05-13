/**
 * 
 */
package com.cloudwatt.apis.bss.spec.accountapi;

import java.io.IOException;
import com.cloudwatt.apis.bss.spec.domain.account.billing.Invoice;
import com.cloudwatt.apis.bss.spec.exceptions.TooManyRequestsException;

/**
 * The Invoice API, use it to retrieve the invoices of an account
 * 
 * @author pierre souchay
 *
 */
public interface AccountInvoicesApi {

    /**
     * Get all the invoices for current account
     * 
     * @return the list of invoices
     */
    Iterable<Invoice> getInvoices() throws IOException, TooManyRequestsException;
}
