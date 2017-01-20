package ru.nullpointer.storefront.domain;

import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import ru.nullpointer.storefront.domain.support.Constants;
import ru.nullpointer.storefront.validation.Email;

/**
 *
 * @author Alexander Yastrebov
 */
public class Account {

    private Integer id;
    //
    @NotNull
    @Size(min = 5, message = "{constraint.pattern}")
    @Email(message = "{constraint.pattern}")
    private String email;
    //
    @NotNull
    @Size(min = Constants.ACCOUNT_MIN_PASSWORD_LENGTH, message = "{constraint.size}")
    private String password;
    //
    private String newPassword;
    private Date newPasswordDate;
    private Date emailAuthenticatedDate;
    private String emailToken;
    private Date emailTokenExpiresDate;
    private Date registrationDate;
    private Date lastLoginDate;
    private Type type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Date getNewPasswordDate() {
        return newPasswordDate;
    }

    public void setNewPasswordDate(Date newPasswordDate) {
        this.newPasswordDate = newPasswordDate;
    }

    public Date getEmailAuthenticatedDate() {
        return emailAuthenticatedDate;
    }

    public void setEmailAuthenticatedDate(Date emailAuthenticatedDate) {
        this.emailAuthenticatedDate = emailAuthenticatedDate;
    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    public Date getEmailTokenExpiresDate() {
        return emailTokenExpiresDate;
    }

    public void setEmailTokenExpiresDate(Date emailTokenExpiresDate) {
        this.emailTokenExpiresDate = emailTokenExpiresDate;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {

        ADMIN, COMPANY, MANAGER
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("email", email)//
                .append("password", (password == null ? "<null>" : "[PROTECTED]"))//
                .append("registrationDate", registrationDate)//
                .append("lastLoginDate", lastLoginDate)//
                .append("type", type)//
                .toString();
    }
}
