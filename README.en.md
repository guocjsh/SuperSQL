<p align="center">
	<img alt="logo" src="https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/logo_superai.gif" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Super SQL 1.0.0-M1</h1>
<h4 align="center">A Lightweight, Easy-to-Use, and Extensible SQL Generation Framework for Java!</h4>

---
### Introduction to Super SQL

Super SQL is a Java framework based on advanced generative large models, focusing on transforming database table structures through Retrieval-Augmented Generation (RAG) technology into intelligent conversions from natural language text to SQL queries (Text to SQL). This framework aims to simplify complex database query processes, enabling developers and users to obtain the required data through simple natural language descriptions.

Key features include:

- **Generative SQL**: Automatically converts natural language questions into precise SQL query statements using powerful generative large models.
- **RAG Training**: Trains database table structures using retrieval-enhanced generation technology to improve the accuracy and efficiency of SQL generation.
- **Type Safety and Ease of Use**: Combines Java's generics mechanism to ensure compile-time type checking, while providing a concise and intuitive API design that is easy to integrate into existing projects.
- **Multi-database Support**: Compatible with multiple mainstream database systems to meet the needs of different application scenarios.
- **Performance Optimization**: Carefully designed and optimized to ensure efficient execution while maintaining good readability.

Super SQL is suitable for applications that require fast and secure complex database operations in Java, and wish to quickly empower traditional enterprise applications with AI through natural language processing technology.

### How Super SQL Works

Super SQL operates based on RAG technology, training database table structures using retrieval-enhanced generation techniques to achieve intelligent conversion from natural language text to SQL queries.

![super-sql-ai](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/how-to-work.png)

### Super-Sql Quick Start
Add the Maven dependency for Super SQL:

```xml
<dependency>
    <groupId>com.aispace.supersql</groupId>
    <artifactId>super-sql-spring-boot-starter</artifactId>
    <version>1.0.0-M1-SNAPSHOT</version>
</dependency>

```

Add dependencies for specific models:

```xml
<!-- Spring AI Azure OpenAI model -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>
</dependency>

<!-- Spring AI Chroma vector database -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-chroma-store-spring-boot-starter</artifactId>
</dependency>

```


```java
// Using Azure Chat model
private final AzureOpenAiChatModel azureChatModel;

private final SpringSqlEngine sqlEngine;

private final SpringVectorStore store;

@GetMapping("getSuperSql")
public Object getSuperSql(@RequestParam String question) {
    String sql = sqlEngine.setChatModel(azureChatModel).generateSql(question);
    Object object = sqlEngine.executeSql(sql);
    return object;
}
```
Spring-Ai official documentation：[https://docs.spring.io/spring-ai/docs/getting-started](https://docs.spring.io/spring-ai/docs/getting-started)

### Related Links
- [[ Spring-AI ]](https://spring.io/projects/spring-ai): Spring AI is an application framework for AI engineering. Its goal is to apply Spring ecosystem design principles (such as portability and modular design) to the AI field, promoting the use of POJOs as building blocks for AI applications.

### Code Repository
- Gitee: [https://gitee.com/guocjsh/super-sql](https://gitee.com/guocjsh/super-sql)