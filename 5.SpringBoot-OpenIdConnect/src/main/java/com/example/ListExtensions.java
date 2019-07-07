package com.example;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListExtensions {

	/**
	 * 根据条件对列表进行对象去除重复操作
	 * 
	 * @param <T>         需要筛选的列表对象
	 * @param <U>         筛选条件的字段
	 * @param enumeration 需要筛选的列表
	 * @param keySelector 需要筛选的列表对象的属性lambda表达式
	 * @return List<T>
	 */
	public static <T, U> List<T> distinct(List<T> enumeration, Function<T, U> keySelector)
	{
		if (enumeration == null || enumeration.size() == 0)
			return new ArrayList<T>();

		return enumeration.stream().filter(getDistinctPredicateByKey(keySelector)).collect(Collectors.toList());
	}
	
	
	/**将两个string[]合并为一个新的string[]
	 * @param firtArray
	 * @param secondArray
	 * @return
	 */
	public static <T> T mergeArray(T firtArray, T secondArray) {
	    if (!firtArray.getClass().isArray() || !secondArray.getClass().isArray()) {
	        throw new IllegalArgumentException();
	    }

	    Class<?> resCompType;
	    Class<?> aCompType = firtArray.getClass().getComponentType();
	    Class<?> bCompType = secondArray.getClass().getComponentType();

	    if (aCompType.isAssignableFrom(bCompType)) {
	        resCompType = aCompType;
	    } else if (bCompType.isAssignableFrom(aCompType)) {
	        resCompType = bCompType;
	    } else {
	        throw new IllegalArgumentException();
	    }

	    int aLen = Array.getLength(firtArray);
	    int bLen = Array.getLength(secondArray);

	    @SuppressWarnings("unchecked")
	    T result = (T) Array.newInstance(resCompType, aLen + bLen);
	    System.arraycopy(firtArray, 0, result, 0, aLen);
	    System.arraycopy(secondArray, 0, result, aLen, bLen);        

	    return result;
	}

	private static <T> Predicate<T> getDistinctPredicateByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}


	/**
	 * 根据排序字段名称，获取排序后的列表
	 * 
	 * @param <T>         需要排序的列表对象
	 * @param <U>         排序条件的字段
	 * @param enumeration 需要排序的列表
	 * @param keySelector 需要排序的列表对象的属性lambda表达式
	 * @param ascending   是否为升序
	 */
	public static <T, U extends Comparable<? super U>> void orderBy(List<T> enumeration,
			Function<? super T, ? extends U> keySelector, boolean ascending)
	{
		enumeration.sort(Comparator.comparing(keySelector));
		if (!ascending)
			Collections.reverse(enumeration);
    }

	/**
	 * 选取某个对象的某个字段，将列表该对象的这个字段转换成：xxxx,yyy,zzz
	 * 
	 * @param <T>         需要转换的列表对象
	 * @param enumeration 需要转换的列表
	 * @param selector    需要转换的列表对象的属性lambda表达式
	 * @return String
	 */
	public static <T> String toCommaSeparatedStringByFilter(List<T> enumeration, Function<T, String> selector)
	{
		if (enumeration == null || enumeration.size() == 0)
			return "";

		return enumeration.stream().map(selector).collect(Collectors.joining(","));
    }

	/**
	 * 将一个字符串列表，转换成：xxxx, yyy, zzz
	 * 
	 * @param enumeration 需要转换的列表
	 * @return String
	 */
	public static String toCommaSeparatedString(List<String> enumeration)
    {
		if (enumeration == null || enumeration.size() == 0)
			return "";

		Function<String, String> selector = s -> s;
		return toCommaSeparatedStringByFilter(enumeration, selector);
    }

	/**
	 * 将一个字符串列表，转换成：N'xxxx', N'yyy', N'zzz'
	 * 
	 * @param enumeration
	 * @return String
	 */
	public static String toCommaSeparatedWhereString(List<String> enumeration)
    {
		if(enumeration == null || enumeration.size() == 0 )
			return "";
		
		List<String> fixList = fixStringList(enumeration, "N'", "'");
		Function<String, String> selector = s -> s;
		return toCommaSeparatedStringByFilter(fixList, selector);
    }

	/**
	 * 将一个int列表，转换成：1, 2, 3
	 * 
	 * @param enumeration 需要转换的列表
	 * @return String
	 */
	public static String toCommaSeparatedInt(List<Integer> enumeration)
    {
		if (enumeration == null || enumeration.size() == 0)
			return "";

		Function<Integer, String> selector = s -> s.toString();
		return toCommaSeparatedStringByFilter(enumeration, selector);
    }

	/**
	 * 将列表中的所有字符添加前缀及后缀
	 * 
	 * @param enumeration
	 * @param prefix      前缀
	 * @param subfix      后缀
	 * @return List<String>
	 */
	public static List<String> fixStringList(List<String> enumeration, String prefix, String subfix)
    {	
		List<String> result = new ArrayList<String>();
		if (enumeration == null || enumeration.size() == 0)
			return result;

		for (String m : enumeration)
        {
			result.add(prefix + m + subfix);
        }

		return result;
    }

	/**
	 * 将以oldValue开头字符中的替换为已newValue为开头
	 * 
	 * @param enumeration
	 * @param oldValue    老值
	 * @param newValue    新值
	 * @return List<String>
	 */
	public static List<String> replaceFirst(List<String> enumeration, String oldValue, String newValue)
    {
		if (enumeration == null || enumeration.size() == 0)
			return new ArrayList<String>();

		List<String> result = new ArrayList<String>();
		for (String m : enumeration)
        {
			result.add(m.replaceFirst(oldValue, newValue));
        }

		return result;
    }

	

}
