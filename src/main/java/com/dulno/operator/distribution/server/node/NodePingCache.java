package com.dulno.operator.distribution.server.node;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.dulno.operator.distribution.client.OperatorClient;

import java.util.List;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class NodePingCache {
  private final Map<OperatorClient, Integer> pings = Maps.newHashMap();

  public void insertNodePing(OperatorClient client, int value) {
    pings.put(client, value);
  }

  public void removeNodePing(OperatorClient client) {
    pings.remove(client);
  }

  public void clear() {
    pings.clear();
  }

  public boolean pingExists(OperatorClient client) {
    return pings.containsKey(client);
  }

  public int findPingValue(OperatorClient client) {
    return pings.get(client);
  }

  public List<OperatorClient> findPendingPingClients() {
    return Lists.newArrayList(pings.keySet());
  }
}
