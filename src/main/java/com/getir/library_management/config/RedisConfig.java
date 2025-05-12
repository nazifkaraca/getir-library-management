package com.getir.library_management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Marks this class as a source of bean definitions for the application context
@Configuration
public class RedisConfig {

    // Defines a RedisTemplate bean to interact with Redis using custom serializers
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory); // Injects the connection factory

        // JSON serializer for Redis values using Jackson
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();

        // Sets default serializer for all types of keys and values unless overridden
        template.setDefaultSerializer(serializer);

        // Keys and Hash keys will be stored as plain Strings in Redis
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Values and Hash values will be stored as JSON using the above serializer
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        // Finalize the template configuration
        template.afterPropertiesSet();
        return template;
    }
}
