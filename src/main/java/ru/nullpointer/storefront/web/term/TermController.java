package ru.nullpointer.storefront.web.term;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Term;
import ru.nullpointer.storefront.service.TermService;
import ru.nullpointer.storefront.service.support.AlphabetResultBuilder;

/**
 * @author ankostyuk
 */
@Controller
public class TermController {

    private Logger logger = LoggerFactory.getLogger(TermController.class);
    //

    private static final String VIEW_NAME = "term";

    @Resource
    private TermService termService;

    @RequestMapping(value = "/term", method = RequestMethod.GET)
    public String handleGet(ModelMap model) {

        logger.debug("no prefix");

        List<String> termToc = termService.getTermToc();

        if (termToc.isEmpty()) {
            return VIEW_NAME;
        }

        model.addAttribute("termToc", termToc);
        model.addAttribute("termTotal", termService.getTermCount());

        return VIEW_NAME;
    }

    @RequestMapping(value = "/term/{prefix}", method = RequestMethod.GET)
    public String handleGetWithPrefix(@PathVariable("prefix") String prefix, ModelMap model) {

        logger.debug("prefix='{}'", prefix);

        if (prefix.length() != 1) {
            return "redirect:/term";
        }

        List<String> termToc = termService.getTermToc();
        
        if (termToc.isEmpty()) {
            return VIEW_NAME;
        }

        List<Term> termList = null;
        String groupName = null;

        if (Character.isDigit(prefix.charAt(0))) {
            termList = termService.getTermListByDigitPrefix();
            if (!termList.isEmpty()) {
                groupName = AlphabetResultBuilder.DIGIT_GROUP;
            }
        } else {
            termList = termService.getTermListByPrefix(prefix);
            if (!termList.isEmpty()) {
                groupName = prefix.substring(0, 1);
            }
        }

        model.addAttribute("termToc", termToc);
        
        if (termList != null) {
            model.addAttribute("termList", termList);
        }

        if (groupName == null) {
            model.addAttribute("termTotal", termService.getTermCount());
        } else {
            model.addAttribute("groupName", groupName);
        }
        
        return VIEW_NAME;
    }
}
