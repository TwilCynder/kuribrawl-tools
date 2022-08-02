package gamedata.exceptions;

/**
 * When things just don't make sense
 */
public class WhatTheHellException extends RuntimeException {

    public WhatTheHellException() {
        super();
    }

    public WhatTheHellException(String message) {
        super(message);
    }

    public WhatTheHellException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhatTheHellException(Throwable cause) {
        super(cause);
    }
}
