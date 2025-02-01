package ru.nskopt.mappers;

public interface Mapper<T, M> {
  T map(M value);

  void update(T dest, M src);
}
