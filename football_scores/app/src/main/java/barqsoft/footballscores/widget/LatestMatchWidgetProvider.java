package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;

import java.text.SimpleDateFormat;
import java.util.Locale;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.interactor.FixturesInteractor;
import barqsoft.footballscores.service.interactor.FixturesInteractorImpl;
import barqsoft.footballscores.service.model.Fixture;
import barqsoft.footballscores.service.model.Result;
import barqsoft.footballscores.service.model.Team;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class LatestMatchWidgetProvider extends AppWidgetProvider {

    FixturesInteractor interactor;
    private Context mContext;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        interactor = new FixturesInteractorImpl(context);

        interactor
                .loadLatestFixture(mContext.getString(R.string.latest_widget_timeframe))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        fixture -> displayFixture(fixture, appWidgetManager, appWidgetIds),
                        throwable -> Log.e("WidgetProvider", throwable.getMessage()));
    }

    private void displayFixture(Fixture fixture, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d("displayFixture", "Away Team id:" + fixture.getAwayTeamId());
        Log.d("displayFixture", "Home Team id:" + fixture.getHomeTeamId());

        Observable.zip(
                interactor.loadTeam(fixture.getAwayTeamId()),
                interactor.loadTeam(fixture.getHomeTeamId()),
                (team, team2) -> new Match(team, team2, fixture))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(match -> {
                    createRemoteViews(appWidgetManager, appWidgetIds, match);
                });


    }

    private void createRemoteViews(AppWidgetManager appWidgetManager, int[] appWidgetIds, Match match) {
        Fixture fixture = match.getFixture();
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();



        for (int appWidgetId : appWidgetIds) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_latest_match);

            views.setTextViewText(R.id.home_name, homeTeam.getShortName());
            views.setTextViewText(R.id.score_textview, formatResult(fixture.getResult()));
            views.setTextViewText(R.id.date_textview, String.format("Match time: %s",simpleDateFormat.format(fixture.getDate())));
            views.setTextViewText(R.id.away_name, awayTeam.getShortName());

            AppWidgetTarget awayTeamTarget = new AppWidgetTarget(mContext, views, R.id.away_crest, appWidgetId);
            AppWidgetTarget homeTeamTarget = new AppWidgetTarget(mContext, views, R.id.home_crest, appWidgetId);

            Glide.with(mContext.getApplicationContext()) // safer!
                    .load(awayTeam.getCrestUrl())
                    .asBitmap()
                    .into(awayTeamTarget);


            Glide.with(mContext.getApplicationContext())
                    .load(homeTeam.getCrestUrl())
                    .asBitmap()
                    .into(homeTeamTarget);



            views.setTextColor(R.id.home_name, ContextCompat.getColor(mContext, R.color.widget_text_color));
            views.setTextColor(R.id.score_textview, ContextCompat.getColor(mContext, R.color.widget_text_color));
            views.setTextColor(R.id.date_textview, ContextCompat.getColor(mContext, R.color.widget_text_color));
            views.setTextColor(R.id.away_name, ContextCompat.getColor(mContext, R.color.widget_text_color));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public String formatResult(Result result) {
        return String.format("%d - %d", result.getGoalsHomeTeam(), result.getGoalsAwayTeam());
    }


}
