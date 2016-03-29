package barqsoft.footballscores.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.interactor.FixturesInteractor;
import barqsoft.footballscores.service.interactor.FixturesInteractorImpl;
import barqsoft.footballscores.service.model.Fixture;
import barqsoft.footballscores.ui.adapter.ScoresAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class MatchDayFragment extends Fragment {

    private static final String KEY_DATE = "MatchDayFragmentDate";
    private static final String KEY_TIMEFRAME = "MatchDayTimeFrame";

    private Date mDate;

    private FixturesInteractor mFixturesInteractor;
    private Calendar mCalendar = Calendar.getInstance();

    @Bind(R.id.scores_list)
    RecyclerView mScoresList;
    private ScoresAdapter mAdapter = new ScoresAdapter(new FixturesInteractorImpl(getContext()));

    public static MatchDayFragment getInstance(Date date, String time) {

        MatchDayFragment instance = new MatchDayFragment();
        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putSerializable(KEY_DATE, date);
        fragmentArgs.putString(KEY_TIMEFRAME, time);
        instance.setArguments(fragmentArgs);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && savedInstanceState == null) {
            mDate = (Date) getArguments().getSerializable(KEY_DATE);
            mCalendar.setTime(mDate);
            mFixturesInteractor = new FixturesInteractorImpl(getContext());
            loadFixtures(getArguments().getString(KEY_TIMEFRAME));
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        mScoresList.setAdapter(mAdapter);
        mScoresList.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void loadFixtures(String time) {
        mFixturesInteractor
                .loadFixtures(time)
                .map(fixtures -> {
                    //Filter Fixtures that are not for our current date
                    for (Iterator<Fixture> it = fixtures.iterator(); it.hasNext(); ) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(it.next().getDate());
                        if (c.get(Calendar.YEAR) != mCalendar.get(Calendar.YEAR) || c.get(Calendar.DAY_OF_MONTH) != mCalendar.get(Calendar.DAY_OF_MONTH)) {
                            it.remove();
                        }
                    }
                    return fixtures;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        fixtures1 -> {
                            mAdapter.setFixtures(fixtures1);
                            mAdapter.notifyDataSetChanged();
                        },
                        Throwable::printStackTrace
                );
    }

}
