package barqsoft.footballscores.service.interactor;

import java.util.List;

import barqsoft.footballscores.service.model.Fixture;
import barqsoft.footballscores.service.model.Team;
import rx.Observable;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public interface FixturesInteractor {

    Observable<List<Fixture>> loadFixtures(String time);
    Observable<Fixture> loadLatestFixture(String time);
    Observable<Team> loadTeam(int id);

}
