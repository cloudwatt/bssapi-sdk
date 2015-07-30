[![Build Status](https://api.travis-ci.org/cloudwatt/bssapi-sdk.svg)](https://travis-ci.org/cloudwatt/bssapi-sdk)

BSS-SDK
=======

This directory contains the api itself ([api directory](api/README.md)) and several working examples in the directory [api/src/test/java/com/cloudwatt/apis/bss/](./api/src/test/java/com/cloudwatt/apis/bss/)

Binaries
--------
[https://github.com/cloudwatt/bssapi-sdk/releases](https://github.com/cloudwatt/bssapi-sdk/releases)

Changelog
---------

- v0.2.3: fixed various bug fixes. Added methods to fetch consumption in real time.
- v0.2.1: first public release. Only read only methods for now.

Business Support System API
===========================

Overview
--------

Business Support System (BSS) API let you manage your account, identity information and automate creation of accounts, identities, and openstack projects. It also exposes Openstack features that are normally open only to Cloud administrators such as granting roles on tenants or creating new users and tenants.

If you need to automatically create tenants, users, add guest users to your tenants, retrieve invoices and consumption, this is probably the API you needs.

Semantics and concepts
----------------------

BSS APIs uses the same semantics as Openstack APIs. It uses the same authentication mechanism (tokens), uses a role-based autorization the same way Openstack does and exposes the same kind of Rest semantics.

In order to use this API, you need to understand the basic concept :

 - the identity (or contact): the user performing a call, basically an email and password. A User of BSS/Openstack API
 - the account: the billing entity. Each month, each account pays for the tenants it owns. For each account, an invoice is generated. Optionnaly, you can create some sub accounts. If you use this features, the parent account pays the bills for all its sub accounts (of course it means the parent account will pay for all consumption of all tenants found in all sub accounts).
 
In order to use an account, an identity needs to have roles in the same way an identity has roles to some tenant(s). In the same way as Openstack, the identity can have one or more roles on a specific tenant, thus the operations you can perform are the union of all the capabilities of all you roles, meaning if you have two roles, you can perform all operations granted by role1 and role2. Once again, those are the same semantics as the ones found in Openstack.

Usage
-----

In Openstack, when an identity connects thru keystone, it can list the tenants it can work with. In BSS API, the same mechanism is present, which means that you can list all the accounts you can work with.

However, while Openstack does not explain what are the features you can use if you have a role, when you list the roles in BSS API, it also gives you the list of operations you can perform (this is known as Capabilities).

By reading the documentation of APIs, you can decide without calling the service whenever you may call it (this feature is not present in Openstack).

Example Get a listing of the invoices of all the accounts I have:

1. Get a token
2. List the capabilities you have on all accounts you can use
3. for each account where I have the capability BILLING_INVOICES, get the listing of invoices for this account

### Example with CURL

Note: this example requires [curl](http://curl.haxx.se/download.html) and [jq](http://stedolan.github.io/jq/download/) (in order to extract the correct fields from JSON data)

```sh

export OS_USERNAME=myemail@example.com
export OS_PASSWORD=mypassword

 # Get a token
export OS_TOKEN=$(curl -H Accept:application/json -H Content-Type:application/json https://identity2.fr1.cloudwatt.com/v2.0/tokens -d '{"auth":{"passwordCredentials": { "username": "myemail@example.com", "password": "MY_PASSWORD"}}}' | jq .access.token.id)

 # List all accounts
ALL_ACCOUNTS=$(curl -H Accept:application/json -H X-Auth-Token:$OS_TOKEN https://bssapi.fr1.cloudwatt.com/bss/1/contact/roles | jq .accounts)

 # For each account wih CAP BILLING_INVOICES, show invoices
accNum=0; for account in $(echo $ALL_ACCOUNTS|jq -r .[].account); do echo "-----" $account; curAccount=$(echo $ALL_ACCOUNTS|jq .[$accNum]); echo $curAccount | grep "BILLING_INVOICES" > /dev/null && curl -H Accept:application/json -H X-Auth-Token:$OS_TOKEN "https://bssapi.fr1.cloudwatt.combss/1/accounts/${account}/listInvoices"|jq . || echo "- Invoices not available"  ; accNum=$(($accNum+1));done
```

As shown, when using HTTP only API, you have to check the Capabilities by yourself.

### Java Example with Java SDK

```java

String email="myemail@example.com";
String password="mypassword";

 // 1. Get API
final BSSApiHandle mainApi = new BSSAccountFactory.Builder(email, password).build().getHandle();

 // 2. List all accounts
for (AccountWithRolesWithOperations a : mainApi.getAccounts()){
    final AccountApi api = a.getApi();
    
 // 3. Check if we can list the API (it automatically checks the cap)
	Optional<AccountInvoicesApi> myApi = api.getInvoicesApi();
    if (myApi.isPresent()) {
        System.out.println("Listing of Invoices for account "+a.getCustomerId());
        for (Invoice invoice : myApi.get().get().setExtensions(InvoiceExtension.pdf).get()) {
           System.out.print("\t" + invoice.getId() 
                                 + " (" + invoice.getTotalInEuros()+ "EUR) created the "
                                 + invoice.getCreateDate() + ", URLs: [");
           for (Map.Entry<String, URI> en : invoice.getInvoicesURI().entrySet()) {
              System.out.print(" " + en.getKey() + ": " + en.getValue().toASCIIString());
           }
           System.out.println("]");
        }
    } else {
       System.out.println("Invoices not available for "+a.getCustomerId());
    }
}
      
```

Compared to raw HTTP APIs, you don't have to check the CAPS, the SDK handles it nicely for you using Optional APIs.


CORS Support
------------

BSS API implements fully CORS (Cross-Origin Resource Sharing), which means you can easily use those APIs within a simple static Web page. It may be useful for creating dashboard or business specific features.

Documentation
-------------

Documentation is always up to date since it is published by the API itself at [https://bssapi.fr1.cloudwatt.com/apidocs/public.api.notes.json](https://bssapi.fr1.cloudwatt.com/apidocs/public.api.notes.json) with a HTML version here: [http://dev.cloudwatt.com/fr/doc/api/api-ref-bss.html](http://dev.cloudwatt.com/fr/doc/api/api-ref-bss.html).

The documentation explains how to use the various API calls and the Capabilities on a given account required to perform a specific call if applicable.

SDK
---

A Java SDK is available for convenience with binaries and source code at [https://github.com/cloudwatt/bssapi-sdk](https://github.com/cloudwatt/bssapi-sdk]). It hides all Capabilities boilerplate to expose only the features you may access using the roles of Identity using Optional<API> objects. Thus, very easy to use. Several examples are provided on how extracting various data and performing some actions. Feel free to contribute or ask for new features.

Other APIs (keystone-admin-api v2)
----------------------------------

If you are already using Keystone admin API v2, BSS Api also provide a partial compatibility layer implementing the keystone-admin-API semantics. The BSS API provide more features, but if you already implemented Keystone-admin-api, it may be convenient to use. Feel free to contact us if you need a broader range of keystone-admin-api compatibility endpoints.

keystone-admin-api is available at this endpoint: [https://identity-admin.fr1.cloudwatt.com/v2.0/](https://identity-admin.fr1.cloudwatt.com/v2.0/).

Note that you need a scoped tenant. Internally, this endpoint uses BSS API, so all capabilities defining which features you may call are defined by the corresponding calls in BSS API.


Usage Limitations
-----------------

The number of calls you may perform per identity and IP is rate-limited. If you experience some issues, please try to limit the number of calls you perform. If you have specific needs, it is still possible to put a higher limit, contact Cloudwatt support for more information.
