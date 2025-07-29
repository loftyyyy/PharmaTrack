package com.rho.ims.api.exception;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
public class DuplicateCredentialException extends RuntimeException{
    private String field;
    private String value;
    private Map<String, String> duplicates;

    public DuplicateCredentialException(String message){
        super(message);
    }


    public DuplicateCredentialException(String field, String value){
        super(field + " already exists");
        this.field = field;
        this.value = value;
        this.duplicates = Map.of(field, value);

    }

    public DuplicateCredentialException(Map<String, String> duplicates){
        super("Duplicate credential(s): " + String.join(", ", duplicates.keySet()) + " already exist");
        this.duplicates = duplicates;

        this.field = null;
        this.value = null;

    }







}
