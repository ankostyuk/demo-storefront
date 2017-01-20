package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Описывает раздел или категорию каталога
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class CatalogItem {

    private Integer id;
    //
    @NotNull
    @Size.List({
        @Size(min = 1, message = "{constraint.size.min}"),
        @Size(max = 63, message = "{constraint.size.max}")
    })
    private String name;
    //
    @Size(max = 15, message = "{constraint.size.max}")
    private String theme;
    //
    private String path;
    private Type type;
    //
    @NotNull
    private Boolean active;

    public enum Type {

        SECTION, CATEGORY
    }

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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("name", name)//
                .append("theme", theme)//
                .append("path", path)//
                .append("type", type)//
                .append("active", active)//
                .toString();
    }
}
