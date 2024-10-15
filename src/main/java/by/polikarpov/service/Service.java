package by.polikarpov.service;

import java.util.List;
import java.util.Optional;

public interface Service<T, E> {
    public List<E> getAll();
    public Optional<E> getById(T id);
    public void add(E entity);
    public void update(E entity);
    public void delete(T id);
}
