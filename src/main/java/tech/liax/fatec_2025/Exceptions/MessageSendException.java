package tech.liax.fatec_2025.Exceptions;

public class MessageSendException extends RuntimeException {

    public MessageSendException(String message, Throwable cause) {
        super(message, cause);
    }

}
