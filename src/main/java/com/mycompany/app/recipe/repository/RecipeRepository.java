package com.mycompany.app.recipe.repository;

import com.mycompany.app.recipe.web.api.model.Recipe;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the {@link User} entity.
 */
@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String>, RecipeRepositoryCustom  {
    /*Optional<Recipe> findOneByActivationKey(String activationKey);

    List<Recipe> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<Recipe> findOneByResetKey(String resetKey);

    Optional<Recipe> findOneByEmailIgnoreCase(String email);

    Optional<Recipe> findOneByLogin(String login);

    Page<Recipe> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);*/
}