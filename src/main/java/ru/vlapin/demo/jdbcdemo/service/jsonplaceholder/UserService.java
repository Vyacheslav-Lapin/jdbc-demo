package ru.vlapin.demo.jdbcdemo.service.jsonplaceholder;

import java.util.List;

import ru.vlapin.demo.jdbcdemo.model.jsonplaceholder.User;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "UserJsonPlaceHolder",
    url = "https://jsonplaceholder.typicode.com",
    path = "users")
public interface UserService {

  @GetMapping
  List<User> all();

  @GetMapping("{id}")
  User findById(@PathVariable Long id);
}
