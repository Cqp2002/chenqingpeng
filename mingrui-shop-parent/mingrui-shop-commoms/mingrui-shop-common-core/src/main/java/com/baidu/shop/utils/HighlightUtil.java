package com.baidu.shop.utils;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;

import javax.swing.border.TitledBorder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName HighlightUtil
 * @Description: TODO
 * @Author shenyaqi
 * @Date 2021/3/3
 * @Version V1.0
 **/
public class HighlightUtil {


    public static HighlightBuilder getHighlightBuilder(String ...field){

        HighlightBuilder highlightBuilder = new HighlightBuilder();

        Arrays.asList(field).stream().forEach(f -> {
            highlightBuilder.field(f);//设置需要高亮的字段
            highlightBuilder.preTags("<span style='color:red'>");//前置标签
            highlightBuilder.postTags("</span>");//后置标签
        });

        return highlightBuilder;
    }

    public static <T> List<T> getHighlightList(List<SearchHit<T>> searchHits){

        return searchHits.stream().map(searchHit -> {
            T content = searchHit.getContent();
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            highlightFields.forEach((key, value) -> {

                try {
                    //不建议使用拼接字符串的方式取首字母大写
                    //String.valueOf(key.toCharArray()[0]).toUpperCase() + key.substring(1) 这段代码就只是为了让首字母大写
                    //content.getClass() 得到类的类型
                    //getMethod()通过方法名得到方法
                    Method method = content.getClass().getMethod("set" + firstCharUpper(key), String.class);
                    //执行方法
                    method.invoke(content, value.get(0));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
            return content;
        }).collect(Collectors.toList());
    }

    /**
     * 将字符串首字母大写
     * ascll码表的值(小写英文字母的值) - 32 --> 大写字母的值
     * @param str
     * @return
     */
    public static String firstCharUpper(String str){

        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

}
