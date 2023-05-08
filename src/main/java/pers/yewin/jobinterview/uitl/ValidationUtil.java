package pers.yewin.jobinterview.uitl;

import pers.yewin.jobinterview.model.pojo.OrderType;
import pers.yewin.jobinterview.model.pojo.SortBy;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.uitl
 */

public class ValidationUtil {

    private ValidationUtil(){}

    public static boolean isValidSortKeyValue(String [] sortKeyValue){
        if(sortKeyValue.length != 2) return false;
        if(!isValidFields(sortKeyValue[0])) return false;
        return isValidOrder(sortKeyValue[1]);
    }

    public static boolean isValidFields(String value){
        if(value.equalsIgnoreCase(SortBy.JOB_TITLE.getValue()) || value.equalsIgnoreCase(SortBy.JOB_TITLE_FIELD.getValue()) )
            return true;
        else if(value.equalsIgnoreCase(SortBy.SALARY.getValue()))
            return true;
        else return value.equalsIgnoreCase(SortBy.GENDER.getValue());
    }

    public static boolean isValidOrder(String value){
        return value.equalsIgnoreCase(OrderType.ASC.getValue()) || value.equalsIgnoreCase(OrderType.DESC.getValue());
    }
}
