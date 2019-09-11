package com.example.bakingapp.Repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakingapp.Models.RecipeModel;
import com.example.bakingapp.Network.RetrofitClient;
import com.example.bakingapp.Network.apiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRepository {



    ArrayList<RecipeModel>recipeModels=new ArrayList<>(  );
    public LiveData<ArrayList<RecipeModel>> fetchRecipes() {
        final MutableLiveData<ArrayList<RecipeModel>> recipes=new MutableLiveData<>();

        /**
         * Calling JSON
         */
        apiInterface api = RetrofitClient.getApiService();

        Call<ArrayList<RecipeModel>> call = api.getRecipes( );

        /**
         * Enqueue Callback will be call when get response...
         */

        call.enqueue( new Callback<ArrayList<RecipeModel>>() {

            @Override
            public void onResponse(Call<ArrayList<RecipeModel>> call, Response<ArrayList<RecipeModel>> response) {
                Log.i( "test121", String.valueOf( response.isSuccessful() ) );


                if (response.isSuccessful()) {
                    /**
                     * Got Successfully
                     */
                    recipeModels=response.body();
                    Log.i( "test121", "isnide" );
                    recipes.setValue( recipeModels );


                }
            }

            @Override
            public void onFailure(Call<ArrayList<RecipeModel>> call, Throwable t) {
                Log.i( "test121","failed" );

            }


        } );
        return recipes;
    }
}
