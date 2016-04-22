package arbitrage;

import arbitrage.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by nguni52 on 16/04/22.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    private CurrencyService currencyService;

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String base;
        try {
            base = args[0];
        } catch(IndexOutOfBoundsException ioobe) {
            base = "USD";
        }

        currencyService.findMaximizedProfits(base);
    }
}
