package gamedata.exceptions;

public class WhatTheHellException extends Exception {

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
