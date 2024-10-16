package by.polikarpov.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object (DAO) interface that defines basic CRUD operations
 * (Create, Read, Update, Delete) that can be performed on a data entity.
 *
 * @param <T> the type of the identifier for the entity
 * @param <E> the type of the entity
 */
public interface Dao<T, E> {

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to be updated
     * @return true if the update was successful; false otherwise
     */
    boolean update(E entity);

    /**
     * Retrieves all entities from the database.
     *
     * @return a list of entities
     */
    List<E> findAll();

    /**
     * Finds an entity by its identifier.
     *
     * @param id the identifier of the entity
     * @return an Optional containing the found entity, or empty if not found
     */
    Optional<E> findById(T id);

    /**
     * Saves a new entity in the database.
     *
     * @param entity the entity to be saved
     * @return the saved entity with any generated values (e.g., ID)
     */
    E save(E entity);

    /**
     * Deletes an entity from the database by its identifier.
     *
     * @param id the identifier of the entity to be deleted
     * @return true if the deletion was successful; false otherwise
     */
    boolean delete(T id);

    /**
     * Deletes an entity associated with a specific reader and book for book lending.
     *
     * @param readerId the identifier of the reader
     * @param bookId the identifier of the book
     * @return true if the deletion was successful; false otherwise
     */
    boolean delete(T readerId, T bookId);
}
