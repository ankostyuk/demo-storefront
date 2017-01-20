package ru.nullpointer.storefront.web.registration;

import com.octo.captcha.service.CaptchaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.validation.BeanValidator;
import javax.servlet.http.HttpServletRequest;
import com.octo.captcha.service.image.ImageCaptchaService;
import javax.annotation.Resource;
import org.springframework.ui.ModelMap;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.web.support.BuildModeSupport;
import ru.nullpointer.storefront.web.support.RegionHelper;

/**
 * 
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
public class CompanyRegistrationController {

    private Logger logger = LoggerFactory.getLogger(CompanyRegistrationController.class);
    //
    @Autowired
    private BeanValidator validator;
    @Resource
    private ImageCaptchaService captchaService;
    @Resource
    private RegistrationService registrationService;
    @Resource
    private RegionHelper regionHelper;
    @Resource
    private BuildModeSupport buildModeSupport;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(
                "name",
                "account.email",
                "account.password");
    }

    @ModelAttribute("company")
    public Company getCompany() {
        Company company = new Company();
        Account account = new Account();
        company.setAccount(account);
        return company;
    }

    @RequestMapping(value = "/registration/company", method = RequestMethod.GET)
    public void handleGet(ModelMap model) {
        buildModeSupport.notFoundInDemo();
        // TODO: определить регион по IP
    }

    @RequestMapping(value = "/registration/company", method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("company") Company company,
            BindingResult result,
            @RequestParam("passwordRepeat") String passwordRepeat,
            @RequestParam("captcha") String captcha,
            ModelMap model,
            HttpServletRequest request) {
        
        buildModeSupport.notFoundInDemo();

        Region region = regionHelper.initRegion(request, model);
        company.setRegionId(region == null ? null : region.getId());

        validator.validate(company, result);

        if (!result.hasFieldErrors("account.email")) {
            // Проверить доступность емайл адреса
            String email = company.getAccount().getEmail();
            if (!registrationService.isEmailAvailable(email)) {
                // Если уже зарегистрирован, перенаправить на страницу логина
                model.clear();
                return "redirect:/login?registered&email=" + email;
            }
        }

        if (!passwordRepeat.isEmpty() && !passwordRepeat.equals(company.getAccount().getPassword())) {
            result.rejectValue("account.password", "validation.Company.account.password.repeat");
        }

        // Проверить капчу
        boolean invalidCaptcha = !validateCaptcha(captcha, request);
        model.addAttribute("invalidCaptcha", invalidCaptcha);

        // Если ошибки или неверная каптча отправляем обратно
        if (result.hasErrors() || invalidCaptcha) {
            logger.debug("Invalid captcha: {}, binding result: {}", invalidCaptcha, result);
            return "registration/company";
        }

        // Все ОК, можно регистрировать
        logger.debug("{}", company);

        registrationService.registerCompany(company);

        logger.debug("Registration success");

        model.clear();
        return "redirect:/registration/success?email=" + company.getAccount().getEmail();
    }

    private boolean validateCaptcha(String captcha, HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        try {
            return captchaService.validateResponseForID(sessionId, captcha);
        } catch (CaptchaServiceException ex) {
            logger.error("Ошибка при проверке каптчи", ex);
            return false;
        }
    }
}
