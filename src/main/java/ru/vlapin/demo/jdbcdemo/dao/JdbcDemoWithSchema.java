package ru.vlapin.demo.jdbcdemo.dao;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedFunction1;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static java.util.UUID.randomUUID;
import static ru.vlapin.demo.jdbcdemo.dao.Student.Fields.fio;
import static ru.vlapin.demo.jdbcdemo.dao.Student.Fields.groupId;

@Slf4j
@UtilityClass
public class JdbcDemoWithSchema {

  @SneakyThrows
  public void main(String... __) {

    @Cleanup val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
//    @Cleanup val connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    @Cleanup val statement = connection.createStatement();

    getFileAsString("schema.sql")
        .ifPresent(CheckedConsumer.<String>of(statement::executeUpdate).unchecked());

    val vasyaPupkinId = randomUUID();
    val fedorProkopovId = randomUUID();

    statement.executeUpdate("""
                                insert into student
                                values ('%s', 'Василий Павлович Пупкин', 1),
                                       ('%s', 'Фёдор Викторович Прокопов', 2)""".formatted(vasyaPupkinId, fedorProkopovId));

//    @Cleanup val resultSet = statement.executeQuery("select id, fio, groupId from student where id = '%s'".formatted(vasyaPupkinId));

    @Cleanup val preparedStatement = connection
        .prepareStatement("select fio, groupId from student where id = ?");

    logStudentById(fedorProkopovId, preparedStatement);
    logStudentById(vasyaPupkinId, preparedStatement);
  }

  private void logStudentById(UUID uuid, PreparedStatement preparedStatement) throws SQLException {
    preparedStatement.setObject(1, uuid);

    @Cleanup val resultSet = preparedStatement.executeQuery();
    while (resultSet.next())
      log.info("Student: {}", Student.builder()
          .id(uuid)
          .fio(resultSet.getString(fio))
          .groupId(resultSet.getInt(groupId))
          .build().toString());
  }

  @SneakyThrows
  public Optional<String> getFileAsString(String fileName) {
    val path = String.format("/%s", fileName);
    return Optional.ofNullable(JdbcDemoWithSchema.class.getResource(path))
        .map(URL::getFile)
        .map(Paths::get)
        .map(CheckedFunction1.<Path, String>narrow(Files::readString).unchecked());
  }
}
