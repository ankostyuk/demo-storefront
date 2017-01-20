package ru.nullpointer.storefront.domain;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"left", "right"})
@JsonWriteNullProperties(value = false)
public class Region {

    private Integer id;
    private String name;
    private Integer left;
    private Integer right;
    private List<Region> path;

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

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getRight() {
        return right;
    }

    public void setRight(Integer right) {
        this.right = right;
    }

    public List<Region> getPath() {
        return path;
    }

    public void setPath(List<Region> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("name", name)//
                .append("left", left)//
                .append("right", right)//
                .append("path", path)//
                .toString();
    }
}
