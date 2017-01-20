package ru.nullpointer.storefront.web.registration;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.web.support.BuildModeSupport;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class RegistrationController {

    @Resource
    private RegistrationService registrationService;
    @Resource
    private BuildModeSupport buildModeSupport;

    @RequestMapping("/registration/activation")
    public String handleActivate(@RequestParam("email") String email, @RequestParam("token") String token, ModelMap model) {
        buildModeSupport.notFoundInDemo();

        boolean result = registrationService.confirmEmail(email, token);

        if (result) {
            return "redirect:/secured/gateway?first_login=true";
        }

        model.put("success", false);
        model.put("email", email);

        return "registration/activation";
    }

    @RequestMapping("/registration/success")
    public void handleSuccess(@RequestParam("email") String email, ModelMap model) {
        buildModeSupport.notFoundInDemo();

        model.put("email", email);
    }
}
