package com.paperless.service;

import com.paperless.dao.UserDAO;
import com.paperless.exception.ValidationException;
import com.paperless.model.User;
import com.paperless.util.Validator;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Admin → User Management + Access Control + System Maintenance.
 * Demonstrates Collections & Generics, synchronized methods.
 */
public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public synchronized List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }

    /** Generic filter using Collections + Generics + Streams */
    public <T extends User> List<T> filterByRole(List<T> users, com.paperless.model.Role role) {
        return users.stream().filter(u -> u.getRole() == role).collect(Collectors.toList());
    }

    public Map<com.paperless.model.Role, Long> countByRole() throws SQLException {
        return userDAO.findAll().stream().collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
    }

    public Map<com.paperless.model.Role, List<User>> groupByRole() throws SQLException {
        return userDAO.findAll().stream().collect(Collectors.groupingBy(User::getRole));
    }

    public boolean deleteUser(int id) throws SQLException { return userDAO.delete(id); }

    public boolean updateUser(User u) throws SQLException, ValidationException {
        Validator.notBlank(u.getName(), "Name");
        Validator.email(u.getEmail());
        return userDAO.update(u);
    }

    public Optional<User> getById(int id) throws SQLException { return userDAO.findById(id); }

    /** Sorted view for Admin dashboard table */
    public List<User> getSortedByName() throws SQLException {
        List<User> list = userDAO.findAll();
        list.sort(Comparator.comparing(User::getName));
        return list;
    }
}
