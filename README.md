# pp-seckill-test

springboot 整合各stater的测试并发秒杀的案例
使用数据库悲观锁/乐观锁,disruptor队列 rabbitmq分布式队列,Redisson分布式锁测试
(rabbitmq,zookeeper,redis等开发环境建议使用docker部署)

```
springboot2.2.1 
spring-boot-starter-data-redis
spring-boot-starter-cache
redisson-spring-boot-starter
spring-boot-starter-amqp
druid-spring-boot-starter
mybatis-plus-boot-starter
swagger-spring-boot-starter
```
swagger: http://localhost:8718/swagger-ui.html

 JMeter工具测试多应用 可使用下方代理
`nginx.conf`
```
http{
  ....
	upstream gateway {
        server 127.0.0.1:8718 weight=1  max_fails=2 fail_timeout=30s;
        server 127.0.0.1:8719 weight=1  max_fails=2 fail_timeout=30s;
    }
	
    server {
        listen 81;
		    server_name localhost;
        location / {
			    proxy_pass http://gateway;
			    proxy_set_header Host $host;
			    proxy_set_header X-Real-IP $remote_addr;
        }
    }
   ....
  }
```
代码引入了spring-cache但未使用,自行使用
未使用nginx限流

参考: 
重复提交:http://www.iocoder.cn/Spring-Boot/battcn/v2-cache-redislock/
秒杀:https://gitee.com/52itstyle/spring-boot-seckill
Redisson分布锁:https://github.com/redisson/redisson/wiki/14.-%E7%AC%AC%E4%B8%89%E6%96%B9%E6%A1%86%E6%9E%B6%E6%95%B4%E5%90%88
docker仓库:https://hub.docker.com/
