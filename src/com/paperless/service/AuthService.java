package com.paperless.service;

import com.paperless.dao.UserDAO;
import com.paperless.exception.AuthenticationException;
import com.paperless.exception.ValidationException;
import com.paperless.model.User;
import com.paperless.util.PasswordUtil;
import com.paperless.util.Validator;

import java.sql.SQLException;
import java.util.Optional;

/** Handles login / registration. */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) throws AuthenticationException, ValidationException, SQLException {
        Validator.email(email);
        Validator.notBlank(password, "Password");
        Optional<User> opt = userDAO.findByEmail(email);
        if (opt.isEmpty()) throw new AuthenticationException("User not found");
        User u = opt.get();
        if (!u.isActive()) throw new AuthenticationException("Account is deactivated");
        if (!PasswordUtil.verify(password, u.getPasswordHash()))
            throw new AuthenticationException("Invalid password");
        return u;
    }

    public User register(String name, String email, String password, com.paperless.model.Role role)
            throws ValidationException, SQLException {
        Validator.notBlank(name, "Name");
        Validator.email(email);
        Validator.notBlank(password, "Password");
        if (password.length() < 6) throw new ValidationException("Password must be at least 6 characters");
        if (userDAO.findByEmail(email).isPresent()) throw new ValidationException("Email already registered");
        User u = new User(name, email, PasswordUtil.hash(password), role);
        return userDAO.save(u);
    }
}
