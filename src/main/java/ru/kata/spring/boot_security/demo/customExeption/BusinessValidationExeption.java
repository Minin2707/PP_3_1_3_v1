package ru.kata.spring.boot_security.demo.customExeption;

public class BusinessValidationExeption extends RuntimeException {
    private final String field;

    public BusinessValidationExeption(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }


}
