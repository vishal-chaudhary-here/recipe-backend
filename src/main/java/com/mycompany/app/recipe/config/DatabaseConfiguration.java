package com.mycompany.app.recipe.config;

import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@EnableMongoRepositories("com.mycompany.app.recipe.repository")
@Import(value = MongoAutoConfiguration.class)
@Configuration
public class DatabaseConfiguration {
    /*@Override
	public String getDatabaseName() {
		return "myMongoDB";
	}
	@Override
	@Bean
	public MongoClient mongoClient() {
		ServerAddress address = new ServerAddress("127.0.0.1", 27017);
		MongoCredential credential = MongoCredential.createCredential("mdbUser", getDatabaseName(), "cp".toCharArray());
		MongoClientOptions options = new MongoClientOptions.Builder().build();
        
		MongoClient client = new MongoClient(address, credential, options);
		return client;
	}*/
}
