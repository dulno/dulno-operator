package com.dulno.operator.distribution.server.node;

import lombok.RequiredArgsConstructor;
import com.dulno.core.event.EventHook;
import com.dulno.core.event.Hook;
import com.dulno.core.log.Log;
import com.dulno.operator.distribution.OperatorDistributionConfiguration;
import com.dulno.operator.distribution.client.OperatorClient;
import com.dulno.operator.distribution.client.OperatorClientRegistry;
import com.dulno.operator.distribution.event.node.NodeHandshakeRequestEvent;
import com.dulno.operator.distribution.packet.outgoing.node.PacketOutgoingHandshakeResponse;

import java.net.InetSocketAddress;

@RequiredArgsConstructor(staticName = "create")
public final class NodeHandshakeRequestHook implements Hook {
  private final OperatorDistributionConfiguration configuration;
  private final OperatorClientRegistry clientRegistry;
  private final Log log;

  @EventHook
  private void nodeHandshake(NodeHandshakeRequestEvent event) {
    if (!event.key().equals(configuration.distributionKey())) {
      event.channel().writeAndFlush(new PacketOutgoingHandshakeResponse(false));
      event.channel().close();
      return;
    }
    var channel = event.channel();
    var channelHost = ((InetSocketAddress) channel.remoteAddress())
      .getAddress().getHostAddress();
    var client = OperatorClient.of(channelHost, channel);
    clientRegistry.registerClient(client);
    client.authorize();
    event.channel().writeAndFlush(new PacketOutgoingHandshakeResponse(true));
    log.info("Node " + channelHost + " has connected");
  }
}