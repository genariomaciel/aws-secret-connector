package com.leicam.secretconnector.config;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

public class SecretManagerClientConfig {
  
  public static SecretsManagerClient create() {

    return SecretsManagerClient.create();
  }
  public static SecretsManagerClient create(String region, String profileName) {

    return SecretsManagerClient.builder()
      .region(Region.of(region))
      .credentialsProvider(ProfileCredentialsProvider.create(profileName))
      .build();
  }

}
