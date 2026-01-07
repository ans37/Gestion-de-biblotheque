package dao;

import java.util.List;

public interface DAO<T> {
    T create(T entity);
    T findById(Long id);
    List<T> findAll();
    T update(T entity);
    boolean delete(Long id);
}