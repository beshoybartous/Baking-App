package com.example.bakingapp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakingapp.Models.RecipeModel;
import com.example.bakingapp.Repository.RecipeRepository;

import java.util.ArrayList;

public class RecipeViewModel extends AndroidViewModel {
    RecipeRepository recipeRepository;
    LiveData<ArrayList<RecipeModel>> recipes=new MutableLiveData<>();
    public RecipeViewModel(Application application) {
        super( application);
        recipeRepository =new RecipeRepository( );
        recipes= recipeRepository.fetchRecipes(  );
//        Log.i( "test121",recipes.getValue().get( 0 ).getName() );

    }
    public LiveData<ArrayList<RecipeModel>> fetchRecipes(){
        return recipes;
    }
}
