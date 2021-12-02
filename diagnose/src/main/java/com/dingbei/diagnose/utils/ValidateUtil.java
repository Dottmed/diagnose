package com.dingbei.diagnose.utils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串验证工具类（网站域名 联系电话 手机号码 邮政编码 邮箱 身份证等）
 *
 */
public class ValidateUtil {
	public static final String EMPTY_STRING = ""; // 空字符串

	/**
	 * 正则验证方法
	 * @param regexstr
	 * @param str
	 * @return
	 */
	public static boolean Match(String regexstr, String str) {
		Pattern regex = Pattern.compile(regexstr, Pattern.CASE_INSENSITIVE| Pattern.DOTALL);
		Matcher matcher = regex.matcher(str);
		return matcher.matches();
	}

	public static String getMatchStr(String regexStr, String str){
		Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE| Pattern.DOTALL);
		Matcher matcher = pattern.matcher(str);
		if(matcher.find())
			return matcher.group();
		else
			return "";
	}

	/**
	 * 解析短信推送内容
	 * @param data
	 * @return
	 */
	public static Map<String, Object> getParameterMap(String data) {
		Map<String, Object> map = null;
		if (data != null) {
			map = new HashMap<String, Object>();
			String[] params = data.split("&");
			for (int i = 0; i < params.length; i++) {
				int idx = params[i].indexOf("=");
				if (idx >= 0) {
					map.put(params[i].substring(0, idx),
							params[i].substring(idx + 1));
				}
			}
		}
		return map;
	}

	/**
	 * 检测字符串是否符合用户名
	 * @param len
	 * @return
	 */
	public static boolean checkingUserName(int len) {
		boolean isValid = true;
		isValid = !(5 < len && len < 17);
		return isValid;
	}

	/**
	 * 检测字符串是否符合密码
	 * @param len
	 * @return
	 */
	public static boolean checkingPwd(int len) {
		boolean isValid = true;
		isValid = !(5 < len && len < 17);
		return isValid;
	}

	/**
	 * 检测字符串是否为中文字符
	 * @param str
	 * @return
	 */
	public static boolean isChinesrChar(String str) {
		return str.length() < str.getBytes().length;
	}

	/**
	 * 邮箱验证
	 * @param mail
	 * @return
	 */
	public static boolean MatchMail(String mail) {
		String mailregex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		return Match(mailregex, mail);
	}

	/**
	 * 手机验证
	 * @param mobile
	 * @return
	 */
	public static boolean MatchMobile(String mobile) {
		//String mobileregex = "^(13[4,5,6,7,8,9]|15[0,8,9,1,7]|188|187)\\d{8}$";
		String mobileregex = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
//		return mobile.matches(mobileregex);
		return Match(mobileregex, mobile);
	}

	/**
	 * 电话验证
	 * @param Tel
	 * @return
	 */
	public static boolean MatchTel(String Tel) {
		String telregex = "(^[0-9]{3,4}-[0-9]{7,8}-[0-9]{3,4}$)|(^[0-9]{3,4}-[0-9]{7,8}$)|(^[0-9]{7,8}-[0-9]{3,4}$)|(^[0-9]{7,15}$)";
		return Match(telregex, Tel);
	}

	public static boolean MatchQuanKou(String str){
//		String regex = "^(\\d+(.\\d+)?)*([/*](\\d(.\\d+)?)+)*$";
		String regex = "^(\\d+(\\.\\d+)?)*([/*]\\d+(\\.\\d+)?){0,2}$";
		return Match(regex,str);
	}

	/**
	 * 域名验证
	 * @param webdomain
	 * @return
	 */
	public static boolean Webdomain(String webdomain) {
		String webdomainregex = "http://([^/]+)/*";
		return Match(webdomainregex, webdomain);
	}

	/**
	 * 邮政编号验证
	 * @param zipcode
	 * @return
	 */
	public static boolean ZipCode(String zipcode) {
		String zipcoderegex = "^[0-9]{6}$";
		return Match(zipcoderegex, zipcode);
	}

	/**
	 * 身份证验证
	 * @param idcard
	 * @return
	 */
	public static boolean IdCardNo(String idcard) {
		HashMap<Integer, String> area = new HashMap<Integer, String>();
		area.put(11, "北京");
		area.put(12, "天津");
		area.put(13, "河北");
		area.put(14, "山西");
		area.put(15, "内蒙古");
		area.put(21, "辽宁");
		area.put(22, "吉林");
		area.put(23, "黑龙江");
		area.put(31, "上海");
		area.put(32, "江苏");
		area.put(33, "浙江");
		area.put(34, "安徽");
		area.put(35, "福建");
		area.put(36, "江西");
		area.put(37, "山东");
		area.put(41, "河南");
		area.put(42, "湖北");
		area.put(43, "湖南");
		area.put(44, "广东");
		area.put(45, "广西");
		area.put(46, "海南");
		area.put(50, "重庆");
		area.put(51, "四川");
		area.put(52, "贵州");
		area.put(53, "云南");
		area.put(54, "西藏");
		area.put(61, "陕西");
		area.put(62, "甘肃");
		area.put(63, "青海");
		area.put(64, "宁夏");
		area.put(65, "新疆");
		area.put(71, "台湾");
		area.put(81, "香港");
		area.put(82, "澳门");
		area.put(91, "国外");
		if(isEmpty(idcard)) return false;
		if(area.get(Integer.parseInt(idcard.substring(0, 2)))==null) return false;
		if(!(idcard.length()==15||idcard.length()==18)) return false;
		if(idcard.length()==15){
			//老身份证
			int year = Integer.parseInt(idcard.substring(2,6))+1900;
			String ereg;
			if (year % 4 == 0||(year% 100 == 0 &&year % 4 == 0 )){
				ereg="^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$";//测试出生日期的合法性
			}else{
				ereg="^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$";//测试出生日期的合法性
			}
			return Match(ereg, idcard);

		}else if(idcard.length()==18){
			//新省份证
			//18位身份号码检测
			//出生日期的合法性检查
			//闰年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))
			//平年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))
			int year = Integer.parseInt(idcard.substring(2,6))+1900;
			String ereg;
			if (year % 4 == 0 ||(year % 100 == 0 && year%4 == 0 )){
				ereg="^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$";//闰年出生日期的合法性正则表达式
			}else{
				ereg="^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$";//平年出生日期的合法性正则表达式
			}
			if(Match(ereg, idcard)){//测试出生日期的合法性
				//计算校验位
				int[] idcards = new int[18];
				for (int i = 0; i < idcard.length(); i++) {
					idcards[i]= Integer.parseInt(idcard.substring(i, i+1));
				}
				int S = (idcards[0] + idcards[10]) * 7
						+ (idcards[1] + idcards[11]) * 9
						+ (idcards[2] + idcards[12]) * 10
						+ (idcards[3] + idcards[13]) * 5
						+ (idcards[4] + idcards[14]) * 8
						+ (idcards[5] + idcards[15]) * 4
						+ (idcards[6] + idcards[16]) * 2
						+ idcards[7] * 1
						+ idcards[8] * 6
						+ idcards[9] * 3 ;
				int Y = S % 11;
				String M = "F";
				String JYM = "10X98765432";
				M = JYM.substring(Y,Y+1);//判断校验位
				//检测ID的校验位
				return M.equalsIgnoreCase(String.valueOf(idcards[17]));
			}
			else
				return false;
		}
		return false;
	}

	/**
	 * 判断字符串是否为空字符串。
	 * @param aString
	 * @return
	 */
	public static boolean isEmpty(String aString) {
		return aString == null || EMPTY_STRING.equals(aString.trim());
	}

	/**
	 * 判断字符串是否是整数
	 * @param aString
	 * @return
	 */
	public static boolean isInteger(String aString) {
		try {
			Integer.parseInt(aString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 * @param value
	 * @return
	 */
	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			return value.contains(".");
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 格式化手机号码
	 * @param aPhoneNum
	 * @return
	 */
	public static String formatPhoneNum(String aPhoneNum) {
		String first = aPhoneNum.substring(0, 3);
		String end = aPhoneNum.substring(7, 11);
		String phoneNumber = first + "****" + end;
		return phoneNumber;
	}

	/**
	 * 将带格式的日期时间字符串dt转换为不带格式的日期时间字符串
	 * @param dt
	 * @return
	 */
	public static String formatDateStrToShortDateStr(String dt) {
		try {
			return new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(dt));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检查字符串是否为纯数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isLetter(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!(s.charAt(i) >= 'A' && s.charAt(i) <= 'Z')
					&& !(s.charAt(i) >= 'a' && s.charAt(i) <= 'z')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 去除字符串中空格
	 * @param aString
	 * @return
	 */
	public static String clearSpaces(String aString) {
		StringTokenizer aStringTok = new StringTokenizer(aString, " ", false);
		String aResult = "";
		while (aStringTok.hasMoreElements()) {
			aResult += aStringTok.nextElement();
		}
		return aResult;
	}

	/**
	 * 处理应用安装人数
	 * @param number
	 * @return
	 */
	public static String numToFormat(int number){
		String result = null;
		int y,w,q;
		if(number>9999){
			w = number/10000;
			q = (number%10000)/1000;
			result = w+"."+q+"万";
		}else if(number>99999999){
			y = number/100000000;
			w = (number%100000000)/10000;
			result = y+"."+w+"亿";
		}else{
			result = ""+number;
		}
		return result;
	}

	public static String formatToUTF8(String str) throws UnsupportedEncodingException {
		if(isEmpty(str)){
			return "";
		}
		String result = new String(str.getBytes("ISO-8859-1"),"UTF8");
		return result;
	}
}
