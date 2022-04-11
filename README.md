# luix-uid-generator
UidGenerator参考了[百度开源的基于Snowflake算法的ID生成器](https://github.com/baidu/uid-generator)
。可以在分布式集群环境下生成全局唯一的long型的ID。它通过消费未来时间克服了雪花算法的并发限制。UidGenerator提前生成ID并缓存在RingBuffer中。同时也解决了时钟回拨(clock moved backwards)的问题。
压测结果单个实例的QPS能超过6,000,000。并且同时适用于物理机和Docker虚拟机环境。另外做了非常多的优化，比如：Bits分布调整、delta seconds自动赋值、worker node ID重复使用提高系统使用年限、增加更高的吞吐能力、同时支持mybatis和JPA。

## Features
* 全局唯一的ID: 无论怎样都不能重复
* 高性能: 本地生成耗时少，默认每秒支持生成419万个ID
* 高可用: 虽说很难实现100%的可用性，但是也要无限接近于100%的可用性
* 简单易用: 提供spring-boot-starter依赖包，开箱即用

## Snowflake algorithm
Snowflake算法采用64bits，也就是一个Long长度的数据结构，结构如下：
```
+------+----------------------+-------------------+-----------------+-----------+
| sign |       timestamp      | data center id    | worker node id  | sequence  |
+------+----------------------+-------------------+-----------------+-----------+
  1bit          41bits               5bits               5bits         12bits
```
* sign: 由于Java中的long类型是带符号的，因此最高位是符号位，正数是0，负数是1，ID都是正数，所以最高位是0
* timestamp: 41位时间戳(单位为毫秒)，41位时间戳不是存储当前时间的时间戳，而是存储时间戳的差值，差值为当前时间戳 - 开始时间戳，这里的的开始时间戳就是ID生成器开始使用的时间。41位的时间戳可以使用69年，公式为(1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69年
* data center id: 5位长度可以支持32个data center(1L << 5 = 32)
* worker node id: 5位长度可以支持32个worker(1L << 5 = 32)，与data center id组合就是32 * 32 = 1024，意味着支持应用启动1024次，按照生产环境有4个pod，一个月启动4次，可以使用5年。
* sequence: 12位长度可以支持每毫秒产生4096个ID序号

## Optimized snowflake algorithm
Snowflake算法的痛点就是没有完整的worker node id生成方案，本ID generator解决了这个问题，其结构分布有些调整，默认结构如下：
```
+------+----------------------+----------------+-----------+
| sign |     delta seconds    | worker node id | sequence  |
+------+----------------------+----------------+-----------+
  1bit          29bits             12bits          22bits
```
* sign: 由于Java中的long类型是带符号的，因此最高位是符号位，正数是0，负数是1，ID都是正数，所以最高位是0
* delta seconds: 29位时间差(单位为秒)，差值为当前时间秒 - 开始时间秒，这里的的开始时间戳就是ID生成器开始使用的时间。29位的时间戳可以使用17.5年左右，公式为(1L << 29) / (60 * 60 * 24 * 365) = 17.5
* worker id: 12位长度可以支持4096个worker(1L << 12 = 4096)，意味着支持应用启动4096次，按照生产环境有4个pod，一个月启动4次，可以使用21年，另外做了ID复用优化，物理机环境下可以大大延长使用时间。另外增加了worker ID超越最大值后从0开始重新计数的优化，同样可以延长使用期限。非生产环境下由于启动比较多，可以适当调大。
* sequence: 22位长度可以支持每秒产生419W个ID序号，也就是4194个/ms

## Requirements
* JDK 8 ~ 17
* Spring Boot 2.0.0 +
* MySQL(用于分配WorkerId)
* org.springframework.jdbc.core.JdbcTemplate(用于分配WorkerId)

## Latest version
1.0.0

## Usage

1. 加入依赖包
```
<dependency>
    <groupId>com.luixtech</groupId>
    <artifactId>uid-generator-spring-boot-starter</artifactId>
    <version>{latestVersion}</version>
</dependency>
```
2. yml配置文件中增加配置信息，配置参数参见com.luixtech.uidgenerator.springboot.config.UidProperties
```
uid:
    worker:
        appId: ${spring.application.name}
```
系统启动式会自动创建mysql的worker ID表，如果不需要自动创建可以将uid.worker.autoCreateWorkerNodeTable设置为false

3. 加上注解@EnableUidGenerator
```
@SpringBootApplication
@EnableUidGenerator
public class UsageSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsageSampleApplication.class, args);
    }
}
```
4. 注入UID生成器实例，然后调用该实例方法可以生成19位数值型ID
```
@Resource
private UidGenerator uidGenerator;
...
uidGenerator.generateUid();
```

5. 另外提供一个单机环境下静态ID生成器方法，不需要执行2、3、4步骤，它并不保证集群环境下唯一性，可以生成19位数值型ID
```
IdGenerator.generateTimestampId();
```

## Build

Run the following command to build the project:

```
./mvnw clean verify
```

## Run

Run UID Generator Usage Sample with the following command:
```
java -jar uid-generator-usage-sample/target/*.jar
```

Then navigate to [http://localhost:8010](http://localhost:8010) in your browser.

## References
[时钟回拨问题咋解决？百度开源的唯一ID生成器UidGenerator](https://zhuanlan.zhihu.com/p/77737855)