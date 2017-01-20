package ru.nullpointer.storefront.service.search;

/**
 * @author ankostyuk
 */
public class SearchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SearchException() {
    }

    public SearchException(String message) {
        super(message);
    }

    public SearchException(Throwable cause) {
        super(cause);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

}
