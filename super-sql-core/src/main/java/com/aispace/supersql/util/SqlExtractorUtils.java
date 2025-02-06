package com.aispace.supersql.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The SqlExtractorUtils class provides methods for extracting SQL statements from a given response string.
 * @author chengjie.guo
 * @since 2025/01/07
 */
@Slf4j
public class SqlExtractorUtils {

    /**
     * Extracts SQL statements from the specified llmResponse string.
     *
     * This method attempts to extract SQL statements using multiple regular expression patterns.
     * The order of extraction is as follows: WITH clause, SELECT statement, Markdown SQL block, Markdown block.
     * If SQL is successfully extracted using any pattern, it returns the extracted SQL statement.
     * If no SQL can be extracted using any pattern, it returns the original llmResponse string.
     *
     * @param llmResponse The string from which to extract the SQL statement.
     * @return The extracted SQL statement or the original llmResponse string if no SQL can be extracted.
     */
    public static String extractSql(String llmResponse) {
        llmResponse = TextProcessor.removeTags(llmResponse);
        // Define patterns
        Pattern withClausePattern = Pattern.compile("\\bWITH\\b .*?;", Pattern.DOTALL);
        Pattern selectPattern = Pattern.compile("SELECT.*?;", Pattern.DOTALL);
        Pattern markdownSqlPattern = Pattern.compile("sql\n(.*?)", Pattern.DOTALL);
        Pattern markdownPattern = Pattern.compile("(.*?)", Pattern.DOTALL);

        // Extract SQL using patterns
        String sql = extractSqlUsingPattern(llmResponse, withClausePattern, "WITH clause");
        if (sql != null) {
            return sql;
        }

        sql = extractSqlUsingPattern(llmResponse, selectPattern, "SELECT statement");
        if (sql != null) {
            return sql;
        }

        sql = extractSqlUsingPattern(llmResponse, markdownSqlPattern, "Markdown SQL block");
        if (sql != null) {
            return sql;
        }

        sql = extractSqlUsingPattern(llmResponse, markdownPattern, "Markdown block");
        if (StrUtil.isNotEmpty(sql)) {
            return sql;
        }

        return llmResponse;
    }

    /**
     * Extracts SQL statements using the specified pattern.
     *
     * This method uses regular expressions to match and extract SQL statements.
     * If a match is found, it logs the extracted SQL statement and returns it.
     *
     * @param llmResponse The string from which to extract the SQL statement.
     * @param pattern The regular expression pattern used for extraction.
     * @param patternDescription The description of the pattern, used for logging.
     * @return The extracted SQL statement or null if no match is found.
     */
    private static String extractSqlUsingPattern(String llmResponse, Pattern pattern, String patternDescription) {
        Matcher matcher = pattern.matcher(llmResponse);
        if (matcher.find()) {
            // 检查是否存在捕获组
            if (matcher.groupCount() > 0) {
                String sql = matcher.group(1);
                log.info("{}: {}", "Extracted SQL", sql);
                return sql;
            } else {
                // 如果没有捕获组，返回整个匹配的内容
                String sql = matcher.group();
                log.info("{}: {}", "Extracted SQL", sql);
                return sql;
            }
        }
        return null;
    }

}
