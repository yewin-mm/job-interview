package pers.yewin.jobinterview.uitl;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.uitl
 */

public class ConstantUtil {

    private ConstantUtil(){}

    public static final String COMMA_REGEX = "\\s*,\\s*";
    public static final String EQUAL_REGEX = "\\s*=\\s*";
    public static final String DASH_REGEX = "\\s*-\\s*";
    public static final String INITIAL = "app.currency";
    public static final String ALL = "all";
    public static final String SUB_NAMES = ".subnames";
    public static final String DOT = ".";

    public static final String STATIC_JOB = "staticJob";
    public static final String STATIC_TYPE = "static";
    public static final String MANUAL_JOB = "manualJob";
    public static final String MANUAL_TYPE = "manual";
    public static final String BAHT = "BAHT";
    public static final String ZERO = "0.0";
    public static final String NORMAL_ZERO = "0";
    public static final String DOLLAR = "$";
    public static final String USD = "USD";
    public static final String BRAZILIAN_DOLLAR = "R$";
    public static final String SPLIT_REGEX = "\\s*(\\s|â‰ˆ|≈|~|-)\\s*";
    public static final String SPACE_REGEX = "\\s+";
    public static final String DIGIT_DOT_REGEX = "^[0-9]*[.]?";
    public static final String EXTRACT_NUMERIC_REGEX = "([0-9]+(\\.?[0-9]*))(\\s+)?([kK])?(\\s+)?([ä¸‡])?(\\s+)?([万])?(\\s+)?([mM])?";
    public static final String EXTRACT_LETTER_REGEX = "[a-zA-Z]+";
    public static final String VALUE_NEAR_USD_REGEX = "(?i)(?:usd|\\$)\\s*(\\d+(?:\\.\\d+)?)\\s*(K)?\\b|\\b(\\d+(?:\\.\\d+)?)\\s*(K)?\\s*(?:usd|\\$)";
    public static final String COMMA = ",";
    public static final String M = "M";
    public static final String K = "k";
    public static final String KCAP = "K";
    public static final String WAN = "ä¸‡"; // ten thousands
    public static final String WAN_ESCAPE = "ä¸";
    public static final String WAN_ESCAPE1 = "ä";
    public static final String WAN_SIGN = "万"; // ten thousands
    public static final String TEN_THOUSANDS = "TENTS";
    public static final String HOURS = "hours";
    public static final String HOUR = "hour";
    public static final String HRS = "hrs";
    public static final String HR = "hr";
    public static final String DAYS = "days";
    public static final String DAY = "day";
    public static final String YEARS = "years";
    public static final String YEAR = "year";
    public static final String YRS = "yrs";
    public static final String YR = "yr";
    public static final String BETWEEN = "between";
    public static final String DASH = "-";
    public static final String JOB = "job";
    public static final String SALARY = "salary";
    public static final String GENDER = "gender";

    public static final String JOB_AND_SALARY_AND_GENDER = "jobAndSalaryAndGender";
    public static final String JOB_AND_SALARY = "jobAndSalary";
    public static final String JOB_AND_GENDER = "jobAndGender";
    public static final String SALARY_AND_GENDER = "salaryAndGender";
    public static final String UNKNOWN = "unknown";
    public static final String THAI_ZONE_ID = "Asia/Bangkok";
    public static final String SALARY_INVALID = "Salary is not valid format.";
    public static final String FILTER_INVALID = "At least one valid filter should be present. (job_title, salary, gender)";
    public static final String SORT_TYPE_INVALID = "Sort type is not valid. (should be ASC or DESC)";
    public static final String PAGE_INVALID = "Page is not valid format.";
    public static final String SIZE_INVALID = "Size is not valid format.";
}
