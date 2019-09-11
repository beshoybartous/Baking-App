package com.example.bakingapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.Models.Ingredient;
import com.example.bakingapp.Models.Step;
import com.example.bakingapp.R;
import com.example.bakingapp.databinding.RecipeDetailItemsBinding;

import java.util.ArrayList;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.RecipeStepViewHolder>{

    Context mContext;
    ArrayList<Step> mStep;
    ArrayList<Ingredient> mIngredient;
    IngredientAdapter ingredientAdapter;

    public RecipeDetailAdapter(Context mContext, StepClickListener stepClickListener, ArrayList<Step>mStep, ArrayList<Ingredient> mIngredient){
        this.mContext=mContext;
        this.stepClickListener=stepClickListener;
        this.mStep=mStep;
        this.mIngredient=mIngredient;
    }
    @NonNull
    @Override
    public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        RecipeDetailItemsBinding recipeDetailItemsBinding = DataBindingUtil.inflate(inflater, R.layout.recipe_detail_items,parent,false);
        return new RecipeDetailAdapter.RecipeStepViewHolder ( recipeDetailItemsBinding );
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepViewHolder holder, int position) {
        if(mIngredient!=null){
            @SuppressLint("WrongConstant") RecyclerView.LayoutManager layoutManager =
                    new LinearLayoutManager( mContext, LinearLayoutManager.VERTICAL, false );
            holder.recipeDetailItemsBinding.rvRecipeIngredients.setLayoutManager( layoutManager );
            ingredientAdapter=new IngredientAdapter(mContext,mIngredient);
            holder.recipeDetailItemsBinding.rvRecipeIngredients.setAdapter( ingredientAdapter );
            mIngredient=null;
        }
        else{
            holder.recipeDetailItemsBinding.steps.setVisibility( View.GONE );
        }
        if(position!=0)
        holder.recipeDetailItemsBinding.recipeSteps2.setText( position +" - "+ mStep.get( position ).getShortDescription() );
        else
            holder.recipeDetailItemsBinding.recipeSteps2.setText( mStep.get( position ).getShortDescription() );


    }

    @Override
    public int getItemCount() {
        return mStep.size();
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RecipeDetailItemsBinding recipeDetailItemsBinding;
        public RecipeStepViewHolder(final RecipeDetailItemsBinding recipeDetailItemsBinding) {
            super( recipeDetailItemsBinding.getRoot() );
            this.recipeDetailItemsBinding=recipeDetailItemsBinding;
            this.recipeDetailItemsBinding.getRoot().setOnClickListener( this );
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
                stepClickListener.onStepClick( position,mStep.get( position ).getId() );
        }
    }
    final private StepClickListener stepClickListener;
    public interface StepClickListener{
        void onStepClick(int position,int stedID);
    }
}
