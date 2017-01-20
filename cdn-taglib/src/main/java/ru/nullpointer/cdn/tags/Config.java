package ru.nullpointer.cdn.tags;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Alexander Yastrebov
 */
class Config implements Serializable {

    private static final long serialVersionUID = 1L;
    //
    private Map<String, Container> containerMap;

    Config(Map<String, Container> containerMap) {
        this.containerMap = containerMap;
    }

    Map<String, Container> getContainerMap() {
        return containerMap;
    }
}
