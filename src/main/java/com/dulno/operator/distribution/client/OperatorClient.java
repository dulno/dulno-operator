package com.dulno.operator.distribution.client;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import com.dulno.core.worker.packet.outgoing.PacketOutgoing;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class OperatorClient {
  public static OperatorClient of(String hostname, Channel channel) {
    return new OperatorClient(hostname, channel);
  }

  @Getter
  private final String hostname;
  @Getter
  private OperatorClientState state = OperatorClientState.UNAUTHORIZED;
  @Getter
  private Channel channel;

  private OperatorClient(String hostname, Channel channel) {
    this.hostname = hostname;
    this.channel = channel;
  }

  /**
   * Sends a packet to the server
   * @param packet The packet that is to be send
   * @param <T> The generic type of the packet
   */
  public <T extends PacketOutgoing> void sendPacket(T packet) {
    if (channel == null) {
      return;
    }
    channel.writeAndFlush(packet);
  }

  /**
   * Disconnects the client from the server
   */
  public void disconnect() {
    channel.close();
  }

  /**
   * Is used to change the state of the client to "authorized"
   */
  public void authorize() {
    state = OperatorClientState.AUTHORIZED;
  }
}

