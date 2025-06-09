package com.dulno.operator.log;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import com.dulno.core.log.Log;

@RequiredArgsConstructor(staticName = "create")
public final class OperatorLogInjectionModule extends AbstractModule {
  @Provides
  @Singleton
  Log provideOperatorLog() throws Exception {
    return Log.create("Operator");
  }
}
