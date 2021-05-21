package com.mycompany.app.recipe.web.api;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.recipe.repository.RecipeRepository;
import com.mycompany.app.recipe.web.api.model.Instruction;
import com.mycompany.app.recipe.web.api.model.Recipe;

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

@SpringBootTest
@AutoConfigureMockMvc
public class RecipeApiControllerTest {
    private static final String RECIPE_ID = "id1";
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

    public static Instruction createCookingInstruction(int stepNo, String stepDescription) {
        Instruction instruction = new Instruction();
        instruction.setStepNo(stepNo);
        instruction.stepDescription(stepDescription);
        return instruction;
    }

    public static Recipe initTestRecipe(RecipeRepository recipeRepository) {
        recipeRepository.deleteAll();
        Recipe recipe = createEntity();
        return recipe;
    }

    @BeforeEach
    public void initTest() {
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
        int databaseSizeBeforeCreate = recipeRepository.findAll().size();

        // Create the User
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

}
