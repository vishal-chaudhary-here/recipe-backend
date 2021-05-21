package com.mycompany.app.recipe.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.mycompany.app.recipe.web.api.model.Recipe;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;

// import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class RecipeRepositoryCustomImpl implements RecipeRepositoryCustom {

    private MongoTemplate mongoTemplate;

    public RecipeRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public long patchRecipe(String id, Map<String, Object> dataMap) {
        Query query = new Query(Criteria.where("id").is(id));

        Update update = new Update();
        dataMap.entrySet().forEach(e -> {
            update.set(e.getKey(), e.getValue());
        });

        return mongoTemplate.updateFirst(query, update, Recipe.class).getModifiedCount();
    }

    @Override
    public List<Recipe> getRecipe(List<String> ids, String[] fields) {
        Query query = new Query(Criteria.where("id").in(ids));
        query.fields().include(fields);
        return mongoTemplate.find(query, Recipe.class);
    }

    @Override
    public List<Recipe> getFilteredRecipe(Query query) {
        return mongoTemplate.find(query, Recipe.class);
    }

    @Override
    public long countFilteredRecipe(Query query) {
        return mongoTemplate.count(query, Recipe.class);
    }

}
