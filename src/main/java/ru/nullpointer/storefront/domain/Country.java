package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonMethod;

/**
 * @author ankostyuk
 */
@JsonAutoDetect(JsonMethod.NONE)
public class Country {

    @NotNull
    @Size.List({
        @Size(min = 2, message = "{constraint.size}"),
        @Size(max = 2, message = "{constraint.size}")
    })
    private String alpha2;

    @NotNull
    @Size.List({
        @Size(min = 1, message = "{constraint.size.min}"),
        @Size(max = 63, message = "{constraint.size.max}")
    })
    private String name;

    @JsonGetter
    public String getAlpha2() {
        return alpha2;
    }

    public void setAlpha2(String alpha2) {
        this.alpha2 = alpha2;
    }

    @JsonGetter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
