package pers.yewin.jobinterview.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;

import static helper.ValidationUtil.isEmptyString;
import static pers.yewin.jobinterview.uitl.ConstantUtil.*;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.config
 */


@Component
public class PropertiesLoader {

    /**
     * This class is to load application properties values and all were dynamic.
     * If you have new currency, just add in application properties, and it will be read by this class.
     */


    @Autowired
    Environment environment;

    private final Set<String> currencyList = new HashSet<>();

    public Set<String> getCurrencyList() {
        return currencyList;
    }

    private final Map<String, Double> currencyExchange = new HashMap<>();
    public Map<String, Double> getCurrencyExchange() {
        return currencyExchange;
    }

    public void cache(){
        Double exchangeRate=1.0;
        String actualCur = "BAHT";
        currencyExchange.put(actualCur, exchangeRate); // BAHT will be default currency in this system
        currencyList.add(actualCur);

        String[] propCurrencyList = Objects.requireNonNull(environment.getProperty(INITIAL + DOT + ALL)).split(COMMA_REGEX);
        for (String currency : propCurrencyList) {
            String [] values = currency.split(EQUAL_REGEX);

            String cur = values[0];

            if(!isEmptyString(cur)){
                actualCur = cur;

                if(values.length>1){
                    exchangeRate = Double.parseDouble(values[1]);
                }

                currencyExchange.put(actualCur.toUpperCase(), exchangeRate);
                currencyList.add(actualCur.toUpperCase());

                /** dynamic reading subnames fields as per currency formal name. */
                String subName = environment.getProperty(INITIAL + DOT + actualCur + SUB_NAMES);
                if(!isEmptyString(subName)){
                    String [] subNames = subName.split(DASH_REGEX);
                    for(String name : subNames){
                        currencyExchange.put(name.toUpperCase(), exchangeRate);
                        currencyList.add(name.toUpperCase());
                    }

                }
            }

        }

        // some special characters are escape while reading, So, I need to put as manual
        currencyExchange.put("₹",0.41); // INR
        currencyExchange.put("¥",0.25); // jpy
        currencyExchange.put("DKKÂ",4.99); // DKK
        currencyExchange.put("SEKÂ",3.30); // SEK
        currencyExchange.put("HUFÂ",0.1); // HUF
        currencyExchange.put("PKRÂ",0.12); // PKR
        currencyExchange.put("Â£",42.62); // GBP
        currencyExchange.put("£",42.62); // GBP
        currencyExchange.put("â‚¬",37.80); // EUR
        currencyExchange.put("€",37.80); // EUR
        currencyExchange.put("ZARÂ",1.85); // ZAR
        currencyExchange.put("NOKÂ",3.19); // NOK
        currencyExchange.put("CHFÂ",37.58); // CHF

        currencyList.add("₹");
        currencyList.add("¥");
        currencyList.add("DKKÂ");
        currencyList.add("SEKÂ");
        currencyList.add("HUFÂ");
        currencyList.add("PKRÂ");
        currencyList.add("Â£");
        currencyList.add("£");
        currencyList.add("â‚¬");
        currencyList.add("€");
        currencyList.add("ZARÂ");
        currencyList.add("NOKÂ");
        currencyList.add("CHFÂ");

    }
}
