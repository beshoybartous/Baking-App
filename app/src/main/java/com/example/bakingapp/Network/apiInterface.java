package com.example.bakingapp.Network;

import com.example.bakingapp.Models.RecipeModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface apiInterface {
    @GET("baking.json")
    Call<ArrayList<RecipeModel>> getRecipes();
}
