package com.example.bakingapp.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import com.example.bakingapp.UI.Activity.Fragment.StepDetailFragment;
import com.example.bakingapp.Models.Ingredient;
import com.example.bakingapp.Models.Step;
import com.example.bakingapp.R;
import com.example.bakingapp.UI.Fragment.RecipeDetailFragment;
import com.example.bakingapp.databinding.ActivityRecipeDetailBinding;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {
    private final static String INGREDIENTS="GET_INGREDIENTS";
    private final static String RECIPE_NAME="GET_RECIPE_NAME";
    private final static String DEVICE_TYPE="GET_DEVICE_TYPE";
    private final static String STEPS="GET_STEPS";
    private final static String CURRENT_STEP="GET_CURRENT_STEP";

    ArrayList<Ingredient>ingredients;
    ArrayList<Step>steps;
    Step step;
    String recipeName;
    private boolean mTwoPane;
    ActivityRecipeDetailBinding activityRecipeDetailBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activityRecipeDetailBinding= DataBindingUtil.setContentView( this, R.layout.activity_recipe_detail );
        if(savedInstanceState==null){
            Intent intent=getIntent();
            recipeName=intent.getStringExtra(RECIPE_NAME  );
            ingredients=intent.getParcelableArrayListExtra( INGREDIENTS );
            steps=intent.getParcelableArrayListExtra( STEPS );
            mTwoPane=intent.getBooleanExtra( DEVICE_TYPE,false );
            step=intent.getParcelableExtra( CURRENT_STEP );
            if(ingredients!=null)
            openFragment(recipeName,ingredients,steps);
        }
        else {
            recipeName=savedInstanceState.getString(RECIPE_NAME  );
            ingredients=savedInstanceState.getParcelableArrayList( INGREDIENTS );
            steps=savedInstanceState.getParcelableArrayList( STEPS );
            mTwoPane=savedInstanceState.getBoolean( DEVICE_TYPE,false );
            step=savedInstanceState.getParcelable( CURRENT_STEP );
        }
        if(mTwoPane){
            setSupportActionBar( (Toolbar) activityRecipeDetailBinding.toolbar );
            getSupportActionBar().setTitle( recipeName );
            getSupportActionBar().setHomeAsUpIndicator( R.drawable.back );
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );
            getSupportActionBar().setDisplayShowHomeEnabled( true );
            ((Toolbar) activityRecipeDetailBinding.toolbar).setNavigationOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            } );
        }
    }

    public void openFragment(String recipeName, ArrayList<Ingredient> ingredients, ArrayList<Step> steps){
        final RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        Bundle args=new Bundle(  );
        args.putString( RECIPE_NAME,recipeName );
        args.putParcelableArrayList( INGREDIENTS,ingredients  );
        args.putParcelableArrayList( STEPS,steps  );
        args.putBoolean( DEVICE_TYPE,mTwoPane );
        args.putParcelable( CURRENT_STEP,steps.get( 0 ) );

        if(mTwoPane){
            StepDetailFragment stepDetailFragment=new StepDetailFragment();
            args.putParcelable( CURRENT_STEP,steps.get( 0 ) );
            recipeDetailFragment.setArguments( args );
            stepDetailFragment.setArguments( args );
            this.getSupportFragmentManager().beginTransaction()
                    .replace( R.id.fragment_container , recipeDetailFragment )
                    .addToBackStack(null)
                    .commit();
            this.getSupportFragmentManager().beginTransaction()
                    .replace( R.id.fragment_container2 , stepDetailFragment )
                    .addToBackStack(null)
                    .commit();
        }
        else{
            recipeDetailFragment.setArguments( args );
            this.getSupportFragmentManager().beginTransaction()
                    .replace( R.id.fragment_container , recipeDetailFragment )
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState );
        outState.putString( RECIPE_NAME, recipeName );
        outState.putParcelableArrayList( INGREDIENTS, ingredients );
        outState.putParcelableArrayList( STEPS, steps );
        outState.putBoolean( DEVICE_TYPE, mTwoPane );
        outState.putParcelable( CURRENT_STEP, step );
    }
    @Override
    public void onBackPressed() {
        if(mTwoPane){
            super.onBackPressed();
            finish();
        }
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 1) {
            super.onBackPressed();
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
}
