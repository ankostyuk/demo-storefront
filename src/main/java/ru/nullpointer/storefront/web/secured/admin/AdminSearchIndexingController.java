package ru.nullpointer.storefront.web.secured.admin;

import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.service.search.SearchLifecycle;

/**
 * @author ankostyuk
 */
@Controller
public class AdminSearchIndexingController {

    private Logger logger = LoggerFactory.getLogger(AdminSearchIndexingController.class);
    //
    @Resource
    private SearchLifecycle searchLifecycle;

    @RequestMapping(value = "/secured/admin/searchindexing", method = RequestMethod.GET)
    public void handleGet(ModelMap model) {
        ThreadPoolExecutor tpe = searchLifecycle.getThreadPoolExecutor();
        model.addAttribute("runningTaskCount", tpe.getTaskCount() - tpe.getCompletedTaskCount());
    }

    @RequestMapping(value = "/secured/admin/searchindexing/catalog/recreate", method = RequestMethod.POST)
    public String handleCatalogRecreatePost(ModelMap model) {
        searchLifecycle.createCatalogIndex();
        return "redirect:/secured/admin/searchindexing";
    }

    @RequestMapping(value = "/secured/admin/searchindexing/company/recreate", method = RequestMethod.POST)
    public String handleCompanyRecreatePost(ModelMap model) {
        searchLifecycle.createCompanyIndex();
        return "redirect:/secured/admin/searchindexing";
    }
}
