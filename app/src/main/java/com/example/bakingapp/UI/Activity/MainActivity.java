package com.example.bakingapp.UI.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.bakingapp.Adapter.RecipeAdapter;
import com.example.bakingapp.Models.Ingredient;
import com.example.bakingapp.Models.RecipeModel;
import com.example.bakingapp.Models.Step;
import com.example.bakingapp.R;
import com.example.bakingapp.UI.Fragment.RecipeDetailFragment;
import com.example.bakingapp.ViewModel.RecipeViewModel;
import com.example.bakingapp.Widget.RecipeWidgetProvider;
import com.example.bakingapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener{
    private final static String RECIPES="GET_RECIPES";
    private final static String INGREDIENTS="GET_INGREDIENTS";
    private final static String RECIPE_NAME="GET_RECIPE_NAME";
    private final static String DEVICE_TYPE="GET_DEVICE_TYPE";
    private final static String STEPS="GET_STEPS";
    private final static String RECYCLER_VIEW_POSITION="Recycler View Position";
    private final static String CURRENT_STEP="GET_CURRENT_STEP";

    ActivityMainBinding mainBinding;
    private RecipeViewModel recipeViewModel;
    RecipeAdapter recipeAdapter;

    int currentOrientation;
    private int rvPosition=0;
    private boolean mTwoPane=false;
    ArrayList<RecipeModel>recipeModels;
    ArrayList<Ingredient>ingredients;
    ArrayList<Step>steps;
    String recipeName;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
              mainBinding= DataBindingUtil.setContentView( this, R.layout.activity_main );
        recipeModels=new ArrayList<>();
        //check device type tablet or mobile
        if(findViewById( R.id.tablet) !=null){
            mTwoPane=true;
        }
        else{
            mTwoPane=false;
        }
        //setup toolbar
        setSupportActionBar( (Toolbar) mainBinding.toolbar );
        recipeViewModel = ViewModelProviders.of( MainActivity.this ).get( RecipeViewModel.class );

        recipeAdapter =new RecipeAdapter(MainActivity.this, MainActivity.this);

        if(savedInstanceState!=null){
            recipeModels=savedInstanceState.getParcelableArrayList( RECIPES );
            rvPosition=savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
            recipeAdapter.setRecipeList( recipeModels);
            recipeAdapter.notifyDataSetChanged();
            if(getIntent()!=null){
                recipeName=getIntent().getStringExtra( RECIPE_NAME );
                ingredients=getIntent().getParcelableArrayListExtra( INGREDIENTS );
                steps=getIntent().getParcelableArrayListExtra( STEPS );
                if( ingredients!=null && steps!=null)
                    openRecipe( recipeName,ingredients,steps );
            }
        }
        else {
            fetchRecipe();

            Intent intent=getIntent();
            if(intent!=null){
                recipeName=intent.getStringExtra( RECIPE_NAME );
                ingredients=intent.getParcelableArrayListExtra( INGREDIENTS );
                steps=intent.getParcelableArrayListExtra( STEPS );
                if( ingredients!=null && steps!=null)
                    openRecipe( recipeName,ingredients,steps );
            }

        }
        currentOrientation= getResources().getConfiguration().orientation;
        if(currentOrientation== Configuration.ORIENTATION_LANDSCAPE || mTwoPane==true){
            int columnNumber= calculateNoOfColumns( this );
            mainBinding.rvRecipes.setLayoutManager( new GridLayoutManager( this,columnNumber ) );
            mainBinding.rvRecipes.getLayoutManager().scrollToPosition(rvPosition);

        }
        else {
            @SuppressLint("WrongConstant") RecyclerView.LayoutManager layoutManager =
                    new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false );
            mainBinding.rvRecipes.setLayoutManager( layoutManager );
            mainBinding.rvRecipes.getLayoutManager().scrollToPosition(rvPosition);
        }
        recipeAdapter.notifyDataSetChanged();
        mainBinding.rvRecipes.setAdapter( recipeAdapter );
    }

    public void fetchRecipe() {
        recipeViewModel.fetchRecipes().observe( MainActivity.this, new Observer<ArrayList<RecipeModel>>() {
            @Override
            public void onChanged(ArrayList<RecipeModel> recipeModel) {
                recipeModels=recipeModel;
                recipeAdapter.setRecipeList( recipeModels);
                recipeAdapter.notifyDataSetChanged();
            }
        } );
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    @Override
    public void onRecipeClickListener(int clickedItem) {
        recipeName=recipeModels.get( clickedItem ).getName();
        ingredients=recipeModels.get( clickedItem ).getIngredients();
        steps=recipeModels.get( clickedItem ).getSteps();

        Intent widgetIntent = new Intent(this,
                RecipeWidgetProvider.class);
        widgetIntent.setAction( AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        widgetIntent.putExtra(RECIPE_NAME, recipeName);
        widgetIntent.putParcelableArrayListExtra(INGREDIENTS, ingredients);
        widgetIntent.putParcelableArrayListExtra(STEPS, steps);
        this.sendBroadcast(widgetIntent);

        openRecipe(recipeName,ingredients,steps);
    }
    public void openRecipe(String recipeName, ArrayList<Ingredient> ingredients, ArrayList<Step> steps){
        final RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle args=new Bundle(  );

        Intent recipeDetailIntent=new Intent( this,RecipeDetailActivity.class );
        recipeDetailIntent.putExtra( RECIPE_NAME,recipeName );
        recipeDetailIntent.putParcelableArrayListExtra(INGREDIENTS,ingredients);
        recipeDetailIntent.putParcelableArrayListExtra(STEPS,steps);
        recipeDetailIntent.putExtra( DEVICE_TYPE,mTwoPane );
        recipeDetailIntent.putExtra( CURRENT_STEP,steps.get( 0 ) );
        startActivity( recipeDetailIntent );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList( RECIPES,recipeModels );
        outState.putInt(RECYCLER_VIEW_POSITION,rvPosition);
        if(currentOrientation==Configuration.ORIENTATION_LANDSCAPE || mTwoPane) {
            GridLayoutManager layoutManager = ((GridLayoutManager) mainBinding.rvRecipes.getLayoutManager());
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            outState.putInt( RECYCLER_VIEW_POSITION, firstVisiblePosition );
        }
        else{
            LinearLayoutManager layoutManager = (LinearLayoutManager) mainBinding.rvRecipes.getLayoutManager();
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            outState.putInt( RECYCLER_VIEW_POSITION, firstVisiblePosition );
        }
        super.onSaveInstanceState( outState );
    }

}
