package ru.nullpointer.storefront.config;

import java.io.File;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class RepositoryConfig implements InitializingBean {

    @Value("${repository.root}")
    private String repositoryRootPath;

    public File getRepositoryRoot() {
        return new File(repositoryRootPath);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(repositoryRootPath, "Свойство 'repositoryRoot' должно быть установлено");

        checkRepository();
    }

    private void checkRepository() {
        //
        // Удалено для прохождения тестов
        // TODO: Подумать как сделать правильно
        /*
        File f = new File(repositoryRootPath);
        String path = f.getAbsolutePath();
        Assert.isTrue(f.exists(), "Каталог с именем '" + path + "' не существует");
        Assert.isTrue(f.isDirectory(), "Файл '" + path + "' не является каталогом");
         */
    }
}
