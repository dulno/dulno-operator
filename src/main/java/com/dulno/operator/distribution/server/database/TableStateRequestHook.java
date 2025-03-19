package com.dulno.operator.distribution.server.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.database.transformation.DatabaseTransformationState;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.core.log.Log;
import com.dulno.operator.distribution.client.OperatorClientRegistry;
import com.dulno.operator.distribution.event.database.TableStateRequestEvent;
import com.dulno.operator.distribution.packet.outgoing.database.PacketOutgoingTableStateRequest;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class TableStateRequestHook implements Hook {
  private final Log log;
  private final OperatorClientRegistry clientRegistry;
  private final TableStateChangeRepository changeRepository;

  @EventHook
  private void tableStateRequest(TableStateRequestEvent event) throws Exception {
    var change = changeRepository.registerStateChange(event.tableClass(),
      event.state(), event.client());
    change.thenAccept(value -> sendTableStateRequest(event.tableClass(),
      event.state()));
    log.info("A new transformation status was requested during the transformation of table " +
      event.tableClass() + ". This is now registered, forwarded and distributed.");
  }

  private void sendTableStateRequest(
    String tableClass, DatabaseTransformationState state
  ) {
    for (var client : clientRegistry.findAllClients()) {
      client.sendPacket(new PacketOutgoingTableStateRequest(tableClass, state));
    }
  }
}
