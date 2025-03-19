package com.dulno.operator.distribution.server.node;

import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.core.log.Log;
import com.dulno.operator.distribution.client.OperatorClientRegistry;
import com.dulno.operator.distribution.event.node.NodeDisconnectEvent;

@RequiredArgsConstructor(staticName = "create")
public final class NodeDisconnectHook implements Hook {
  private final OperatorClientRegistry clientRegistry;
  private final Log log;

  @EventHook
  private void nodeDisconnect(NodeDisconnectEvent event) {
    var client = event.client();
    clientRegistry.unregisterClient(client);
    var reason = event.reason();
    if (reason.isConnectionFailed()) {
      return;
    }
    if (reason.isShutdown()) {
      log.info("The node " + event.client().hostname() + " has disconnected");
    } else {
      log.warning("The node " + event.client().hostname() + " has timed out");
    }
  }
}
