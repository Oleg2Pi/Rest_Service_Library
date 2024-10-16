package by.polikarpov.service;

import java.util.List;
import java.util.Optional;

/**
 * A generic service interface that defines common operations for managing entities.
 *
 * @param <T> the type of the entity's identifier
 * @param <E> the type of the entity (e.g., DTOs)
 */
public interface Service<T, E> {

    /**
     * Retrieves all entities.
     *
     * @return a list of all entities of type E
     */
    public List<E> getAll();

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity to retrieve
     * @return an Optional containing the entity if found, otherwise an empty Optional
     */
    public Optional<E> getById(T id);

    /**
     * Adds a new entity.
     *
     * @param entity the entity to add
     */
    public void add(E entity);

    /**
     * Updates an existing entity.
     *
     * @param entity the entity to update
     */
    public void update(E entity);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id the identifier of the entity to delete
     */
    public void delete(T id);
}
