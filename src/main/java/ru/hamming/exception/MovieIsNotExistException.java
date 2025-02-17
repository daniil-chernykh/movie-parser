package ru.hamming.exception;

public class MovieIsNotExistException extends RuntimeException {
    public MovieIsNotExistException(String message) {
        super(message);
    }
}
