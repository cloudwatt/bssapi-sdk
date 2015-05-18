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
     * Type of invoices you want to fetch
     * 
     * @author pierre souchay
     *
     */
    public static enum InvoiceExtension {
        /**
         * PDF invoices
         */
        pdf,
        /**
         * CSV invoice (not contractual, but useful for reports)
         */
        csv
    };

    /**
     * Interface returned by API
     * 
     * @author pierre souchay
     *
     */
    public static interface InvoicesQueryBuilder {

        /**
         * Get the invoices, call this once you customization is over
         * 
         * @return the list of invoices
         */
        public Iterable<Invoice> get() throws IOException, TooManyRequestsException;

        /**
         * Get the invoices with given type of invoices
         * 
         * @param extensions the extension list you want to have
         * @return itself
         */
        public InvoicesQueryBuilder setExtensions(InvoiceExtension... extensions);
    }

    /**
     * Get all the builder to fetch invoices for current account.
     * 
     * @return a builder you can customize, simply use {@link InvoicesQueryBuilder#get()} if you don't want to customize
     *         the request and use default parameters
     */
    InvoicesQueryBuilder get();
}
