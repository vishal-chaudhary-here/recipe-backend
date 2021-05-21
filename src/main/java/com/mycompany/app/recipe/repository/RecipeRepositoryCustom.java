package com.mycompany.app.recipe.repository;

import java.util.List;
import java.util.Map;

import com.mycompany.app.recipe.web.api.model.Recipe;

import org.springframework.data.mongodb.core.query.Query;

public interface RecipeRepositoryCustom {
    long patchRecipe(String id,  Map<String, Object> dataMap);
    List<Recipe> getRecipe(List<String> ids,  String[] fields);
    public List<Recipe> getFilteredRecipe(Query query);
    public long countFilteredRecipe(Query query);
}
