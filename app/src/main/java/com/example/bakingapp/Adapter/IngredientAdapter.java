package com.example.bakingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bakingapp.Models.Ingredient;
import com.example.bakingapp.R;
import com.example.bakingapp.databinding.IngredientItemsBinding;
import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.RecipeIngredientViewHolder>{

    Context mContext;
    ArrayList<Ingredient>mIngredients;
    public IngredientAdapter(Context mContext, ArrayList<Ingredient>mIngredients){
        this.mContext=mContext;
        this.mIngredients=mIngredients;
    }
    @NonNull
    @Override
    public RecipeIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        IngredientItemsBinding ingredientItemsBinding = DataBindingUtil.inflate(inflater, R.layout.ingredient_items,parent,false);
        return new IngredientAdapter.RecipeIngredientViewHolder ( ingredientItemsBinding );
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientViewHolder holder, int position) {
            holder.ingredientItemsBinding.ingredientDetail.setText(
                    mIngredients.get( position ).getQuantity()+" "+
                    mIngredients.get( position ).getMeasure()+" "+
                    mIngredients.get( position ).getIngredient() );
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    public class RecipeIngredientViewHolder extends RecyclerView.ViewHolder{
        IngredientItemsBinding ingredientItemsBinding;
        public RecipeIngredientViewHolder(final IngredientItemsBinding ingredientsItemsBinding) {
            super( ingredientsItemsBinding.getRoot() );
            this.ingredientItemsBinding=ingredientsItemsBinding;
        }
    }
}
