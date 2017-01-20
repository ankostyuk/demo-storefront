package ru.nullpointer.storefront.dao;

import java.util.List;
import ru.nullpointer.storefront.domain.Term;

/**
 * @author ankostyuk
 */
public interface TermDAO {

    /**
     * Возвращает количество терминов
     */
    int getTermCount();

    /**
     * Возвращает термин по идентификатору
     * @param id идентификатор термина
     * @return термин или <code>null</code> если термина с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    Term getTermById(Integer id);

    /**
     * Возвращает список терминов.
     * @return
     */
    List<Term> getTermList();

    /**
     * Возвращает список префиксов наименований терминов без учета регистра
     * @return
     */
    List<String> getTermNamePrefixList();

    /**
     * Возвращает список терминов по префиксу наименования без учета регистра.
     * Сортировка в алфавитном порядке.
     * @return
     * @throws IllegalArgumentException если <code>prefix</code> равен <code>null</code>
     */
    List<Term> getTermListByPrefix(String prefix);

    /**
     * Возвращает список терминов имеющих начальным символом в наименовании цифру.
     * Сортировка в алфавитном порядке.
     * @return
     */
    List<Term> getTermListByDigitPrefix();

    /**
     * Добавляет термин. Устанавливает термину полученный <code>id</code>.
     * @param term термин
     * @throws IllegalArgumentException если <code>term</code> равен <code>null</code>
     */
    void insert(Term term);

    /**
     * Обновляет информацию о термине.
     * Не обновляет идентификатор.
     * @param term термин
     * @throws IllegalArgumentException если <code>term</code> равен <code>null</code>
     * или <code>id</code> термина равен <code>null</code>
     */
    void updateInfo(Term term);

    /**
     * Удаляет термин.
     * @param id идентификатор термина
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    void delete(Integer id);
}
