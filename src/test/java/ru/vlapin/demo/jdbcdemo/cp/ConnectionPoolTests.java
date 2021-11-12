package ru.vlapin.demo.jdbcdemo.cp;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.vlapin.demo.jdbcdemo.dao.Student;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.vlapin.demo.jdbcdemo.dao.Student.Fields.fio;
import static ru.vlapin.demo.jdbcdemo.dao.Student.Fields.groupId;

@Slf4j
class ConnectionPoolTests {

  ConnectionPool connectionPool = new ConnectionPool(5,
                                                     "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                                                     "", "");

  @Test
  @SneakyThrows
  @DisplayName("ConnectionPool works correctly")
  void connectionPoolWorksCorrectlyTest() {
    @Cleanup val connection = connectionPool.getConnection();
    @Cleanup val statement = connection.createStatement();

    statement.executeUpdate("create table if not exists student (id uuid primary key, fio varchar not null, groupId integer)");

    val vasyaPupkinId = randomUUID();
    val fedorProkopovId = randomUUID();

    statement.executeUpdate("""
                                insert into student
                                values ('%s', 'Василий Петрович Пупкин', 1),
                                       ('%s', 'Фёдор Викторович Прокопов', 2)""".formatted(vasyaPupkinId, fedorProkopovId));

    @Cleanup val preparedStatement = connection.prepareStatement("select fio, groupId from student where id = ?");

    assertThat(logStudentById(vasyaPupkinId, preparedStatement)).isNotNull()
        .extracting(Student::getFio, Student::getGroupId)
        .contains("Василий Петрович Пупкин", 1);

    assertThat(logStudentById(fedorProkopovId, preparedStatement)).isNotNull()
        .extracting(Student::getFio, Student::getGroupId)
        .contains("Фёдор Викторович Прокопов", 2);
  }

  private static Student logStudentById(UUID uuid,
                                        @NotNull PreparedStatement preparedStatement) throws SQLException {
    preparedStatement.setObject(1, uuid);
    @Cleanup val resultSet = preparedStatement.executeQuery();
    if (resultSet.next())
      return Student.builder()
          .id(uuid)
          .fio(resultSet.getString(fio))
          .groupId(resultSet.getInt(groupId))
          .build();
    else
      throw new RuntimeException("Нет студента c таким id: %s".formatted(uuid));
  }
}
