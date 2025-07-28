package com.rho.ims.api.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ResourceNotFoundException extends RuntimeException{
    private String field;
    private String value;
    private Map<String, String> duplicates;

    public ResourceNotFoundException(String message){
        super(message);

    }

    public ResourceNotFoundException(String field, String value){
        super(field + " not found");
        this.value = value;
        this.field = field;
        this.duplicates = Map.of(field, value);

    }

    public ResourceNotFoundException(Map<String, String> duplicates){
        super("Duplicate credential(s): " + String.join(", ", duplicates.keySet()) + " not found");
        this.duplicates = duplicates;
        this.value = null;
        this.field = null;
    }



}
