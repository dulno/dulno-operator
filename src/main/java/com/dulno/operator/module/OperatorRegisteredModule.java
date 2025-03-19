package com.dulno.operator.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.File;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class OperatorRegisteredModule {
  private final OperatorModule module;
  private final String name;
  private final String version;
  private final OperatorModuleLoadPriority priority;
  private final File file;
}
