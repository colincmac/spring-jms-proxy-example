package com.example.servicebus;

import com.azure.identity.extensions.implementation.template.AzureAuthenticationTemplate;
import com.azure.identity.extensions.implementation.token.AccessTokenResolver;
import com.azure.identity.extensions.implementation.token.AccessTokenResolverOptions;
import com.azure.spring.cloud.autoconfigure.implementation.jms.AzureServiceBusJmsCredentialSupplier;
import java.util.Properties;

// Override the Credential Supplier here: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/spring-cloud-azure-autoconfigure/src/main/java/com/azure/spring/cloud/autoconfigure/implementation/jms/AzureServiceBusJmsCredentialSupplier.java
// this is used to change the authentication method to use the AzureAuthenticationTemplate and fetch a token based on the Default Azure Credential
// It's used here: https://github.com/Azure/azure-sdk-for-java/blob/f71fbefc3732c04667f6aca31bce64662670ef68/sdk/spring/spring-cloud-azure-autoconfigure/src/main/java/com/azure/spring/cloud/autoconfigure/implementation/jms/ServiceBusJmsPasswordlessConfiguration.java#L31
public class CustomAzureServiceBusJmsCredentialSupplier extends AzureServiceBusJmsCredentialSupplier {

    // The Authentication Template is defined here: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/identity/azure-identity-extensions/src/main/java/com/azure/identity/extensions/implementation/template/AzureAuthenticationTemplate.java
    // It's a wrapper class around TokenCredentialProvider & AccessTokenResolver
    // The properties for those 2 classes are shown here: https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/identity/azure-identity-extensions/src/main/java/com/azure/identity/extensions/implementation/enums/AuthProperty.java
    private final AzureAuthenticationTemplate azureAuthenticationTemplate;

    // Uncomment for Default constructor
    // public CustomAzureServiceBusJmsCredentialSupplier(Properties properties) {
    //   super(properties);
      
    //   azureAuthenticationTemplate = new AzureAuthenticationTemplate();
    //   azureAuthenticationTemplate.init(properties);
    // }

    public CustomAzureServiceBusJmsCredentialSupplier(Properties properties) {
      super(properties);
      var tokenCredentialProvider = new CustomTokenProvider();
      var accessTokenResolver = AccessTokenResolver.createDefault(new AccessTokenResolverOptions(properties));
      
      azureAuthenticationTemplate = new AzureAuthenticationTemplate(tokenCredentialProvider, accessTokenResolver);
      azureAuthenticationTemplate.init(properties);
    }

    // You can change this to whatever you'd llke to return the token
    @Override
    public String get() {
      return azureAuthenticationTemplate.getTokenAsPassword();
    }
}