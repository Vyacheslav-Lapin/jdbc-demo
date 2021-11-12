package ru.vlapin.demo.jdbcdemo.cp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vavr.CheckedFunction0;
import io.vavr.Function2;
import lombok.SneakyThrows;

public class ConnectionPool {

  ArrayBlockingQueue<PooledConnection> connections;

  Function<Connection, PooledConnection> pooledConnectionMapper =
      Function2.of(PooledConnection::new)
          .apply(this::returnPooledConnectionToPool);

  public ConnectionPool(int size, String jdbcUrl, String login, String password) {
    connections = Stream.generate(
            CheckedFunction0.of(() -> DriverManager.getConnection(jdbcUrl, login, password)).unchecked())
        .limit(size)
        .map(pooledConnectionMapper)
        .collect(Collectors.toCollection(() -> new ArrayBlockingQueue<>(size)));
  }

  private void returnPooledConnectionToPool(PooledConnection pooledConnection) {
    connections.add(pooledConnection);
  }

  @SneakyThrows
  public Connection getConnection() {
    return connections.take();
  }
}
