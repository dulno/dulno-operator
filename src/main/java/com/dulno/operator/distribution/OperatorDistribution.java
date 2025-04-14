package com.dulno.operator.distribution;

import com.dulno.operator.distribution.packet.incoming.node.PacketIncomingDisconnect;
import com.dulno.operator.distribution.packet.incoming.node.PacketIncomingHandshakeRequest;
import com.dulno.operator.distribution.packet.incoming.node.PacketIncomingPong;
import com.dulno.operator.distribution.packet.outgoing.node.PacketOutgoingEnvironment;
import com.dulno.operator.distribution.server.node.NodeDisconnectHook;
import com.dulno.operator.distribution.server.node.NodeHandshakeRequestHook;
import com.dulno.operator.distribution.server.node.NodePongHook;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.dulno.operator.distribution.packet.incoming.node.*;
import com.dulno.operator.distribution.server.node.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventExecutor;
import com.dulno.core.event.HookRegistry;
import com.dulno.core.log.Log;
import com.dulno.core.packet.PacketEventRepository;
import com.dulno.core.packet.PacketRegistry;
import com.dulno.core.worker.packet.outgoing.PacketOutgoing;
import com.dulno.operator.distribution.client.OperatorClient;
import com.dulno.operator.distribution.client.OperatorClientRegistry;
import com.dulno.operator.distribution.event.database.TableDiscrepancyEvent;
import com.dulno.operator.distribution.event.database.TableStateRequestEvent;
import com.dulno.operator.distribution.event.database.TableStateResponseEvent;
import com.dulno.operator.distribution.event.node.NodeDisconnectEvent;
import com.dulno.operator.distribution.event.node.NodePongEvent;
import com.dulno.operator.distribution.packet.incoming.database.PacketIncomingTableDiscrepancy;
import com.dulno.operator.distribution.packet.incoming.database.PacketIncomingTableStateRequest;
import com.dulno.operator.distribution.packet.incoming.database.PacketIncomingTableStateResponse;
import com.dulno.operator.distribution.server.OperatorServer;
import com.dulno.operator.distribution.server.database.TableDiscrepancyHook;
import com.dulno.operator.distribution.server.database.TableStateRequestHook;
import com.dulno.operator.distribution.server.database.TableStateResponseHook;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class OperatorDistribution {
  private final Injector injector;
  private final OperatorDistributionConfiguration configuration;
  private final PacketRegistry packetRegistry;
  private final EventExecutor eventExecutor;
  private final HookRegistry hookRegistry;
  private final OperatorClientRegistry clientRegistry;
  private final PacketEventRepository packetEventRepository;
  private final Log log;
  private OperatorServer operatorServer;

  /**
   * Initializes node distribution (registers packets, hooks, events and
   * opens server)
   * @throws Exception
   */
  public void initialize() throws Exception {
    operatorServer = OperatorServer.create(packetRegistry, eventExecutor,
      clientRegistry, packetEventRepository,
      configuration.distributionPort());
    registerPackets();
    registerHooks();
    registerEvents();
    operatorServer.openAsync(() -> {});
  }

  private void registerPackets() throws Exception {
    packetRegistry.registerPacket(PacketIncomingHandshakeRequest.class);
    packetRegistry.registerPacket(PacketIncomingPong.class);
    packetRegistry.registerPacket(PacketIncomingDisconnect.class);
    packetRegistry.registerPacket(PacketIncomingTableDiscrepancy.class);
    packetRegistry.registerPacket(PacketIncomingTableStateRequest.class);
    packetRegistry.registerPacket(PacketIncomingTableStateResponse.class);
  }

  private void registerHooks() {
    hookRegistry.register(NodeHandshakeRequestHook.create(configuration,
      clientRegistry, this, log));
    hookRegistry.register(injector.getInstance(NodePongHook.class));
    hookRegistry.register(NodeDisconnectHook.create(clientRegistry, this, log));
    hookRegistry.register(injector.getInstance(TableDiscrepancyHook.class));
    hookRegistry.register(injector.getInstance(TableStateRequestHook.class));
    hookRegistry.register(injector.getInstance(TableStateResponseHook.class));
  }

  private void registerEvents() {
    packetEventRepository.<OperatorClient, PacketIncomingPong>registerEvent(
      PacketIncomingPong.class, (client, packet) ->
        NodePongEvent.create(client, packet.value()));
    packetEventRepository.<OperatorClient, PacketIncomingDisconnect>registerEvent(
      PacketIncomingDisconnect.class, (client, packet) -> NodeDisconnectEvent.create(
        client, NodeDisconnectEvent.DisconnectReason.SHUTDOWN));
    packetEventRepository.registerEvent(PacketIncomingTableDiscrepancy.class,
      (client, packet) -> TableDiscrepancyEvent.create(packet.tableClass()));
    packetEventRepository.<OperatorClient, PacketIncomingTableStateRequest>registerEvent(
      PacketIncomingTableStateRequest.class, (client, packet) ->
        TableStateRequestEvent.create(client, packet.tableClass(), packet.state()));
    packetEventRepository.registerEvent(PacketIncomingTableStateResponse.class,
      (client, packet) -> TableStateResponseEvent.create(packet.tableClass(),
        packet.state()));
  }

  /**
   * Sends PacketOutgoingEnvironment to all registered clients
   */
  public void notifyEnvironment() {
    for (var client : clientRegistry.findAllClients()) {
      var neighbors = clientRegistry.findAllClients().stream()
        .filter(neighbor -> neighbor != client)
        .filter(neighbor -> neighbor.state().isAuthorized())
        .map(OperatorClient::hostname)
        .toList();
      client.sendPacket(new PacketOutgoingEnvironment(client.hostname(), neighbors));
    }
  }

  /**
   * Sends an outgoing packet to all registered clients
   * @param packet The packet that will be sent
   */
  public void broadcastPacket(PacketOutgoing packet) {
    operatorServer.broadcastPacket(packet);
  }

  /**
   * Closes server
   */
  public void disconnect() {
    operatorServer.close();
  }
}
