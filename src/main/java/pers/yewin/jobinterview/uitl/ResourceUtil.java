package pers.yewin.jobinterview.uitl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static helper.CommonUtil.printWarn;
import static helper.ConstantUtil.ERROR_PARAM_MESSAGE;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.uitl
 */

public class ResourceUtil {

    private ResourceUtil(){}

    public static String read(InputStream file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(file))) {
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
            printWarn(ERROR_PARAM_MESSAGE, e.getMessage());
        }
        return contentBuilder.toString();
    }
}
