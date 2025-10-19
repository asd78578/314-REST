package ru.demo.exception;

public class UserUsernameExistException extends RuntimeException {
    public UserUsernameExistException(String msg) {
        super(msg);
    }
}
