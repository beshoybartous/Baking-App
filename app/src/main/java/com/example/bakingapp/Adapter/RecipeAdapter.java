package com.example.bakingapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.Models.RecipeModel;
import com.example.bakingapp.R;
import com.example.bakingapp.databinding.RecipeItemsBinding;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.BakingRecpipeViewHolder> {
    private final static String BROWNIES="Brownies";
    private final static String CHEESECAKE="Cheesecake";
    private final static String NUTELLA_PIE="Nutella Pie";
    private final static String YELLOW_CAKE="Yellow Cake";

    ArrayList<RecipeModel> mRecipeList;
    Context mContext;
    public RecipeAdapter(Context context, RecipeClickListener recipeClickListener){
        mContext=context;
        this.recipeClickListener=recipeClickListener;
        mRecipeList=new ArrayList<>(  );
    }
    public void setRecipeList( ArrayList<RecipeModel> recipeList){
        mRecipeList=recipeList;
    }
    @NonNull
    @Override
    public BakingRecpipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        RecipeItemsBinding recipeItemsBinding = DataBindingUtil.inflate(inflater, R.layout.recipe_items,parent,false);
        return new BakingRecpipeViewHolder( recipeItemsBinding );    }

    @Override
    public void onBindViewHolder(@NonNull BakingRecpipeViewHolder holder, int position) {
        if(mRecipeList!=null) {
            if (mRecipeList.get( position ).getName().equals( BROWNIES )) {
                holder.recipeItemsBinding.reciepeImage.setImageResource( R.drawable.brownies );
            } else if (mRecipeList.get( position ).getName().equals( CHEESECAKE )) {
                holder.recipeItemsBinding.reciepeImage.setImageResource( R.drawable.cheesecake );
            } else if (mRecipeList.get( position ).getName().equals( NUTELLA_PIE )) {
                holder.recipeItemsBinding.reciepeImage.setImageResource( R.drawable.nutella_pie );
            } else if (mRecipeList.get( position ).getName().equals( YELLOW_CAKE )) {
                holder.recipeItemsBinding.reciepeImage.setImageResource( R.drawable.yellow_cake );
            }
            holder.recipeItemsBinding.recipeName.setText( mRecipeList.get( position ).getName() );
        }

    }

    @Override
    public int getItemCount() {
        if(mRecipeList!=null)
        return mRecipeList.size();
        else return 1;
    }



    public class BakingRecpipeViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        RecipeItemsBinding recipeItemsBinding;

        public BakingRecpipeViewHolder(final RecipeItemsBinding binding) {
            super( binding.getRoot() );
            this.recipeItemsBinding=binding;
            binding.getRoot().setOnClickListener(  this );
        }

        @Override
        public void onClick(View view) {
            recipeClickListener.onRecipeClickListener( getAdapterPosition() );
        }
    }
    final private RecipeClickListener recipeClickListener;
    public interface RecipeClickListener{
        void onRecipeClickListener(int clickedItem);
    }
}
