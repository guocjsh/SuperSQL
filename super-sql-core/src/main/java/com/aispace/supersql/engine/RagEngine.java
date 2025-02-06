package com.aispace.supersql.engine;

import java.io.File;

/**
 * @author chengjie.guo
 */
public interface RagEngine {


    /**
     * 向训练数据添加 DDL 语句。
     * <p>
     * 该方法用于接收一个 DDL（数据定义语言）语句作为参数，并将其添加到训练数据中。
     * 其目的是更新或修改训练数据库的结构。
     * @param ddl 要添加的 DDL 语句，字符串格式。
     * <p>
     * Adds a DDL statement to the training data.
     * <p>
     * This method is used to receive a DDL (Data Definition Language) statement as a parameter and add it to the training data.
     * The purpose is to update or modify the structure of the training database.
     * @param ddl The DDL statement to add, in string format.
     */
    void addDdl(String ddl);


    /**
     * 向训练数据添加问题及其对应的 SQL 查询。
     * <p>
     * 该方法用于通过添加问题和 SQL 查询来扩展训练数据集，以便后续对模型进行训练和优化。
     * 该方法接受两个参数：问题和 SQL 查询，并且不返回任何值。
     *
     * @param question 要添加的问题，应为字符串格式。
     * @param sql      要添加的 SQL 查询，也应为字符串格式。
     *
     * Adds a question and its corresponding SQL query to the training data.
     * <p>
     * This method is used to expand the training dataset by adding questions and SQL queries for subsequent training and optimization of the model.
     * It accepts two parameters: the question and the SQL query, and does not return any value.
     *
     * @param question The question to add, which should be a string.
     * @param sql      The SQL query to add, which should also be a string.
     */
    void addQuestionSql(String question, String sql);


    /**
     * 向训练数据添加文档。
     * <p>
     * 该方法用于接收一个文档字符串作为参数，并将其添加到训练数据中。
     * 其目的是为模型提供额外的训练数据，以增强模型的理解能力。
     *
     * @param documentation 要添加的文档，应为字符串格式。
     *
     * Adds a documentation string to the training data.
     * <p>
     * This method is used to receive a documentation string as a parameter and add it to the training data.
     * The purpose is to provide additionaltraining data to the model to enhance its understanding.
     * @param documentation The documentation to add, which should be a string.
     **/
    void addDocumentation(String documentation);


    /**
     * 向训练数据添加文档。
     * <p>
     * 该方法用于接收一个文档文件作为参数，并将其添加到训练数据中。
     * 其目的是为模型提供额外的训练数据，以增强模型的理解能力。
     *
     * @param documentation 要添加的文档，应为文件格式。
     *
     * Adds a documentation file to the training data.
     * <p>
     * This method is used to receive a documentation file as a parameter and add it to the training data.
     * The purpose is to provide additionaltraining data to the model to enhance its understanding.
     **/
    void addDocumentation(File documentation);


}
