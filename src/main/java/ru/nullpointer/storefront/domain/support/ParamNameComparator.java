package ru.nullpointer.storefront.domain.support;

import java.util.Comparator;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.param.Param;

/**
 * @author ankostyuk
 */
public class ParamNameComparator implements Comparator<Param> {

    @Override
    public int compare(Param param1, Param param2) {
        Assert.notNull(param1);
        Assert.notNull(param2);
        Assert.notNull(param1.getName());
        Assert.notNull(param2.getName());

        return param1.getName().compareTo(param2.getName());
    }
}
