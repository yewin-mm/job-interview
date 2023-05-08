package pers.yewin.jobinterview.uitl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static helper.ValidationUtil.isEmptyString;
import static pers.yewin.jobinterview.uitl.ConstantUtil.*;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.uitl
 */

public class SalaryCalculator {

    private SalaryCalculator(){}

    /**
     * There are around more than 120 difference format under salary column in your input file. Total salary value is 3777.
     * Difference currencies unit is not hard to control (there are total of 48 difference currency format)
     * But the format are unstructured, and it's hard to control. Anmd some of them have character encoding issue.
     * So, I used this class to control that kinds of unstructured random format.

     * Eg1. - difference format for same currency: USD 1000, 1000 USD, $1000 USD, 1000$, 1000USD, 100kÂ£, Â£37,000 GBP,
     * Eg2. - character encoding issues:  â‚¬30,000, ä¸‡, Â¥144000â‰ˆ$20921
     * Eg3. - same values showed with two values and difference equal approximate sign: 60000PLN (circa 16000$), 90000 BRL / 22500 USD, 99KBRL ~27.2KUSD by today's rate, 336000 RUB (~4800USD)
     * Eg4. - unknown currency: g23, ~70000?
     * Eg5. - without number: HAVE, Food, Rrr, no, a trap a day makes the boner go away
     * Eg6. - per with difference format: 60k/yr, 31/ hr, 40 hr

     * For more details, please reference to resources/jobinfo-data/sample-differences-format-data.txt

     * Logic - Take as BAHT if not specific and BAHT will be default currency in this system
     *       - If we found two approximate equal two values, take one value and USD is first priority
     *       - Take BAHT if we found unknown currency like g23 (will be 23 BAHT)
     *       - Take BAHT as 0.0 if we can't find numeric value (eg. Have, no, etc text)
     */

    public static double calculateSalary(String salary, Map<String, Double> currencyExchange, Set<String> currencyList){

        Map<String, String> currencyAndAmount = getCurrencyAndAmount(salary, currencyList);

        for (Map.Entry<String, String> entry : currencyAndAmount.entrySet()) {
            if (currencyExchange.containsKey(entry.getKey())) {
                double exchangeRate = currencyExchange.get(entry.getKey());
                double salaryValue = Double.parseDouble(entry.getValue());

                double test = salaryValue * exchangeRate;
                return test;
            }
        }
        return 0.0;
    }

    /**
     * This method get currency and amount as key value pair.
     */
    private static Map<String, String> getCurrencyAndAmount(String salary, Set<String> currencyList){
        Map<String, String> currencyAndAmount = new HashMap<>();
        if(isEmptyString(salary, "Salary")) {
            currencyAndAmount.put(BAHT, ZERO); // If salary is null or empty, add default value.
            return currencyAndAmount;
        }else {
            String currency = getCurrency(salary, currencyList);

            if(isEmptyString(currency)){
                currencyAndAmount.put(BAHT, ZERO); // If unknown currency, we return as default value.
                return currencyAndAmount;
            }

            List<String> duplicateList = getDuplicateList(salary, currencyList);
            currencyAndAmount = checkListAndProcess(duplicateList, salary, currency, currencyList);

        }

        return currencyAndAmount;

    }


    /**
     * This method is the main method to check list and do as per list size and update the final values
     */
    private static Map<String, String> checkListAndProcess(List<String> duplicateList, String salary, String currency, Set<String> currencyList){
        Map<String, String> currencyAndAmount = new HashMap<>();
        if (duplicateList.isEmpty()) {
            currencyAndAmount.put(BAHT, ZERO); // If we can't find the number value, add default value.

        } else if (duplicateList.size() == 1) {
            String amt = perCalculate(salary, prepareNumeric(duplicateList.get(0)));
            currencyAndAmount.put(currency, amt);

        } else {

            if (duplicateList.size() == 2 && salary.toLowerCase().contains(BETWEEN) || salary.contains(DASH)) {
                String amt = betweenCalculate(prepareNumeric(duplicateList.get(0)), prepareNumeric(duplicateList.get(1)));
                amt = perCalculate(salary, amt);
                currencyAndAmount.put(currency, amt);
            } else {

                // if more than one amount in salary and not have between sign,
                // it will be two currencies value, So I will process with one amount value
                String[] valueAndCurr = getValueFromTwoValues(salary, currencyList);
                if (!isEmptyString(valueAndCurr[1])) {
                    String amt = perCalculate(salary, prepareNumeric(valueAndCurr[1]));
                    currencyAndAmount.put(valueAndCurr[0], amt); // update currency
                } else
                    currencyAndAmount.put(BAHT, ZERO); // If we can't find the usd, add default value.
            }
        }
        return currencyAndAmount;
    }


    /**
     * This method to get currency from given input by finding pre-defined currencies list
     * If we can't find that, we assume that as BAHT and that will be default currency in this system.
     * Here, I used contains to find the currency.
     */
    public static String getCurrency(String salary, Set<String> currencyList){
        boolean foundOther = false;
        boolean foundDollar = false;
        String currency = BAHT; // if we don't find the currency, we will define as default currency "BAHT"
        for(String s: currencyList){
            // R$ is Brazilian currency, and It's hard to handle for only R keywords including $.
            if(salary.toUpperCase().contains(BRAZILIAN_DOLLAR)){
                return BRAZILIAN_DOLLAR;
            }
            if(salary.contains(DOLLAR)){ // check for $
                foundDollar = true;
                String removeDollar = salary.replace(DOLLAR, "");
                if(removeDollar.toUpperCase().contains(s)){ // check for other currency eg. AUD$
                    foundOther = true;
                    currency = s.toUpperCase();
                    break;
                }
            }else if(salary.toUpperCase().contains(s)){
                return s;
            }
        }

        if(foundOther)
            return currency;
        if(foundDollar)
            return DOLLAR;

        return currency;

    }

    /**
     * This method to get amount as list and to check more than one amount
     */
    private static List<String> getDuplicateList(String salary, Set<String> currencyList){
        // split by space and add K if K was behind value after space
        List<String> splitList = getSplit(salary);
        String amt;
        List<String> duplicate = new ArrayList<>();
        for(String s : splitList){
            // get the numeric value
            amt = extractValue(s, currencyList);
            if(!isEmptyString(amt)){
                duplicate.add(amt);
            }
        }
        return duplicate;
    }


    /**
     * This method is for splitting space, ~, â‰ˆ, ≈
     */
    private static List<String> getSplit(String salary){


        salary = salary.replace(COMMA, ""); // remove comma
        salary = combineContiguousNumbers(salary); // combine contiguous number if separated by space.

        String[] parts = salary.split(SPLIT_REGEX);
        List<String> outputList = new ArrayList<>();

        int i = 0;
        while (i < parts.length) {
            String part = parts[i];
            if (i < parts.length - 1) {
                String nextPart = parts[i + 1];
                if (nextPart.toLowerCase().startsWith(K) || nextPart.startsWith(WAN) || nextPart.startsWith(WAN_SIGN) || nextPart.toUpperCase().startsWith(M)) {
                    part += " " + nextPart;
                    i++;
                }
            }
            outputList.add(part);
            i++;
        }
        return outputList;
    }

    /**
     * This method is for combine contiguous number, Eg- 60 000 to 60000
     */
    private static String combineContiguousNumbers(String input) {
        String[] words = input.split(SPACE_REGEX);
        StringBuilder output = new StringBuilder(words[0]);

        for (int i = 1; i < words.length; i++) {
            String currentWord = words[i];
            String previousWord = words[i-1];

            String preLastChar = previousWord.substring(previousWord.length()-1);
            String currFirstChar = currentWord.substring(0,1);


            if (preLastChar.matches(DIGIT_DOT_REGEX) && currFirstChar.matches(DIGIT_DOT_REGEX)) {
                output.append(currentWord);
            } else {
                output.append(" ").append(currentWord);
            }
        }

        return output.toString();
    }

    /**
     * This method is to extract actual amount and including k and other values like Millions
     */
    private static String extractValue(String input, Set<String> currencyList) {

        Pattern p = Pattern.compile(EXTRACT_NUMERIC_REGEX);
        Matcher m = p.matcher(input);
        if (m.find()) {
            String value = m.group(1);
            if (value.endsWith(DOT)) {
                value += NORMAL_ZERO;
            }
            String k = m.group(4);
            if (k != null && k.equalsIgnoreCase(K)) {
                value += K;
            }

            value = noticeSigns(value, m, input, currencyList);

            return value;
        }
        return null;
    }

    /**
     * This method to notice sign and prepare final format.
     */
    private static String noticeSigns(String value, Matcher matcher, String input, Set<String> currencyList){
        String tenThousand = matcher.group(6);
        if(tenThousand==null){
            tenThousand = matcher.group(8);
        }
        String million = matcher.group(10);
        if (tenThousand != null) {
            switch (tenThousand.toLowerCase()) {
                case WAN: // "ä¸‡" is chinese character for thousands
                case WAN_ESCAPE:
                case WAN_ESCAPE1:  // ¸‡ is escape in String suffix, so we assume ä as thousands
                case WAN_SIGN:
                    value = value + TEN_THOUSANDS;
                    break;

                default:

            }
        }

        if (million != null && million.equalsIgnoreCase(M)) {
            value = findMillion(input, value, currencyList);
        }

        return value;
    }


    /**
     * This method to notice millions. but sometime, some currency is start with M too (eg. MMK, MYR), So, we need to find out currency or just M or Millions
     */
    private static String findMillion(String input, String value, Set<String> currencyList){

        boolean foundMCurr = false;
        for(String curr : currencyList){
            if(curr.startsWith(M) && input.contains(curr)) {
                foundMCurr = true;
                input = input.replace(curr, "");
                if (input.toUpperCase().contains(M)) {
                    value = new StringBuilder(value).append(M).toString();
                    break;
                }
            }

        }
        if(!foundMCurr){
            value = value + M;
        }
        return value;
    }


    /**
     * This method to prepare amount when amount was include K or Million, etc...
     */
    private static String prepareNumeric(String input){
        double value = 0.0;
        boolean foundSymbol = false;
        if(isEmptyString(input)){
            return ZERO;
        }
        double amount = Double.parseDouble(input.replaceAll("[^\\d.]+", ""));
        // for k
        if (input.contains(K)) {
            value = amount * 1000;
            foundSymbol=true;
        }
        // for chinese 10000 wan
        if (input.contains(TEN_THOUSANDS)) {
            if(foundSymbol)
                value = value * 10000;
            else value = amount * 10000;
            foundSymbol=true;
        }
        // for millions
        if (input.toUpperCase().contains("M")) {
            if(foundSymbol)
                value = value * 1000000;
            else value = amount * 1000000;
            foundSymbol=true;
        }

        if(!foundSymbol)
            return input;
        else return String.valueOf(value);
    }


    /**
     * This method to find per and calculate as per unit.
     */
    private static String perCalculate(String salary, String value){
        double salaryValue = Double.parseDouble(value);
        String letter = extractLetters(salary);
        String[] letters = letter.split(SPACE_REGEX);
        boolean conditionMet = false;
        for(String word : letters){
            if(word.equalsIgnoreCase(HOURS) || word.equalsIgnoreCase(HOUR) || word.equalsIgnoreCase(HRS) || word.equalsIgnoreCase(HR)){
                salaryValue = getDayByHour(salaryValue);
                salaryValue = getMonthByDay(salaryValue);
                conditionMet = true;
            }
            else if(word.equalsIgnoreCase(DAYS) || word.equalsIgnoreCase(DAY)){
                salaryValue = getMonthByDay(salaryValue);
                conditionMet = true;

            }
            else if(word.equalsIgnoreCase(YEARS) || word.equalsIgnoreCase(YEAR) || word.equalsIgnoreCase(YRS)
                    || word.equalsIgnoreCase(YR)){
                salaryValue = getMonthByYear(salaryValue);
                conditionMet = true;
            }
            if (conditionMet) {
                break;
            }
        }

        return String.valueOf(salaryValue);
    }

    /**
     * This method to extract letter only
     */
    private static String extractLetters(String input) {
        Pattern pattern = Pattern.compile(EXTRACT_LETTER_REGEX);
        Matcher matcher = pattern.matcher(input);

        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(matcher.group()).append(" ");
        }

        return builder.toString().trim();
    }


    /**
     * This method to calculate between values
     */
    private static String betweenCalculate(String firstValue, String secondValue){
        double firstDoubleValue = Double.parseDouble(firstValue);
        double secondDoubleValue = Double.parseDouble(secondValue);
        double between = (firstDoubleValue + secondDoubleValue) / 2;
        return String.valueOf(between);
    }


    private static double getDayByHour(double value){
        return value * 8; // approximate working hour in a day.
    }

    private static double getMonthByDay(double value){
        return value * 22; // approximate working day in a month.
    }

    private static double getMonthByYear(double value){
        return value / 12; // 12 months in a year
    }


    /**
     * Below method is take one value if the salary is described as two currency values.
     */
    private static String [] getValueFromTwoValues(String salary, Set<String> currencyList) {

        salary = combineContiguousNumbers(salary);

        // below regex is find the USD(usd) or $ in given salary and take the value from near that currency.

        String value="";

        String [] valuesAndCurr = new String[2];

        Pattern pattern = Pattern.compile(VALUE_NEAR_USD_REGEX);
        Matcher matcher = pattern.matcher(salary);

        if (matcher.find()) {
            valuesAndCurr[0]=USD;

            String kValue;
            if(matcher.group(1)!=null)
                value = matcher.group(1);
            else value = matcher.group(3);

            if(matcher.group(2)!=null)
                kValue = matcher.group(2);
            else kValue = matcher.group(4);


            if (kValue != null) {
                value += K; // add k if k was there.
                value = prepareNumeric(value);
            }

            if(value==null) // to cover null pointer case
                value ="";


        } else {
            // if we can't find USD, we will take other currency
            String result = getFirstValue(salary);
            String currency = getCurrency(result, currencyList);

            valuesAndCurr[0]=currency;

            value = extractValue(result, currencyList);
            if(isEmptyString(value)){
                value = NORMAL_ZERO;
            }
            else {
                value = prepareNumeric(value);
            }

        }
        valuesAndCurr[1]=value;
        return valuesAndCurr;
    }

    private static String getFirstValue(String salary) {

        int index = -1;

        int spaceIndex = salary.indexOf(' ');
        if (spaceIndex != -1 && spaceIndex + 1 < salary.length()) {
            for (int i = spaceIndex + 1; i < salary.length(); i++) {
                if (Character.isDigit(salary.charAt(i))) {
                    index = i;
                    break;
                }
            }
        }

        if (index != -1) {
            return salary.substring(0, index);
        }

        return "";

    }

}
