package ru.nullpointer.storefront.dao;

/**
 * 
 * @author ankostyuk
 */
// TODO привести в порядок
public class CatalogItemException extends Exception {

    private static final long serialVersionUID = 1L;

    public CatalogItemException() {
    }

    public CatalogItemException(String message) {
        super(message);
    }

    public CatalogItemException(Throwable cause) {
        super(cause);
    }

    public CatalogItemException(String message, Throwable cause) {
        super(message, cause);
    }

}
