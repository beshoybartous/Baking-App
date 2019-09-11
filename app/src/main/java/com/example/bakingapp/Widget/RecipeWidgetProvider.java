package com.example.bakingapp.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.bakingapp.UI.Activity.MainActivity;
import com.example.bakingapp.Models.Ingredient;
import com.example.bakingapp.Models.RecipeModel;
import com.example.bakingapp.Models.Step;
import com.example.bakingapp.R;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {
    private final static String INGREDIENTS="GET_INGREDIENTS";
    private final static String STEPS="GET_STEPS";
    private final static String RECIPE_NAME="GET_RECIPE_NAME";

    public static final String ACTION_TOAST="actiontoast";
    public static final String EXTRA_ITEM_POSITION="extraItemPosition";

    static ArrayList<Ingredient> ingredients;
    static ArrayList<RecipeModel>recipeModels;
    static ArrayList<Step>steps;
    private static  String recipeName;
    static RemoteViews views;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        int randomNumber=(int)(Math.random()*1000);
        // Construct the RemoteViews object
        views = new RemoteViews( context.getPackageName(), R.layout.recipe_widget_provider );

        Intent serviceIntent=new Intent( context,RecipeWidgetService.class );
        Bundle data=new Bundle(  );
        //to create new RecipeWidgetItemFactory every time list updated
        data.putInt( AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId+randomNumber );
        //click on list items
        Intent clickIntent=new Intent( context,RecipeWidgetProvider.class );
        clickIntent.setAction( ACTION_TOAST );
        PendingIntent clickPendingIntent=PendingIntent.getBroadcast( context,0,clickIntent,0 );

        if(ingredients!=null) {
            data.putParcelableArrayList( INGREDIENTS, ingredients  );
            serviceIntent.setData(Uri.fromParts("content", String.valueOf(appWidgetId+randomNumber), null));
            serviceIntent.putExtra("Bundle1",data);
            serviceIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            context.sendBroadcast(serviceIntent);
            views.setRemoteAdapter( R.id.ingredient_widget_list, serviceIntent );
            views.setTextViewText( R.id.recoipe_title,recipeName );
            views.setViewVisibility(  R.id.ingredient_widget_list, View.VISIBLE );
            views.setPendingIntentTemplate(  R.id.ingredient_widget_list,clickPendingIntent );

        }

        else{

            views.setViewVisibility( R.id.ingredient_widget_list, View.GONE );
        }

        Intent intent = new Intent( context, MainActivity.class );
        PendingIntent pendingIntent=PendingIntent.getActivity( context,0,intent,0 );
        // Instruct the widget manager to update the widget
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Bundle appWidgetOptions=appWidgetManager.getAppWidgetOptions( appWidgetId );
            resizeWidget( appWidgetOptions,views );
        }

        appWidgetManager.updateAppWidget( appWidgetId, views );

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget( context, appWidgetManager, appWidgetId );

        }
    }

    private static void resizeWidget(Bundle opetions, RemoteViews views){
        int minWdith=opetions.getInt( AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH );
        int minHeight=opetions.getInt( AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT );
        int maxWidth=opetions.getInt( AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH );
        int maxHeight=opetions.getInt( AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT );


        if(maxHeight>100 && minWdith>100){
            views.setViewVisibility( R.id.ingredient_widget_list, View.VISIBLE );
        }
        else{
            views.setViewVisibility( R.id.ingredient_widget_list,View.GONE );
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.recipe_widget_provider );
        resizeWidget( newOptions,views );
        appWidgetManager.updateAppWidget( appWidgetId,views );


    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive( context, intent );
        if(intent.getAction().equals( AppWidgetManager.ACTION_APPWIDGET_UPDATE )) {
            ingredients = intent.getParcelableArrayListExtra( INGREDIENTS );
            recipeName=intent.getStringExtra( RECIPE_NAME );
            steps=intent.getParcelableArrayListExtra( STEPS );
            if(ingredients!=null) {
                Log.i( "ingredeintupdate", ingredients.get( 0 ).getIngredient() );
                int ids[] = intent.getIntArrayExtra( AppWidgetManager.EXTRA_APPWIDGET_IDS );
                onUpdate( context, AppWidgetManager.getInstance( context ), ids );
            }
        }
        else if(intent.getAction().equals( ACTION_TOAST )){
            Intent launchActivity = new Intent(context, MainActivity.class);
            launchActivity.putExtra( RECIPE_NAME,recipeName );
            launchActivity.putParcelableArrayListExtra( INGREDIENTS,ingredients );
            launchActivity.putParcelableArrayListExtra( STEPS,steps );
            launchActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity( launchActivity );

        }
    }
}

