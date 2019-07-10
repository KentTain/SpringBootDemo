package com.example;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class StringExtensions {

	/**
	 * 判断字符是否为空
	 * 
	 * @param str 判断是否为空的字符串
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isNullOrEmpty(String str) {
		return (str == null || "".equals(str));
	}
	
	/**
	 * 获取座机号中的分机号 </br>
	 * 例如：获取座机号82680051-9003的分机号吗：9003
	 * 
	 * @param text
	 * @return String
	 * @author tianc
	 */
	public static String getExtensionNumber(String text) {
		String[] tels = text.split("-");
		if (tels != null)
			return tels[0];
		return "";
	}

	/**
	 * 获取以/结尾的Url路径， </br>
	 * 例如：www.xxxx.com/
	 * 
	 * @param text
	 * @return String
	 * @author tianc
	 */
	public static String endWithSlash(String text) {
		return text.endsWith("/") ? text : text + "/";
	}

	/**
	 * 替换最后字符
	 * 
	 * @param text            需要替换的字符串
	 * @param strToReplace    需要替换的旧字符
	 * @param replaceWithThis 需要替换的新字符
	 * @return
	 */
	public static String replaceLast(String text, String strToReplace, String replaceWithThis) {
		return text.replaceFirst("(?s)" + strToReplace + "(?!.*?" + strToReplace + ")", replaceWithThis);
	}

	/**
	 * 将列表中每个项进行加工后返回新的列表（for List） </br>
	 * List<String> stringList = Arrays.asList("1","2","3"); </br>
	 * List<Integer> integerList = convertList(stringList, s -> Integer.parseInt(s));
	 * 
	 * @param <T>  源列表数据类型：例如 List<String>
	 * @param <U>  结果列表数据类型：例如 List<String>
	 * @param from 源列表
	 * @param func 数据转换的lambda表达式
	 * @return List<U>
	 * @author tianc
	 */
	public static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
		return from.stream().map(func).collect(Collectors.toList());
	}

	/**
	 * 将列表中每个项进行加工后返回新的列表（for arrays） </br>
	 * String[] stringArr = {"1","2","3"}; </br>
	 * Double[] doubleArr = convertArray(stringArr, Double::parseDouble, Double[]::new);
	 * 
	 * @param <T>       源列表数据类型：例如 String[]
	 * @param <U>       结果列表数据类型：例如 String[]
	 * @param from      源列表
	 * @param func      数据转换的lambda表达式
	 * @param generator
	 * @return U[]
	 * @author tianc
	 */
	public static <T, U> U[] convertArray(T[] from, Function<T, U> func, IntFunction<U[]> generator) {
		return Arrays.stream(from).map(func).toArray(generator);
	}

	/**
	 * 将aaa,bbb,ccc转换为列表项 String[]{aaa,bbb,ccc}
	 * 
	 * @param text aaa,bbb,ccc
	 * @return String[]
	 * @author tianc
	 */
	public static String[] arrayFromCommaDelimitedStrings(String text) {
		if (isNullOrEmpty(text))
			return new String[] {};

		String[] tokens = text.trim().split(",");
		List<String> tokenList = Arrays.asList(tokens).stream().map(m -> m.trim()).filter(m -> !isNullOrEmpty(m))
				.collect(Collectors.toList());

		return tokenList.toArray(new String[tokenList.size()]);
	}

	/**
	 * 将aaa,bbb,ccc转换为列表项 String[]{aaa,bbb,ccc}
	 * 
	 * @param text      aaa,bbb,ccc 或 aaa-bbb-ccc
	 * @param separator 分隔符
	 * @return String[]
	 * @author tianc
	 */
	public static String[] arrayFromCommaDelimitedStringsBySplitChar(String text, String separator) {
		if (isNullOrEmpty(text))
			return new String[] {};

		String[] tokens = text.trim().split(separator);
		List<String> tokenList = Arrays.asList(tokens).stream().map(m -> m.trim()).filter(m -> !isNullOrEmpty(m))
				.collect(Collectors.toList());
		return tokenList.toArray(new String[tokenList.size()]);
	}

	/**
	 * 将字符串：1,2,3 转换为列表项 int[]{1,2,3}
	 * 
	 * @param text 字符串：1,2,3
	 * @return Integer[]
	 * @author tianc
	 */
	public static Integer[] arrayFromCommaDelimitedIntegers(String text) {
		if (isNullOrEmpty(text))
			return new Integer[] {};

		String[] tokens = text.trim().split(",");
		Integer[] result = convertArray(tokens, Integer::parseInt, Integer[]::new);

		return result;
	}

	/**
	 * 将字符串：1|2|3 转换为列表项 int[]{1,2,3}
	 * 
	 * @param text      1,2,3 或者 1-2-3
	 * @param separator 分隔符
	 * @return Integer[]
	 * @author tianc
	 */
	public static Integer[] arrayFromCommaDelimitedIntegersBysplitChar(String text, String separator) {
		if (isNullOrEmpty(text))
			return new Integer[] {};

		String[] tokens = text.trim().split(separator);
		Integer[] result = convertArray(tokens, Integer::parseInt, Integer[]::new);

		return result;
	}

	/**
	 * 将AccessId=xxxxxx;AccessKey=xxxxxx;转换为列表项 Dictionary<String, String>()
	 * 
	 * @param connectionString
	 * @return Dictionary<String, String>
	 * @author tianc
	 */
	public static Dictionary<String, String> keyValuePairFromConnectionString(String connectionString) {
		if (isNullOrEmpty(connectionString))
			return null;

		String[] keyPairs = connectionString.split(";");
		Dictionary<String, String> result = new Hashtable<String, String>();
		for (String keyPair : keyPairs) {
			if (!isNullOrEmpty(keyPair)) {
				int index = keyPair.indexOf("=");
				String key = keyPair.substring(0, index).toLowerCase();
				String value = keyPair.substring(index + 1);
				result.put(key, value);
			}
		}

		return result;
	}

	/**
	 * 将中文字符转化Unicode编码
	 * 
	 * @param str
	 * @return String
	 * @author tianc
	 */
	public static String chineseToUnicode(String str) {
		char[] chars = str.toCharArray();
		String returnStr = "";
		for (int i = 0; i < chars.length; i++) {
			returnStr += "\\u" + Integer.toString(chars[i], 16);
		}
		return returnStr;
	}

	/**
	 * 将Unicode编码字符转化中文
	 * 
	 * @param str
	 * @return String
	 * @author tianc
	 */
	public static String unicodeToChinese(String str) {
		/** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格 */
		String[] strs = str.split("\\\\u");
		String returnStr = "";
		// 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
		for (int i = 1; i < strs.length; i++) {
			returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
		}
		return returnStr;
	}

	/**
	 * 验证身份证是否合法
	 * 
	 * @param idCard 要验证的身份证
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isIdCard(String idCard) {
		// 如果为空，认为验证合格
		if (isNullOrEmpty(idCard)) {
			return false;
		}

		// 清除要验证字符串中的空格
		idCard = idCard.trim();

		// 模式字符串
		StringBuilder rule = new StringBuilder();
		rule.append("^(11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|");
		rule.append("50|51|52|53|54|61|62|63|64|65|71|81|82|91)");
		rule.append("(\\d{13}|\\d{15}[\\dx])$");

		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(idCard);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	/**
	 * 验证EMail是否合法
	 * 
	 * @param email 要验证的Email
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isEmail(String email) {
		// 如果为空，认为验证不合格
		if (isNullOrEmpty(email)) {
			return false;
		}

		// 清除要验证字符串中的空格
		email = email.trim();

		// 模式字符串
		String rule = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$";

		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	/**
	 * 验证银行帐号是否合法
	 * 
	 * @param bankAccount
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isBankAccount(String bankAccount) {
		if (isNullOrEmpty(bankAccount))
			return false;

		bankAccount = bankAccount.trim();

		String rule = "^([1-9]{1})(\\d{14}|\\d{15}|\\d{16}|\\d{17}|\\d{18})$";
		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(bankAccount);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	/**
	 * 验证url是否合法
	 * 
	 * @param url URL地址
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isUrl(String url) {
		if (isNullOrEmpty(url))
			return false;

		url = url.trim();

		String rule = "^(https?|ftp):\\/\\/(((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:)*)?(((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))|((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?)(:\\d*)?)(\\/((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|)*)*)?)?(\\?((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|)|[\\uE000-\\uF8FF]|\\/|\\?)*)?(\\#((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|)|\\/|\\?)*)?$";

		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	/**
	 * 验证座机号
	 * 
	 * @param phone 座机号
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isTelephone(String phone) {
		String rule = "^((\\(\\d{2,3}\\))|(\\d{3}\\-))?(\\(0\\d{2,3}\\)|0\\d{2,3}-)?[1-9]\\d{6,7}(\\-\\d{1,4})?$";
		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(phone);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	/**
	 * 验证手机号
	 * 
	 * @param phone 手机号
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isMobile(String phone) {
		String rule = "^(13|14|15|17|18)\\d{9}$";
		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(phone);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	/**
	 * 验证手机号
	 * 
	 * @param phone 手机号
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isTelOrMobile(String phone) {
		return isTelephone(phone) || isMobile(phone);
	}

	/**
	 * 验证QQ号
	 * 
	 * @param qq QQ号
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isQQ(String qq) {
		String rule = "^[1-9]\\d{4,11}$";
		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(qq);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	public static boolean engNum(String engnum) {
		String rule = "^[0-9a-zA-Z]*$";
		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(engnum);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	public static boolean lenEngNum(String lenEngNum, int length) {
		return length >= lenEngNum.length() && engNum(lenEngNum);
	}

	/**
	 * 验证IP地址是否合法
	 * 
	 * @param ip 要验证的IP地址
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isIP(String ip) {
		// 如果为空，认为验证合格
		if (isNullOrEmpty(ip)) {
			return true;
		}

		// 清除要验证字符串中的空格
		ip = ip.trim();

		// 模式字符串
		String rule = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";
		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(ip);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	/**
	 * 验证是否为数字
	 * 
	 * @param number 要验证的数字
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isNumber(String number) {
		// 如果为空，认为验证不合格
		if (isNullOrEmpty(number)) {
			return false;
		}

		// 清除要验证字符串中的空格
		number = number.trim();

		// 模式字符串
		String rule = "^[0-9]+[0-9]*[.]?[0-9]*$";
		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(number);
		// 字符串是否与正则表达式相匹配
		return matcher.matches();
	}

	/// <summary>
	/// HTML转行成TEXT
	/// </summary>
	/// <param name="strHtml"></param>
	/// <returns></returns>
	public static String htmlToTxt(String strHtml) {
		String[] aryReg = { "<script[^>]*?>.*?</script>",
				"<(\\/\\s*)?!?((\\w+:)?\\w+)(\\w+(\\s*=?\\s*(([\"\"'])(\\\\[\"\"'tbnr]|[^\\7])*?\\7|\\w+)|.{0})|\\s)*?(\\/\\s*)?>",
				"([\\r\\n])[\\s]+", "&(quot|#34);", "&(amp|#38);", "&(lt|#60);", "&(gt|#62);", "&(nbsp|#160);",
				"&(iexcl|#161);", "&(cent|#162);", "&(pound|#163);", "&(copy|#169);", "&#(\\d+);", "-->", "<!--.*\\n" };

		String newReg = aryReg[0];
		String strOutput = strHtml;
		for (int i = 0; i < aryReg.length; i++) {
			// Regex regex = new Regex(aryReg[i], RegexOptions.IgnoreCase);
			// strOutput = regex.Replace(strOutput, "");
		}

		strOutput.replace("<", "");
		strOutput.replace(">", "");
		strOutput.replace("\r\n", "");

		return strOutput;
	}

	/**
	 * 把手机号码8位账户替换为*
	 * 
	 * @param source
	 * 
	 * @return String
	 * @author tianc
	 */
	public static String toHideMobile(String source) {
		if (source == null || source.length() != 11)
			return source;
		return source.substring(0, 3) + "********";
	}
	
	/**
	 * 前台显示邮箱的掩码替换(由tzhqq.com等替换成t*****qq.com)
	 * 
	 * @param email 邮箱
	 * @return String
	 * @author tianc
	 */
	public static String toHideEmail(String email) {
		String strArg = "";
		String SendEmail = "";

		String rule = "(\\\\w)\\\\w+";
		// 验证
		Pattern pattern = Pattern.compile(rule.toString(), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);

		if (matcher.matches()) {
			strArg = matcher.group(1) + "*****";
			SendEmail = email.replaceFirst("\\w+", strArg);
		} else
			SendEmail = email;
		return SendEmail;
	}

	public static boolean isOwnDomain(String source) {
		boolean isOwnDomain = !source.endsWith("cfwin.com") || !source.endsWith("starlu.com");
		return isOwnDomain;
	}
}
