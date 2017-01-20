package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonMethod;
import ru.nullpointer.storefront.validation.Url;

/**
 *
 * @author Alexander Yastrebov
 */
@JsonAutoDetect(JsonMethod.NONE)
public class Model {

    private Integer id;
    private Integer categoryId;
    private Integer paramSetId;
    //
    @NotNull(message = "{constraint.notnull}")
    private Integer brandId;
    //
    @NotNull
    @Size.List({
        @Size(min = 3, message = "{constraint.size.min}"),
        @Size(max = 60, message = "{constraint.size.max}")
    })
    private String name;
    //
    @NotNull
    @Size.List({
        @Size(min = 1, message = "{constraint.size.min}"),
        @Size(max = 63, message = "{constraint.size.max}")
    })
    private String vendorCode;
    //
    @Size(max = 240, message = "{constraint.size.max}")
    private String description;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    private String keywords;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    @Url(message = "{constraint.url}")
    private String site;
    //
    private String image;
    //
    private String paramDescription;

    @JsonGetter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getParamSetId() {
        return paramSetId;
    }

    public void setParamSetId(Integer paramSetId) {
        this.paramSetId = paramSetId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @JsonGetter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("id", id)//
                .append("categoryId", categoryId)//
                .append("paramSetId", paramSetId)//
                .append("name", name)//
                .append("vendorCode", vendorCode)//
                .toString();
    }
}
