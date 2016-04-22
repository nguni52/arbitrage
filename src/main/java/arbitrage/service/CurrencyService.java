package arbitrage.service;

import arbitrage.model.CurrencyExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by nguni52 on 16/04/22.
 */
@Service
public class CurrencyService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    public void findMaximizedProfits(String base) {
        log.info("BASE IS ::: " + base);
        RestTemplate restTemplate = new RestTemplate();
        CurrencyExchange currencyExchange = restTemplate.getForObject("http://api.fixer.io/latest?base=USD", CurrencyExchange.class);
        log.info(currencyExchange.toString());
    }
}
