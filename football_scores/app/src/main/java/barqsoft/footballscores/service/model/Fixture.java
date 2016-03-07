package barqsoft.footballscores.service.model;

import java.util.Date;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class Fixture {

    private int id;
    private int soccerseasonId;
    private Date date;
    private String status;
    private int matchday;
    private String homeTeamName;
    private int homeTeamId;
    private String awayTeamName;
    private int awayTeamId;
    private Result result;


    public int getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(int homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(int awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public int getSoccerseasonId() {
        return soccerseasonId;
    }

    public void setSoccerseasonId(int soccerseasonId) {
        this.soccerseasonId = soccerseasonId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMatchday() {
        return matchday;
    }

    public void setMatchday(int matchday) {
        this.matchday = matchday;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
