package ru.vlapin.demo.jdbcdemo.dao;

import java.sql.DriverManager;

import io.vavr.CheckedConsumer;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import static ru.vlapin.demo.jdbcdemo.dao.JdbcDemoWithSchema.*;

public class SavepointExample {

  @SneakyThrows
  public static void main(String... __) {
    @Cleanup val cn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

    @Cleanup val st = cn.createStatement();

    getFileAsString("schema.sql")
        .ifPresent(CheckedConsumer.<String>of(st::executeUpdate).unchecked());

    cn.setAutoCommit(false);

    st.executeUpdate("""
        insert into employees(first_name, last_name)
        values ('Игорь', 'Цветков')""");

// ...

    // Устанавливаем именнованную точку сохранения.
    val svpt = cn.setSavepoint("NewEmp");

    st.executeUpdate("""
        update employees
        set address = 'ул. Седых, 19-34'
        where last_name = 'Цветков'""");

    // ...

    cn.rollback(svpt);
    // ...

    // Запись о работнике вставлена, но адрес не обновлен.
    cn.commit();

  }
}
