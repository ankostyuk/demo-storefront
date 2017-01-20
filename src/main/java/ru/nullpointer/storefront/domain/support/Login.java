package ru.nullpointer.storefront.domain.support;

import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import ru.nullpointer.storefront.validation.Email;

/**
 *
 * @author Alexander Yastrebov
 */
public class Login {

    @Size(min = 5, message = "{constraint.pattern}")
    @Email(message = "{constraint.pattern}")
    private String email;
    //
    @Size(min = Constants.ACCOUNT_MIN_PASSWORD_LENGTH, message = "{constraint.size}")
    private String password;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("email", email)//
                .append("password", (password == null ? "<null>" : "[PROTECTED]"))//
                .toString();
    }
}
