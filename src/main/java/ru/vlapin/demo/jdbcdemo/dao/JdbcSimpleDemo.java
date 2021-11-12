package ru.vlapin.demo.jdbcdemo.dao;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static java.util.UUID.randomUUID;
import static ru.vlapin.demo.jdbcdemo.dao.Student.Fields.fio;
import static ru.vlapin.demo.jdbcdemo.dao.Student.Fields.groupId;
import static ru.vlapin.demo.jdbcdemo.dao.Student.Fields.id;

@Slf4j
public class JdbcSimpleDemo {

  @SneakyThrows
  public static void main(String... __) {

//    val load= ServiceLoader.load(Driver.class);

    @Cleanup val connection = DriverManager.getConnection(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    @Cleanup val statement = connection.createStatement();
    statement.executeUpdate("create table if not exists student (id uuid primary key, fio varchar not null, groupId integer)");

    val vasyaPupkinId = randomUUID();
    val fedorProkopovId = randomUUID();

    statement.executeUpdate("""
            insert into student
            values ('%s', 'Василий Петрович Пупкин', 1),
                   ('%s', 'Фёдор Викторович Прокопов', 2)""".formatted(vasyaPupkinId, fedorProkopovId));

//    @Cleanup val resultSet = statement.executeQuery("select id, fio, groupId from student where id = '%s'".formatted(vasyaPupkinId));

    @Cleanup val preparedStatement = connection.prepareStatement("select id, fio, groupId from student where id = ?");

    logStudentById(fedorProkopovId, preparedStatement);
    logStudentById(vasyaPupkinId, preparedStatement);
  }

  private static void logStudentById(UUID uuid, PreparedStatement preparedStatement) throws SQLException {
    preparedStatement.setObject(1, uuid);

    @Cleanup val resultSet = preparedStatement.executeQuery();
    while (resultSet.next())
      log.info("Student: {}", Student.builder()
          .id(resultSet.getObject(id, UUID.class))
          .fio(resultSet.getString(fio))
          .groupId(resultSet.getInt(groupId))
          .build().toString());
  }
}
