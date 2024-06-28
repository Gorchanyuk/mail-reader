package ru.gorchanyuk.mail.reader.util;

public enum ContentType {
    PLAIN("text/plain"),
    HTML("text/html"),
    MIXED("multipart/mixed"),
    ALTERNATIVE("multipart/alternative"),
    MULTIPART("multipart/*");

    private final String value;

    ContentType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
