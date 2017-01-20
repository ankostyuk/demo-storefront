package ru.nullpointer.storefront.web;

/**
 * @author ankostyuk
 */
public class SearchSettings {

    private String text;
    private Integer catalogItemId;
    private String example;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCatalogItemId() {
        return catalogItemId;
    }

    public void setCatalogItemId(Integer catalogItemId) {
        this.catalogItemId = catalogItemId;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
