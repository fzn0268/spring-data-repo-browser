package fzn.projects.java.springdatarepobrowser.configuration;

import fzn.projects.java.springdatarepobrowser.model.Car;
import fzn.projects.java.springdatarepobrowser.repository.CarRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.keyvalue.repository.query.CachingKeyValuePartTreeQuery;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.repository.query.RedisQueryCreator;
import org.springframework.data.redis.repository.support.RedisRepositoryFactoryBean;

@Configuration
public class RedisConfiguration {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactoryA() {
        RedisConnectionFactory connectionFactory = new LettuceConnectionFactory();
        ((LettuceConnectionFactory) connectionFactory).setDatabase(1);
        return connectionFactory;
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplateA() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryA());
        return template;
    }

    @Bean
    public RedisKeyValueAdapter redisKeyValueAdapterA() {
        return new RedisKeyValueAdapter(redisTemplateA());
    }

    @Bean
    public CarRepository carRepositoryA(RedisMappingContext redisMappingContext) {
        RedisKeyValueTemplate keyValueTemplate = new RedisKeyValueTemplate(redisKeyValueAdapterA(), redisMappingContext);
        RedisRepositoryFactoryBean<CarRepository, Car, String> factoryBean = new RedisRepositoryFactoryBean<>(CarRepository.class);
        factoryBean.setKeyValueOperations(keyValueTemplate);
        factoryBean.setQueryCreator(RedisQueryCreator.class);
        factoryBean.setQueryType(CachingKeyValuePartTreeQuery.class);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactoryB() {
        RedisConnectionFactory connectionFactory = new LettuceConnectionFactory();
        ((LettuceConnectionFactory) connectionFactory).setDatabase(2);
        return connectionFactory;
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplateB() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactoryB());
        return template;
    }

    @Bean
    public RedisKeyValueAdapter redisKeyValueAdapterB() {
        return new RedisKeyValueAdapter(redisTemplateB());
    }

    @Bean
    public CarRepository carRepositoryB(RedisMappingContext redisMappingContext) {
        RedisKeyValueTemplate keyValueTemplate = new RedisKeyValueTemplate(redisKeyValueAdapterB(), redisMappingContext);
        RedisRepositoryFactoryBean<CarRepository, Car, String> factoryBean = new RedisRepositoryFactoryBean<>(CarRepository.class);
        factoryBean.setKeyValueOperations(keyValueTemplate);
        factoryBean.setQueryCreator(RedisQueryCreator.class);
        factoryBean.setQueryType(CachingKeyValuePartTreeQuery.class);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
