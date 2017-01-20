package ru.nullpointer.storefront.web.country;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.domain.Country;
import ru.nullpointer.storefront.service.CountryService;

/**
 * @author ankostyuk
 */
@Controller
public class CountryController {

    private static final int GET_COUNTRY_LIMIT = 5;

    @Resource
    private CountryService countryService;

    @ResponseBody
    @RequestMapping(value = "/country/suggest", method = RequestMethod.GET)
    public List<Country> handleSuggest(@RequestParam("q") String query) {
        return countryService.getCountryListByText(query, GET_COUNTRY_LIMIT);
    }
}
