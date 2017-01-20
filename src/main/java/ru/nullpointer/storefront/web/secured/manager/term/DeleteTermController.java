package ru.nullpointer.storefront.web.secured.manager.term;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Term;

import ru.nullpointer.storefront.service.TermService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/term/delete/{id}")
public class DeleteTermController {

    @Resource
    private TermService termService;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer termId, ModelMap model) {
        Term term = getTerm(termId);
        model.addAttribute("term", term);
        return "secured/manager/term/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer termId) {
        Term term = getTerm(termId);
        termService.deleteTerm(term);
        return "redirect:/secured/manager/term";
    }

    private Term getTerm(Integer termId) {
        Term term = termService.getTermById(termId);
        if (term == null) {
            throw new NotFoundException();
        }
        return term;
    }
}
