package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ankostyuk
 */
public class Term {

    private Integer id;

    @NotNull
    @Size.List({
        @Size(min = 1, message = "{constraint.size.min}"),
        @Size(max = 127, message = "{constraint.size.max}")
    })
    private String name;

    @NotNull
    @Size(min = 2, message = "{constraint.size.min}")
    private String description;

    @Size(max = 255, message = "{constraint.size.max}")
    private String source;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
