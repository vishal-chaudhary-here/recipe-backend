package com.mycompany.app.recipe.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mycompany.app.recipe.repository.RecipeRepository;
import com.mycompany.app.recipe.util.MessageConstants;
import com.mycompany.app.recipe.util.RecipeUtil;
import com.mycompany.app.recipe.util.StringConstants;
import com.mycompany.app.recipe.web.api.RecipeApiDelegate;
import com.mycompany.app.recipe.web.api.model.Recipe;

import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mycompany.app.recipe.exception.BadRequestException;
import com.mycompany.app.recipe.exception.ConflictException;
import com.mycompany.app.recipe.exception.NotFoundException;

@Service
public class RecipeApiDelegateImpl implements RecipeApiDelegate {

    private RecipeRepository recipeRepository;

    public RecipeApiDelegateImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public ResponseEntity<Recipe> createRecipe(Recipe receipe) {
        
        if(RecipeUtil.PREDICATE_ID_IS_NULL_EMPTY.or(RecipeUtil.PREDICATE_HREF_IS_NULL_EMPTY).negate().test(receipe)) {
            throw new BadRequestException(MessageConstants.ID_AND_HREF_MUST_NOT_PRESENT, 
            MessageConstants.PLEASE_REMOVE_ID_AND_HREF_PROPERTY);
        }

        if(RecipeUtil.PREDICATE_NAME_IS_NULL_EMPTY.test(receipe)) {
            throw new BadRequestException(MessageConstants.UNIQUE_NAME_REQUIRED, 
            MessageConstants.PLEASE_PROVIDE_NAME);
        }

        Example<Recipe> example = Example.of(new Recipe().name(receipe.getName()), 
            RecipeUtil.getExampleMatcherForId(StringConstants.RECIPE_PROPERTIES_EXCEPT_NAME));
        
        if(recipeRepository.exists(example)) {
            throw new ConflictException(MessageConstants.NAME_ALREADY_PRESENT, 
            MessageConstants.RECIPE_NAME_ALREADY_PRESENT);
        }

        Recipe result = recipeRepository.save(receipe);
        result.setHref(String.valueOf(
            ServletUriComponentsBuilder.fromCurrentRequestUri().path(result.getId()).toUriString()));
        return new ResponseEntity<Recipe>(recipeRepository.save(result), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Recipe> retrieveRecipe(String id, String fields) {

        if(RecipeUtil.STRING_IS_NULL_OR_EMPTY.test(id)) {
            throw new BadRequestException(MessageConstants.ID_MUST_NOT_NULL, 
            MessageConstants.PLEASE_PROVIDE_ID);
        }

        String[] field = {StringConstants.ID, StringConstants.HREF};
        if(fields != null && fields.length() > 0) {
            field = fields.split(StringConstants.COMMA_STRING);
        }

        List<Recipe> recipe = recipeRepository.getRecipe(Arrays.asList(id),  field);
        if(recipe.size() > 0) {
            return new ResponseEntity<Recipe>(recipe.get(0), 
            HttpStatus.OK);
        }

        throw new NotFoundException(MessageConstants.ID_NOT_EXIST, 
            MessageConstants.PLEASE_PROVIDE_VALID_ID);
    }

    private Map<String, String> getFilterMap(String filterString) {
        Map<String, String> filterMap = new HashMap<>();
        
        if(filterString == null || filterString.isBlank()) {
            return filterMap;
        } 

        char[] charArray = filterString.toCharArray();;
        
        StringBuilder stringBuilder = new StringBuilder();

        String key = null;
        String previousCommaValue = StringConstants.EMPTY;

        for(int i=0 ; i< charArray.length; i++) {
            char scannedChar= charArray[i];
            if(scannedChar == StringConstants.EQUAL_CHAR) {
                if(key != null && !key.isBlank()) {
                    filterMap.put(key, previousCommaValue);
                    key = null;
                    previousCommaValue = StringConstants.EMPTY;
                }
                
                key = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                continue;
            }

            if(scannedChar == StringConstants.COMMA_CHAR) {
                if(!previousCommaValue.isBlank()) {
                    stringBuilder.insert(0, scannedChar);
                }

                previousCommaValue = previousCommaValue.concat(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                continue;
            }

            stringBuilder = stringBuilder.append(scannedChar);
        }

        if(!previousCommaValue.isBlank()) {
            stringBuilder.insert(0, StringConstants.COMMA_CHAR);
        }

        filterMap.put(key, previousCommaValue.concat(stringBuilder.toString()));

        return filterMap;
    }

    @Override
    public ResponseEntity<List<Recipe>> listRecipe(String filters, List<String> fields, String sort,
        Integer offset, Integer limit) {
        
        if(limit != null && limit > 100) {
            throw new BadRequestException(MessageConstants.LIMIT_SIZE_EXCEED, 
            MessageConstants.MAX_FETCH_LIMIT_100);
        }

        String[] fieldArray = null;

        if(fields != null && !fields.isEmpty()) {
            fieldArray = fields.toArray(new String[0]);
        }
        
        Map<String, String> filterMap = getFilterMap(filters);
        Map<String, String> sortMap = getFilterMap(sort);

        Query query = RecipeUtil.getFilteredQuery(filterMap, fieldArray, sortMap);
		long recordCount = recipeRepository.countFilteredRecipe(query);
		
		query.skip(offset == null ? 0 : offset);
        query.limit(limit == null ? 10 : limit);
		
        List<Recipe> recipeList = recipeRepository.getFilteredRecipe(query);

        if(recipeList.size() > 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(StringConstants.X_RESULT_COUNT, String.valueOf(recipeList.size()));
            headers.add(StringConstants.X_TOTAL_COUNT, String.valueOf(recordCount));

            return new ResponseEntity<List<Recipe>>(recipeList, headers, HttpStatus.OK);
        }
    
        throw new NotFoundException(MessageConstants.NO_RECORD_FOUND, 
            MessageConstants.NO_RECORD_FOUND);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<Recipe> patchRecipe(String id, Object recipe) {
        if(!(recipe instanceof HashMap)) {
            throw new BadRequestException(MessageConstants.WRONG_DATA_FORMAT, 
            MessageConstants.PLEASE_PASS_DATA_IN_KEY_VALUE_PAIR);
        }

        if(!recipeRepository.existsById(id)) {
            throw new NotFoundException(MessageConstants.NOT_FOUND, 
            MessageConstants.RECORD_NOT_FOUND);
        }

        Map<String, Object> data = (Map<String, Object>) recipe;

        if(data.containsKey(StringConstants.ID) 
            || data.containsKey(StringConstants.HREF) 
            || data.containsKey(StringConstants.NAME)) {
            throw new BadRequestException(MessageConstants.UPDATE_ERROR, 
            MessageConstants.ID_HREF_AND_NAME_NOT_UPDATABLE);
        }
        
        recipeRepository.patchRecipe(id, data);

        return new ResponseEntity<>(recipeRepository.findById(id).get(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Void> deleteRecipe(String id) {
        if(!recipeRepository.existsById(id)) {
            throw new NotFoundException(MessageConstants.NOT_FOUND, 
            MessageConstants.RECORD_NOT_FOUND);
        }
        
        recipeRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Recipe> updateRecipe(String id,
        Recipe recipe) {

        if(recipe.getHref() != null || recipe.getName() != null ) {
            throw new BadRequestException(MessageConstants.UPDATE_ERROR, 
            MessageConstants.ID_HREF_AND_NAME_NOT_UPDATABLE);
        }

        Optional<Recipe> recipeOptional = recipeRepository.findById(id);

        if(!recipeOptional.isPresent()) {
            throw new NotFoundException(MessageConstants.NOT_FOUND, 
            MessageConstants.RECORD_NOT_FOUND);
        }

        Recipe updateRecipe = recipeOptional.get();
        updateRecipe.setNoOfPeopleCanEat(recipe.getNoOfPeopleCanEat());
        updateRecipe.setIsVegetarian(recipe.getIsVegetarian());
        updateRecipe.setCreationDateTime(recipe.getCreationDateTime());
        updateRecipe.setIngredients(recipe.getIngredients());
        updateRecipe.setCookingInstructions(recipe.getCookingInstructions());

        return new ResponseEntity<>(recipeRepository.save(updateRecipe), HttpStatus.OK);
    }
}
