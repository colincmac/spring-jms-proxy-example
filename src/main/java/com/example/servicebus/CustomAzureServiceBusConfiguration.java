package com.example.servicebus;

import org.apache.qpid.jms.JmsConnectionExtensions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.azure.identity.implementation.IdentityClientOptions;
import com.azure.spring.cloud.autoconfigure.implementation.jms.AzureServiceBusJmsCredentialSupplier;
import com.azure.spring.cloud.autoconfigure.implementation.jms.properties.AzureServiceBusJmsProperties;
import com.azure.spring.cloud.autoconfigure.jms.ServiceBusJmsConnectionFactoryCustomizer;


@Configuration
public class CustomAzureServiceBusConfiguration {

    @Bean
    @Primary
    AzureServiceBusJmsCredentialSupplier customAzureServiceBusJmsCredentialSupplier(AzureServiceBusJmsProperties azureServiceBusJmsProperties) {
        return new CustomAzureServiceBusJmsCredentialSupplier(azureServiceBusJmsProperties.toPasswordlessProperties());
    }

    // @Bean
    // IdentityClientOptions setIdentityClientProxy(IdentityClientOptions identityClientOptions) {
    //     return new CustomAzureServiceBusJmsCredentialSupplier(azureServiceBusJmsProperties.toPasswordlessProperties());
    // }
    // @Bean
    // @Primary
    // ServiceBusJmsConnectionFactoryCustomizer jmsAADAuthenticationCustomizer(CustomAzureServiceBusJmsCredentialSupplier credentialSupplier) {
    //     return factory -> {
    //         factory.setExtension(JmsConnectionExtensions.USERNAME_OVERRIDE.toString(), (connection, uri) -> "$jwt");
    //         factory.setExtension(JmsConnectionExtensions.PASSWORD_OVERRIDE.toString(), (connection, uri) ->
    //                 credentialSupplier.get()
    //         );
    //     };
    // }
}