package ru.nullpointer.storefront.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alexander Yastrebov
 */
public enum Role {

    ROLE_MANAGER_CATALOG,
    ROLE_MANAGER_MODEL,
    ROLE_MANAGER_BRAND,
    ROLE_MANAGER_DICTIONARY,
    //
    ROLE_MANAGER_MODERATOR_OFFER,
    //
    ROLE_MANAGER_COMPANY_LOGIN;

    public static List<Role> getManagerRoleList() {
        List<Role> result = new ArrayList<Role>();
        for (Role r : values()) {
            if (r.isManagerRole()) {
                result.add(r);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public boolean isManagerRole() {
        return name().startsWith("ROLE_MANAGER_");
    }
}
