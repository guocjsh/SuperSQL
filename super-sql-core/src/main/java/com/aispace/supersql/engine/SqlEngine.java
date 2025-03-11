package com.aispace.supersql.engine;

import com.aispace.supersql.builder.FollowupQuestionsBuilder;
import com.aispace.supersql.builder.TrainBuilder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author chengjie.guo
 */
public interface SqlEngine {

    /**
     * 根据给定的问题生成 SQL 查询。
     * 此方法接收一个字符串格式的问题，并生成一个能够回答该问题的 SQL 查询。
     * 可选地，允许语言模型 (LLM) 访问数据以生成更准确的 SQL 查询。
     *
     * @param question 要生成 SQL 查询的问题。
     * @return 回答问题的 SQL 查询。
     * <p>
     * Generates an SQL query based on a given question.
     * This method takes a question in string format and generates an SQL query that answers the question.
     * It can optionally allow the LLM (Language Model) to access the data for more accurate SQL generation.
     * @param question The question to generate an SQL query for.
     * @return The SQL query that answers the question.
     */
    String generateSql(String question);



    /**
     * 根据问题和对象生成摘要信息
     * 该方法旨在处理特定问题，并根据提供的对象生成相应的摘要信息
     * 例如，当需要从复杂数据结构中提取关键信息时，此方法可以根据问题的上下文
     * 生成一个简洁的摘要，以便于用户快速理解相关信息
     *
     * @param question 一个字符串，表示用户的问题或查询，用于指导摘要的生成
     * @param context 需要总结的内容，表示需要生成摘要的数据源
     * @return 返回一个字符串，表示根据问题和对象生成的摘要信息
     */
    Flux<ChatResponse> generateSummary(String question, String context);

    /**
     * 生成可以询问 SuperSQL 的跟进问题列表。
     * 该方法通过FollowupQuestionsBuilder构建并返回一个跟进问题的列表
     * 主要用于需要根据一定条件或参数动态生成问题列表的场景
     *
     * @param questionsBuilder FollowupQuestionsBuilder的实例，用于构建跟进问题
     * @return 返回一个可以询问 SuperSQL 的跟进问题列表。
     */
    List<String> followupQuestions(FollowupQuestionsBuilder questionsBuilder);


    /**
     * 生成Echarts的JSON数据。
     * 该方法接收一个对象，并使用Echarts的JSON格式生成一个JSON对象。
     * 主要用于将数据转换为Echarts的JSON格式，以便于可视化展示。
     *
     * @param data 需要转换为Echarts的JSON格式的对象。
     * @return 返回一个Echarts的JSON格式的JSON对象。
     */
    JSONObject generateEcharsJson(Object data);

    /**
     * 生成重写的问题。
     * 如果新问题与上一个问题相关，则将两个问题合并生成一个重写的问题。
     * 如果新问题是独立的且与上一个问题无关，则返回新问题。
     *
     * @param lastQuestion 上一个问题。
     * @param newQuestion 新问题。
     * @return 合并后的问题（如果相关），否则返回新问题。
     */
    String rewrittenQuestion(String lastQuestion, String newQuestion);


    /**
     * 验证给定的 SQL 查询是否有效。
     * 此方法接收一个字符串格式的 SQL 查询，并验证它是否有效。
     *
     * @param sql 要验证的 SQL 查询。
     * @return 如果 SQL 查询有效，则返回 true；否则返回 false。
     * <p>
     * Validates a given SQL query.
     * This method takes a SQL query in string format and validates whether it is valid.
     * @param sql The SQL query to validate.
     * @return True if theSQL query is valid, False otherwise.
     **/
    Boolean validSql(String sql);


    /**
     * 训练模型。
     *
     * 该方法用于根据提供的问题、SQL查询、DDL语句、文档和训练计划来训练模型。
     * 它旨在增强模型根据输入理解和生成适当的SQL查询或响应的能力。
     *
     * @param train 用于训练的参数。
     * Trains the model.
     *
     * This method is used to train the model based on the provided question, SQL query, DDL statement, documentation, and training plan.
     * It aims to enhance the model's ability to understand and generate appropriate SQL queries or responses based on the input.
     *
     * @param train The params to train on.
     */
    void train(TrainBuilder train);

}
