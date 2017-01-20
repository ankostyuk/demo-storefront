package ru.nullpointer.storefront.web.exception;

/**
 *
 * @author Alexander Yastrebov
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }
}
