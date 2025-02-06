package com.aispace.supersql.service;

import java.util.List;
import java.util.Map;

public interface IExecuteSqlService {

    /**
     * 执行SQL语句
     *
     * 此方法用于执行给定的SQL语句，可以是查询、插入、更新或删除操作
     * 它不返回任何数据集相关的执行结果，仅执行提供的SQL命令
     *
     * @param sql 要执行的SQL语句，作为字符串传递
     * @return 返回一个Object类型的对象，具体返回值的类型和内容取决于SQL操作的类型和执行结果
     *
     * Execute SQL statement
     * This method is used to execute the given SQL statement, which can be a query, insert, update, or delete operation.
     * It does not return any dataset-related execution results; it only executes the provided SQL command.
     *
     * @param sql The SQL statement to be executed, passed as a string.
     * @return Returns an Object type object, the specific type and content of which depend on the type and execution result of the SQL operation.

     */
    List<Map<String,Object>> executeSql(String sql);
}
