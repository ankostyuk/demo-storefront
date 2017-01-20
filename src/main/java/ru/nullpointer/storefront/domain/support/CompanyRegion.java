package ru.nullpointer.storefront.domain.support;

import java.util.List;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Region;

/**
 * @author ankostyuk
 */
public class CompanyRegion {
    
    private Company company;
    private Region selfRegion;
    private List<Region> deliveryRegionList;

    public CompanyRegion(Company company, Region selfRegion, List<Region> deliveryRegionList) {
        Assert.notNull(company);
        Assert.notNull(selfRegion);
        Assert.notNull(deliveryRegionList);

        this.company = company;
        this.selfRegion = selfRegion;
        this.deliveryRegionList = deliveryRegionList;
    }

    public Company getCompany() {
        return company;
    }

    public Region getSelfRegion() {
        return selfRegion;
    }

    public List<Region> getDeliveryRegionList() {
        return deliveryRegionList;
    }
}
