package ru.nullpointer.storefront.web.secured.manager.term;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Term;
import ru.nullpointer.storefront.service.TermService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/term/edit/{id}")
public class EditTermController {

    @Resource
    private TermService termService;
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("term")
    public Term getTerm(@PathVariable("id") Integer termId) {
        Term term = termService.getTermById(termId);
        if (term == null) {
            throw new NotFoundException();
        }
        return term;
    }

    @InitBinder("term")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("name", "description", "source");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/term/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("term") Term term, BindingResult result,
            ModelMap model) {

        validator.validate(term, result);

        if (result.hasErrors()) {
            return "secured/manager/term/edit";
        }

        termService.updateTermInfo(term);

        return "redirect:/secured/manager/term";
    }
}
