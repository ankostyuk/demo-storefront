package ru.nullpointer.storefront.service.search;

/**
 * @author ankostyuk
 */
public class IndexException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    public IndexException() {
    }

    public IndexException(String message) {
        super(message);
    }

    public IndexException(Throwable cause) {
        super(cause);
    }

    public IndexException(String message, Throwable cause) {
        super(message, cause);
    }

}
