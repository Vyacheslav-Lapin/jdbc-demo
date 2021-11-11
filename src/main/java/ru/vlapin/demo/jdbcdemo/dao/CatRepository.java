package ru.vlapin.demo.jdbcdemo.dao;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlapin.demo.jdbcdemo.model.Cat;

public interface CatRepository extends JpaRepository<Cat, UUID> {
}
