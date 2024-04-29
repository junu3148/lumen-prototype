package com.lumenprototype.exception;

public class MetadataValidationException extends IllegalArgumentException {
    public MetadataValidationException(String message) {
        super(message);
    }
}