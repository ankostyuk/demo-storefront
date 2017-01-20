package ru.nullpointer.storefront.service.search;

import ru.nullpointer.storefront.service.search.catalog.CatalogIndexer;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Brand;

import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.search.catalog.tasks.AddBrandIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.AddCategoryIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.AddModelIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.AddOfferIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.AddSectionIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.CreateCatalogIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.DeleteBrandIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.DeleteCategoryIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.DeleteModelIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.DeleteOfferIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.DeleteSectionIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.UpdateBrandIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.UpdateCategoryIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.UpdateCompanyOfferIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.UpdateModelIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.UpdateOfferIndex;
import ru.nullpointer.storefront.service.search.catalog.tasks.UpdateSectionIndex;
import ru.nullpointer.storefront.service.search.company.CompanyIndexer;
import ru.nullpointer.storefront.service.search.company.DeleteCompanyOfferIndex;
import ru.nullpointer.storefront.service.search.company.tasks.AddCompanyOfferIndex;
import ru.nullpointer.storefront.service.search.company.tasks.CreateCompanyIndex;

/**
 * @author ankostyuk
 */
// TODO при отсутствии индекса создавать индекс в основном потоке или с
// ожиданием окончания индексирования
@Component
public class SearchLifecycle implements InitializingBean, SmartLifecycle {

    private Logger logger = LoggerFactory.getLogger(SearchLifecycle.class);
    //

    public static final boolean POOLING_DEFAULT = true;
    private static final int THREAD_COUNT_MAX = 1; // Использовать только один поток!

    private boolean autoStartup;

    @Resource
    private CatalogIndexer catalogIndexer;
    @Resource
    private CompanyIndexer companyIndexer;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private boolean pooling;

    private boolean createIndexOnStartup;
    private boolean waitOnShutdown;
    private long waitOnShutdownTimeout;
    private TimeUnit waitOnShutdownTimeUnit;

    public SearchLifecycle(ThreadPoolTaskExecutor threadPoolTaskExecutor, boolean autoStartup) {
        Assert.notNull(threadPoolTaskExecutor);
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.autoStartup = autoStartup;

        this.pooling = POOLING_DEFAULT;
    }

    /**
     * по умолчанию {@link POOLING_DEFAULT}
     */
    public boolean isPooling() {
        return pooling;
    }

    /**
     * по умолчанию {@link POOLING_DEFAULT}
     */
    public void setPooling(boolean pooling) {
        this.pooling = pooling;
    }

    public boolean isCreateIndexOnStartup() {
        return createIndexOnStartup;
    }

    public void setCreateIndexOnStartup(boolean createIndexOnStartup) {
        this.createIndexOnStartup = createIndexOnStartup;
    }

    public boolean isWaitOnShutdown() {
        return waitOnShutdown;
    }

    /**
     * Устанавливает использование ожидания завершения задач при "уничтожении"
     * экземпляра класса c "замораживанием" вызывающего потока. Отменяет
     * использование
     * <code>ThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown<code>.
     */
    public void setWaitOnShutdown(boolean waitOnShutdown) {
        this.waitOnShutdown = waitOnShutdown;
    }

    public long getWaitOnShutdownTimeout() {
        return waitOnShutdownTimeout;
    }

    public void setWaitOnShutdownTimeout(long waitOnShutdownTimeout) {
        this.waitOnShutdownTimeout = waitOnShutdownTimeout;
    }

    public TimeUnit getWaitOnShutdownTimeUnit() {
        return waitOnShutdownTimeUnit;
    }

    public void setWaitOnShutdownTimeUnit(TimeUnit waitOnShutdownTimeUnit) {
        this.waitOnShutdownTimeUnit = waitOnShutdownTimeUnit;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        threadPoolTaskExecutor.setCorePoolSize(THREAD_COUNT_MAX);
        if (waitOnShutdown) {
            threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(false);
        }
    }

    @PreDestroy
    public void preDestroy() {
    }

    @Override
    public boolean isAutoStartup() {
        return autoStartup;
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE; // would be among the first to start and the
                                  // last to stop
    }

    @Override
    public boolean isRunning() {
        return getThreadPoolExecutor().getTaskCount() > 0;
    }

    @Override
    public void start() {
        if (createIndexOnStartup) {
            createCatalogIndex();
            createCompanyIndex();
        }
    }

    @Override
    public void stop(Runnable callback) {
        stop();
    }

    @Override
    public void stop() {
        shutdown();
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolTaskExecutor.getThreadPoolExecutor();
    }

    /**
     * Останавливает выполнение задач. Ожидание выполнения задач зависит от
     * {@link setWaitOnShutdown} <code>setWaitOnShutdown</code> и
     * <code>ThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown<code>
     */
    public void shutdown() {
        if (waitOnShutdown) {
            logger.debug("Ожидание выполнения задач...");
            ThreadPoolExecutor tpe = getThreadPoolExecutor();
            tpe.shutdown();
            boolean terminated = false;
            try {
                terminated = tpe.awaitTermination(waitOnShutdownTimeout, waitOnShutdownTimeUnit);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Исключение при ожидании выполнения задач: ", e);
            } finally {
                if (terminated) {
                    logger.debug("Задачи завершены");
                } else {
                    logger.warn("Выполнение задач(и) не завершено. Возможно нарушение целостности индекса и синхронности индекса с данными. Увеличьте таймаут ожидания или оптимизируйте работу с индексом :)");
                }
            }
        } else {
            threadPoolTaskExecutor.shutdown();
        }
    }

    /**
     * Возвращает количество документов в поисковом индексе каталога
     */
    public int getCatalogIndexDocCount() {
        try {
            return catalogIndexer.getIndexDocCount();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Возвращает количество документов в поисковом индексе компаний
     */
    public int getCompanyIndexDocCount() {
        try {
            return companyIndexer.getIndexDocCount();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Запускает задачу на создание поискового индекса каталога
     */
    public void createCatalogIndex() {
        if (pooling) {
            threadPoolTaskExecutor.execute(new CreateCatalogIndex(catalogIndexer));
        } else {
            new CreateCatalogIndex(catalogIndexer).run();
        }
    }

    /**
     * Запускает задачу на добавление (обновление) раздела каталога в поисковый
     * индекс каталога
     */
    public void addCatalogSectionIndex(CatalogItem sectionItem) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new AddSectionIndex(catalogIndexer, sectionItem));
        } else {
            new AddSectionIndex(catalogIndexer, sectionItem).run();
        }
    }

    /**
     * Запускает задачу на добавление (обновление) категории каталога в поисковый
     * индекс каталога
     */
    public void addCatalogCategoryIndex(CatalogItem categoryItem) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new AddCategoryIndex(catalogIndexer, categoryItem));
        } else {
            new AddCategoryIndex(catalogIndexer, categoryItem).run();
        }
    }

    /**
     * Запускает задачу на удаление раздела каталога из поискового индекса
     * каталога
     */
    public void deleteCatalogSectionIndex(Integer sectionId) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new DeleteSectionIndex(catalogIndexer, sectionId));
        } else {
            new DeleteSectionIndex(catalogIndexer, sectionId).run();
        }
    }

    /**
     * Запускает задачу на удаление категории каталога из поискового индекса
     * каталога
     */
    public void deleteCatalogCategoryIndex(Integer categoryId) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new DeleteCategoryIndex(catalogIndexer, categoryId));
        } else {
            new DeleteCategoryIndex(catalogIndexer, categoryId).run();
        }
    }

    /**
     * Запускает задачу на обновление раздела каталога в поисковом индексе
     * каталога
     */
    public void updateCatalogSectionIndex(CatalogItem sectionItem) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new UpdateSectionIndex(catalogIndexer, sectionItem));
        } else {
            new UpdateSectionIndex(catalogIndexer, sectionItem).run();
        }
    }

    /**
     * Запускает задачу на обновление категории каталога в поисковом индексе
     * каталога
     */
    public void updateCatalogCategoryIndex(CatalogItem categoryItem) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new UpdateCategoryIndex(catalogIndexer, categoryItem));
        } else {
            new UpdateCategoryIndex(catalogIndexer, categoryItem).run();
        }
    }

    /**
     * Запускает задачу на добавление (обновление) товарного предложения в поисковом
     * индексе каталога
     */
    public void addCatalogOfferIndex(Offer offer) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new AddOfferIndex(catalogIndexer, offer));
        } else {
            new AddOfferIndex(catalogIndexer, offer).run();
        }
    }

    /**
     * Запускает задачу на обновление товарного предложения в поисковом индексе
     * каталога
     */
    public void updateCatalogOfferIndex(Offer offer) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new UpdateOfferIndex(catalogIndexer, offer));
        } else {
            new UpdateOfferIndex(catalogIndexer, offer).run();
        }
    }

    /**
     * Запускает задачу на обновление товарных предложений компании в поисковом индексе
     * каталога
     */
    public void updateCatalogCompanyOfferIndex(Company company) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new UpdateCompanyOfferIndex(catalogIndexer, company));
        } else {
            new UpdateCompanyOfferIndex(catalogIndexer, company).run();
        }
    }

    /**
     * Запускает задачу на удаление товарного предложения из поискового индекса
     * каталога
     */
    public void deleteCatalogOfferIndex(Offer offer) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new DeleteOfferIndex(catalogIndexer, offer));
        } else {
            new DeleteOfferIndex(catalogIndexer, offer).run();
        }
    }

    /**
     * Запускает задачу на добавление (обновление) модели в поисковый
     * индекс каталога
     */
    public void addCatalogModelIndex(Model model) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new AddModelIndex(catalogIndexer, model));
        } else {
            new AddModelIndex(catalogIndexer, model).run();
        }
    }

    /**
     * Запускает задачу на обновление модели в поисковом индексе
     * каталога
     */
    public void updateCatalogModelIndex(Model model) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new UpdateModelIndex(catalogIndexer, model));
        } else {
            new UpdateModelIndex(catalogIndexer, model).run();
        }
    }

    /**
     * Запускает задачу на удаление модели из поискового индекса
     * каталога
     */
    public void deleteCatalogModelIndex(Model model) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new DeleteModelIndex(catalogIndexer, model));
        } else {
            new DeleteModelIndex(catalogIndexer, model).run();
        }
    }

    /**
     * Запускает задачу на добавление (обновление) бренда в поисковый
     * индекс каталога
     */
    public void addCatalogBrandIndex(Brand brand) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new AddBrandIndex(catalogIndexer, brand));
        } else {
            new AddBrandIndex(catalogIndexer, brand).run();
        }
    }

    /**
     * Запускает задачу на обновление бренда в поисковом индексе
     * каталога
     */
    public void updateCatalogBrandIndex(Brand brand) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new UpdateBrandIndex(catalogIndexer, brand));
        } else {
            new UpdateBrandIndex(catalogIndexer, brand).run();
        }
    }

    /**
     * Запускает задачу на удаление бренда из поискового индекса
     * каталога
     */
    public void deleteCatalogBrandIndex(Brand brand) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new DeleteBrandIndex(catalogIndexer, brand));
        } else {
            new DeleteBrandIndex(catalogIndexer, brand).run();
        }
    }

    /**
     * Запускает задачу на создание поискового индекса компаний
     */
    public void createCompanyIndex() {
        if (pooling) {
            threadPoolTaskExecutor.execute(new CreateCompanyIndex(companyIndexer));
        } else {
            new CreateCompanyIndex(companyIndexer).run();
        }
    }

    /**
     * Запускает задачу на добавление (обновление) товарного предложения в поисковом
     * индексе компаний
     */
    public void addCompanyOfferIndex(Offer offer) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new AddCompanyOfferIndex(companyIndexer, offer));
        } else {
            new AddCompanyOfferIndex(companyIndexer, offer).run();
        }
    }

    /**
     * Запускает задачу на обновление товарного предложения в поисковом индексе
     * компаний
     */
    public void updateCompanyOfferIndex(Offer offer) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new ru.nullpointer.storefront.service.search.company.tasks.UpdateCompanyOfferIndex(companyIndexer, offer));
        } else {
            new ru.nullpointer.storefront.service.search.company.tasks.UpdateCompanyOfferIndex(companyIndexer, offer).run();
        }
    }

    /**
     * Запускает задачу на удаление товарного предложения из поискового индекса
     * компаний
     */
    public void deleteCompanyOfferIndex(Offer offer) {
        if (pooling) {
            threadPoolTaskExecutor.execute(new DeleteCompanyOfferIndex(companyIndexer, offer));
        } else {
            new DeleteCompanyOfferIndex(companyIndexer, offer).run();
        }
    }

}
