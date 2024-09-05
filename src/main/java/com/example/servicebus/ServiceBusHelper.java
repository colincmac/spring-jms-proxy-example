package com.example.servicebus;

import java.time.Duration;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.azure.identity.extensions.implementation.credential.provider.TokenCredentialProvider;
import com.azure.identity.extensions.implementation.template.AzureAuthenticationTemplate;
import com.azure.identity.extensions.implementation.token.AccessTokenResolver;
import com.azure.spring.cloud.autoconfigure.implementation.jms.AzureServiceBusJmsCredentialSupplier;
import com.azure.spring.cloud.autoconfigure.implementation.jms.properties.AzureServiceBusJmsProperties;
import com.azure.spring.cloud.autoconfigure.implementation.properties.core.authentication.TokenCredentialConfigurationProperties;
import com.azure.spring.cloud.core.implementation.properties.AzurePasswordlessPropertiesMapping;
import com.azure.spring.jms.ServiceBusJmsConnectionFactory;
import org.apache.qpid.jms.JmsConnectionExtensions;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSProducer;
import jakarta.jms.Topic;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;

import org.apache.qpid.jms.JmsConnectionFactory;

// @Service
public class ServiceBusHelper {
  
  @Autowired
  AzureServiceBusJmsProperties azureServiceBusJmsProperties;

  @Autowired
  private JmsTemplate jmsTemplate;

  public JMSProducer producer;
  public JMSConsumer consumer;
  public JMSContext context;
  public Topic topic;
  private final String AAD_TOKEN_USERNAME = "$jwt";

  // example if you want to autoconfigure the properties
  public static final String PREFIX = "spring.jms.servicebus";

  private static final String AMQP_URI_FORMAT = "amqps://%s?amqp.idleTimeout=%d";
  // private final AzureAuthenticationTemplate azureAuthenticationTemplate;

  // https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/spring-cloud-azure-core/src/main/java/com/azure/spring/cloud/core/properties/profile/AzureEnvironmentProperties.java#L407
  private static final String SERVICE_BUS_DOMAIN_NAME = "servicebus.windows.net";
  
  // https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/spring/spring-cloud-azure-autoconfigure/src/main/java/com/azure/spring/cloud/autoconfigure/implementation/jms/properties/AzureServiceBusJmsProperties.java
  private Duration idleTimeout = Duration.ofMinutes(2);

  // https://github.com/Azure/azure-sdk-for-java/blob/c804b5c86a447cb530e13c16b1879cc9af752ea6/sdk/spring/spring-cloud-azure-autoconfigure/src/main/java/com/azure/spring/cloud/autoconfigure/implementation/jms/properties/AzureServiceBusJmsProperties.java#L28
  // private static final String SERVICE_BUS_SCOPE_AZURE = "https://servicebus.azure.net/.default";

  // private final AzureServiceBusJmsCredentialSupplier azureServiceBusJmsCredentialSupplier;
  // private final AzureServiceBusJmsProperties azureServiceBusJmsProperties;

  @Autowired
  private Environment env;

  // https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/identity/azure-identity-extensions/src/main/java/com/azure/identity/extensions/implementation/enums/AuthProperty.java
  public ServiceBusHelper() {
    // I'm grabbing these properties from the environment, but it's better if you can use the ConfigurationProperties annotation or Autowire the properties directly
    // var namespace = env.getProperty("spring.jms.servicebus.namespace");
    // var azureServiceBusJmsProperties = new AzureServiceBusJmsProperties();
    // azureServiceBusJmsProperties.setEnabled(true);
    // azureServiceBusJmsProperties.setNamespace(namespace);
    // azureServiceBusJmsProperties.setPricingTier(env.getProperty("spring.jms.servicebus.pricing-tier"));
    // // azureAuthenticationTemplate = new AzureAuthenticationTemplate();
    // // azureAuthenticationTemplate.init(properties);
    // azureServiceBusJmsCredentialSupplier = new AzureServiceBusJmsCredentialSupplier(azureServiceBusJmsProperties.toPasswordlessProperties());
    // // azureAuthenticationTemplate = new AzureAuthenticationTemplate();
    // // azureAuthenticationTemplate.init(properties);
    // var factory = getClientFactory();
    // context = factory.createContext();
    // producer = context.createProducer();  
  }

  public void setTopic(String topicName) {
    topic = context.createTopic(topicName);
    consumer = context.createDurableConsumer(topic, "demo-subscription");
  }

  public void send(String message) {
    var msg = context.createTextMessage(message);
    producer.send(topic, msg);
  }

  public void receive() {
    try {
      var msg = consumer.receive();
      System.out.println(msg.getBody(String.class));
    } catch (Exception e) {
      // TODO: handle exception
      System.out.println(e);
    }

  }

  public ServiceBusJmsConnectionFactory getClientFactory() {
    var namespace = env.getProperty("spring.jms.servicebus.namespace");

    // Seen here https://github.com/Azure/azure-sdk-for-java/blob/ffe3739c5c5a9b60d3b26a681dd19474971061ff/sdk/spring/spring-cloud-azure-autoconfigure/src/main/java/com/azure/spring/cloud/autoconfigure/implementation/jms/ServiceBusJmsConnectionFactoryFactory.java#L60
    // Similar to https://github.com/Azure/azure-servicebus-jms/blob/0c37ca3970ef384f678ba22a65dfcd51c65945dc/src/main/java/com/azure/servicebus/jms/ServiceBusJmsConnectionFactory.java#L378
    String remoteUrl = String.format(AMQP_URI_FORMAT, namespace + "." + SERVICE_BUS_DOMAIN_NAME, idleTimeout.toMillis());
    var factory = new ServiceBusJmsConnectionFactory(remoteUrl);


    // Seen here https://github.com/Azure/azure-sdk-for-java/blob/9e8d501b671a2ce7359da23c2506c1cc461ea6e6/sdk/spring/spring-cloud-azure-autoconfigure/src/main/java/com/azure/spring/cloud/autoconfigure/implementation/jms/ServiceBusJmsPasswordlessConfiguration.java#L31
    // Similar to https://github.com/Azure/azure-servicebus-jms/blob/0c37ca3970ef384f678ba22a65dfcd51c65945dc/src/main/java/com/azure/servicebus/jms/ServiceBusJmsConnectionFactory.java#L438
    factory.setExtension(JmsConnectionExtensions.USERNAME_OVERRIDE.toString(), (connection, uri) -> "$jwt");
    // factory.setExtension(JmsConnectionExtensions.PASSWORD_OVERRIDE.toString(), (connection, uri) ->
    //   azureServiceBusJmsCredentialSupplier.get()
    // );
    return factory;
  }

  public JMSProducer getProducer() {
    var factory = getClientFactory();
    var context = factory.createContext();
    return context.createProducer();
  }

}
