package ru.nullpointer.storefront.domain.support;

import java.util.List;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class ResultGroup<T> {

    private String name;
    private List<T> list;

    public ResultGroup(String name, List<T> list) {
        Assert.hasText(name);
        Assert.notNull(list);

        this.name = name;
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public List<T> getList() {
        return list;
    }
}
