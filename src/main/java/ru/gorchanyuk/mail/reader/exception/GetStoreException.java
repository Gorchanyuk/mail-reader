package ru.gorchanyuk.mail.reader.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GetStoreException extends RuntimeException {

    public GetStoreException(String message) {
        super(message);
    }
}
