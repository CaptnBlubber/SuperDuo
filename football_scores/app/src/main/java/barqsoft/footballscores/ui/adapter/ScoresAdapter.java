package barqsoft.footballscores.ui.adapter;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.interactor.FixturesInteractor;
import barqsoft.footballscores.service.model.Fixture;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ScoresViewHolder> {

    List<Fixture> mFixtures = Collections.emptyList();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm");

    FixturesInteractor mFixturesInteractor;

    public ScoresAdapter(FixturesInteractor interactor) {
        mFixturesInteractor = interactor;
    }

    public void setFixtures(List<Fixture> fixtures) {
        mFixtures = fixtures;
        notifyDataSetChanged();
    }

    @Override
    public ScoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.scores_list_item, parent, false);

        return new ScoresViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ScoresViewHolder holder, int position) {


        Fixture match = mFixtures.get(position);


        holder.mAwayName.setText(match.getAwayTeamName());
        holder.mHomeName.setText(match.getHomeTeamName());
        holder.mScore.setText(holder.itemView.getResources().getString(R.string.match_format, match.getResult().getGoalsHomeTeam(), match.getResult().getGoalsAwayTeam()));
        holder.mDate.setText(simpleDateFormat.format(match.getDate()));

        holder.mShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            } else {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            }
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, v.getResources().getString(R.string.share_message, match.getHomeTeamName(), match.getAwayTeamName(), v.getResources().getString(R.string.match_format, match.getResult().getGoalsHomeTeam(), match.getResult().getGoalsAwayTeam())));
            v.getContext().startActivity(shareIntent);

        });


        mFixturesInteractor
                .loadTeam(match.getHomeTeamId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        team -> Glide
                                .with(holder.itemView.getContext())
                                .load(team.getCrestUrl())
                                .placeholder(R.drawable.ic_launcher)
                                .into(holder.mHomeCrest)
                );


        mFixturesInteractor
                .loadTeam(match.getAwayTeamId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        team -> Glide
                                .with(holder.itemView.getContext())
                                .load(team.getCrestUrl())
                                .placeholder(R.drawable.ic_launcher)
                                .into(holder.mAwayCrest)
                );


    }

    @Override
    public int getItemCount() {
        return mFixtures.size();
    }

    public class ScoresViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.home_crest)
        ImageView mHomeCrest;
        @Bind(R.id.home_name)
        TextView mHomeName;
        @Bind(R.id.score)
        TextView mScore;
        @Bind(R.id.date)
        TextView mDate;
        @Bind(R.id.away_crest)
        ImageView mAwayCrest;
        @Bind(R.id.away_name)
        TextView mAwayName;
        @Bind(R.id.share)
        Button mShare;


        public ScoresViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
