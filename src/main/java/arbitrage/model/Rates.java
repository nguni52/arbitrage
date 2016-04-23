package arbitrage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by nguni52 on 16/04/22.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class Rates implements Serializable {
    private float AUD;
    private float BGN;
    private float BRL;
    private float CAD;
    private float CHF;
    private float CNY;
    private float CZK;
    private float DKK;
    private float GBP;
    private float HKD;
    private float HRK;
    private float HUF;
    private float IDR;
    private float ILS;
    private float INR;
    private float JPY;
    private float KRW;
    private float MXN;
    private float MYR;
    private float NOK;
    private float NZD;
    private float PHP;
    private float PLN;
    private float RON;
    private float RUB;
    private float SEK;
    private float SGD;
    private float THB;
    private float TRY;
    private float USD;
    private float ZAR;
}
