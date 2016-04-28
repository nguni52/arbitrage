package arbitrage.service;

import arbitrage.model.CurrencyExchange;
import arbitrage.model.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by nguni52 on 16/04/22.
 */
@Service
public class CurrencyService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private List<String> linkedFields = new LinkedList<>();
    private CurrencyExchange currencyExchange;
    private LinkedList<Path> pathList = new LinkedList<>();

    /**
     * Find maximised profits based on a base currency that one has selected to check
     *
     * @param base - Base currency
     * @throws URISyntaxException
     * @throws IOException
     */
    public void findMaximizedProfits(String base) throws URISyntaxException, IOException {
        log.info("\n\n\n\n\n");
        log.info("***********************************************");
        log.info("************ BEGIN PROCESS PATH ***************");
        log.info("***********************************************");
        log.info("\n");
        final long t0 = System.currentTimeMillis();
        RestTemplate restTemplate = new RestTemplate();
        currencyExchange = restTemplate.getForObject("http://api.fixer.io/latest?base={currency}", CurrencyExchange.class, base);
        pathList.add(new Path(base, (double) 1.0f));
        getKeys();
        getLongestPath();
        printPath();
        final long t1 = System.currentTimeMillis();
        final long elapsedTimeMillis = t1 - t0;
        log.info("Processing Time: " + elapsedTimeMillis + "ms\n");
        log.info("************ END PROCESS PATH *****************");
        log.info("***********************************************");
    }

    public void getKeys() {
        Set keys = currencyExchange.getRates().keySet();
        linkedFields.addAll(keys);
    }

    /**
     * Get the longest path based on the base currency. This is done by checking the lowest exchange rate, and then using
     * that exchange rate, get the largest exchange rate for each of the currencies related to that lowest exchange rate.
     */
    private void getLongestPath() {
        // get the currency with smallest exchange rate
        ConcurrentSkipListMap<String, Double> smallest = getSmallest();
        pathList.add(new Path(smallest.firstEntry().getKey(), smallest.firstEntry().getValue()));

        // get the currency with largest exchange rate, based on the smallest exchange rate
        ConcurrentSkipListMap<String, Double> largest = getLargest(smallest);
        pathList.add(new Path(largest.firstEntry().getKey(), largest.firstEntry().getValue()));
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
            if (smallest.firstEntry().getValue().compareTo(rate) > 0) {
                smallest.clear();
                smallest.put(field, rate);
            }
        }

        return smallest;
    }

    /**
     * This method returns the largest exchange rate of the cycle.
     * <p>
     * Top achieve this, go through each of the rates. Compare it to the base currency, and if it is larger, set this
     * as the current largest rate that maximizes profit, then continue until the largest exchange rate,
     * which is larger than the base currency is found.
     *
     * @param smallest -  the lowest exchange rate with respect to the base currency.
     * @return
     */
    private ConcurrentSkipListMap<String, Double> getLargest(ConcurrentSkipListMap<String, Double> smallest) {
        ConcurrentSkipListMap<String, Double> largest = new ConcurrentSkipListMap<>();
        largest.put("largest", (double) Integer.MIN_VALUE);

        RestTemplate restTemplate = new RestTemplate();
        CurrencyExchange tempCurrencyExchange = restTemplate.getForObject("http://api.fixer.io/latest?base={currency}",
                CurrencyExchange.class, smallest.firstEntry().getKey());

        for (String rateName : linkedFields) {
            Double rateValue = tempCurrencyExchange.getRates().get(rateName);
            Double originalRateValue = currencyExchange.getRates().get(rateName);

            try {
                if (rateValue.compareTo(originalRateValue) > 0) {
                    // is this rate value bigger than the current largest rate value
                    // change rate value to base rate value
                    if ("largest".equalsIgnoreCase(largest.firstEntry().getKey())) {
                        largest.clear();
                        largest.put(rateName, rateValue);
                    } else {
                        Double currentRateBaseValue = rateValue / originalRateValue;
                        Double largestRateBaseValue = largest.firstEntry().getValue() / currencyExchange.getRates().get(largest.firstEntry().getKey());
                        if (currentRateBaseValue.compareTo(largestRateBaseValue) > 0) {
                            largest.clear();
                            largest.put(rateName, rateValue);
                        }
                    }
                }
            } catch (NullPointerException npe) {
                log.debug("null pointer for getting rate value");
            }
        }

        return largest;
    }

    private void printPath() {
        int pathSize = pathList.size();
        StringBuilder sb = new StringBuilder();
        for (Path path : pathList) {
            sb.append(path.getBase() + " (" + path.getRate() + ")" + " => ");
        }
        Double lastElementRate = currencyExchange.getRates().get(pathList.get(pathSize-1).getBase()) / pathList.get(pathSize-1).getRate();
//        log.info(pathList.get(pathSize-1).getBase() + "::" + pathList.get(pathSize-1).getRate().toString());
        sb.append(lastElementRate.toString() + " " + currencyExchange.getBase() + " > 1 " + currencyExchange.getBase());
        log.info(sb.toString());
    }
}
