package com.websitefunkymonky1.pi;

public class StorageException extends  RuntimeException{
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
