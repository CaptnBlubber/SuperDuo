package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.interactor.FixturesInteractor;
import barqsoft.footballscores.service.interactor.FixturesInteractorImpl;
import barqsoft.footballscores.service.model.Fixture;
import barqsoft.footballscores.service.model.Result;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext());
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final int mCount = 5;
    private List<Fixture> mWidgetItems = new ArrayList<>();
    private Context mContext;

    private FixturesInteractor interactor;

    public StackRemoteViewsFactory(Context context) {
        mContext = context;
        interactor = new FixturesInteractorImpl(context);
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mWidgetItems.clear();
    }

    public int getCount() {
        return mWidgetItems.size();
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.

        Fixture fixture = mWidgetItems.get(position);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy - HH:mm", Locale.US);
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_stack_matches_item);

        rv.setTextViewText(R.id.score, formatResult(fixture.getResult()));
        rv.setTextViewText(R.id.teams, formatTeams(fixture.getHomeTeamName(), fixture.getAwayTeamName()));
        rv.setTextViewText(R.id.date_textview, String.format("Match time: %s", simpleDateFormat.format(fixture.getDate())));
        rv.setTextColor(R.id.score, ContextCompat.getColor(mContext, R.color.widget_text_color));
        rv.setTextColor(R.id.teams, ContextCompat.getColor(mContext, R.color.widget_text_color));
        rv.setTextColor(R.id.date_textview, ContextCompat.getColor(mContext, R.color.widget_text_color));


        return rv;
    }

    public String formatTeams(String home, String away) {
        return String.format("%s vs. %s", home, away);
    }

    public String formatResult(Result result) {
        return String.format("%d - %d", result.getGoalsHomeTeam(), result.getGoalsAwayTeam());
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.


        interactor
                .loadFixtures(mContext.getString(R.string.stack_widget_timeframe))
                .take(mCount)
                .toBlocking()
                .subscribe(
                        matches -> mWidgetItems.addAll(matches),
                        throwable -> Log.e("WidgetProvider", throwable.getMessage()));
    }
}