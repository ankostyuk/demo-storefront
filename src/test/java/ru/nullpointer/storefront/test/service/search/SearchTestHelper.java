package ru.nullpointer.storefront.test.service.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PostConstruct;

import javax.annotation.Resource;
import org.apache.lucene.document.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.dao.BrandDAO;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.OfferDAO;

import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.search.SearchLifecycle;
import ru.nullpointer.storefront.service.search.SearchUtils;
import ru.nullpointer.storefront.service.search.IdentityFieldValue;
import ru.nullpointer.storefront.service.search.Type;

/**
 * @author ankostyuk
 */
@Component
class SearchTestHelper {

    private Logger logger = LoggerFactory.getLogger(CatalogSearchTest.class);
    //

    public static final long WAITING_FOR_COMPLETE_TASKS_SLEEP_DEFAULT = 500L;

    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private ModelDAO modelDAO;
    @Resource
    private BrandDAO brandDAO;
    @Resource
    private SearchLifecycle searchLifecycle;

    private List<Character> cyrillicAlphabet;
    private List<Character> latinAlphabet;

    @PostConstruct
    public void init() {
        cyrillicAlphabet = new ArrayList<Character>();
        for (char c = 'а'; c <= 'я'; c++) {
            cyrillicAlphabet.add(c);
        }
        latinAlphabet = new ArrayList<Character>();
        for (char c = 'a'; c <= 'z'; c++) {
            latinAlphabet.add(c);
        }
    }

    public Character[] getCyrillicAlphabet() {
        return cyrillicAlphabet.toArray(new Character[0]);
    }

    public Character[] getLatinAlphabet() {
        return latinAlphabet.toArray(new Character[0]);
    }

    public String toVariableCase(String string) {
        String str = "";
        for (int i = 0; i < string.length(); i++) {
            str += (i % 2 == 0 ? string.substring(i, i + 1).toLowerCase() : string.substring(i, i + 1).toUpperCase());
        }
        assertEquals(0, string.compareToIgnoreCase(str));
        return str;
    }

    public List<CatalogItem> getActiveCatalogItemSubTree(Integer sectionId) {
        List<CatalogItem> activeSubTree = new ArrayList<CatalogItem>();
        List<CatalogItem> subTree = catalogItemDAO.getSubTree(sectionId);
        for (CatalogItem item : subTree) {
            if (item.getActive()) {
                activeSubTree.add(item);
            }
        }
        return activeSubTree;
    }

    public CatalogInfo getCatalogInfo() {
        CatalogInfo catalogInfo = new CatalogInfo();

        //
        catalogInfo.setActiveItemCount(getActiveCatalogItemSubTree(null).size());

        //
        int activeOfferCount = 0;
        int modelOfferCount = 0;

        List<CatalogItem> items = catalogItemDAO.getSubTree(null);

        Set<Integer> categoryIdSet = new HashSet<Integer>();

        for (CatalogItem item : items) {
            if (CatalogItem.Type.CATEGORY.equals(item.getType())) {
                //if (item.getActive()) { // TODO ?
                    activeOfferCount += offerDAO.getCategoryAccessibleOfferCount(item.getId());
                    modelOfferCount += offerDAO.getCategoryAccessibleModelOfferCount(item.getId());
                //}
                categoryIdSet.add(item.getId());
            }
        }

        catalogInfo.setActiveOfferCount(activeOfferCount);
        catalogInfo.setActiveModelOfferCount(modelOfferCount);

        //
        int modelCount = 0;
        
        Map<Integer, Integer> countMap = modelDAO.getCategoryModelCountMap(categoryIdSet);

        for (Integer count : countMap.values()) {
            modelCount += count;
        }

        catalogInfo.setModelCount(modelCount);

        catalogInfo.setBrandCount(brandDAO.getBrandCount());

        return catalogInfo;
    }

    public void testConformityIndex() {
        CatalogInfo catalogInfo = getCatalogInfo();
        int docCount = searchLifecycle.getCatalogIndexDocCount();
        assertEquals(
                catalogInfo.getActiveItemCount()
                + catalogInfo.getActiveOfferCount() - catalogInfo.getActiveModelOfferCount()
                + catalogInfo.getModelCount()
                + catalogInfo.getBrandCount(),
                docCount);
    }

    public void testTypeOrder(List<Document> docs) {
        if (docs.isEmpty()) {
            return;
        }

        List<Type> typeList = getTypeOrderList();
        List<Type> orderList = new ArrayList<Type>(typeList.size());
        List<Type> normalizedList = new ArrayList<Type>(typeList.size());

        Type checkType = null;

        for (int i = 0; i < docs.size(); i++) {
            Document doc = docs.get(i);
            
            Type type = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY)).getType();

            if (type.equals(checkType)) {
                continue;
            } else {
                checkType = type;
                orderList.add(type);
            }
        }

        for (Type type : typeList) {
            if (orderList.contains(type)) {
                normalizedList.add(type);
            }
        }

        int index = Collections.indexOfSubList(normalizedList, orderList);

        assertEquals(normalizedList.size(), orderList.size());
        assertEquals(0, index);
    }

    public List<Type> getTypeOrderList() {
        Type[] types = Type.values();

        List<Type> list = new ArrayList<Type>(types.length);
        for (Type type : types) {
            list.add(type);
        }
        return list;
    }

    public void testEqualsDoc(Document expectedDoc, Document actualDoc) {
        assertEquals(expectedDoc.get(SearchUtils.FIELD_IDENTITY), actualDoc.get(SearchUtils.FIELD_IDENTITY));
        assertEquals(expectedDoc.get(SearchUtils.FIELD_NAME), actualDoc.get(SearchUtils.FIELD_NAME));
    }

    public boolean isDifferentTypeDocs(List<Document> docs) {
        if (docs.size() < 2) {
            return false;
        }

        Type type = new IdentityFieldValue(docs.get(0).get(SearchUtils.FIELD_IDENTITY)).getType();

        for (int i = 1; i < docs.size(); i++) {
            if (!type.equals(new IdentityFieldValue(docs.get(i).get(SearchUtils.FIELD_IDENTITY)).getType())) {
                return true;
            }
        }
        
        return false;
    }

    public void waitingForCompleteTasks() {
        logger.debug("TEST Ожидание выполнения задач...");
        ThreadPoolExecutor tpe = searchLifecycle.getThreadPoolExecutor();
        do {
            try {
                Thread.sleep(WAITING_FOR_COMPLETE_TASKS_SLEEP_DEFAULT);
            } catch (InterruptedException e) {
                logger.error("{}", e);
                break;
            }
        } while (tpe.getTaskCount() - tpe.getCompletedTaskCount() > 0);
    }

    public class CatalogInfo {
        int activeItemCount;
        int activeOfferCount;
        int activeModelOfferCount;
        int modelCount;
        int brandCount;

        public int getActiveItemCount() {
            return activeItemCount;
        }

        public void setActiveItemCount(int activeItemCount) {
            this.activeItemCount = activeItemCount;
        }

        public int getActiveOfferCount() {
            return activeOfferCount;
        }

        public void setActiveOfferCount(int activeOfferCount) {
            this.activeOfferCount = activeOfferCount;
        }

        public int getActiveModelOfferCount() {
            return activeModelOfferCount;
        }

        public void setActiveModelOfferCount(int activeModelOfferCount) {
            this.activeModelOfferCount = activeModelOfferCount;
        }

        public int getModelCount() {
            return modelCount;
        }

        public void setModelCount(int modelCount) {
            this.modelCount = modelCount;
        }

        public int getBrandCount() {
            return brandCount;
        }

        public void setBrandCount(int brandCount) {
            this.brandCount = brandCount;
        }
    }
}
