package com.mycompany.app.recipe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mycompany.app.recipe.web.api.model.Instruction;
import com.mycompany.app.recipe.web.api.model.Recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RecipeRepositoryTest {
    private static final Integer INSTRUCTION_ID_1 = 1;
    private static final String DIRECTION_1 = "Switch on the stove";
    private static final Integer INSTRUCTION_ID_2 = 2;
    private static final String DIRECTION_2 = "Put the Pan on the stove";
    private static final String CREATION_DATE = "dd‐MM‐yyyy HH:mm";
    private static final String RECEIPE_NAME_1 = "egg burger";
    private static final String RECEIPE_NAME_2 = "omlette";
    private static final String RECEIPE_ID_1 = "egg-01";
    private static final String RECEIPE_ID_2 = "egg-02";
    private String recipeId;
    
    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    public void init() {
        recipeRepository.deleteAll();

        Instruction instruction1 = new Instruction().stepNo(INSTRUCTION_ID_1).stepDescription(DIRECTION_1);
        Instruction instruction2 = new Instruction().stepNo(INSTRUCTION_ID_2).stepDescription(DIRECTION_2);

        List<Instruction> instructionList = new ArrayList<>();
        instructionList.add(instruction1);
        instructionList.add(instruction2);

        List<String> ingredientList = new ArrayList<>();
        ingredientList.add("egg");
        ingredientList.add("oil");

        Recipe recipe1 = new Recipe()
            .id(RECEIPE_ID_1)
            .name(RECEIPE_NAME_1)
            .creationDateTime(CREATION_DATE)
            .isVegetarian(false)
            .noOfPeopleCanEat(4)
            .cookingInstructions(instructionList)
            .ingredients(ingredientList);

        this.recipeId = recipeRepository.save(recipe1).getId();
    
    }

    @Test
    public void createRecipeTest() {

        Instruction instruction1 = new Instruction().stepNo(INSTRUCTION_ID_1).stepDescription(DIRECTION_1);
        Instruction instruction2 = new Instruction().stepNo(INSTRUCTION_ID_2).stepDescription(DIRECTION_2);

        List<Instruction> instructionList = new ArrayList<>();
        instructionList.add(instruction1);
        instructionList.add(instruction2);

        List<String> ingredientList = new ArrayList<>();
        ingredientList.add("egg");
        ingredientList.add("oil");

        Recipe recipe1 = new Recipe()
            .id(RECEIPE_ID_2)
            .name(RECEIPE_NAME_2)
            .creationDateTime(CREATION_DATE)
            .isVegetarian(true)
            .noOfPeopleCanEat(4)
            .cookingInstructions(instructionList)
            .ingredients(ingredientList);

        Recipe saved = recipeRepository.save(recipe1);
        assertEquals(saved.getName(), saved.getName());
    }

    @Test
    void listRecipeTest() {
        List<Recipe> recipeList = recipeRepository.findAll();
        assertEquals(recipeList.size(), 1);
    }

    @Test
    void patchRecipeTest() {
        String patchHref = "href-test";

        Recipe recipe = recipeRepository.findById(recipeId).get();
        
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("href", patchHref);

        long updatedCount = recipeRepository.patchRecipe(recipe.getId(), updateMap);
        Optional<Recipe> recipePatched = recipeRepository.findById(recipe.getId());

        assertEquals(updatedCount, 1);
        assertEquals(recipePatched.get().getHref(), patchHref);
    }

    @Test
    void updateRecipeTest() {
        String updateName = "update-test";
        int updateNoOfPeopleCanEat = 40;

        List<Recipe> recipeList = recipeRepository.findAll();
        
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeList.get(0).getId());
        Recipe recipe = recipeOptional.get();
        recipe.setName(updateName);
        recipe.setNoOfPeopleCanEat(updateNoOfPeopleCanEat);

        Recipe updatedRecipe = recipeRepository.save(recipe);

        assertEquals(updatedRecipe.getId(), recipeList.get(0).getId());
        assertEquals(updatedRecipe.getNoOfPeopleCanEat(), updateNoOfPeopleCanEat);
        assertEquals(updatedRecipe.getName(), updateName);
        assertEquals(recipeList.size(), recipeRepository.count());
    }

    @Test
    void deleteRecipeTest() {
        List<Recipe> recipeList = recipeRepository.findAll();
        recipeRepository.deleteById(recipeList.get(0).getId());
        
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeList.get(0).getId());
       
        assertFalse(recipeOptional.isPresent());
        assertEquals(recipeList.size()-1, recipeRepository.findAll().size());
    }
}
