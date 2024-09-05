package com.example.servicebus;

import java.net.InetSocketAddress;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.util.HttpClientOptions;

import com.azure.identity.DefaultAzureCredentialBuilder;

import com.azure.identity.extensions.implementation.credential.TokenCredentialProviderOptions;
import com.azure.identity.extensions.implementation.credential.provider.TokenCredentialProvider;

import jakarta.annotation.Nullable;

public class CustomTokenProvider implements TokenCredentialProvider {

  private final TokenCredentialProviderOptions options;

  private final TokenCredential tokenCredential;

  private final HttpClient httpClient;

  CustomTokenProvider() {
      this.options = new TokenCredentialProviderOptions();
      var httpClientOptions = new HttpClientOptions();
      httpClientOptions.setProxyOptions(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("localhost", 8888)));
      this.httpClient = HttpClient.createDefault(httpClientOptions);
      this.tokenCredential = get(this.options);

  }

  CustomTokenProvider(TokenCredentialProviderOptions options) {
      this.options = options;
      var httpClientOptions = new HttpClientOptions();
      httpClientOptions.setProxyOptions(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("localhost", 8888)));
      this.httpClient = HttpClient.createDefault(httpClientOptions);
      this.tokenCredential = get(this.options);
      // this.httpClient = NettyAsyncHttpClientBuilder.create().proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("localhost", 8888))).build();
  }

  @Override
  public TokenCredential get() {
      return tokenCredential;
  }

  @Override
  public TokenCredential get(TokenCredentialProviderOptions options) {
      if (options == null) {
          return new DefaultAzureCredentialBuilder().build();
      }
      return resolveTokenCredential(options);
  }

  private TokenCredential resolveTokenCredential(TokenCredentialProviderOptions options) {
      final String tenantId = options.getTenantId();
      final String clientId = options.getClientId();
      final String authorityHost = options.getAuthorityHost();

      var credential = new DefaultAzureCredentialBuilder()
              .authorityHost(authorityHost)
              .tenantId(tenantId)
              .managedIdentityClientId(clientId)
              // .httpClient(httpClient) // Adding a custom HTTP Client
              .build();
      return credential;
  }

  private boolean hasText(@Nullable String str) {
      return (str != null && !str.isEmpty() && containsText(str));
  }

  private boolean containsText(CharSequence str) {
      int strLen = str.length();
      for (int i = 0; i < strLen; i++) {
          if (!Character.isWhitespace(str.charAt(i))) {
              return true;
          }
      }
      return false;
  }
}