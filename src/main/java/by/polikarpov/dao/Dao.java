package by.polikarpov.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, E> {
    boolean update(E entity);
    List<E> findAll();
    Optional<E> findById(T id);
    E save(E entity);
    boolean delete(T id);
    boolean delete(T readerId, T bookId);
}
