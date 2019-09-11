package com.example.bakingapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.bakingapp.Models.Ingredient;
import com.example.bakingapp.R;

import java.util.ArrayList;

import static com.example.bakingapp.Widget.RecipeWidgetProvider.EXTRA_ITEM_POSITION;

public class RecipeWidgetService extends RemoteViewsService {
    private final static String INGREDIENTS="GET_INGREDIENTS";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeWidgetItemFactory(getApplicationContext(),intent);
    }

    class RecipeWidgetItemFactory implements RemoteViewsFactory{
        private Context context;
        private int appWidgetID;
        ArrayList<Ingredient> ingredients;
        Intent intent;
        int AppWidgetId;
        RecipeWidgetItemFactory(Context context,Intent intent){
            this.context=context;
            this.intent=intent;
            AppWidgetId  = Integer.valueOf(intent.getData().getSchemeSpecificPart());

            Bundle b=intent.getBundleExtra( "Bundle1" );
           // this.appWidgetID=  b.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID );
            this.ingredients=b.getParcelableArrayList( INGREDIENTS );
        }
        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (ingredients!=null)
            return ingredients.size();
            else
                return 1;
        }
        //like bindviewholder
        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views=new RemoteViews( context.getPackageName(), R.layout.ingredient_widget_items );
            if(ingredients!=null) {

                views.setTextViewText( R.id.tv_ingredient,
                        ingredients.get( i ).getQuantity()+" "+
                                ingredients.get( i ).getMeasure()+" "+
                                ingredients.get( i ).getIngredient() );
                Intent fillIntent=new Intent(  );
                fillIntent.putExtra(EXTRA_ITEM_POSITION,i);
                views.setOnClickFillInIntent( R.id.tv_ingredient,fillIntent );
            }
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
