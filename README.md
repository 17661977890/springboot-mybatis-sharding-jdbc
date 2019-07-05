# springBoot-mybatis-sharding-jdbc的单库分表和分库分表

## 概述：
* ShardingSphere，它由Sharding-JDBC、Sharding-Proxy和Sharding-Sidecar（计划中）这3款相互独立的产品组成。定位为轻量级Java框架。其实就是一个增强版的JDBC驱动，完全兼容JDBC和各种ORM框架。内部改写了SQL的添加和查询规则。适用于任何基于Java的ORM框架，如：JPA, Hibernate, Mybatis, Spring JDBC Template或直接使用JDBC。
* 目前已经进入Apache孵化器。以4.x版本为新的发布开始

![image](https://camo.githubusercontent.com/97a4eac3a5eac7cad65d3e85ad0865df4cc6923a/687474703a2f2f7778312e73696e61696d672e636e2f6c617267652f30303662374e786e677931673266373579697674736a33316375306a793077392e6a7067)

### 测试---单库分表
* 1、首先集成一个不分库只分表的模式。创建一个springboot项目，这里使用Sharding-JDBC3.0版本。使用sharding-jdbc-spring-boot-starter集成

* 2、创建测试数据局test_order。分别创建三张表t_address， t_user0，t_user1。这里假设t_user这个预计随着系统的运行。公司发展很好，以后数据量会暴增。所以提前进行水平分片存储。相对于垂直分片，它不再将数据根据业务逻辑分类，而是通过某个字段（或某几个字段），根据某种规则将数据分散至多个库或表中，每个分片仅包含数据的一部分。这样单表数据量降下来了，mysql的B+树的检索效率就提高了

* 3、在dev 配置文件中配置单库分表的规则（分片存储的表规则）

* 3.1、行表达式标识符可以使用${...}或$->{...}，但前者与Spring本身的属性文件占位符冲突，因此在Spring环境中使用行表达式标识符建议使用$->{...}。

* 4、写完基本的crud操作，postman进行测试:http://localhost:8080/user/save    ---项目启动指定配置文件指向dev
![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705110702.png)

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705110712.png)

### 测试---分库分表
* 1、既分库又分表其实只需要在配置文件修改一个分片规则即可，不用修改业务任何代码。分库分表的数据表不能用自增主键，Sharding-JDBC会自动分配一个id，默认使用雪花算法（snowflake）生成64bit的长整型数据。

* 2、创建测试数据局order1 order2。分别创建三张表t_address， t_user0，t_user1

* 3、在test 配置文件中配置t_user的分库库分表的规则(根据city_id 分库-根据sex 分表)

* 4、postman进行测试:http://localhost:8080/user/save2    ---项目启动指定配置文件指向test
![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705113431.png)

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705113453.png)

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705113455.png)

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705113458.png)
