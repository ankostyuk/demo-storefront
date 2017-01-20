package ru.nullpointer.storefront.web.secured.manager.company;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.RegistrationService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class ManagerCompanyController {

    private static final int DEFAULT_PAGE_SIZE = 25;
    //
    @Resource
    private CompanyService companyService;
    @Resource
    private RegistrationService registrationService;

    @RequestMapping(value = "/secured/manager/company", method = RequestMethod.GET)
    public void handleCompanyGet(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "page", required = false) Integer page,
            ModelMap model) {

        PageConfig pageConfig = new PageConfig(page, DEFAULT_PAGE_SIZE);

        PaginatedQueryResult<Company> queryResult = companyService.getCompanyList(text, pageConfig);
        model.addAttribute("queryResult", queryResult);

        model.addAttribute("accountMap", buildCompanyAccountMap(queryResult.getList()));
    }

    @RequestMapping(value = "/secured/manager/company/login", method = RequestMethod.GET)
    public String handleCompanyLoginGet(@RequestParam("email") String email) {
        registrationService.authenticateCompanyAccount(email);
        return "redirect:/secured/company";
    }

    private Map<Integer, Account> buildCompanyAccountMap(List<Company> companyList) {
        if (companyList.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Integer> accountIdSet = new HashSet<Integer>();
        for (Company c : companyList) {
            accountIdSet.add(c.getId());
        }
        return registrationService.getAccountMap(accountIdSet);
    }
}
