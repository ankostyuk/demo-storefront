package ru.nullpointer.storefront.service.search;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Field;
import org.springframework.util.Assert;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.OpenBitSet;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.CompanyRegion;

/**
 * @author ankostyuk
 */
// TODO cache
public class RegionFilter extends Filter {

	private String fieldName;
	private Region region;

	public RegionFilter(String fieldName, Region region) {
        Assert.hasText(fieldName);
        Assert.notNull(region);

		this.fieldName = fieldName;
		this.region = region;
	}

    @Override
    public DocIdSet getDocIdSet(IndexReader reader) throws IOException {

        OpenBitSet bitSet = new OpenBitSet(reader.maxDoc());
		Term term = new Term(fieldName);
		TermEnum termEnum = reader.terms(term);

        if (termEnum != null) {
			Term currentTerm = termEnum.term();

            while (currentTerm != null) {
                if (term.field().equals(currentTerm.field())) {
                    if (isMatch(currentTerm.text(), region)) {
                        TermDocs termDocs = reader.termDocs(currentTerm);
                        while (termDocs.next()) {
                            bitSet.set(termDocs.doc());
                        }
                    }
                }

                if (!termEnum.next()) {
					break;
				}
				currentTerm = termEnum.term();
            }
        }

        return bitSet;
    }

    /*
     Format:
      #companyRegion.selfRegion.left#-#companyRegion.selfRegion.right#
      &#companyRegion.deliveryRegionList[0].left#-#companyRegion.deliveryRegionList[0].right#
      |#companyRegion.deliveryRegionList[1].left#-#companyRegion.deliveryRegionList[1].right#
      ...
      |#companyRegion.deliveryRegionList[N].left#-#companyRegion.deliveryRegionList[N].right#
    
     Example:
      4-7&106-107|804-805|10-11|4-7
    */
    public static Field buildOfferRegionField(String fieldName, Offer offer, CompanyRegion companyRegion) {
        Assert.hasText(fieldName);
        Assert.notNull(offer);
        Assert.notNull(companyRegion);

        Region selfRegion = companyRegion.getSelfRegion();

        StringBuilder regionValue =
                new StringBuilder(selfRegion.getLeft().toString()).append("-").append(selfRegion.getRight());

        if (offer.getDelivery()) {
            List<Region> deliveryRegionList = companyRegion.getDeliveryRegionList();

            if (!deliveryRegionList.isEmpty()) {
                regionValue.append("&");
                for (int i = 0; i < deliveryRegionList.size(); i++) {
                    Region region = deliveryRegionList.get(i);
                    if (i != 0) {
                        regionValue.append("|");
                    }
                    regionValue.append(region.getLeft()).append("-").append(region.getRight());
                }
            }
        }

        return new Field(fieldName, regionValue.toString(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    public static Field buildModelRegionField(String fieldName, Model model) {
        Assert.hasText(fieldName);
        Assert.notNull(model);

        String s = "0-" + Integer.MAX_VALUE;
        StringBuilder regionValue = new StringBuilder(s).append("&").append(s);

        return new Field(fieldName, regionValue.toString(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
    }

    /*
     (#region.left# <= #selfRegion.left# AND #selfRegion.right# <= #region.right#) OR
     (not empty deliveryRegionList AND (
             (#region.left# <= #deliveryRegionList[].left# AND #deliveryRegionList[].right# <= #region.right#) OR
             (#deliveryRegionList[].left# < #region.left# AND #region.right# < #deliveryRegionList[].right#)
         )
     )
    */
    public static boolean isMatch(String fieldText, Region region) {
        int regionLeft = region.getLeft();
        int regionRight = region.getRight();
        int count = 0;
        int left = 0;
        int right = 0;
        int si = 0;
        char c = 0;
        int li = fieldText.length();

        for (int i = 1; i <= li; i++) {
            c = i == li ? '|' : fieldText.charAt(i);
            
            if (c == '-') {
                left = Integer.parseInt(fieldText.substring(si, i));
                i++;
                si = i;
            } else
            if (c == '&' || c == '|') {
                right = Integer.parseInt(fieldText.substring(si, i));
                
                if ( (regionLeft <= left && right <= regionRight)
                     || (count == 1 && (left < regionLeft && regionRight < right))) {
                    return true;
                }

                i++;
                si = i;

                if (c == '&') {
                    count = 1;
                }
            }
        }

        return false;
    }
}