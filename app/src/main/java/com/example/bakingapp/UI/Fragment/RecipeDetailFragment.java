package com.example.bakingapp.UI.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bakingapp.UI.Activity.Fragment.StepDetailFragment;
import com.example.bakingapp.Adapter.RecipeDetailAdapter;
import com.example.bakingapp.Models.Ingredient;
import com.example.bakingapp.Models.Step;
import com.example.bakingapp.R;
import com.example.bakingapp.databinding.FragmentRecipeDetailBinding;

import java.util.ArrayList;

public class RecipeDetailFragment extends Fragment implements RecipeDetailAdapter.StepClickListener{


    RecipeDetailAdapter recipeDetailAdapter;

    private final static String INGREDIENTS="GET_INGREDIENTS";
    private final static String STEPS="GET_STEPS";
    private final static String CURRENT_STEP="GET_CURRENT_STEP";
    private final static String STEPS_ID="GET_STEPS_ID";
    private final static String RECIPE_NAME="GET_RECIPE_NAME";
    private final static String DEVICE_TYPE="GET_DEVICE_TYPE";
    private final static String BROWNIES="Brownies";
    private final static String CHEESECAKE="Cheesecake";
    private final static String NUTELLA_PIE="Nutella Pie";
    private final static String YELLOW_CAKE="Yellow Cake";
    ArrayList<Ingredient> ingredients;
    ArrayList<Step> steps;
    String recipeName;
    private boolean mTwoPane=false;
    FragmentRecipeDetailBinding fragmentRecipeDetailBinding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRecipeDetailBinding = DataBindingUtil.
                inflate( inflater, R.layout.fragment_recipe_detail, container, false );

        final AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();

        if(savedInstanceState!=null){
            recipeName=savedInstanceState.getString( RECIPE_NAME );
            ingredients=savedInstanceState.getParcelableArrayList( INGREDIENTS );
            steps=savedInstanceState.getParcelableArrayList( STEPS );
            mTwoPane=savedInstanceState.getBoolean( DEVICE_TYPE );

        }
        else {
            if (getArguments() != null) {
                recipeName= getArguments().getString( RECIPE_NAME );
                ingredients = getArguments().getParcelableArrayList( INGREDIENTS );
                steps = getArguments().getParcelableArrayList( STEPS );
                mTwoPane=getArguments().getBoolean( DEVICE_TYPE );
            }
        }


        @SuppressLint("WrongConstant") RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false );
        fragmentRecipeDetailBinding.rvRecipeSteps.setLayoutManager( layoutManager );
        recipeDetailAdapter =new RecipeDetailAdapter( getContext(), RecipeDetailFragment.this,steps,ingredients );
        fragmentRecipeDetailBinding.rvRecipeSteps.setAdapter( recipeDetailAdapter );
        if(!mTwoPane) {
            appCompatActivity.setSupportActionBar( fragmentRecipeDetailBinding.toolbar );
            if (appCompatActivity.getSupportActionBar() != null) {
                setCollapseToolBarImage( recipeName );
                appCompatActivity.getSupportActionBar().setTitle( recipeName );
                appCompatActivity.getSupportActionBar().setHomeAsUpIndicator( R.drawable.back );
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled( true );
                appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled( true );
                fragmentRecipeDetailBinding.toolbar.setNavigationOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();

                    }
                } );
            }
        }
        final View rootView =fragmentRecipeDetailBinding.getRoot();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString( RECIPE_NAME,recipeName );
        outState.putParcelableArrayList(INGREDIENTS,ingredients);
        outState.putParcelableArrayList(STEPS,steps);
        outState.putBoolean( DEVICE_TYPE,mTwoPane );

    }

    @Override
    public void onStepClick(int positoin ,int stepID) {
        final StepDetailFragment stepDetailFragment = new StepDetailFragment();
        Bundle args=new Bundle(  );
        args.putParcelableArrayList( STEPS, steps );
        args.putParcelable( CURRENT_STEP,steps.get( positoin ) );
        args.putInt( STEPS_ID,stepID);
        //args.putString(RECIPE_NAME,recipeModels.get( clickedItem ).getName()  );

        if(mTwoPane) {
            args.putBoolean( DEVICE_TYPE,mTwoPane );
            stepDetailFragment.setArguments( args );
            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace( R.id.fragment_container2, stepDetailFragment )
                    .commit();
        }
        else{
            stepDetailFragment.setArguments( args );
            getActivity()
                    .getSupportFragmentManager().beginTransaction()
                    .replace( R.id.fragment_container, stepDetailFragment )
                    .addToBackStack( null )
                    .commit();
        }
    }
    public void setCollapseToolBarImage(String recipeName){
        fragmentRecipeDetailBinding.collapsingToolBar.setVisibility( View.VISIBLE );
        if(recipeName.equals( BROWNIES )){
            fragmentRecipeDetailBinding.collapsingToolBar.setBackgroundResource( R.drawable.brownies );
        }
        else if(recipeName.equals( CHEESECAKE )){
            fragmentRecipeDetailBinding.collapsingToolBar.setBackgroundResource( R.drawable.cheesecake );
        }
        else if(recipeName.equals( NUTELLA_PIE )){
            fragmentRecipeDetailBinding.collapsingToolBar.setBackgroundResource(R.drawable.nutella_pie );
        }
        else if(recipeName.equals( YELLOW_CAKE )){
            fragmentRecipeDetailBinding.collapsingToolBar.setBackgroundResource( R.drawable.yellow_cake );
        }
    }
}
