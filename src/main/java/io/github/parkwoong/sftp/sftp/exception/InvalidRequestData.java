package io.github.parkwoong.sftp.sftp.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(BAD_REQUEST)
public class InvalidRequestData extends RuntimeException {

    public InvalidRequestData(Class<?> clazz) {

        super(clazz.getSimpleName() + " is invalid");
    }
}
