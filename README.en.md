<p align="center">
	<img alt="logo" src="https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/logo.png" width="200" height="60">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Super SQL 1.0.0-M1</h1>
<h4 align="center">A Lightweight, Easy-to-Use, and Extensible SQL Generation Framework for Java!</h4>
<p align="center">
  <a href="./README.en.md"><img alt="README in English" src="https://img.shields.io/badge/English-d9d9d9"></a>
  <a href="./README.md"><img alt="简体中文版自述文件" src="https://img.shields.io/badge/简体中文-d9d9d9"></a>
</p>

---

### Introduction to SuperSQL

SuperSQL is a Java framework for NL2SQL (Natural Language to SQL) based on advanced generative large models from domestic and international sources. It focuses on transforming database schema into SQL queries using Retrieval-Augmented Generation (RAG) technology. This framework aims to simplify complex database query processes, allowing developers and users to retrieve required data through simple natural language descriptions.

#### Key Features:
- **Generative SQL**: Utilizes powerful generative large models to automatically convert natural language questions into precise SQL query statements.
- **RAG Training**: Deep learning training of database schema using RAG technology to improve the accuracy and efficiency of SQL generation.
- **Type Safety & Easy to Use**: Combines Java generics to ensure compile-time type checking, while providing a simple and intuitive API design that is easy to integrate into existing projects.
- **Multi-Database Support**: Compatible with multiple mainstream database systems to meet the needs of different application scenarios.
- **Performance Optimization**: Carefully designed and optimized to ensure efficient execution while maintaining good readability.

SuperSQL is suitable for developers who want to quickly and securely perform complex database operations in Java applications and wish to leverage AI capabilities through natural language processing technology.

---

### How SuperSQL Works

SuperSQL is based on **RAG** technology. It uses retrieval-augmented generation to train on database schema and achieve intelligent conversion from natural language text to SQL queries.

![super-sql-ai](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/how-to-work.png)

---

### Quick Start

#### Maven Dependency

To use SuperSQL, add the following Maven dependency to your [pom.xml](file://D:\IronGuo\open\super-sql\pom.xml):

```xml
<dependency>
    <groupId>com.aispace.supersql</groupId>
    <artifactId>super-sql-spring-boot-starter</artifactId>
    <version>1.0.0-M1</version>
</dependency>
```


#### Configuration

Set the `init-train` configuration item in your [application.yml](file://D:\IronGuo\open\super-sql\super-sql-console\src\main\resources\application.yml). The default value is `false`, meaning no training is performed. If set to `true`, it will automatically train based on the database connection configuration.

```yaml
super-sql:
  init-train: false
```


---

### Large Language Model Configuration

#### Azure OpenAI

Add the Azure OpenAI dependency:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-azure-openai</artifactId>
</dependency>
```


Azure configuration in `application.yml`:

```yaml
ai:
  azure:
    openai:
      api-key: xxxxxxxxxxxxxx
      chat:
        options:
          deployment-name: gpt-4o-latest
      endpoint: https://your-resource-name.openai.azure.com/
      embedding:
        options:
          deployment-name: embedding-ada-002
```


Example usage:

```java
private final AzureOpenAiChatModel azureChatModel;
private final SpringSqlEngine sqlEngine;
private final SpringVectorStore store;

@GetMapping("getSuperSql")
public Object getSuperSql(@RequestParam String question) {
    String sql = sqlEngine.setChatModel(azureChatModel)
            .setOptions(RagOptions.builder()
                    .topN(5)
                    .rerank(false)
                    .limitScore(0.4)
                    .build())
            .generateSql(question);
    Object object = sqlEngine.executeSql(sql);
    return object;
}
```

#### Enable ReRanker Re-ranking Configuration
```
spring:
  ai:
     # Re-ranking configuration. You can use the free trial on Gitee AI or deploy Xinference locally.
    reranker:
      enabled: true
      model: Qwen3-Reranker-8B
      baseUrl: https://ai.gitee.com/v1/rerank #http://localhost:9997/v1/rerank
      api-key: xxxxxxxxxx
```

#### Ollama

Add the Ollama dependency:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```


Ollama configuration:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: deepseek-r1:32b
          temperature: 0.7
      embedding:
        options:
          model: mxbai-embed-large
      init:
        pull-model-strategy: never
        embedding:
          additional-models:
            - mxbai-embed-large
```


Example usage:

```java
private final OllamaChatModel chatModel;
private final SpringSqlEngine sqlEngine;
private final SpringVectorStore store;

@GetMapping("getSuperSql")
public Object getSuperSql(@RequestParam String question) {
    String sql = sqlEngine.setChatModel(chatModel).generateSql(question);
    Object object = sqlEngine.executeSql(sql);
    return object;
}
```


---

### Vector Database Configuration

#### Chroma

Add the Chroma dependency:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-chroma</artifactId>
</dependency>
```


Start local Chroma using Docker:

```shell
docker run -it --rm --name chroma -p 8000:8000 ghcr.io/chroma-core/chroma:1.0.0
```


#### Other Supported Vector Databases

Refer to [Spring AI Vector Databases](https://docs.spring.io/spring-ai/reference/1.0/api/vectordbs.html)

---

### Example Response

![Request Example](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/post-example.jpg)

---

### Custom Training

#### Train with DDL Statements

```java
@GetMapping("trainDdl")
public String trainDDl() {
    String ddl = """
        CREATE TABLE `dtp_hospital` (
        ...
        ) ENGINE = INNODB ...;
    """;
    sqlEngine.setChatModel(azureChatModel).train(TrainBuilder.builder().content(ddl).policy(TrainPolicyType.DDL).build());
    return "successful training";
}
```


#### Train with Specific SQL

```java
@GetMapping("trainSql")
public String trainSql() {
    String sql="SELECT * FROM DTP_HOSPITAL WHERE DISTRICT LIKE '%Huangpu%';";
    String question="Which hospitals are in Huangpu District?";
    sqlEngine.setChatModel(azureChatModel).train(TrainBuilder.builder().content(sql).question(question).policy(TrainPolicyType.SQL).build());
    return "successful training";
}
```


---

### Launch SuperSQL Console

Start the `super-sql-console` Spring Boot project.

Import the `super-sql-ui` project into VSCode:

```bash
npm install
npm run dev
```


Visit: [http://localhost:5173/chat](http://localhost:5173/chat)

![Request Example](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/WechatIMG1674.jpg)

Result in the database:

![Request Example](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/WechatIMG1675.jpg)

---

### Official Documentation

Visit: [http://www.ai-space.com.cn/](http://www.ai-space.com.cn/)

### Reference Documentation

Spring-AI Official Documentation: [https://docs.spring.io/spring-ai/docs/getting-started](https://docs.spring.io/spring-ai/docs/getting-started)

### Links

- [Spring-AI](https://spring.io/projects/spring-ai): Spring AI is an application framework for AI engineering. Its goal is to apply Spring ecosystem design principles (such as portability and modular design) to the AI field and promote the use of POJOs as building blocks for AI applications.

---

### Source Code

- Gitee: [https://gitee.com/guocjsh/super-sql](https://gitee.com/guocjsh/super-sql)
- GitHub: [https://github.com/guocjsh/SuperSQL](https://github.com/guocjsh/SuperSQL)
- GitCode: [https://gitcode.com/GuoChengJie/SuperSQL](https://gitcode.com/GuoChengJie/SuperSQL)
