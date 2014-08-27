/**
 * 
 */
package com.hfdemo.filesharedemo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;

//import com.tencent.mtt.R;

/*
 * Copyright (C) 2005-2010 TENCENT Inc.All Rights Reserved.
 * FileName：StringUtil.java
 * Description：
 * History：
 * 1.0 theonewang 2011-12-19 Create
 */

public class StringUtils
{
	private static final String			REGEX_URL_PATTERN_STRING	= "(((https?://|ftp://)(?:(?:[-_0-9a-zA-Z.~!$&'\\(\\)*+,;=%]+\\.)+[-_0-9a-zA-Z.~!$&'\\(\\)*+,;=%]+))|(([-_0-9a-zA-Z.~!$&'\\(\\)*+,;=%]+@)?(?<![-_0-9a-zA-Z~$&'*+])www\\.([-_0-9a-zA-Z.~!$&'\\(\\)*+,;=%]+\\.)+[-_0-9a-zA-Z.~!$&'\\(\\)*+,;=%]+)|(([-_0-9a-zA-Z.~!$&'\\(\\)*+,;=%]+@)?(?<![-_0-9a-zA-Z~$&'*+])(([0-9a-zA-Z]|%\\d\\d)+\\.)*([0-9a-zA-Z]|%\\d\\d)+(\\.com\\b|\\.net\\b|\\.cn\\b|\\.org\\b))|(https?://|ftp://)((\\d{1,3}\\.){3}\\d{1,3}))(?:\\:\\d+)?(([/#][-_0-9a-zA-Z.~!$&'\\(\\)*+,;=:@/?#%]*)|(\\?[-_0-9a-zA-Z.~!$&'\\(\\)*+,;=:@/?#%]+))?";
	private static final Pattern		URL_REGEX_PATTERN			= Pattern.compile(REGEX_URL_PATTERN_STRING);

	private static final String			REGEX_QVOD_PATTERN_STRING	= "http://(.+)(\\.)(.+)[^\\w]*(.*)(\\.rmvb|\\.avi|\\.wmv|\\.mp4|\\.rm|\\.3gp|\\.mov|\\.asf|\\.mkv|\\.mpg|\\.mpeg|\\.m3u8|\\.f4v|\\.flv)";
	private static final Pattern		QVOD_REGEX_PATTERN			= Pattern.compile(REGEX_QVOD_PATTERN_STRING);


	public static final String			TAG							= "StringUtil";

	/** 文本处理状态机：普通状态 */
	public static final int				TEXT_COMMON					= 0;

	/** 文本处理状态机：特殊字符状态 */
	public static final int				TEXT_SPEC					= 1;

	/** 文本处理状态机：特殊字符-数字状态 */
	public static final int				TEXT_NUM					= 2;

	/** 文本处理状态机：特殊字符-数字-十六进制状态 */
	public static final int				TEXT_HEX					= 3;

	/** 文本处理状态机：特殊字符-数字-十进制状态 */
	public static final int				TEXT_DEC					= 4;

	/** 文本处理状态机：特殊字符-数字-八进制状态 */
	public static final int				TEXT_OCTAL					= 5;

	/** 文本处理状态机：特殊字符-空格状态 */
	public static final int				TEXT_BLOCK					= 6;


	public static final float			K							= 1024;
	public static final float			M							= 1024 * 1024;
	public static final float			G							= 1024 * 1024 * 1024;

	private static final String			SIZE_UNIT_BYTE				= "B";
	private static final String			SIZE_UNIT_KB				= "KB";
	private static final String			SIZE_UNIT_MB				= "MB";
	private static final String			SIZE_UNIT_GB				= "GB";

	private static final String			SIZE_BYTE					= "%dB";
	private static final String			SIZE_KB						= "%.2fKB";
	private static final String			SIZE_1KB					= "%.0fKB";
	private static final String			SIZE_KB_FIX					= "%.1fKB";
	private static final String			SIZE_MB						= "%.2fMB";
	private static final String			SIZE_GB						= "%.2fGB";
	// private static final String SIZE_UNKNOWN = "未知";
	private static final String			SPEED_BYTE					= "%dB/S";
	private static final String			SPEED_KB					= "%.2fKB/S";
	private static final String			SPEED_MB					= "%.2fMB/S";
	private static final String			SPEED_GB					= "%.2fGB/S";

	private static final String			SIZE_PREFIX					= "%.";
	private static final String			SIZE_KB_POSTFIX				= "fKB";
	private static final String			SIZE_MB_POSTFIX				= "fMB";
	private static final String			SIZE_GB_POSTFIX				= "fGB";
	private static final String			TIMES_MILLION				= "%.2f亿";
	
	
	private static final String 		STRING_UNKNOW				= "未知";//copy from strings.xml R.string.unknown.
	private static final String			STRING_DOWNLAOD_FILE_SIZE_UNKNOWN = "--";//R.string.download_file_size_unknown.
	private static final String 		STRING_FILE_SIZE_UNKOWN			= "0B";//R.string.file_size_unknown;
	private static final String			STRING_SAVE_FLOW_DATA							= "数据流量";//R.string.save_flow_data
	
	/**************************************** base function ****************************************/

	/**
	 * 转换字符串
	 * 1.将转义字符替换；
	 * 2.将特殊进制数值转换为十进制数值
	 * 
	 * @param text
	 */
	public static String convertString(String text)
	{
		if (text == null)
		{
			return null;
		}

		int type = TEXT_COMMON;
		StringBuffer textBuffer = new StringBuffer();
		char[] charArr = text.toCharArray();
		int iter = 0;
		/* 数字位数 */
		int digitalCount = 0;
		/* 数字字符串的实际数值 */
		int value = 0;

		while (iter < charArr.length)
		{
			switch (type)
			{
				case TEXT_COMMON:
				{
					switch (charArr[iter])
					{
						case 0x09:/* Tab */
						case 0x0A:/* 换行 */
						case 0x0D:/* 回车 */
						case 0x20:/* 空格 */
							textBuffer.append(' ');
							type = TEXT_BLOCK;
							break;
						case '&':
							// TODO:从Kjava抄写，但是从代码来看，在解析16进制数值的时候会出错，以后验证
							type = TEXT_SPEC;
							break;
						default:
							textBuffer.append(charArr[iter]);
							break;
					}
					break;
				}
				case TEXT_SPEC:
				{
					if (charArr[iter] == '#')
					{
						type = TEXT_NUM;
						value = 0;
						digitalCount = 0;
					}
					else
					{
						/* &amp;转义为'&' */
						if (iter + 4 <= charArr.length && (charArr[iter] == 'a' || charArr[iter] == 'A')
								&& (charArr[iter + 1] == 'm' || charArr[iter + 1] == 'M') && (charArr[iter + 2] == 'p' || charArr[iter + 2] == 'P')
								&& charArr[iter + 3] == ';')
						{
							textBuffer.append("&");
							iter += 3;
						}
						/* &lt;转义为'<' */
						else if (iter + 3 <= charArr.length && (charArr[iter] == 'l' || charArr[iter] == 'L')
								&& (charArr[iter + 1] == 't' || charArr[iter + 1] == 'T') && charArr[iter + 2] == ';')
						{
							textBuffer.append("<");
							iter += 2;
						}
						/* &gt;转义为'>' */
						else if (iter + 3 <= charArr.length && (charArr[iter] == 'g' || charArr[iter] == 'G')
								&& (charArr[iter + 1] == 't' || charArr[iter + 1] == 'T') && charArr[iter + 2] == ';')
						{
							textBuffer.append(">");
							iter += 2;
						}
						/* &apos;转义为''' */
						else if (iter + 5 <= charArr.length && (charArr[iter] == 'a' || charArr[iter] == 'A')
								&& (charArr[iter + 1] == 'p' || charArr[iter + 1] == 'P') && (charArr[iter + 2] == 'o' || charArr[iter + 2] == 'O')
								&& (charArr[iter + 3] == 's' || charArr[iter + 3] == 'S') && charArr[iter + 4] == ';')
						{
							textBuffer.append("'");
							iter += 4;
						}
						/* &quot;转义为'"' */
						else if (iter + 5 <= charArr.length && (charArr[iter] == 'q' || charArr[iter] == 'Q')
								&& (charArr[iter + 1] == 'u' || charArr[iter + 1] == 'U') && (charArr[iter + 2] == 'o' || charArr[iter + 2] == 'O')
								&& (charArr[iter + 3] == 't' || charArr[iter + 3] == 'T') && charArr[iter + 4] == ';')
						{
							textBuffer.append("\"");
							iter += 4;
						}
						/* &nbsp;转义为' ' */
						else if (iter + 5 <= charArr.length && (charArr[iter] == 'n' || charArr[iter] == 'N')
								&& (charArr[iter + 1] == 'b' || charArr[iter + 1] == 'B') && (charArr[iter + 2] == 's' || charArr[iter + 2] == 'S')
								&& (charArr[iter + 3] == 'p' || charArr[iter + 3] == 'P') && charArr[iter + 4] == ';')
						{
							textBuffer.append(" ");
							iter += 4;
						}
						/* &yen;转义为'￥' */
						else if (iter + 4 <= charArr.length && (charArr[iter] == 'y' || charArr[iter] == 'Y')
								&& (charArr[iter + 1] == 'e' || charArr[iter + 1] == 'E') && (charArr[iter + 2] == 'n' || charArr[iter + 2] == 'N')
								&& charArr[iter + 3] == ';')
						{
							textBuffer.append("￥");
							iter += 3;
						}
						/* &copy;转义为版权符号 */
						else if (iter + 5 <= charArr.length && (charArr[iter] == 'c' || charArr[iter] == 'C')
								&& (charArr[iter + 1] == 'o' || charArr[iter + 1] == 'O') && (charArr[iter + 2] == 'p' || charArr[iter + 2] == 'P')
								&& (charArr[iter + 3] == 'y' || charArr[iter + 3] == 'Y') && charArr[iter + 4] == ';')
						{
							textBuffer.append("\u00A9");
							iter += 4;
						}
						/* &ldquo;转义为中文左引号'“' */
						else if (iter + 6 <= charArr.length
								&& //
								(charArr[iter] == 'l' || charArr[iter] == 'L') && (charArr[iter + 1] == 'd' || charArr[iter + 1] == 'D')
								&& (charArr[iter + 2] == 'q' || charArr[iter + 2] == 'Q') && (charArr[iter + 3] == 'u' || charArr[iter + 3] == 'U')
								&& (charArr[iter + 4] == 'o' || charArr[iter + 4] == 'O') && (charArr[iter + 5] == ';'))
						{
							textBuffer.append("“");
							iter += 5;
						}
						/* &rdquo;转义为中文右引号'”' */
						else if (iter + 6 <= charArr.length && (charArr[iter] == 'r' || charArr[iter] == 'R')
								&& (charArr[iter + 1] == 'd' || charArr[iter + 1] == 'D') && (charArr[iter + 2] == 'q' || charArr[iter + 2] == 'Q')
								&& (charArr[iter + 3] == 'u' || charArr[iter + 3] == 'U') && (charArr[iter + 4] == 'o' || charArr[iter + 4] == 'O')
								&& (charArr[iter + 5] == ';'))
						{
							textBuffer.append("”");
							iter += 5;
						}
						/* &uarr;转义为'↑' */
						else if (iter + 5 <= charArr.length
								&& (charArr[iter] == 'u' || charArr[iter] == 'U') //
								&& (charArr[iter + 1] == 'a' || charArr[iter + 1] == 'A') && (charArr[iter + 2] == 'r' || charArr[iter + 2] == 'R')
								&& (charArr[iter + 3] == 'r' || charArr[iter + 3] == 'R') && (charArr[iter + 4] == ';'))
						{
							textBuffer.append("↑");
							iter += 4;
						}
						/* &rarr;转义为'→' */
						else if (iter + 5 <= charArr.length
								&& (charArr[iter] == 'r' || charArr[iter] == 'R') //
								&& (charArr[iter + 1] == 'a' || charArr[iter + 1] == 'A') && (charArr[iter + 2] == 'r' || charArr[iter + 2] == 'R')
								&& (charArr[iter + 3] == 'r' || charArr[iter + 3] == 'R') && (charArr[iter + 4] == ';'))
						{
							textBuffer.append("→");
							iter += 4;
						}
						/* &darr;转义为'↓' */
						else if (iter + 5 <= charArr.length
								&& //
								(charArr[iter] == 'd' || charArr[iter] == 'D') //
								&& (charArr[iter + 1] == 'a' || charArr[iter + 1] == 'A') && (charArr[iter + 2] == 'r' || charArr[iter + 2] == 'R')
								&& (charArr[iter + 3] == 'r' || charArr[iter + 3] == 'R') && (charArr[iter + 4] == ';'))
						{
							textBuffer.append("↓");
							iter += 4;
						}
						/* &larr;转义为'←' */
						else if (iter + 5 <= charArr.length
								&& //
								(charArr[iter] == 'l' || charArr[iter] == 'L') //
								&& (charArr[iter + 1] == 'a' || charArr[iter + 1] == 'A') && (charArr[iter + 2] == 'r' || charArr[iter + 2] == 'R')
								&& (charArr[iter + 3] == 'r' || charArr[iter + 3] == 'R') && (charArr[iter + 4] == ';'))
						{
							textBuffer.append("←");
							iter += 4;
						}
						/* &trade;转义为版权TM标志 */
						else if (iter + 6 <= charArr.length
								&& (charArr[iter] == 't' || charArr[iter] == 'T') //
								&& (charArr[iter + 1] == 'r' || charArr[iter + 1] == 'R') && (charArr[iter + 2] == 'a' || charArr[iter + 2] == 'A')
								&& (charArr[iter + 3] == 'd' || charArr[iter + 3] == 'D') && (charArr[iter + 4] == 'e' || charArr[iter + 4] == 'E')
								&& (charArr[iter + 5] == ';'))
						{
							textBuffer.append("\u8482");
							iter += 5;
						}
						/* &ndash;转义为'–' */
						else if (iter + 6 <= charArr.length
								&& (charArr[iter] == 'n' || charArr[iter] == 'N') //
								&& (charArr[iter + 1] == 'd' || charArr[iter + 1] == 'D') && (charArr[iter + 2] == 'a' || charArr[iter + 2] == 'A')
								&& (charArr[iter + 3] == 's' || charArr[iter + 3] == 'S') && (charArr[iter + 4] == 'h' || charArr[iter + 4] == 'H')
								&& (charArr[iter + 5] == ';'))
						{
							textBuffer.append("–");
							iter += 5;
						}
						/* &mdash;转义为'—' */
						else if (iter + 6 <= charArr.length
								&& (charArr[iter] == 'm' || charArr[iter] == 'M') //
								&& (charArr[iter + 1] == 'd' || charArr[iter + 1] == 'D') && (charArr[iter + 2] == 'a' || charArr[iter + 2] == 'A')
								&& (charArr[iter + 3] == 's' || charArr[iter + 3] == 'S') && (charArr[iter + 4] == 'h' || charArr[iter + 4] == 'H')
								&& (charArr[iter + 5] == ';'))
						{
							textBuffer.append("—");
							iter += 5;
						}
						/* &rsaquo;转化为U+203A */
						else if (iter + 7 <= charArr.length
								&& (charArr[iter] == 'r' || charArr[iter] == 'R')//
								&& (charArr[iter + 1] == 's' || charArr[iter + 1] == 'S') && (charArr[iter + 2] == 'a' || charArr[iter + 2] == 'A')
								&& (charArr[iter + 3] == 'q' || charArr[iter + 3] == 'Q') && (charArr[iter + 4] == 'u' || charArr[iter + 4] == 'U')
								&& (charArr[iter + 5] == 'o' || charArr[iter + 5] == 'O') && (charArr[iter + 6] == ';'))
						{
							textBuffer.append("\u203A");
							iter += 6;
						}
						else
						{
							textBuffer.append(charArr[iter - 1]);
							textBuffer.append(charArr[iter]);
						}
						type = TEXT_COMMON;
					}
					break;
				}
				case TEXT_NUM:
				{
					if (charArr[iter] == 'x' || charArr[iter] == 'X')
					{
						type = TEXT_HEX;
					}
					else if (charArr[iter] == 'o' || charArr[iter] == 'O')
					{
						type = TEXT_OCTAL;
					}
					else if (charArr[iter] <= '9' && charArr[iter] >= '0')
					{
						type = TEXT_DEC;
						/* 十进制没有开头表示字符，所以指针要回退 */
						iter--;
					}
					break;
				}
				case TEXT_DEC:
				{
					if (charArr[iter] <= '9' && charArr[iter] >= '0' && digitalCount++ < 5)
					{
						/* 最大值65536 */
						// TODO:为什么需要限制最大值？
						value = value * 10 + (charArr[iter] - '0');
					}
					else if (charArr[iter] == ';')
					{
						if (value > 65535)
						{
						}
						else
						{
							textBuffer.append((char) value);
						}
						type = TEXT_COMMON;
					}
					else
					{
						textBuffer.append(charArr[iter]);
						type = TEXT_COMMON;
					}
					break;
				}
				case TEXT_HEX:
				{
					if (charArr[iter] <= '9' && charArr[iter] >= '0' && digitalCount++ < 4)
					{ // 16,max=65535
						value = value * 16 + (charArr[iter] - '0');
					}
					else if (charArr[iter] <= 'F' && charArr[iter] >= 'A' && digitalCount++ < 4)
					{
						value = value * 16 + (charArr[iter] - 'A' + 10);
					}
					else if (charArr[iter] <= 'f' && charArr[iter] >= 'a' && digitalCount++ < 4)
					{
						value = value * 16 + (charArr[iter] - 'a' + 10);
					}
					else if (charArr[iter] == ';')
					{
						if (value > 65535)
						{
							// PkgTools.println("can ignore error: number is over!");
						}
						else
						{
							textBuffer.append((char) value);
						}
						type = TEXT_COMMON;
					}
					else
					{
						textBuffer.append(charArr[iter]);
						type = TEXT_COMMON;
					}
					break;
				}
				case TEXT_OCTAL:// 八进制
					if (charArr[iter] <= '7' && charArr[iter] >= '0' && digitalCount++ < 8)
					{ // 8,max=65535
						value = value * 8 + (charArr[iter] - '0');
					}
					else if (charArr[iter] == ';')
					{
						if (value > 65535)
						{
							// PkgTools.println("can ignore error: number is over!");
						}
						else
						{
							textBuffer.append((char) value);
						}
						type = TEXT_COMMON;
					}
					else
					{
						textBuffer.append(charArr[iter]);
						type = TEXT_COMMON;
					}
					break;
				case TEXT_BLOCK:
					/* 空格，包括普通空格，'\r' '\n'以及制表符 */
					if (charArr[iter] != 0x20 && charArr[iter] != 0x0D && charArr[iter] != 0x0A && charArr[iter] != 0x09)
					{
						type = TEXT_COMMON;
						iter--;
					}
					break;
				default:
					break;
			}
			iter++;
		}
		return textBuffer.toString();
	}

	/**
	 * 判空
	 * 
	 * @param str
	 * @return 请用TextUtils.isEmpty()
	 */
	public static boolean isEmpty(String str)
	{
		return str == null || "".equals(str.trim());
	}

	public static boolean isStringEqual(String str1, String str2)
	{
		return (isEmpty(str1) && isEmpty(str2)) || (str1 != null && str1.equals(str2));
	}

	public static boolean isStringEqualsIgnoreCase(String str1, String str2)
	{
		return (isEmpty(str1) && isEmpty(str2)) || (str1 != null && str1.equalsIgnoreCase(str2));
	}



	/**
	 * 将串targetString中的字串matcher替换为newText。
	 * 
	 * @param targetString
	 * @param matcher
	 * @param newText
	 * @return 替换后的串
	 */
	public static String replace(String targetString, String matcher, String newText)
	{
		Pattern p = Pattern.compile("\r|\n");
		Matcher m = p.matcher(targetString);
		return m.replaceAll(newText);
	}

	/**
	 * 去除头部空格
	 * 
	 * @param str
	 * @return
	 */
	public static String removeHeadSpace(String str)
	{
		while (str.startsWith(" "))
		{
			str = str.substring(1);
		}
		return str;
	}

	/**
	 * 判断一个字符串是否是数字
	 */
	public static boolean isNumeric(String str)
	{
		if (isEmpty(str))
		{
			return false;
		}

		for (int i = str.length(); --i >= 0;)
		{
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	/**
	 * 替换字符串并染色
	 * 
	 * @param ssb 源字符串
	 * @param wildcard 需要被替换的字符串
	 * @param dyedString 替换wildcard的字符串并对其染色
	 * @param color 颜色
	 */
	public static void replaceAndDye(SpannableStringBuilder ssb, String wildcard, String dyedString, int color)
	{
		String string = ssb.toString();
		int start = string.indexOf(wildcard);
		if (start >= 0)
		{
			int end = start + wildcard.length();
			ssb.replace(start, end, dyedString);
			end = start + dyedString.length();
			ssb.setSpan(new ForegroundColorSpan(color), start, end, SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE);
		}
	}

	/************************************* application function ************************************/

	/**
	 * Get data size string.
	 */
	public static String getSizeString(float size)
	{
		if (size < 0)
		{
			return STRING_UNKNOW;//AppEngine.getInstance().getApplicationContext().getString(R.string.unknown);
		}
		else if (size < K)
		{
			return String.format(SIZE_BYTE, (int) size);
		}
		else if (size < M)
		{
			return String.format(SIZE_KB, size / K);
		}
		else if (size < G)
		{
			return String.format(SIZE_MB, size / M);
		}
		else
		{
			return String.format(SIZE_GB, size / G);
		}
	}

	public static String getSkinSizeString(float size)
	{
		if (size < 0)
		{
			return STRING_UNKNOW;// AppEngine.getInstance().getApplicationContext().getString(R.string.unknown);
		}
		else if (size < K)
		{
			return String.format(SIZE_BYTE, (int) size);
		}
		else if (size < M)
		{
			return String.format(SIZE_1KB, size / K);
		}
		else if (size < G)
		{
			return String.format(SIZE_MB, size / M);
		}
		else
		{
			return String.format(SIZE_GB, size / G);
		}
	}

	public static String getSizeStringFixK(float size)
	{
		if (size < M && size > K)
		{
			return String.format(SIZE_KB_FIX, size / K);
		}
		else
		{
			return getSizeString(size);
		}
	}

	/**
	 * 通过精度precision来决定要取得的string保存几位小数
	 * 
	 * @param size
	 * @param precision
	 * @return
	 */
	public static String getSizeStringByPrecision(float size, int precision)
	{
		if (size < 0 || precision < 0)
		{
			return STRING_UNKNOW;//AppEngine.getInstance().getApplicationContext().getString(R.string.unknown);
		}
		else if (size < K)
		{
			return String.format(SIZE_BYTE, (int) size);
		}
		else if (size < M)
		{
			return String.format((new StringBuilder()).append(SIZE_PREFIX).append(precision).append(SIZE_KB_POSTFIX).toString(), size / K);
		}
		else if (size < G)
		{
			return String.format((new StringBuilder()).append(SIZE_PREFIX).append(precision).append(SIZE_MB_POSTFIX).toString(), size / M);
		}
		else
		{
			return String.format((new StringBuilder()).append(SIZE_PREFIX).append(precision).append(SIZE_GB_POSTFIX).toString(), size / G);
		}
	}

	/**
	 * 将下载次数格式化为万为单位的数值
	 * 
	 * @param count
	 * @return
	 */
	public static String formatDownloadCount(long count)
	{
		if (count < 0)
		{
			return "";
		}
		else if (count < 10000)
		{
			return count + "";
		}
		else if (count < 10000 * 10000)
		{
			return count / 10000 + "万";
		}
		else
		{
			return String.format(TIMES_MILLION, (float) count / (10000 * 10000));
		}
	}

	public static String getSizeStringWithoutB(float size)
	{
		if (size < 0)
		{
			return STRING_UNKNOW;//AppEngine.getInstance().getApplicationContext().getString(R.string.unknown);
		}
		else if (size < K)
		{
			// return String.format(SIZE_BYTE, (int) size);
			return "0K";
		}
		else if (size < M)
		{
			return String.format(SIZE_KB, size / K);
		}
		else if (size < G)
		{
			return String.format(SIZE_MB, size / M);
		}
		else
		{
			return String.format(SIZE_GB, size / G);
		}
	}

	public static String getSizeUnit(float size)
	{
		if (size < 0)
		{
			return "";
		}
		else if (size < K)
		{
			return SIZE_UNIT_BYTE;
		}
		else if (size < M)
		{
			return SIZE_UNIT_KB;
		}
		else if (size < G)
		{
			return SIZE_UNIT_MB;
		}
		else
		{
			return SIZE_UNIT_GB;
		}
	}

	public static String getSizeWithoutUnit(float size)
	{
		if (size < 0)
		{
			return STRING_UNKNOW;//AppEngine.getInstance().getApplicationContext().getString(R.string.unknown);
		}
		else if (size < K)
		{
			return "0";
		}
		else if (size < M)
		{
			return String.format("%.2f", size / K);
		}
		else if (size < G)
		{
			return String.format("%.2f", size / M);
		}
		else
		{
			return String.format("%.2f", size / G);
		}
	}

	/**
	 * Get data size string.
	 */
	public static String getSizeString(long size)
	{
		return getSizeString((float) size);
	}

	public static String getDownloadSizeString(long size)
	{
		if (size <= 0)
		{
			return STRING_DOWNLAOD_FILE_SIZE_UNKNOWN;//MttResources.getString(R.string.download_file_size_unknown);
		}
		return getSizeString((float) size);
	}

	public static String getFileSizeString(long size)
	{
		if (size <= 0)
		{
			return STRING_FILE_SIZE_UNKOWN;//MttResources.getString(R.string.file_size_unknown);
		}
		return getDownloadSizeString(size);
	}

	public static String getSaveFlowSizeString(long size)
	{
		if (size <= 0)
		{
			return STRING_SAVE_FLOW_DATA;//MttResources.getString(R.string.save_flow_data);
		}
		return getSizeString((float) size);
	}

	/**
	 * 获取指定有效位数的小数。若digit为3，则number=1.2345得到1.23，number=123.45得到123
	 * 
	 * @param number
	 * @param digit 有效位数
	 * @return
	 */
	public static String getSignificantFigure(float number, int digit)
	{
		String str = String.format("%." + digit + "f", number);
		int integerNumber = str.indexOf('.');
		return str.substring(0, (integerNumber < digit) ? digit + 1 : integerNumber);
	}

	/**
	 * Get data speed string.
	 */
	public static String getSpeedString(float speed)
	{
		if (speed < 0)
		{
			return String.format(SPEED_BYTE, 0);
		}
		else if (speed < K)
		{
			return String.format(SPEED_BYTE, (int) speed);
		}
		else if (speed < M)
		{
			return String.format(SPEED_KB, speed / K);
		}
		else if (speed < G)
		{
			return String.format(SPEED_MB, speed / M);
		}
		else
		{
			return String.format(SPEED_MB, speed / G);
		}
	}

	/**
	 * 根据paint，width，截断text
	 * levijiang 2011-07-15
	 * 
	 * @param text
	 * @param paint
	 * @param width
	 * @return
	 */
	public static String textCutoff(final String text, Paint paint, int width)
	{
		if (isEmpty(text) || paint == null || width <= 0)
		{
			return text;
		}

		String dstText = text;
		float fontSize = paint.measureText(dstText);

		while (fontSize > width)
		{
			dstText = dstText.substring(0, dstText.length() - 1);
			fontSize = paint.measureText(dstText);
		}

		return dstText;
	}

	/**
	 * 从A标签中获取Title以及url，放在String[0]和String[1]中
	 * 
	 * @param content
	 * @return
	 */
	public static String[] getAttributeFromTagA(String content)
	{
		if (isEmpty(content) || !content.contains("<a") || !content.contains("/a>"))
			return null;

		// 增加捕获，防止不标准的格式出现
		try
		{
			int start = content.indexOf("<a");
			int end = content.indexOf("/a>");

			if (start != -1 && end != -1)
			{
				end += 3;

				// <a href="url">title</a>
				// 取出TagA
				String sTagA = content.substring(start, end);
				int urlStart = sTagA.indexOf("href=");
				if (urlStart <= 0)
					return null;
				String urlString = sTagA.substring(urlStart);
				int urlEnd = urlString.indexOf(">");
				if (urlEnd <= 0)
					return null;
				String[] attribute = { "", "" };
				attribute[0] = urlString.substring(6, urlEnd - 1);
				String titleString = urlString.substring(urlEnd);
				int titleEnd = titleString.indexOf("<");
				if (titleEnd <= 0)
					return null;
				attribute[1] = titleString.substring(1, titleEnd);
				return attribute;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * It replace all "\r" and "\n" with " " from the argument, then return the
	 * fixed
	 * result.
	 * 
	 * @param text the string to be modified.
	 * @return the modified string
	 * @author Denverhan
	 */
	public static String removeNextLine(String text)
	{
		if (StringUtils.isEmpty(text))
		{
			return text;
		}

		Pattern p = Pattern.compile("\r|\n");
		Matcher m = p.matcher(text);
		String result = m.replaceAll(" ");



		return result;
	}

	/**
	 * restore%22to"
	 * 
	 * @param text which is to be modfied
	 * @return the modified text.
	 */
	public static String restoreQuotation(String text)
	{
		if (StringUtils.isEmpty(text))
		{
			return text;
		}

		Pattern p = Pattern.compile("%22");
		Matcher m = p.matcher(text);
		String result = m.replaceAll("\"");



		return result;
	}


	public static int getStringHeight(int fontSize)
	{
		// String str = "高度";
		// GdiMeasure tm = new GdiMeasureImpl();
		// tm.setFontSize(fontSize);
		// QSize size = new QSize();
		// tm.getStringWidthHeight(str, size);
		return fontSize;
	}

	/**
	 * add by damon
	 * 将System.currentTimeMillis() 转化成format( 例如MM月dd日 HH:mm）
	 * 
	 * @param time
	 * @return
	 */
	public static String dataFormatTranslator(long time, String format)
	{
		String dateString;
		Date date = new Date();
		date.setTime(time);
		SimpleDateFormat df = new SimpleDateFormat(format);
		df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		dateString = df.format(date);
		return dateString;
	}


	public static boolean haveChineseChar(String str)
	{
		if (StringUtils.isEmpty(str))
		{
			return false;
		}

		int len = str.length();

		for (int i = 0; i < len; i++)
		{
			char c = str.charAt(i);

			if (c >= 0x4E00 && c <= 0x9FFF || c >= 0xFE30 && c <= 0xFFA0)
			{
				return true;
			}
		}

		return false;
	}

	public static boolean isCanPrintString(String str)
	{
		if (StringUtils.isEmpty(str))
		{
			return false;
		}

		int len = str.length();

		for (int i = len - 1; i >= 0; i--)
		{
			char c = str.charAt(i);

			if (!(c >= 0x4E00 && c <= 0x9FFF || c >= 0xFE30 && c <= 0xFFA0 || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c < 48 || c > 57))
			{
				return false;
			}
		}

		return true;
	}

	/**************************************** move from NumberFormatter @kenlai ****************************************/

	/*
	 * Copyright (C) 2005-2010 TENCENT Inc.All Rights Reserved.
	 * FileName：NumberFormatter.java
	 * Description：
	 * History：
	 * 1.0 samuelmo Jun 18, 2010 Create
	 */
	public static byte parseByte(String str, byte defaultValue)
	{
		try
		{
			return Byte.parseByte(str);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	public static int parseInt(String str, int defaultValue)
	{
		try
		{
			return Integer.parseInt(str);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	public static short parseShort(String str, int defaultValue)
	{
		try
		{
			return Short.parseShort(str);
		}
		catch (Exception e)
		{
			return (short) defaultValue;
		}
	}

	public static long parseLong(String str, long defaultValue)
	{
		try
		{
			return Long.parseLong(str);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	public static float parseFloat(String str, float defaultValue)
	{
		try
		{
			return Float.parseFloat(str);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	public static String stringForTime(int mTime)
	{

		StringBuilder mFormatBuilder = new StringBuilder();
		Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
		int totalSeconds = mTime / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;
		mFormatBuilder.setLength(0);
		if (hours <= 0)
		{
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
		return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();

	}

	public static String getQvodVideoName(String url)
	{
		if (url != null)
		{ // 获得真正的视频名字
			String[] urlItems = url.split("\\|");
			if (urlItems.length >= 3)
			{
				String name = urlItems[2];
				if (name.length() > 0 && !name.contains("\\u") && !name.contains("%"))
					return name;
			}
		}
		return null;
	}

	/**
	 * 从字符串中解析所有有效的URL
	 * 
	 * @param content 包含URL的字符串内容
	 * @return 返回URL字符串列表
	 */
	public static ArrayList<String> getStringUrl(String content)
	{
		ArrayList<String> array = new ArrayList<String>();
		Matcher matcher = URL_REGEX_PATTERN.matcher(content);
		while (matcher.find())
		{
			array.add(matcher.group());
		}

		return array;
	}

	public static ArrayList<String> getQvodUrl(String content)
	{
		ArrayList<String> array = new ArrayList<String>();
		Matcher matcher = QVOD_REGEX_PATTERN.matcher(content);
		while (matcher.find())
		{
			array.add(matcher.group());
		}

		return array;
	}

	/**
	 * 比较两个字符串，并且中文比英文大
	 * 
	 * @return sa小于sb则返回-1，大于返回1，相等返回0
	 */
	public static int compareString(String sa, String sb)
	{
		if (sa == null)
			return -1;
		else if (sb == null)
			return 1;

		// 将每一位做对比
		for (int i = 0;; i++)
		{
			// “不存在第i个字符”的情况
			if (sa.length() == i)
				return -1;
			else if (sb.length() == i)
				return 1;
			else if (sa.charAt(i) != sb.charAt(i))
			{
				boolean me = isChinese(sa.charAt(i));
				boolean a = isChinese(sb.charAt(i));
				if (me && !a)
					return 1;
				else if (!me && a)
					return -1;
				else
					// 两边的第i个字符都是中文，或都是英文，再用这个方法。因为各机型中这个方法对中文和英文的排序经常不一致
					return Collator.getInstance().compare(sa.substring(i), sb.substring(i));
			}
		}
	}

	/**
	 * 判断字符是否是中文
	 */
	public static boolean isChinese(char c)
	{
		return (c >= 0x4E00 && c <= 0x9FFF || c >= 0xFE30 && c <= 0xFFA0);
	}

	/**
	 * 截断字符串，一个中文字符占两位
	 */
	public static String trunc(String str, int maxLen)
	{
		if (str == null || str.length() * 2 <= maxLen)
		{
			return str;
		}

		StringBuilder buf = new StringBuilder();
		final int N = str.length();
		int leftLen = maxLen;
		for (int i = 0; i < N; i++)
		{
			char c = str.charAt(i);
			buf.append(c);
			leftLen--;
			if (isChinese(c))
			{
				leftLen--;
			}
			if (leftLen <= 0)
			{
				break;
			}
		}

		return buf.toString();
	}

	/**
	 * 解析以逗号分隔的整数串
	 */
	public static ArrayList<Integer> parseInts(String str)
	{
		ArrayList<Integer> rs = new ArrayList<Integer>();
		if (StringUtils.isEmpty(str))
		{
			return rs;
		}
		String[] ints = str.split(",");
		for (String s : ints)
		{
			try
			{
				rs.add(Integer.valueOf(s));
			}
			catch (Exception e)
			{
			}
		}
		return rs;
	}

	public static String intsToString(ArrayList<Integer> ints)
	{
		StringBuilder sb = new StringBuilder();
		for (Integer v : ints)
		{
			if (sb.length() != 0)
			{
				sb.append(',');
			}
			sb.append(v + "");
		}
		return sb.toString();
	}

	public static ArrayList<String> removeEmptyStr(ArrayList<String> old)
	{
		ArrayList<String> newArray = new ArrayList<String>();

		for (String str : old)
		{
			if (!isEmpty(str))
			{
				newArray.add(str);
			}
		}

		return newArray;
	}
}
