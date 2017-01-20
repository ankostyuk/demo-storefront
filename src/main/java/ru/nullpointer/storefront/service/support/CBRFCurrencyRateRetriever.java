package ru.nullpointer.storefront.service.support;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import ru.nullpointer.storefront.domain.CurrencyRate;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class CBRFCurrencyRateRetriever implements CurrencyRateRetriever {

    private Logger logger = LoggerFactory.getLogger(CBRFCurrencyRateRetriever.class);
    //
    @Override
    public List<CurrencyRate> getRates(String defaultCurrency, List<String> currencyList, Date date) {
        Document doc = readXml(date);
        if (doc != null) {
            return parseDocument(doc, defaultCurrency, currencyList);
        }
        return Collections.emptyList();
    }

    private List<CurrencyRate> parseDocument(Document doc, String defaultCurrency, List<String> currencyList) {
        List<CurrencyRate> result = new ArrayList<CurrencyRate>();
        try {
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpath = xfactory.newXPath();
            for (String currency : currencyList) {
                Node currencyNode = (Node) xpath.evaluate("/ValCurs/Valute[CharCode='" + currency + "']", doc, XPathConstants.NODE);
                if (currencyNode != null) {
                    String nominal = (String) xpath.evaluate("Nominal", currencyNode, XPathConstants.STRING);
                    String value = (String) xpath.evaluate("Value", currencyNode, XPathConstants.STRING);

                    nominal = nominal.replace(',', '.');
                    value = value.replace(',', '.');

                    logger.debug("nominal: {} value: {}", new Object[]{nominal, value});

                    CurrencyRate currencyRate = new CurrencyRate();
                    currencyRate.setFromCurrency(currency);
                    currencyRate.setFromRate(new BigDecimal(nominal));
                    currencyRate.setToCurrency(defaultCurrency);
                    currencyRate.setToRate(new BigDecimal(value));
                    
                    result.add(currencyRate);
                } else {
                    logger.warn("currencyNode({}) is null", currency);
                }
            }
        } catch (XPathExpressionException ex) {
            logger.error("{}", ex);
        }
        return result;
    }

    private Document readXml(Date date) {
        Document document = null;
        try {
            String stringUrl = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + formatDate(date);
            logger.debug("xml url: {}", stringUrl);

            URL url = new URL(stringUrl);
            InputStream inputStream = url.openStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(inputStream);
            inputStream.close();
            return document;
        } catch (ParserConfigurationException ex) {
            logger.error("{}", ex);
        } catch (MalformedURLException ex) {
            logger.error("{}", ex);
        } catch (IOException ex) {
            logger.error("{}", ex);
            return null;
        } catch (SAXException ex) {
            logger.error("{}", ex);
        }
        return document;
    }

    private String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }
}
