package com.mycompany.app.recipe.config;

import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories("com.mycompany.app.recipe.repository")
@Import(value = MongoAutoConfiguration.class)
@Configuration
public class DatabaseConfiguration {}
