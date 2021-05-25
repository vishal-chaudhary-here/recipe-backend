package com.mycompany.app.recipe.web.api;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.recipe.repository.RecipeRepository;
import com.mycompany.app.recipe.util.ErrorConstants;
import com.mycompany.app.recipe.util.MessageConstants;
import com.mycompany.app.recipe.web.api.model.Instruction;
import com.mycompany.app.recipe.web.api.model.Recipe;
import com.mycompany.app.recipe.web.api.model.Error;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles={"admin","user"})
public class RecipeApiControllerTest {
    private static final String RECIPE_ID = "id1";
    private static final String RECIPE_ID_2 = "id2";
    private static final String RECIPE_HREF = "http://localhost:8080/demo-api/recipeManagement/v1/stripe/"+RECIPE_ID;
    private static final String RECIPE_NAME = "recipe-name";
    private static final String RECIPE_CREATION_DATE = "02‐12‐1995 12:24";
    private static final Boolean RECIPE_IS_VEGETARIAN = false;
    private static final Integer RECIPE_INSTRUCTION_STEP_NO_1 = 1;
    private static final Integer RECIPE_INSTRUCTION_STEP_NO_2 = 2;
    private static final String RECIPE_INSTRUCTION_STEP_DESCRIPTION_1 = "instruction-01";
    private static final String RECIPE_INSTRUCTION_STEP_DESCRIPTION_2 = "instruction-02";
    private static final List<String> RECIPE_INGREDIENTS = Arrays.asList("ingredient-01", "ingredient-02", "ingredient-03");

    private static final String ENTITY_API_URL = "/demo-api/recipeManagement/v1/recipe";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @MockBean
    private RecipeApiDelegate recipeApiDelegate;

    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    public static Recipe createEntity() {
        List<Instruction> instructionList = new ArrayList<>();
        instructionList.add(createCookingInstruction(RECIPE_INSTRUCTION_STEP_NO_1, RECIPE_INSTRUCTION_STEP_DESCRIPTION_1));
        instructionList.add(createCookingInstruction(RECIPE_INSTRUCTION_STEP_NO_2, RECIPE_INSTRUCTION_STEP_DESCRIPTION_2));

        Recipe recipe = new Recipe();
        recipe.setId(RECIPE_ID);
        recipe.setHref(RECIPE_HREF);
        recipe.setName(RECIPE_NAME);
        recipe.setCreationDateTime(RECIPE_CREATION_DATE);
        recipe.setIsVegetarian(RECIPE_IS_VEGETARIAN);
        recipe.setCookingInstructions(instructionList);
        recipe.setIngredients(RECIPE_INGREDIENTS);
        return recipe;
    }

    public static Error createError(String reason, String message, String errCode, String status) {
        Error error = new Error();
        error.setReason(reason);
        error.setMessage(message);
        error.setCode(errCode);
        error.setStatus(status);
        return error;
    }

    public static Instruction createCookingInstruction(int stepNo, String stepDescription) {
        Instruction instruction = new Instruction();
        instruction.setStepNo(stepNo);
        instruction.stepDescription(stepDescription);
        return instruction;
    }

    public static Recipe initTestRecipe(RecipeRepository recipeRepository) {
        Recipe recipe = createEntity();
        return recipe;
    }

    @BeforeEach
    public void initTest() {
        recipeRepository.deleteAll();
        recipe = initTestRecipe(recipeRepository);
    }

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createRecipe() throws Exception {
        Recipe recipe = createEntity();
        ResponseEntity<Recipe> entity = new ResponseEntity<Recipe>(recipe, HttpStatus.CREATED);
        when(recipeApiDelegate.createRecipe(recipe)).thenReturn(entity);

        RequestBuilder request = MockMvcRequestBuilders.post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(recipe));

        ResultMatcher resultMatcher = MockMvcResultMatchers.status().isCreated();
        restRecipeMockMvc
            .perform(request)
            .andExpect(resultMatcher);
    }

    @Test
    void createRecipeWithNameEmptyOrNull() throws Exception {
        Error error = createError(MessageConstants.UNIQUE_NAME_REQUIRED, 
            MessageConstants.PLEASE_PROVIDE_NAME, 
            ErrorConstants.ERR_01, 
            ErrorConstants.BAD_REQUEST);
        Recipe recipe = createEntity();
        recipe.setName(null);
        ResponseEntity entity = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        when(recipeApiDelegate.createRecipe(recipe)).thenReturn(entity);
        RequestBuilder request = MockMvcRequestBuilders.post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(recipe));

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isBadRequest());

    }
    
    @Test
    void createRecipeWithExistingName() throws Exception {
        Error error = createError(
            MessageConstants.NAME_ALREADY_PRESENT, 
            MessageConstants.RECIPE_NAME_ALREADY_PRESENT, 
            ErrorConstants.ERR_02, 
            ErrorConstants.CONFLICT);

        Recipe recipe = createEntity();
        ResponseEntity entity = new ResponseEntity<>(error, HttpStatus.CONFLICT);

        when(recipeApiDelegate.createRecipe(recipe)).thenReturn(entity);
        RequestBuilder request = MockMvcRequestBuilders.post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(recipe));

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isConflict());

    }

    @Test
    void retrieveRecipeWithValidId() throws Exception {
        Recipe recipe = createEntity();
        ResponseEntity<Recipe> entity = new ResponseEntity<>(recipe, HttpStatus.OK);

        when(recipeApiDelegate.retrieveRecipe(RECIPE_ID, null)).thenReturn(entity);
        RequestBuilder request = MockMvcRequestBuilders.get(ENTITY_API_URL + "/" + RECIPE_ID)
            .contentType(MediaType.APPLICATION_JSON);

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.href").isNotEmpty())
            .andExpect(jsonPath("$.id").isNotEmpty());

    }

    @Test
    void getRecipeWithInvalidId() throws Exception {
        Error error = createError(
            MessageConstants.ID_NOT_EXIST, 
            MessageConstants.PLEASE_PROVIDE_VALID_ID, 
            ErrorConstants.ERR_01, 
            ErrorConstants.BAD_REQUEST);

        ResponseEntity entity = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        when(recipeApiDelegate.retrieveRecipe(RECIPE_ID_2, null)).thenReturn(entity);
        RequestBuilder request = MockMvcRequestBuilders.get(ENTITY_API_URL + "/" + RECIPE_ID_2)
            .contentType(MediaType.APPLICATION_JSON);

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.href").doesNotExist())
            .andExpect(jsonPath("$.id").doesNotExist());

    }

    @Test
    void getRecipeWithDefinedFields() throws Exception {
        Recipe recipe = createEntity();
        recipe.setId(null);
        recipe.setHref(null);
        recipe.isVegetarian(null);
        recipe.setNoOfPeopleCanEat(null);
        recipe.setCreationDateTime(null);
        recipe.cookingInstructions(null);

        ResponseEntity<Recipe> entity = new ResponseEntity<>(recipe, HttpStatus.OK);

        String fields = "name, ingredients";

        when(recipeApiDelegate.retrieveRecipe(RECIPE_ID, fields)).thenReturn(entity);
        RequestBuilder request = MockMvcRequestBuilders
            .get(ENTITY_API_URL + "/" + RECIPE_ID + "?" + "fields=" + fields)
            .contentType(MediaType.APPLICATION_JSON);

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").isNotEmpty())
            .andExpect(jsonPath("$.ingredients").isNotEmpty())
            .andExpect(jsonPath("$.id").isEmpty())
            .andExpect(jsonPath("$.href").isEmpty())
            .andExpect(jsonPath("$.isVegetarian").isEmpty())
            .andExpect(jsonPath("$.noOfPeopleCanEat").isEmpty())
            .andExpect(jsonPath("$.creationDateTime").isEmpty())
            .andExpect(jsonPath("$.cookingInstructions").isEmpty());
    }

    @Test
    void getAllRecipe() throws Exception {

        ResponseEntity<List<Recipe>> entity = new ResponseEntity<List<Recipe>>(Arrays.asList(createEntity()), 
            HttpStatus.OK);

        when(recipeApiDelegate.listRecipe(null, null, null, null, null)).thenReturn(entity);
        RequestBuilder request = MockMvcRequestBuilders
            .get(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON);

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*]").isArray());

    }

    @Test
    void getAllRecipeWhenNoRecord() throws Exception {
        Error error = createError(
            MessageConstants.NO_RECORD_FOUND, 
            MessageConstants.NO_RECORD_FOUND, 
            ErrorConstants.ERR_01, 
            ErrorConstants.BAD_REQUEST);

        ResponseEntity entity = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        when(recipeApiDelegate.listRecipe(null, null, null, null, null)).thenReturn(entity);
        RequestBuilder request = MockMvcRequestBuilders.get(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON);

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteRecipeWithValidId() throws Exception {

        ResponseEntity entity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(recipeApiDelegate.deleteRecipe(RECIPE_ID)).thenReturn(entity);
        RequestBuilder request = MockMvcRequestBuilders
            .delete(ENTITY_API_URL + "/" + RECIPE_ID)
            .contentType(MediaType.APPLICATION_JSON);

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isNoContent());
    }

    @Test
    void patchRecipeIdNotPossible() throws Exception {
        Error error = createError(
            MessageConstants.UPDATE_ERROR, 
            MessageConstants.ID_HREF_AND_NAME_NOT_UPDATABLE, 
            ErrorConstants.ERR_01, 
            ErrorConstants.BAD_REQUEST);

        ResponseEntity entity = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

        Map<String, Object> data = new HashMap<>();
        data.put("id", RECIPE_ID_2);

        when(recipeApiDelegate.patchRecipe(RECIPE_ID, data)).thenReturn(entity);
        
        RequestBuilder request = MockMvcRequestBuilders
            .patch(ENTITY_API_URL + "/" + RECIPE_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(data));

        restRecipeMockMvc
            .perform(request)
            .andExpect(status().isBadRequest());
    }

}
