package ru.nullpointer.storefront.web.secured.manager.model;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/manager/model/delete/{id}")
public class DeleteModelController {

    @Resource
    private ModelService modelService;
    @Resource
    private MatchService matchService;
    @Resource
    private ImageService imageService;

    private Model getModel(Integer id) {
        Model model = modelService.getModelById(id);
        if (model == null) {
            throw new NotFoundException();
        }
        return model;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer modelId, ModelMap modelMap) {
        Model model = getModel(modelId);

        modelMap.addAttribute("model", model);
        modelMap.addAttribute("modelInfo", matchService.getModelInfo(model.getId(), null));

        return "secured/manager/model/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer modelId) {
        Model model = getModel(modelId);

        imageService.setModelImage(model, null);
        modelService.deleteModel(model);

        return "redirect:/secured/manager/model/" + model.getCategoryId();
    }
}
