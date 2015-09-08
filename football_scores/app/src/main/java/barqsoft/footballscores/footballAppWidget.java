package barqsoft.footballscores;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class footballAppWidget extends AppWidgetProvider {

    public static final String TAG = footballAppWidget.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Date today = new Date(System.currentTimeMillis());
        String[] dateSelection = new String[1];
        dateSelection[0] = new SimpleDateFormat("yyyy-MM-dd").format(today);

        Cursor cursor = context.getContentResolver().query(
                DatabaseContract.scores_table.buildScoreWithDate(), null, null, dateSelection, null);

        try {

            if (cursor != null && cursor.moveToFirst()) {

                // There may be multiple widgets active, so update all of them
                final int N = appWidgetIds.length;
                Log.d(TAG, String.format("appWidgetIds.length = %d", N));
                for (int i = 0; i < N; i++) {
                    updateAppWidget(context, appWidgetManager, appWidgetIds[i], cursor);
                }
            }
        } finally {

            cursor.close();
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "onDisabled");
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Cursor cursor) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.football_app_widget);

        String homeTeam = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        String awayTeam = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));

        views.setTextViewText(R.id.home_name, homeTeam);
        views.setTextViewText(R.id.away_name, awayTeam);
        views.setTextViewText(R.id.score_textview,
                Utilities.getScores(cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL))));

        views.setImageViewResource(R.id.home_crest, Utilities.getTeamCrestByTeamName(homeTeam));
        views.setImageViewResource(R.id.away_crest, Utilities.getTeamCrestByTeamName(awayTeam));

        views.setTextViewText(R.id.date_textview, String.format("%s %s",
                cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.DATE_COL)),
                cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL))));
//        views.setTextViewText(R.id.date_textview, Utilities.getDateAndTime());

/*
        home_name = (TextView) view.findViewById(R.id.home_name);
        away_name = (TextView) view.findViewById(R.id.away_name);
        score     = (TextView) view.findViewById(R.id.score_textview);
        date      = (TextView) view.findViewById(R.id.date_textview);
        home_crest = (ImageView) view.findViewById(R.id.home_crest);
        away_crest = (ImageView) view.findViewById(R.id.away_crest);
*/

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

