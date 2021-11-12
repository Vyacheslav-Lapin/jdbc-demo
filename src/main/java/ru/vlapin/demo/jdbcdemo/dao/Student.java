package ru.vlapin.demo.jdbcdemo.dao;

import java.util.UUID;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

import static java.util.UUID.randomUUID;

@Value
@ToString
@FieldNameConstants
@Builder(toBuilder = true)
public class Student {

  @Builder.Default
  UUID id = randomUUID();

  String fio;

  Integer groupId;
}
