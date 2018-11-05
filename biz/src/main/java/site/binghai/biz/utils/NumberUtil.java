package site.binghai.biz.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {

    public static String getNumber(String string){
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll("");
    }
}
