package com.mycompany.app.recipe.util;

public final class StringConstants {
    private StringConstants() {}
    public static final String ADMIN = "admin"; 
    public static final String USER = "user";
    public static final String ID = "id"; 
    public static final String HREF = "href";
    public static final String NAME = "name"; 
    public static final String CREATION_DATE_TIME = "creationDateTime"; 
    public static final String IS_VEGETARIAN = "isVegetarian"; 
    public static final String NO_OF_PEOPLE_CAN_EAT = "noOfPeopleCanEat"; 
    public static final String COOKING_INSTRUCTIONS = "cookingInstructions"; 
    public static final String INGREDIENTS = "ingredients"; 
    public static final String EMPTY = "";
    public static final String X_RESULT_COUNT = "X-Result-Count";
    public static final String X_TOTAL_COUNT = "X-Total-Count";
    public static final String COMMA_STRING = ",";

    public static final char COMMA_CHAR = ','; 
    public static final char EQUAL_CHAR = '='; 

    public static final String[] RECIPE_PROPERTIES_EXCEPT_NAME = {ID,HREF,CREATION_DATE_TIME,IS_VEGETARIAN,
        NO_OF_PEOPLE_CAN_EAT,COOKING_INSTRUCTIONS,INGREDIENTS};

    
}
