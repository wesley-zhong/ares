package com.ares.core.excetion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogicException extends RuntimeException {
    private int errCode;
    private String message;

    public LogicException(int errCode, String message) {
        super(message);
        this.errCode = errCode;
        this.message = message;
    }

    public LogicException(int errCode, String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return "LogicException:[code]=" + errCode + "[msg]=" + message;
    }
}
