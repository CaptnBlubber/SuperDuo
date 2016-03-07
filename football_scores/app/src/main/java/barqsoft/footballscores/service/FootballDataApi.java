package barqsoft.footballscores.service;

import barqsoft.footballscores.service.model.FixturesResponse;
import barqsoft.footballscores.service.model.Team;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public interface FootballDataApi {

    @GET("fixtures")
    Observable<Response<FixturesResponse>> getFixtures(@Query("timeFrame") String timeframe);

    @GET("teams/{id}")
    Observable<Response<Team>> getTeam(@Path("id") int id);




}
