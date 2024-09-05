# Java Proxy with HTTP Client
This is only a few solutions I've tried. This is not production grade. You can build all of these solutions using the base classes and options manually as well, but I opted for Autoconfigure for now.

## Solutions

### 1. Override all Azure clients with a proxy using Autoconfiguration
You can set a proxy on the http client for all Azure SDK clients by configuring the application.properties (or yaml) like below
```yaml
spring:
  cloud:
    azure:
      proxy:
        host: localhost
        port: 8888
        username: optional
        password: optional
        nonProxyHosts: localhost
        enabled: true
```
The reference for the proxy object is here: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/core/azure-core/src/main/java/com/azure/core/http/ProxyOptions.java

For reference, all configuration options are listed here: https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/configuration-properties-all

### 2. Override the [`AzureServiceBusJmsCredentialSupplier`](./src/main/java/com/example/servicebus/CustomAzureServiceBusJmsCredentialSupplier.java)
Add a bean like in ['CustomAzureServiceBusConfiguration'](./src/main/java/com/example/servicebus/CustomAzureServiceBusConfiguration.java) to override the default implementation.


### 2.a Using the above override, use a custom Credential Supplier with an HTTP Client configured with a proxy
The [`CustomTokenProvider`](./src/main/java/com/example/servicebus/CustomTokenProvider.java) overrides the Token Provider in the AuthenticationTemplate above


## Additional Notes for my reference:

https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/spring-cloud-azure-autoconfigure/src/main/java/com/azure/spring/cloud/autoconfigure/implementation/jms/properties/AzureServiceBusJmsProperties.java

https://github.com/Azure/azure-sdk-for-java/issues/40897 - [FEATURE REQ] Support auth by customer defined TokenCredencial for all scenarios | & https://github.com/Azure/azure-sdk-for-java/blob/f71fbefc3732c04667f6aca31bce64662670ef68/sdk/spring/spring-cloud-azure-integration-tests/src/test/java/com/azure/spring/cloud/integration/tests/servicebus/jms/ServiceBusJmsPasswordlessIT.java#L20C73-L20C129