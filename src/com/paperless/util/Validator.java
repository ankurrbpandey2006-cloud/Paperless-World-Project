package com.paperless.util;

import com.paperless.exception.ValidationException;

public final class Validator {
    private Validator() {}

    public static void notBlank(String value, String field) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " must not be blank");
        }
    }

    public static void email(String email) throws ValidationException {
        notBlank(email, "Email");
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Invalid email format");
        }
    }

    public static void notNull(Object obj, String field) throws ValidationException {
        if (obj == null) throw new ValidationException(field + " must not be null");
    }
}
