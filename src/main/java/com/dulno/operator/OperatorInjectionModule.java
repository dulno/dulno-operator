package com.dulno.operator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.util.Config;
import lombok.RequiredArgsConstructor;
import com.dulno.operator.distribution.OperatorDistributionInjectionModule;
import com.dulno.operator.log.OperatorLogInjectionModule;
import com.dulno.operator.module.OperatorModuleInjectionModule;

@RequiredArgsConstructor(staticName = "create")
public final class OperatorInjectionModule extends AbstractModule {
  @Override
  protected void configure() {
    install(OperatorLogInjectionModule.create());
    install(OperatorModuleInjectionModule.create());
    install(OperatorDistributionInjectionModule.create());
  }

  @Provides
  @Singleton
  AppsV1Api provideKubernetesApi() throws Exception {
    var client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);
    return new AppsV1Api();
  }
}
