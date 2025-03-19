package com.dulno.operator.distribution.event.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import com.dulno.core.event.Event;
import com.dulno.operator.distribution.client.OperatorClient;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class NodePongEvent extends Event {
  private final OperatorClient client;
  private final int value;
}
