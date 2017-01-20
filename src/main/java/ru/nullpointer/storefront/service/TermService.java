package ru.nullpointer.storefront.service;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.TermDAO;
import ru.nullpointer.storefront.domain.Term;
import ru.nullpointer.storefront.domain.support.ResultGroup;
import ru.nullpointer.storefront.service.support.AlphabetResultBuilder;
import ru.nullpointer.storefront.service.support.FieldUtils;

/**
 * @author ankostyuk
 */
@Service
public class TermService {

    @Resource
    private TermDAO termDAO;

    /**
     * Возвращает количество терминов
     */
    @Transactional(readOnly = true)
    public int getTermCount() {
        return termDAO.getTermCount();
    }

    /**
     * Возвращает термин по идентификатору
     * @param id идентификатор термина
     * @return термин или <code>null</code> если термина с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Term getTermById(Integer termId) {
        return termDAO.getTermById(termId);
    }

    /**
     * Возвращает список первых букв наименований терминов без учета регистра
     * @return
     */
    @Transactional(readOnly = true)
    public List<String> getTermToc() {
        List<String> prefixList = termDAO.getTermNamePrefixList();

        return new TermResultBuilder().buildToc(prefixList);
    }

    /**
     * Возвращает список терминов сгруппированных по первой букве наименования без учета регистра
     * @return
     */
    @Transactional(readOnly = true)
    public List<ResultGroup<Term>> getTermGroupList() {
        List<Term> termList = termDAO.getTermList();

        return new TermResultBuilder().buildResult(termList);
    }

    /**
     * Возвращает список терминов по префиксу наименования без учета регистра.
     * Сортировка в алфавитном порядке.
     * @return
     * @throws IllegalArgumentException если <code>prefix</code> равен <code>null</code> или пустой строке
     */
    @Transactional(readOnly = true)
    public List<Term> getTermListByPrefix(String prefix) {
        Assert.hasText(prefix);
        return termDAO.getTermListByPrefix(prefix);
    }

    /**
     * Возвращает список терминов имеющих начальным символом в наименовании цифру.
     * Сортировка в алфавитном порядке.
     * @return
     */
    @Transactional(readOnly = true)
    public List<Term> getTermListByDigitPrefix() {
        return termDAO.getTermListByDigitPrefix();
    }

    /**
     * Добавляет термин. Устанавливает термину полученный <code>id</code>.
     * @param term термин
     * @throws IllegalArgumentException если <code>term</code> равен <code>null</code>
     */
    @Secured("ROLE_MANAGER_DICTIONARY")
    @Transactional
    public void storeTerm(Term term) {
        checkTerm(term);

        termDAO.insert(term);
    }

    /**
     * Обновляет информацию о термине.
     * Не обновляет идентификатор.
     * @param term термин
     * @throws IllegalArgumentException если <code>term</code> равен <code>null</code>
     * или <code>id</code> термина равен <code>null</code>
     */
    @Secured("ROLE_MANAGER_DICTIONARY")
    @Transactional
    public void updateTermInfo(Term term) {
        checkTerm(term);

        termDAO.updateInfo(term);
    }

    /**
     * Удаляет термин.
     * @param id идентификатор термина
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Secured("ROLE_MANAGER_DICTIONARY")
    @Transactional
    public void deleteTerm(Term term) {
        Assert.notNull(term);
        termDAO.delete(term.getId());
    }

    private void checkTerm(Term term) {
        Assert.notNull(term);
        Assert.hasText(term.getName());
        Assert.hasText(term.getDescription());

        FieldUtils.nullifyIfEmpty(term, "source");
    }

    private static class TermResultBuilder extends AlphabetResultBuilder<Term> {

        @Override
        public String getGroupValue(Term term) {
            return term.getName();
        }
    }
}
