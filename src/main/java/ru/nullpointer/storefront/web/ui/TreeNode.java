package ru.nullpointer.storefront.web.ui;

import java.util.HashMap;
import java.util.Map;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class TreeNode {

    /**
     * Наименование узла
     */
    private String name;
    /**
     * Описание 
     */
    private String description;
    /**
     * Уровень вложенности узла.
     * Узлы верхнего уровня имеет значение раное 1
     */
    private int level;
    /**
     * Флаг того что узел является текущим
     */
    private boolean current;
    /**
     * Флаг того что узел является неактивным
     */
    private boolean disabled;
    /**
     * Гиперссылка на узел
     */
    private Link link;
    /**
     * Дополнительные данные узла
     */
    private Map<String, Object> data;

    public TreeNode(String name, String description, int level, boolean current, boolean disabled, Link link, Map<String, Object> data) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.current = current;
        this.disabled = disabled;
        this.link = link;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public boolean isCurrent() {
        return current;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public Link getLink() {
        return link;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public static class Builder {

        private String name;
        private String description;
        private int level = 1;
        private boolean current = false;
        private boolean disabled = false;
        private Link link = null;
        private Map<String, Object> data = new HashMap<String, Object>();

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setCurrent(boolean current) {
            this.current = current;
            return this;
        }

        public Builder setDisabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder setLink(Link link) {
            this.link = link;
            return this;
        }

        public Builder addData(String key, Object value) {
            this.data.put(key, value);
            return this;
        }

        public TreeNode build() {
            Assert.isTrue(level > 0);
            Assert.notNull(name);

            return new TreeNode(name, description, level, current, disabled, link, data);
        }
    }
}
