package com.paperless.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface - demonstrates generics.
 */
public interface GenericDAO<T, K> {
    T save(T entity) throws SQLException;
    Optional<T> findById(K id) throws SQLException;
    List<T> findAll() throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean delete(K id) throws SQLException;
}
