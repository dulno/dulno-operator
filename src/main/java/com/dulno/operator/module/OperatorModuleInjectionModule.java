package com.dulno.operator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import com.dulno.core.log.Log;

@RequiredArgsConstructor(staticName = "create")
public final class OperatorModuleInjectionModule extends AbstractModule {
  @Provides
  @Singleton
  OperatorModuleLoader provideModuleLoader(
    Log log, Injector injector
  ) {
    return OperatorModuleLoader.create(log, System.getProperty("user.dir") +
      "/modules/", injector);
  }
}
