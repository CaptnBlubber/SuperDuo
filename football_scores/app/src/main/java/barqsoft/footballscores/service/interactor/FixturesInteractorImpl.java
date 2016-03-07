package barqsoft.footballscores.service.interactor;

import android.content.Context;

import java.util.List;

import barqsoft.footballscores.service.FootballDataApiService;
import barqsoft.footballscores.service.model.Fixture;
import barqsoft.footballscores.service.model.FixturesResponse;
import barqsoft.footballscores.service.model.Team;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

import static barqsoft.footballscores.service.rx.Transformers.applyHttpErrorOperator;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class FixturesInteractorImpl implements FixturesInteractor {

    private Context mContext;

    public FixturesInteractorImpl(Context context) {
        mContext = context;
    }

    @Override
    public Observable<List<Fixture>> loadFixtures(String time) {
        return FootballDataApiService
                .getInstance(mContext)
                .getFixtures(time)
                .compose(applyHttpErrorOperator())
                .map(Response::body)
                .map(FixturesResponse::getFixtures)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Fixture> loadLatestFixture(String time) {
        return loadFixtures(time).map(fixtures -> fixtures.get(0));
    }

    @Override
    public Observable<Team> loadTeam(int id) {
        return FootballDataApiService
                .getInstance(mContext)
                .getTeam(id)
                .compose(applyHttpErrorOperator())
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }
}
