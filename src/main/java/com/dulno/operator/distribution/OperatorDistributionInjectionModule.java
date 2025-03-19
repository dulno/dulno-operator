package com.dulno.operator.distribution;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class OperatorDistributionInjectionModule extends AbstractModule {
  @Provides
  @Singleton
  OperatorDistributionConfiguration provideOperatorDistributionConfiguration() throws Exception {
    return OperatorDistributionConfiguration.createAndLoad();
  }
}
