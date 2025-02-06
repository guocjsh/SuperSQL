package com.aispace.supersql.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor {
   public static String removeTags(String text) {
    // 处理 null 输入
    if (text == null) {
        return "";
    }

    // 定义一个更严格的正则表达式来匹配 <think> 和 </think> 标签及其内容
    // 这里假设标签内容不会超过 1000 个字符，可以根据实际需求调整
    String regex = "<think>(.*?)</think>";
    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
    Matcher matcher = pattern.matcher(text);

    // 使用 StringBuffer 来构建结果字符串
    StringBuffer result = new StringBuffer();

    // 替换匹配到的内容为空字符串
    while (matcher.find()) {
        matcher.appendReplacement(result, "");
    }
    matcher.appendTail(result);

    return result.toString();
}

 
    public static void main(String[] args) {
        String text = "这是一些文本。<think>这是需要被去除的内部逻辑</think>这是另一部分文本。";
        String processedText = removeTags(text);
        System.out.println(processedText);
    }
}