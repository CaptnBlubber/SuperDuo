package barqsoft.footballscores.service.model;

import java.util.Collections;
import java.util.List;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class FixturesResponse {

    private String timeFrameStart;
    private String timeFrameEnd;
    private int count;
    private List<Fixture> fixtures = Collections.emptyList();

    public String getTimeFrameStart() {
        return timeFrameStart;
    }

    public void setTimeFrameStart(String timeFrameStart) {
        this.timeFrameStart = timeFrameStart;
    }

    public String getTimeFrameEnd() {
        return timeFrameEnd;
    }

    public void setTimeFrameEnd(String timeFrameEnd) {
        this.timeFrameEnd = timeFrameEnd;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(List<Fixture> fixtures) {
        this.fixtures = fixtures;
    }
}
