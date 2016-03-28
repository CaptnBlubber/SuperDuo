package barqsoft.footballscores.widget;

import barqsoft.footballscores.service.model.Fixture;
import barqsoft.footballscores.service.model.Team;

/**
     * View Model For Remote Views containing  All necessary Data
     */
    class Match {
        private Team homeTeam;
        private Team awayTeam;
        private Fixture fixture;

        public Match(Team homeTeam, Team awayTeam, Fixture fixture) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.fixture = fixture;
        }

        public Team getHomeTeam() {
            return homeTeam;
        }

        public void setHomeTeam(Team homeTeam) {
            this.homeTeam = homeTeam;
        }

        public Team getAwayTeam() {
            return awayTeam;
        }

        public void setAwayTeam(Team awayTeam) {
            this.awayTeam = awayTeam;
        }

        public Fixture getFixture() {
            return fixture;
        }

        public void setFixture(Fixture fixture) {
            this.fixture = fixture;
        }
    }