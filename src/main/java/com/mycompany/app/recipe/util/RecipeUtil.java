package com.mycompany.app.recipe.util;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.mycompany.app.recipe.web.api.model.Recipe;

import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public interface RecipeUtil {
    public static Predicate<String> STRING_IS_NULL_OR_EMPTY = str -> str == null || str.isBlank();
    public static Predicate<Recipe> PREDICATE_ID_IS_NULL_EMPTY = recipe -> recipe.getId() == null || recipe.getId().isBlank();
    public static Predicate<Recipe> PREDICATE_HREF_IS_NULL_EMPTY = recipe -> recipe.getHref() == null || recipe.getHref().isBlank();
    public static Predicate<Recipe> PREDICATE_NAME_IS_NULL_EMPTY = recipe -> recipe.getName() == null || recipe.getName().isBlank();
    
    static ExampleMatcher getExampleMatcherForId(String[] ignoreProperties) {
        return ExampleMatcher.matching()     
            .withIgnorePaths(ignoreProperties)
            .withIncludeNullValues()                             
            .withStringMatcher(StringMatcher.EXACT);
    }

    static Query getFilteredQuery(Map<String, String> filterMap, String[] fields, Map<String, String> sortMap) {
        Query query = new Query();

        if (fields != null && fields.length > 0) {
            query.fields().include(fields);
        }

        if (sortMap != null && sortMap.containsKey("ASC")) {
            query.with(Sort.by(Direction.ASC, sortMap.get("ASC").split(",")));
        }

        if (sortMap != null && sortMap.containsKey("DESC")) {
            query.with(Sort.by(Direction.DESC, sortMap.get("DESC").split(",")));
        }

        filterMap.entrySet().forEach(e -> {
            Criteria criteria =  null;

            if (e.getValue().contains(",")) {
                criteria = Criteria.where(e.getKey()).in(Arrays.asList(e.getValue().split(",")));
            } else if (e.getKey().equals("id")) {
                criteria = Criteria.where(e.getKey()).is(e.getValue());
            } else {
                criteria = Criteria.where(e.getKey()).regex(Pattern.compile(e.getValue()));
            }
            query.addCriteria(criteria);
        });

        return query;
    }
}
