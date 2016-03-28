package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class StackMatchesWidgetProvider extends AppWidgetProvider {

    @Override
    @SuppressWarnings("deprecation")
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_stack_matches);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                rv.setRemoteAdapter(R.id.stack_view, intent);
            }
            else {
                rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);
            }

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}