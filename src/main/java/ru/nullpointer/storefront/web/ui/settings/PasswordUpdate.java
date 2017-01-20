package ru.nullpointer.storefront.web.ui.settings;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import ru.nullpointer.storefront.domain.support.Constants;

/**
 *
 * @author Alexander Yastrebov
 */
public class PasswordUpdate {

    private String oldPassword;
    //
    @NotNull
    @Size(min = Constants.ACCOUNT_MIN_PASSWORD_LENGTH, message = "{constraint.size}")
    private String newPassword;
    //
    @NotNull
    @Size(min = Constants.ACCOUNT_MIN_PASSWORD_LENGTH, message = "{constraint.size}")
    private String newPasswordRepeat;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordRepeat() {
        return newPasswordRepeat;
    }

    public void setNewPasswordRepeat(String newPasswordRepeat) {
        this.newPasswordRepeat = newPasswordRepeat;
    }
}
