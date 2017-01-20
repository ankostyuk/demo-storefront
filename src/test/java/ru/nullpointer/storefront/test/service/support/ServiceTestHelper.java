package ru.nullpointer.storefront.test.service.support;

import static org.junit.Assert.*;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.security.AccountAuthenticationProvider;

/**
 * @author ankostyuk
 */
@Component
public class ServiceTestHelper {

    private static final String MANAGER_CATALOG_EMAIL = "manager1@example.com";
    private static final String MANAGER_CATALOG_PASSWORD = "manager1";

    private static final String MANAGER_BRAND_EMAIL = "manager3@example.com";
    private static final String MANAGER_BRAND_PASSWORD = "manager3";

    private static final String MANAGER_MODEL_EMAIL = "manager3@example.com";
    private static final String MANAGER_MODEL_PASSWORD = "manager3";

    private static final String COMPANY_EMAIL = "company1@example.com";
    private static final String COMPANY_PASSWORD = "company1";

    @Autowired
    private AccountAuthenticationProvider accountAuthenticationProvider;

    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private ModelDAO modelDAO;
    @Resource
    private CompanyDAO companyDAO;

    public void authenticateAsManagerCatalog() {
        Authentication auth = accountAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(MANAGER_CATALOG_EMAIL, MANAGER_CATALOG_PASSWORD));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public void authenticateAsManagerModel() {
        Authentication auth = accountAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(MANAGER_MODEL_EMAIL, MANAGER_MODEL_PASSWORD));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public void authenticateAsManagerBrand() {
        Authentication auth = accountAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(MANAGER_BRAND_EMAIL, MANAGER_BRAND_PASSWORD));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public void authenticateAsCompany() {
        Authentication auth = accountAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(COMPANY_EMAIL, COMPANY_PASSWORD));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public Category getOfferCategory() {
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            if (offerDAO.getCategoryOfferCount(category.getId()) > 0) {
                return category;
            }
        }
        fail();
        return null;
    }

    public Category getAccessibleOfferCategory() {
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            if (offerDAO.getCategoryAccessibleOfferCount(category.getId()) > 0) {
                return category;
            }
        }
        fail();
        return null;
    }

    public Category getModelCategory() {
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            if (modelDAO.getCategoryModelCount(category.getId()) > 0) {
                return category;
            }
        }
        fail();
        return null;
    }

    public Offer getExistOffer() {
        Category category = getOfferCategory();
        List<Offer> offers = offerDAO.getCategoryOfferPaginatedList(category.getId(), new PageConfig(1, 10));
        assertFalse(offers.isEmpty());
        return offers.get(0);
    }

    public Model getExistModel() {
        Category category = getModelCategory();
        List<Model> models = modelDAO.getCategoryModelList(category.getId(), new PageConfig(1, 10));
        assertFalse(models.isEmpty());
        return models.get(0);
    }

    public Company getExistCompany() {
        List<Company> companyList = companyDAO.getCompanyList(null, new PageConfig(1, 1));
        assertNotNull(companyList);
        assertFalse(companyList.isEmpty());

        return companyList.get(0);
    }
}
