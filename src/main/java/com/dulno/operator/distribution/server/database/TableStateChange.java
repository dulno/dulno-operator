package com.dulno.operator.distribution.server.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import com.dulno.core.database.transformation.DatabaseTransformationState;
import com.dulno.operator.distribution.client.OperatorClient;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(staticName = "create")
public final class TableStateChange {
  private final String tableClass;
  private final DatabaseTransformationState state;
  private final int replicas;
  private int responses;
  private final OperatorClient requester;

  public void addResponse() {
    responses++;
  }
}
