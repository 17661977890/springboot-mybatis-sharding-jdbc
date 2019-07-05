# springBoot-mybatis-sharding-jdbc的单库分表和分库分表

## 一、概述：
* ShardingSphere，它由Sharding-JDBC、Sharding-Proxy和Sharding-Sidecar（计划中）这3款相互独立的产品组成。定位为轻量级Java框架。其实就是一个增强版的JDBC驱动，完全兼容JDBC和各种ORM框架。内部改写了SQL的添加和查询规则。适用于任何基于Java的ORM框架，如：JPA, Hibernate, Mybatis, Spring JDBC Template或直接使用JDBC。
* 目前已经进入Apache孵化器。以4.x版本为新的发布开始

* 官方网站：https://shardingsphere.apache.org/document/legacy/3.x/document/cn/overview/

* 配置文件注释配置 可以去官网查看具体配置意义

![image](https://camo.githubusercontent.com/97a4eac3a5eac7cad65d3e85ad0865df4cc6923a/687474703a2f2f7778312e73696e61696d672e636e2f6c617267652f30303662374e786e677931673266373579697674736a33316375306a793077392e6a7067)


### （1）测试---单库分表
* 1、首先集成一个不分库只分表的模式。创建一个springboot项目，这里使用Sharding-JDBC3.0版本。使用sharding-jdbc-spring-boot-starter集成

* 2、创建测试数据局test_order。分别创建三张表t_address， t_user0，t_user1。这里假设t_user这个预计随着系统的运行。公司发展很好，以后数据量会暴增。所以提前进行水平分片存储。相对于垂直分片，它不再将数据根据业务逻辑分类，而是通过某个字段（或某几个字段），根据某种规则将数据分散至多个库或表中，每个分片仅包含数据的一部分。这样单表数据量降下来了，mysql的B+树的检索效率就提高了

* 3、在dev 配置文件中配置单库分表的规则（分片存储的表规则）

* 3.1、行表达式标识符可以使用${...}或$->{...}，但前者与Spring本身的属性文件占位符冲突，因此在Spring环境中使用行表达式标识符建议使用$->{...}。

* 4、写完基本的crud操作，postman进行测试:http://localhost:8080/user/save    ---项目启动指定配置文件指向dev

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705110702.png)

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705110712.png)

### （2）测试---分库分表
* 1、既分库又分表其实只需要在配置文件修改一个分片规则即可，不用修改业务任何代码。分库分表的数据表不能用自增主键，Sharding-JDBC会自动分配一个id，默认使用雪花算法（snowflake）生成64bit的长整型数据。

* 2、创建测试数据局order1 order2。分别创建三张表t_address， t_user0，t_user1

* 3、在test 配置文件中配置t_user的分库库分表的规则(根据city_id 分库-根据sex 分表)

* 4、postman进行测试:http://localhost:8080/user/save2    ---项目启动指定配置文件指向test

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705113431.png)

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705113453.png)

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705113455.png)

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190705113458.png)

## 二、分库分表：
* 一、背景
随着时间和业务的发展，数据库中的数据量增长是不可控的，库和表中的数据会越来越大，随之带来的是更高的磁盘、IO、系统开销，甚至性能上的瓶颈，而一台服务的资源终究是有限的，因此需要对数据库和表进行拆分，从而更好的提供数据服务。
当用户表达到千万级别，在做很多操作的时候都会很吃力，所以当数据增长到1000万以上就需要分库分表来缓解单库（表）的压力。
* 二、什么是分库分表[1]

简单来说，就是指通过某种特定的条件，将我们存放在同一个数据库中的数据分散存放到多个数据库（主机）上面，以达到分散单台设备负载的效果。
数据的切分（Sharding）根据其切分规则的类型，可以分为两种切分模式。一种是按照不同的表（或者Schema）来切分到不同的数据库（主机）之上，这种切可以称之为数据的垂直（纵向）切分；另外一种则是根据表中的数据的逻辑关系，将同一个表中的数据按照某种条件拆分到多台数据库（主机）上面，这种切分称之为数
据的水平（横向）切分。
垂直切分的最大特点就是规则简单，实施也更为方便，尤其适合各业务之间的耦合度非常低，相互影响很小，业务逻辑非常清晰的系统。在这种系统中，可以很容易做到将不同业务模块所使用的表分拆到不同的数据库中。根据不同的表来进行拆分，对应用程序的影响也更小，拆分规则也会比较简单清晰。
水平切分于垂直切分相比，相对来说稍微复杂一些。因为要将同一个表中的不同数据拆分到不同的数据库中，对于应用程序来说，拆分规则本身就较根据表名来拆分更为复杂，后期的数据维护也会更为复杂一些。
* 三、垂直切分 [1]

个数据库由很多表的构成，每个表对应着不同的业务，垂直切分是指按照业务将表进行分类，分布到不同
的数据库上面，这样也就将数据或者说压力分担到不同的库上面，如下图：

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/5815733-036a5ca2abfc7562.webp)

系统被切分成了，用户，订单交易，支付几个模块。
一个架构设计较好的应用系统，其总体功能肯定是由很多个功能模块所组成的，而每一个功能模块所需要的数据对应到数据库中就是一个或者多个表。而在架构设计中，各个功能模块相互之间的交互点越统一越少，系统的耦合度就越低，系统各个模块的维护性以及扩展性也就越好。这样的系统，实现数据的垂直切分也就越容易。

但是往往系统之有些表难以做到完全的独立，存在这扩库 join 的情况，对于这类的表，就需要去做平衡，是数据库让步业务，共用一个数据源，还是分成多个库，业务之间通过接口来做调用。在系统初期，数据量比较少，或者资源有限的情况下，会选择共用数据源，但是当数据发展到了一定的规模，负载很大的情况，就需
要必须去做分割。
一般来讲业务存在着复杂 join 的场景是难以切分的，往往业务独立的易于切分。如何切分，切分到何种
程度是考验技术架构的一个难题。
下面来分析下垂直切分的优缺点：
优点：

拆分后业务清晰，拆分规则明确；
系统之间整合或扩展容易；
数据维护简单。

缺点：

部分业务表无法 join，只能通过接口方式解决，提高了系统复杂度；
受每种业务不同的限制存在单库性能瓶颈，不易数据扩展跟性能提高；
事务处理复杂。

由于垂直切分是按照业务的分类将表分散到不同的库，所以有些业务表会过于庞大，存在单库读写与存储瓶颈，所以就需要水平拆分来做解决。

* 四、水平切分 [1]

相对于垂直拆分，水平拆分不是将表做分类，而是按照某个字段的某种规则来分散到多个库之中，每个表中包含一部分数据。简单来说，我们可以将数据的水平切分理解为是按照数据行的切分，就是将表中的某些行切分到一个数据库，而另外的某些行又切分到其他的数据库中，如图

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/5815733-22ad7881f990831d.webp)

拆分数据就需要定义分片规则。关系型数据库是行列的二维模型，拆分的第一原则是找到拆分维度。比如：
从会员的角度来分析，商户订单交易类系统中查询会员某天某月某个订单，那么就需要按照会员结合日期来拆分，不同的数据按照会员 ID 做分组，这样所有的数据查询 join 都会在单库内解决；如果从商户的角度来讲，要查询某个商家某天所有的订单数，就需要按照商户 ID 做拆分；但是如果系统既想按会员拆分，又想按商家数据，则会有一定的困难。如何找到合适的分片规则需要综合考虑衡。
几种典型的分片规则包括：

按照用户 ID 求模，将数据分散到不同的数据库，具有相同数据用户的数据都被分散到一个库中；
按照日期，将不同月甚至日的数据分散到不同的库中；
按照某个特定的字段求摸，或者根据特定范围段分散到不同的库中。

如图，切分原则都是根据业务找到适合的切分规则分散到不同的库，下面用用户 ID 求模举

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/5815733-bb8d87a644f833d9.webp)

既然数据做了拆分有优点也就优缺点。
优点 ：

拆分规则抽象好，join 操作基本可以数据库做；
不存在单库大数据，高并发的性能瓶颈；
应用端改造较少；
提高了系统的稳定性跟负载能力。

缺点 ：

拆分规则难以抽象；
分片事务一致性难以解决；
数据多次扩展难度跟维护量极大；
跨库 join 性能较差

## 三、mycat 和 ShardingSphere 的原理区分：
* 1）mycat是一个中间件的第三方应用，sharding-jdbc是一个jar包
* 2）使用mycat时不需要改代码，而使用sharding-jdbc时需要修改代码

* Mycat(proxy中间件层):

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/1227483-20180826205043063-1180010669.png)

* Sharding-jdbc(TDDL为代表的应用层):

![image](https://github.com/17661977890/springboot-mybatis-sharding-jdbc/blob/master/src/main/resources/image/1227483-20180826205516638-921055686.png)

