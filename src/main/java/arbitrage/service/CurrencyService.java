package arbitrage.service;

import arbitrage.model.CurrencyExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by nguni52 on 16/04/22.
 */
@Service
public class CurrencyService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private List<String> linkedFields = new LinkedList<>();
    private CurrencyExchange currencyExchange;
    private ConcurrentSkipListMap<String, Double> path = new ConcurrentSkipListMap<>();

    public void findMaximizedProfits(String base) throws URISyntaxException, IOException {
        log.info("BASE IS ::: " + base);
        RestTemplate restTemplate = new RestTemplate();
        currencyExchange = restTemplate.getForObject("http://api.fixer.io/latest?base={currency}", CurrencyExchange.class, base);
//        log.info(String.valueOf(currencyExchange.getRates().getAUD()));
        log.info(currencyExchange.toString());

        getKeys();
        getLongestPath();
    }

    public void getKeys() {
        Set keys = currencyExchange.getRates().keySet();
        linkedFields.addAll(keys);
    }

    /**
     *
     */
    private void getLongestPath() {
        log.info(linkedFields.toString());
        // get the currency with smallest exchange rate
        ConcurrentSkipListMap<String, Double> smallest = getSmallest();
        log.info("smallest is: " + smallest);
        path.put(smallest.firstEntry().getKey(), smallest.firstEntry().getValue());

        // get the currency with largest exchange rate, based on the smallest exchange rate
        ConcurrentSkipListMap<String, Double> largest = getLargest(smallest);
        log.info("\n\n\nlargest is: " + largest);
        //path.put(largest.firstEntry().getKey(), largest.firstEntry().getValue());
    }

    /**
     * This method returns the smallest exchange rate the base currency has
     *
     * @return
     */
    private ConcurrentSkipListMap<String, Double> getSmallest() {
        ConcurrentSkipListMap<String, Double> smallest = new ConcurrentSkipListMap<>();
        smallest.put("smallest", (double) Integer.MAX_VALUE);
        for (String field : linkedFields) {
            Double rate = currencyExchange.getRates().get(field);
            log.info(String.valueOf(rate));
            if (smallest.firstEntry().getValue().compareTo(rate) > 0) {
                smallest.clear();
                smallest.put(field, rate);
            }
        }

        return smallest;
    }

    /**
     * This method returns the largest number of the cycle.
     * <p>
     * Top achieve this, go through each of the rates. Compare it to the base currency, and if it is larger, set this
     * as the current largest rate that maximizes profit, then continue until the largest exchange rate,
     * which is larger than the base currency is found.
     *
     * @return
     * @param smallest
     */
    private ConcurrentSkipListMap<String, Double> getLargest(ConcurrentSkipListMap<String, Double> smallest) {
        ConcurrentSkipListMap<String, Double> largest = new ConcurrentSkipListMap<>();
        largest.put("largest", (double) Integer.MIN_VALUE);

        RestTemplate restTemplate = new RestTemplate();
        CurrencyExchange tempCurrencyExchange = restTemplate.getForObject("http://api.fixer.io/latest?base={currency}",
                CurrencyExchange.class, smallest.firstEntry().getKey());
        log.info(tempCurrencyExchange.toString());

        for(String rateName : linkedFields) {
            Double rateValue = tempCurrencyExchange.getRates().get(rateName);
            Double originalRateValue = currencyExchange.getRates().get(rateName);
            log.info("Exchange rate for: " + rateName + "::" + String.valueOf(rateValue + ":::" + originalRateValue));

            try {
                if (rateValue.compareTo(originalRateValue) > 0) {

                    largest.clear();
                    largest.put(rateName, rateValue);
                }
            } catch(NullPointerException npe) {
                log.debug("null pointer for getting rate value");
            }
        }

        return largest;
    }

}
