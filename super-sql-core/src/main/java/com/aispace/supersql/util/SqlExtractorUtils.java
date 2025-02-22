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
        //llmResponse = TextProcessor.removeTags(llmResponse);
        // Define patterns
        Pattern withClausePattern = Pattern.compile("\\bWITH\\b .*?;", Pattern.DOTALL);
        Pattern selectPattern = Pattern.compile("SELECT.*?;", Pattern.DOTALL);
        Pattern markdownSqlPattern = Pattern.compile("sql\n(.*?)", Pattern.DOTALL);
        Pattern markdownPattern = Pattern.compile("(.*?)", Pattern.DOTALL);

        Pattern markdownSqlPattern2 = Pattern.compile("(?s)```sql\\s*\\n(.*?)\\s*```", Pattern.DOTALL); // DOTALL使.匹配包括换行符在内的所有字符

        // Extract SQL using patterns
        String sql = extractSqlUsingPattern(llmResponse, withClausePattern, "WITH clause");
        if (sql != null) {
            return sql;
        }

        sql = extractSqlUsingPattern(llmResponse, selectPattern, "SELECT statement");
        if (sql != null) {
            return sql;
        }

        sql = extractSqlUsingPattern(llmResponse, markdownSqlPattern2, "Markdown SQL block");
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


    public static String extractSqlbb(String text) {
        // 使用正则表达式匹配SQL语句
        Pattern sqlPattern =  Pattern.compile("(?i)\\b(SELECT|INSERT|UPDATE|DELETE|CREATE|ALTER|DROP)\\b[^;]*;",Pattern.DOTALL);
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        Matcher matcher = sqlPattern.matcher(text);
        StringBuilder sqlBuilder = new StringBuilder();

        while (matcher.find()) {
            sqlBuilder.append(matcher.group()).append("\n");
        }

        return sqlBuilder.toString().trim();
    }

    public static void main(String[] args) {
        String r= """
                <think>
                好的，我需要帮用户生成一个SQL查询，查找距离中山医院附近五公里内支持双通道的药店。首先，我得看一下已有的表结构。

                用户之前给出的表包括dtp_hospital、dhp_to_dpp_distance等。根据之前的回答，用户使用了DTP_HOSPITAL和H_P_DISTANCE表，但现在提供的上下文中，H_P_DISTANCE可能不存在，取而代之的是dhp_to_dpp_distance表或者dhp_to_dpp_distance_11h。

                查看dhp_to_dpp_distance表的列，发现有hospital_name（医院名称）、distance（距离）、dual_channel（是否双通道药店）。表中还有dtp_org_name（药店机构名称）。这个表似乎直接关联了医院和药房的距离，以及药房的双通道属性。

                所以正确的做法应该是从dhp_to_dpp_distance表中筛选出hospital_name包含“中山医院”，distance小于等于5公里，且dual_channel为1（表示支持双通道）的记录，并选择药房的相关信息如机构名称、地址等。这样就不需要做多表连接，因为dhp_to_dpp_distance已经包含了必要的信息。

                之前的查询可能使用了不同的表结构，但现在根据提供的上下文，dhp_to_dpp_distance表已经包含所需的所有字段，因此直接查询该表即可。需要确认dual_channel的值是否为1表示支持双通道，根据注释确实如此。因此最终SQL应为从dhp_to_dpp_distance中选择符合条件的记录，显示药房名称和地址等信息。
                </think>

                ```sql
                SELECT
                    dtp_org_name AS pharmacy_name,
                    org_address AS pharmacy_address,
                    distance
                FROM
                    dtp.dhp_to_dpp_distance
                WHERE
                    hospital_name LIKE '%中山医院%'
                    AND distance <= 5
                    AND dual_channel = 1
                ```

                """;

        String s = extractSql(r);
        System.out.println(s);



    }

}
