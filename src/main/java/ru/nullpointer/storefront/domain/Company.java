package ru.nullpointer.storefront.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import ru.nullpointer.storefront.validation.Url;

/**
 *
 * @author Alexander Yastrebov
 */
public class Company {

    private Integer id;
    //
    @NotNull
    @Size(min = 3, max = 63, message = "{constraint.size}")
    private String name;
    //
    @NotNull(message = "{constraint.notnull}")
    private Integer regionId;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    private String address;
    //
    @Size(max = 63, message = "{constraint.size.max}")
    private String contactPhone;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    private String contactPerson;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    @Url(message = "{constraint.url}")
    private String site;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    private String schedule;
    //
    @Size(max = 1000, message = "{constraint.size.max}")
    private String scope;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    private String deliveryConditions;
    //
    @Valid
    private PaymentConditions paymentConditions = new PaymentConditions();
    //
    private String logo;
    //
    @Valid
    private Account account;

    public static final class PaymentConditions {

        @Size(max = 255, message = "{constraint.size.max}")
        private String text;
        private Boolean cash;
        private Boolean cashless;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Boolean getCash() {
            return cash;
        }

        public void setCash(Boolean cash) {
            this.cash = cash;
        }

        public Boolean getCashless() {
            return cashless;
        }

        public void setCashless(Boolean cashless) {
            this.cashless = cashless;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                    .append("text", text)//
                    .append("cash", cash)//
                    .append("cashless", cashless)//
                    .toString();
        }
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

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDeliveryConditions() {
        return deliveryConditions;
    }

    public void setDeliveryConditions(String deliveryConditions) {
        this.deliveryConditions = deliveryConditions;
    }

    public PaymentConditions getPaymentConditions() {
        return paymentConditions;
    }

    public void setPaymentConditions(PaymentConditions paymentConditions) {
        this.paymentConditions = paymentConditions;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("name", name)//
                .append("regionId", regionId)//
                .append("address", address)//
                .append("contactPhone", contactPhone)//
                .append("contactPerson", contactPerson)//
                .append("site", site)//
                .append("schedule", schedule)//
                .append("scope", scope)//
                .append("deliveryConditions", deliveryConditions)//
                .append("paymentConditions", paymentConditions)//
                .append("logo", logo)//
                .append("account", account)//
                .toString();
    }
}
