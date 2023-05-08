package pers.yewin.jobinterview.uitl;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.uitl
 */

public class CommonUtil {
    CommonUtil(){}
    public static boolean isValidNumber(String value){
        try{
            Integer.parseInt(value);
            return true;
        }catch (NumberFormatException ne){
            return false;
        }
    }
}
