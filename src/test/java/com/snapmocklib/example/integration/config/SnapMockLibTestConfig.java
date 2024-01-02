package com.snapmocklib.example.integration.config;

import com.snapmocklib.services.aspect.StepOutputAspectService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnapMockLibTestConfig {

  @Bean
  public StepOutputAspectService stepOutputAspectService() {
    return new StepOutputAspectService();
  }
}
