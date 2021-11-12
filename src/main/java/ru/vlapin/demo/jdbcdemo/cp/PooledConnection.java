package ru.vlapin.demo.jdbcdemo.cp;

import java.sql.Connection;
import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class PooledConnection implements Connection {

  Consumer<PooledConnection> closer;

  @Delegate(excludes = AutoCloseable.class)
  Connection connection;

  @Override
  public void close() {
    closer.accept(this);
  }

  @SneakyThrows
  public void reallyClose() {
    connection.close();
  }
}
