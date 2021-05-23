package com.mycompany.app.recipe.repository;

import com.mycompany.app.recipe.web.api.model.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String>, RecipeRepositoryCustom  {}