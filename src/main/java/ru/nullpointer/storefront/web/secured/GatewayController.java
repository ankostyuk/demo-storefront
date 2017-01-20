package ru.nullpointer.storefront.web.secured;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.security.AccountDetails;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.service.SecurityService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class GatewayController {

    private Logger logger = LoggerFactory.getLogger(GatewayController.class);
    //
    @Resource
    private SecurityService securityService;
    //
    private Map<Account.Type, String> redirectMap = new HashMap<Account.Type, String>();

    public GatewayController() {
        redirectMap.put(Account.Type.ADMIN, "/secured/admin");
        redirectMap.put(Account.Type.COMPANY, "/secured/company");
        redirectMap.put(Account.Type.MANAGER, "/secured/manager");
    }

    @RequestMapping("/secured/gateway")
    public String handle(HttpServletRequest request, ModelMap model) {
        String redirectUrl = "/";
        try {
            AccountDetails details = securityService.getAuthenticatedAccountDetails();

            redirectUrl = redirectMap.get(details.getAccountType());
            if (redirectUrl == null) {
                throw new IllegalArgumentException("Unknown account type '" + details.getAccountType() + "'");
            }

            // пробросить все параметры
            Enumeration e = request.getParameterNames();
            if (e != null) {
                while (e.hasMoreElements()) {
                    String p = e.nextElement().toString();
                    model.addAttribute(p, request.getParameterValues(p));
                }
            }
        } catch (AuthenticationCredentialsNotFoundException ex) {
            logger.warn("Пользователь не аутентифицирован");
        }

        logger.debug("Перенаправляем на {}, model: {}", redirectUrl, model);
        return "redirect:" + redirectUrl;
    }
}
