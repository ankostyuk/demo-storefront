package ru.nullpointer.storefront.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander Yastrebov
 */
public class Offer {

    private Integer id;
    //
    @NotNull(message = "{constraint.notnull}")
    private Integer categoryId;
    //
    private Integer companyId;
    //
    @NotNull
    @Size.List({
        @Size(min = 3, message = "{constraint.size.min}"),
        @Size(max = 60, message = "{constraint.size.max}")
    })
    private String name;
    //
    @Size(max = 240, message = "{constraint.size.max}")
    private String description;
    //
    @Size(max = 63, message = "{constraint.size.max}")
    private String brandName;
    //
    //
    @Size(max = 63, message = "{constraint.size.max}")
    private String modelName;
    //
    @Size(max = 2, message = "{constraint.size.max}")
    private String originCountry;
    //
    // Точность минимального и максимального значений равна (9,2)
    // т.е. 7 разрядов до запятой и 2 разряда после.
    //
    @NotNull(message = "{constraint.notnull}")
    @DecimalMin(value = "0.01", message = "{constraint.decimalmin}")
    @DecimalMax(value = "9999999", message = "{constraint.decimalmax}")
    private BigDecimal price;
    //
    @NotNull
    @Pattern(regexp = "RUB|USD|EUR", message = "{constraint.pattern}")
    private String currency;
    //
    // Точность минимального и максимального значений равна (9,4)
    // т.е. 5 разрядов до запятой и 4 разряда после.
    //
    @NotNull(message = "{constraint.notnull}")
    @DecimalMin(value = "0.0001", message = "{constraint.decimalmin}")
    @DecimalMax(value = "99999", message = "{constraint.decimalmax}")
    private BigDecimal ratio;
    //
    private BigDecimal unitPrice;
    //
    @NotNull(message = "{constraint.notnull}")
    private Date actualDate;
    //
    @NotNull
    private Boolean active;
    //
    @NotNull
    private Boolean available;
    //
    @NotNull
    private Boolean delivery;
    //
    private Date createDate;
    private Date editDate;
    private String image;
    //
    private Integer brandId;
    private Integer paramSetId;
    private Integer modelId;
    private String paramDescription;
    //
    private Status status;
    private Integer moderatorId;
    private Date moderationStartDate;
    private Date moderationEndDate;
    private Integer rejectionMask;

    public enum Status {

        APPROVED,
        PENDING,
        REJECTED
    };

    /**
     * Набор причин отклонения предложений.
     * При изменении - только добавлять.
     * Значения не менять - поползет база.
     */
    public enum Rejection {

        MISPRINT(1 << 0),
        CAPITAL(1 << 1),
        SPACING(1 << 2),
        CONTACT(1 << 3),
        LAWBREAK(1 << 4),
        CATEGORY(1 << 5),
        MARKUP(1 << 6),
        IMAGE(1 << 7),
        ENUM(1 << 8),
        OTHER(1 << 9);
        //
        private final int value;

        Rejection(int value) {
            this.value = value;
        }
    }

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

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * Возвращает ISO 3166 alpha-2 код страны производства
     * @return
     */
    public String getOriginCountry() {
        return originCountry;
    }

    /**
     * Устанавливает ISO 3166 alpha-2 код страны производства
     */
    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Date getActualDate() {
        return actualDate;
    }

    public void setActualDate(Date actualDate) {
        this.actualDate = actualDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getDelivery() {
        return delivery;
    }

    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getParamSetId() {
        return paramSetId;
    }

    public void setParamSetId(Integer paramSetId) {
        this.paramSetId = paramSetId;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(Integer moderatorId) {
        this.moderatorId = moderatorId;
    }

    public Date getModerationStartDate() {
        return moderationStartDate;
    }

    public void setModerationStartDate(Date moderationStartDate) {
        this.moderationStartDate = moderationStartDate;
    }

    public Date getModerationEndDate() {
        return moderationEndDate;
    }

    public void setModerationEndDate(Date moderationEndDate) {
        this.moderationEndDate = moderationEndDate;
    }

    public Integer getRejectionMask() {
        return rejectionMask;
    }

    public void setRejectionMask(Integer rejectionMask) {
        this.rejectionMask = rejectionMask;
    }

    public boolean isParametrized() {
        return (paramSetId != null);
    }

    public boolean isModelLinked() {
        return (modelId != null);
    }

    public List<Rejection> getRejectionList() {
        if (rejectionMask == null) {
            return null;
        } else {
            int mask = rejectionMask.intValue();
            List<Rejection> result = new ArrayList<Rejection>(Rejection.values().length);
            for (Rejection r : Rejection.values()) {
                if ((mask & r.value) != 0) {
                    result.add(r);
                }
            }
            return result;
        }
    }

    public void setRejectionList(List<Rejection> rejectionList) {
        if (rejectionList == null) {
            rejectionMask = null;
        } else {
            int mask = 0;
            for (Rejection r : rejectionList) {
                if (r != null) {
                    mask |= r.value;
                } else {
                    throw new IllegalArgumentException("rejection list contains null values");
                }
            }
            rejectionMask = Integer.valueOf(mask);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("status", status)//
                .append("categoryId", categoryId)//
                .append("companyId", companyId)//
                .append("name", name)//
                .append("price", price)//
                .append("currency", currency)//
                .append("ratio", ratio)//
                .append("unitPrice", unitPrice)//
                .append("actualDate", actualDate)//
                .append("active", active)//
                .append("createDate", createDate)//
                .append("editDate", editDate)//
                .append("image", image)//
                .append("brandId", brandId)//
                .append("paramSetId", paramSetId)//
                .append("modelId", modelId)//
                .toString();
    }
}
