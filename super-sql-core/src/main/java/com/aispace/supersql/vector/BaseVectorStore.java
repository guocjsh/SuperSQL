package com.aispace.supersql.vector;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter.Expression;

import java.util.List;
import java.util.Optional;

/**
 * 向量数据库交互操作
 * @author chengjie.guo
 */
public interface BaseVectorStore {

    /**
     * 向文档集合中添加多个文档
     * 此方法主要用于批量添加文档到文档集合中，便于一次性处理多个文档的场景
     *
     * @param documents 一个Document对象的列表，代表待添加的文档集合
     */
    void add(List<Document> documents);


    /**
     * 删除指定的一组记录
     * 此方法接受一个字符串ID列表，用于指定待删除的记录
     * 它返回一个Optional布尔值，表示删除操作是否成功
     * 使用Optional包装返回值是为了处理可能的空值情况，提供更安全的空值处理方式
     *
     * @param idList 要删除的记录的ID列表，不能为null
     */
    void delete(List<String> idList);


    /**
     * 执行相似性搜索的函数
     *
     * 该函数旨在根据提供的查询字符串，从文档集合中找出与查询内容相似的文档
     * 它通常用于信息检索系统中，以帮助用户找到与他们查询意图最匹配的文档
     *
     * @param query 查询字符串，用户输入的用于搜索的关键词或短语
     * @return 返回一个文档列表，这些文档与查询字符串具有最高的相似度，按照相关性排序
     */
    List<Document> similaritySearch(String query);

    /**
     * 执行相似性搜索的函数
     * 根据给定的搜索请求，从文档集合中找出与请求相似的文档
     *
     * @param request 搜索请求对象，包含搜索所需的信息，如查询向量、搜索范围等
     * @return 返回一个文档列表，这些文档与搜索请求具有较高的相似度
     */
    List<Document> similaritySearch(SearchRequest request);

    /**
     * 根据标签搜索文档
     *
     * @param query   查询字符串
     * @param tag     标签
     * @param topK    返回的文档数量
     * @return 返回一个文档列表，这些文档与查询字符串具有较高的相似度，并按照相关性排序
     */
    List<Document> searchByTag(String query,String tag,Integer topK);


    /**
     * 根据标签搜索文档
     *
     * @param query   查询字符串
     * @param expression     标签
     * @param topK    返回的文档数量
     * @return 返回一个文档列表，这些文档与查询字符串具有较高的相似度，并按照相关性排序
     */
    List<Document> searchByTag(String query, Expression expression, Integer topK);


    /**
     * 根据表达式搜索文档
     * 此方法使用一个表达式对象作为参数，该表达式定义了搜索的条件和范围
     * 它通过解析表达式并根据其条件检索相关文档，返回一个文档列表
     * 主要用于执行复杂的查询操作，允许用户根据特定需求定制搜索条件
     *
     * @param expression 表达式对象，定义了搜索的条件和范围不能为null
     * @return 返回一个文档列表，这些文档满足由表达式定义的搜索条件
     *         如果没有文档匹配，将返回一个空列表
     */
    List<Document> searchByExpression(String query,Expression expression);
}
