package com.maogu.htclibrary.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qiu.d
 */
public class StringUtil {

    private static final int    MOBIL_F_TAG        = 3;
    private static final int    MOBIL_L_TAG        = 7;
    private static final int    WEEK_TAG           = 7;
    private static final int    MOBIL_P_TAG_PREFIX = 6;
    private static final int    MOBIL_N_TAG_PREFIX = 10;
    private static final String TAG                = "StringUtil";

    /**
     * 字符串去空格，回车，换行，制表符
     *
     * @param str 要修改的字符串
     * @return 修改完成的字符串
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 判断字符串是否为null或者空字符串
     *
     * @param input 输入的字符串
     * @return 如果为null或者空字符串，返回true；否则返回false
     */
    public static boolean isNullOrEmpty(String input) {
        return TextUtils.isEmpty(input) || 0 == input.trim().length();
    }

    /**
     * 判断字符串中是否含有中文字符
     *
     * @param s 需要判断的字符串
     * @return true为包含中文
     */
    public static boolean containChinese(String s) {
        if (null == s) {
            return false;
        }

        Pattern pattern = Pattern.compile(".*[\u4e00-\u9fbb]+.*");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * 判断输入的是否为汉字字符
     *
     * @return 是否为中文
     */
    public static boolean isChinese(char a) {
        return String.valueOf(a).matches("[\u4E00-\u9FA5]");
    }

    /**
     * 处理手机号码
     *
     * @param userMobile 需要处理的手机号码
     * @return String 处理后的手机号码
     */
    public static String getProcessedMobile(String userMobile) {
        String mProcessedDrawMobile = "";
        if (!StringUtil.isNullOrEmpty(userMobile)) {
            EvtLog.d(TAG, userMobile);
            // 判断是否是+86开头的手机号码
            if ('+' == userMobile.charAt(0) && userMobile.length() >= MOBIL_N_TAG_PREFIX) {
                mProcessedDrawMobile = userMobile.substring(0, MOBIL_P_TAG_PREFIX) + "****"
                        + userMobile.substring(MOBIL_N_TAG_PREFIX);
            } else if ('+' != userMobile.charAt(0) && userMobile.length() >= MOBIL_L_TAG) {
                mProcessedDrawMobile = userMobile.substring(0, MOBIL_F_TAG) + "****"
                        + userMobile.substring(MOBIL_L_TAG);
            } else {
                mProcessedDrawMobile = userMobile;
            }
        }
        return mProcessedDrawMobile;
    }

    public static String getStartMobile(String userMobile) {
        String mProcessedDrawMobile = "";
        if (!StringUtil.isNullOrEmpty(userMobile)) {
            if ('+' != userMobile.charAt(0) && userMobile.length() == 11) {
                mProcessedDrawMobile = userMobile.substring(0, 4) + "*******";
            }
        }
        return mProcessedDrawMobile;
    }

    /**
     * 转换保留小数位 字符串
     *
     * @param i      小数位数
     * @param numStr 数字字符串
     * @return String
     */
    public static String getDecimalFormat(int i, String numStr) {
        try {
            if (numStr != null && !"".equals(numStr)) {
                BigDecimal bd = new BigDecimal(numStr);
                bd = bd.setScale(i, BigDecimal.ROUND_HALF_UP);
                return bd.toString();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 转换保留小数位
     *
     * @param i   小数位数
     * @param num 要转换的数字
     * @return double 返回类型
     */
    public static double decimalFormat(int i, Double num) {
        String temp = getDecimalFormat(i, num.toString());
        return Double.valueOf(temp);
    }

    /**
     * 检查是否是字符串
     *
     * @param input 被检查字符串
     * @return 是否是数字组成的字符串，包括0开头;<br>
     * 如果为空或者空字符串，返回true；比如："011"返回true，"a11"返回false
     */
    public static boolean isNumberString(String input) {
        if (!isNullOrEmpty(input)) {
            if (input.matches("[0-9]+")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param str 带检测的字符串
     * @return String 去掉多余的.与0
     */
    public static String filterZeroAndDot(String str) {
        if (!isNullOrEmpty(str) && str.indexOf(".") > 0) {
            // 去掉多余的0
            str = str.replaceAll("0+?$", "");
            // 如最后一位是.则去掉
            str = str.replaceAll("[.]$", "");
        }
        return str;
    }

    public static String getDecimalFormatString(double input, int digits) {
        BigDecimal bd = new BigDecimal(input);
        bd = bd.setScale(digits, BigDecimal.ROUND_HALF_UP);

        return filterZeroAndDot(bd.toString());
    }

    public static boolean isMobileNumber(String mobiles) {
        Pattern p = Pattern.compile("^(13|14|15|17|18)\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }


    /**
     * 是否是汉字，英文，数字或者其中几个组成
     *
     * @param s 字符串
     * @return boolean 是否是汉字，英文，数字或者其中几个组成
     */
    public static boolean isValidEngOrChOrNum(String s) {
        if (null == s) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\u4E00-\u9FA5A-Za-z0-9]*$");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    /**
     * 验证中文名字
     */
    public static boolean isValidName(String name) {
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5\\·]{2,10}$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    /**
     * 隐藏字符串中间部分 用*代替
     *
     * @param number 加密字符串
     * @param start  起始位置
     * @param end    结束位置
     * @return 加密后的字符串
     */
    public static String hideMiddleNumber(String number, int start, int end) {
        if (isNullOrEmpty(number)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            if (i >= start && i <= number.length() - end - 1) {
                sb.append('*');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 验证身份证
     */
    public static boolean isValidIdCard(String idCard) {
        Pattern pattern = Pattern.compile("^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$");
        Matcher matcher = pattern.matcher(idCard);
        return matcher.matches();
    }
}