package ru.nullpointer.storefront.web.secured.admin.account;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.domain.support.AccountSorting;
import ru.nullpointer.storefront.domain.support.AliasUtils;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.service.SecurityService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class AdminAccountController {

    private Logger logger = LoggerFactory.getLogger(AdminAccountController.class);
    //
    private static final int DEFAULT_PAGE_SIZE = 25;
    //
    @Resource
    private CompanyService companyService;
    @Resource
    private RegistrationService registrationService;
    @Resource
    private SecurityService securityService;

    @RequestMapping(value = "/secured/admin/account/company", method = RequestMethod.GET)
    public void handleCompanyGet(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "page", required = false) Integer page,
            ModelMap model) {

        PageConfig pageConfig = new PageConfig(page, DEFAULT_PAGE_SIZE);

        PaginatedQueryResult<Company> queryResult = companyService.getCompanyList(text, pageConfig);
        model.addAttribute("queryResult", queryResult);

        model.addAttribute("accountMap", buildCompanyAccountMap(queryResult.getList()));
    }

    @RequestMapping(value = "/secured/admin/account/manager", method = RequestMethod.GET)
    public void handleManagerGet(
            @RequestParam(value = "sort", required = false) String sort,
            ModelMap model) {

        AccountSorting sorting = AliasUtils.fromAlias(sort, AccountSorting.EMAIL_ASCENDING);
        model.addAttribute("sorting", sorting);

        List<Account> accountList = registrationService.getManagerList(sorting);
        model.addAttribute("accountList", accountList);

        model.addAttribute("roleMap", getRoleMap(accountList));
    }

    @RequestMapping(value = "/secured/admin/account/admin", method = RequestMethod.GET)
    public void handleAdminGet(
            @RequestParam(value = "sort", required = false) String sort,
            ModelMap model) {

        AccountSorting sorting = AliasUtils.fromAlias(sort, AccountSorting.EMAIL_ASCENDING);
        model.addAttribute("sorting", sorting);

        List<Account> accountList = registrationService.getAdminList(sorting);
        model.addAttribute("accountList", accountList);
    }

    private Map<Integer, List<Role>> getRoleMap(List<Account> accountList) {
        Map<Integer, List<Role>> result = new HashMap<Integer, List<Role>>(accountList.size());
        for (Account a : accountList) {
            result.put(a.getId(), securityService.getAccountRoles(a.getId()));
        }
        return result;
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
