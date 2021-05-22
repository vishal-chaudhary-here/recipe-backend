package com.mycompany.app.recipe.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mycompany.app.recipe.repository.RecipeRepository;
import com.mycompany.app.recipe.util.RecipeUtil;
import com.mycompany.app.recipe.web.api.ApiUtil;
import com.mycompany.app.recipe.web.api.RecipeApiDelegate;
import com.mycompany.app.recipe.web.api.model.Recipe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mycompany.app.recipe.exception.BadRequestException;
import com.mycompany.app.recipe.exception.ConflictException;
import com.mycompany.app.recipe.exception.NotFoundException;

import org.springframework.http.MediaType;

@Service
public class RecipeApiDelegateImpl implements RecipeApiDelegate {

    private RecipeRepository recipeRepository;

    public RecipeApiDelegateImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public ResponseEntity<Recipe> createRecipe(Recipe receipe) {
        
        if(RecipeUtil.PREDICATE_ID_IS_NULL_EMPTY.or(RecipeUtil.PREDICATE_HREF_IS_NULL_EMPTY).negate().test(receipe)) {
            throw new BadRequestException("Id and Href must not present in request", 
            "Please remove the Id and Href Property");
        }

        if(RecipeUtil.PREDICATE_NAME_IS_NULL_EMPTY.test(receipe)) {
            throw new BadRequestException("Name must present in request", 
            "Please Provide Name");
        }

        String[] ignoreProperties = {"id","href","creationDateTime","isVegetarian","noOfPeopleCanEat","cookingInstructions","ingredients"};
        Example<Recipe> example = Example.of(new Recipe().name(receipe.getName()), 
            RecipeUtil.getExampleMatcherForId(ignoreProperties));
        
        if(recipeRepository.exists(example)) {
            throw new ConflictException("Name Already Present", 
            "Recipe Name is Already Present");
        }

        Recipe result = recipeRepository.save(receipe);
        result.setHref(String.valueOf(
            ServletUriComponentsBuilder.fromCurrentRequestUri().path(result.getId()).toUriString()));
        return new ResponseEntity<Recipe>(recipeRepository.save(result), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Recipe> retrieveRecipe(String id, String fields) {

        if(RecipeUtil.STRING_IS_NULL_OR_EMPTY.test(id)) {
            throw new BadRequestException("Id and Href must not present in request", 
            "Please remove the Id and Href Property");
        }

        String[] field = {"id", "href"};
        if(fields != null && fields.length() > 0) {
            field = fields.split(",");
        }

        List<Recipe> recipe = recipeRepository.getRecipe(Arrays.asList(id),  field);
        if(recipe.size() > 0) {
            return new ResponseEntity<Recipe>(recipe.get(0), 
            HttpStatus.OK);
        }

        throw new NotFoundException("Id not exist", 
            "Please provide valid Id");
    }

    private Map<String, String> getFilterMap(String filterString) {
        Map<String, String> filterMap = new HashMap<>();
        
        if(filterString == null || filterString.isBlank()) {
            return filterMap;
        } 

        char[] charArray = filterString.toCharArray();;
        
        StringBuilder stringBuilder = new StringBuilder();

        String key = null;
        String previousCommaValue = "";

        for(int i=0 ; i< charArray.length; i++) {
            char scannedChar= charArray[i];
            if(scannedChar == '=') {
                if(key != null && !key.isBlank()) {
                    filterMap.put(key, previousCommaValue);
                    key = null;
                    previousCommaValue = "";
                }
                
                key = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                continue;
            }

            if(scannedChar == ',') {
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
            stringBuilder.insert(0, ',');
        }

        filterMap.put(key, previousCommaValue.concat(stringBuilder.toString()));

        return filterMap;
    }

    @Override
    public ResponseEntity<List<Recipe>> listRecipe(String filters, List<String> fields, String sort,
        Integer offset, Integer limit) {
        
        if(limit != null && limit > 100) {
            throw new BadRequestException("Limit Size Exceed", 
            "Max fetch limit 100");
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
            headers.add("X-Result-Count", String.valueOf(recipeList.size()));
            headers.add("X-Total-Count", String.valueOf(recordCount));

            return new ResponseEntity<List<Recipe>>(recipeList, headers, HttpStatus.OK);
        }
    
        throw new NotFoundException("No Record Found", 
                "For this search criteria. No Record");
    }

    @Override
    public ResponseEntity<Recipe> patchRecipe(String id, Object recipe) {
        if(!(recipe instanceof HashMap)) {
            throw new BadRequestException("Wrong Data Format", 
                "Please pass data in key-value pair");
        }

        if(!recipeRepository.existsById(id)) {
            throw new NotFoundException("Not Found", 
                "Record Not Found");
        }

        Map<String, Object> data = (Map<String, Object>) recipe;

        if(data.containsKey("id") || data.containsKey("href") || data.containsKey("name")) {
            throw new BadRequestException("Update Error", 
                "Id, href and Name are not Patchable attribute");
        }
        
        recipeRepository.patchRecipe(id, data);

        return new ResponseEntity<>(recipeRepository.findById(id).get(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Void> deleteRecipe(String id) {
        if(!recipeRepository.existsById(id)) {
            throw new NotFoundException("Not Found", 
                "Record Not Found");
        }
        
        recipeRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Recipe> updateRecipe(String id,
        Recipe recipe) {

        if(recipe.getHref() != null || recipe.getName() != null ) {
            throw new BadRequestException("Update Error", 
                "Id, href and Name are not Updatable attribute");
        }

        Optional<Recipe> recipeOptional = recipeRepository.findById(id);

        if(!recipeOptional.isPresent()) {
            throw new NotFoundException("Not Found", 
                "Record Not Found");
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
