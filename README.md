<p align="center">
	<img alt="logo" src="https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/logo.png" width="200" height="60">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Super Sql 1.0.0-M1</h1>
<h4 align="center">中国人自己的生成式SQL Java框架！轻量，易用，易扩展!</h4>

---

### Super-Sql 介绍
Super-Sql 是一个基于国内外先进生成式大模型的Java框架，专注于将数据库表结构通过检索增强生成（RAG, Retrieval-Augmented Generation）技术进行训练，从而实现从自然语言文本到SQL查询的智能转换（Text to SQL）。该框架旨在简化复杂的数据库查询过程，使开发者和用户能够通过简单的自然语言描述获取所需数据。

主要特性包括：

- **生成式SQL**：利用强大的生成式大模型，自动将自然语言问题转化为精确的SQL查询语句。
- **RAG训练**：通过检索增强生成技术对数据库表结构进行深度学习训练，提高SQL生成的准确性和效率。
- **类型安全与灵活易用**：结合Java的泛型机制确保编译期类型检查，同时提供简洁直观的API设计，易于集成到现有项目中。
- **多数据库支持**：兼容多种主流数据库系统，满足不同应用场景的需求。
- **性能优化**：经过精心设计与调优，在保证高效执行的同时保持良好的可读性。

Super-Sql 适用于希望在Java应用程序中快速、安全地进行复杂数据库操作，并且希望通过自然语言处理技术为传统企业应用快速的实现AI赋能。



### Super-Sql 工作原理

Super-Sql 的工作原理基于 RAG 技术，通过检索增强生成技术对数据库表结构进行深度学习训练，从而实现从自然语言文本到SQL查询的智能转换。

![super-sql-ai](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/how-to-work.png)


### Super-Sql 快速开始

#### 导入Super-Sql的maven依赖

```xml
<dependency>
    <groupId>com.aispace.supersql</groupId>
    <artifactId>super-sql-spring-boot-starter</artifactId>
    <version>1.0.0-M1-SNAPSHOT</version>
</dependency>

```

### 配置Super-Sql的配置文件

配置init-train配置项，默认为false，表示不进行训练，如果为true，则自动根据数据库连接配置进行全表训练。
```yaml
super-sql:
  init-train: false
```


### 大语言模型配置

#### Azure OpenAI
```xml
 <!--spring ai azure openai 模型-->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>
</dependency>
```
azure的配置文件配置如下：
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

请求调用Text To SQL
```java
//使用azure的Chat模型
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


#### Ollama
添加ollam的spring ai依赖
```xml
<!--spring ai azure ollama-->
<dependency>
   <groupId>org.springframework.ai</groupId>
   <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
</dependency>
```
ollama的配置文件配置如下：

#### deepseek

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
embedding模型配置

```yaml
spring:
  ai:
    ollama:
      embedding:
        options:
          model: mxbai-embed-large
      init:
        pull-model-strategy: never
        embedding:
          additional-models:
            - mxbai-embed-large
```
参考修改Embedding模型[Ollama Embedding Models](https://ollama.com/search?c=embedding)

请求调用Text To SQL
```java
//使用azure的Chat模型
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

### 向量数据库配置

#### Chroma
```xml
<!--spring ai chroma 的向量数据库-->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-chroma-store-spring-boot-starter</artifactId>
</dependency>
```

#### 其他支持的向量数据库

参考[Spring AI Vector Databases](https://docs.spring.io/spring-ai/reference/1.0/api/vectordbs.html)


### 请求结果返回示例
![请求示例](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/post-example.jpg)

### 训练指定内容

#### 强化训练数据库的DDL语句
```java
 @GetMapping("trainDdl")
    public String trainDDl() {
        String ddl = """
                 CREATE TABLE `dtp_hospital` (
                 	`id` BIGINT NOT NULL COMMENT '主键',
                 	`province` VARCHAR ( 20 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '省份',
                 	`city` VARCHAR ( 20 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '城市',
                 	`reporting_team` VARCHAR ( 50 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '提报团队',
                 	`district` VARCHAR ( 20 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '区',
                 	`hospital_name` VARCHAR ( 100 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '申请的DTP药房 主要对应的医院名称',
                 	`hospital_code` VARCHAR ( 50 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '申请DTP主要对应的医院code',
                 	`hospital_address` VARCHAR ( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '医院具体地址',
                 	`location` VARCHAR ( 100 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '医院所在经纬度',
                 	`del_flag` INT NULL DEFAULT 0 COMMENT '删除状态 0正常 1已删除',
                 	`create_by` VARCHAR ( 32 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                 	`create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                 	`update_by` VARCHAR ( 32 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                 	`update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                 	`image` VARCHAR ( 255 ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '图片',
                 	PRIMARY KEY ( `id` ) USING BTREE )
                 	ENGINE = INNODB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '医院表' ROW_FORMAT = Dynamic;
                """;
        sqlEngine.setChatModel(azureChatModel).train(TrainBuilder.builder().content(ddl).policy(TrainPolicyType.DDL).build());
        return "successful training";
    }
```
#### 单独训练指定SQL
```java
  @GetMapping("trainSql")
    public String trainSql() {
        String sql="SELECT * FROM DTP_HOSPITAL WHERE DISTRICT LIKE '%黄浦区%';";
        String question="在黄浦区的医院有哪些？";
        sqlEngine.setChatModel(azureChatModel).train(TrainBuilder.builder().content(sql).question(question).policy(TrainPolicyType.SQL).build());
        return "successful training";
    }

```

### 启动SuperSQL控制台项目


启动super-sql-console的springboot项目

导入super-sql-ui项目到vscode

```bash
npm install
npm run dev
```
访问：[http://localhost:5173/chat](http://localhost:5173/chat)


![请求示例](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/WechatIMG1674.jpg)

数据库中执行的效果

![请求示例](https://tiger-bucket.oss-cn-shanghai.aliyuncs.com/super-sql/WechatIMG1675.jpg)



### SuperSQL官方文档

访问：[http://www.ai-space.com.cn/](http://www.ai-space.com.cn/)

### 参考文档
Spring-Ai的官方文档：[https://docs.spring.io/spring-ai/docs/getting-started](https://docs.spring.io/spring-ai/docs/getting-started)


### 友情链接
- [[ Spring-AI ]](https://spring.io/projects/spring-ai)：Spring AI是一个用于AI工程的应用框架。它的目标是将Spring生态系统设计原则（如可移植性和模块化设计）应用于AI领域，并促进将pojo作为AI领域应用程序的构建块。



### 代码托管
- Gitee：[https://gitee.com/guocjsh/super-sql](https://gitee.com/guocjsh/super-sql)


