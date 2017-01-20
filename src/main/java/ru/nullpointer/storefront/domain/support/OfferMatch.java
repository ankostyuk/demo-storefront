package ru.nullpointer.storefront.domain.support;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;

/**
 *
 * @author Alexander Yastrebov
 */
public class OfferMatch extends AbstractMatch {

    private Offer offer;
    private Company company;
    private Region companyRegion;

    public OfferMatch(Offer offer, Company company, Region companyRegion) {
        Assert.notNull(offer);
        Assert.notNull(company);
        Assert.notNull(companyRegion);
        //
        this.offer = offer;
        this.company = company;
        this.companyRegion = companyRegion;
    }

    public Offer getOffer() {
        return offer;
    }

    public Company getCompany() {
        return company;
    }

    public Region getCompanyRegion() {
        return companyRegion;
    }

    @Override
    public Match.Type getType() {
        return Match.Type.OFFER;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("offer", offer)//
                .append("companyName", company.getName())//
                .append("companyRegion", companyRegion)//
                .toString();
    }
}
