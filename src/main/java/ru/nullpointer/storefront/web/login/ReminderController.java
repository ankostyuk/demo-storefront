package ru.nullpointer.storefront.web.login;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.web.support.BuildModeSupport;

/**
 *
 * @author Alexander
 */
@Controller
@RequestMapping("/login/reminder")
public class ReminderController {

    private Logger logger = LoggerFactory.getLogger(ReminderController.class);
    //
    @Resource
    private RegistrationService registrationService;
    @Resource
    private BuildModeSupport buildModeSupport;

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet() {
        buildModeSupport.notFoundInDemo();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@RequestParam("email") String email, ModelMap model) {
        buildModeSupport.notFoundInDemo();

        boolean success = false;
        if (email.trim().length() != 0) {
            success = registrationService.remindPassword(email);
        }

        // все нормально, перенаправляем на страницу логина
        if (success) {
            return "redirect:/login?reminder&email=" + email;
        } else {
            // что-то пошло не так, выводим сообщение об ошибке
            model.addAttribute("error", true);
            return "login/reminder";
        }
    }
}
