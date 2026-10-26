package com.swp391.g1.dao;
import java.util.List;

public interface IGenericDAO<T, K> {
    List<T> findAll();
    T findById(K id);
    K insert(T entity);
    boolean update(T entity);
    boolean delete(K id);
}
