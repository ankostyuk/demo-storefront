package ru.nullpointer.storefront.web.secured.manager.term;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Term;
import ru.nullpointer.storefront.domain.support.ResultGroup;
import ru.nullpointer.storefront.service.TermService;

/**
 * @author ankostyuk
 */
@Controller
public class ManagerTermController {

    @Resource
    private TermService termService;

    @RequestMapping(value = "/secured/manager/term", method = RequestMethod.GET)
    public void handleGet(ModelMap model) {

        List<String> termToc = termService.getTermToc();
        
        if (termToc.isEmpty()) {
            return;
        }

        List<ResultGroup<Term>> termGroupList = termService.getTermGroupList();

        model.addAttribute("termGroupList", termGroupList);
        model.addAttribute("termToc", termToc);
    }
}
