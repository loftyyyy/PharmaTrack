package com.rho.ims.api.exception;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
public class DuplicateCredentialException extends RuntimeException{
    private final String field;
    private final String value;

    public DuplicateCredentialException(String field, String value){
        super(field + " already exist");

        this.field = field;
        this.value = value;

    }







}
