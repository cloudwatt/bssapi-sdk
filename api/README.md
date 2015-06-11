Cloudwatt Public API
====================

Overview
--------

This API is intended for users that would like to use advanced Cloudwatt features including :

- Automatic Provisioning of accounts and tenants
- Get Invoice informations
- Get consumption in real time...

Build
-----

Run maven 3+ with following command to build api package and Javadoc Jar:

mvn package javadoc:jar

Using it
--------

The directory [src/test/java/com/cloudwatt/apis/bss/](./src/test/java/com/cloudwatt/apis/bss/TestAPI.java) contains all in one file examples about how to use the SDK:

- Simple text client: [TestApi.java](./src/test/java/com/cloudwatt/apis/bss/TestAPI.java) to show you to use easily the API.
)
- Basic Swing GUI Client: [TestApiGUI.java](./src/test/java/com/cloudwatt/apis/bss/TestApiGUI.java) - a very simple GUI to display your BSS Cloudwatt informations

Launch those tests with your Cloudwatt credentials.
