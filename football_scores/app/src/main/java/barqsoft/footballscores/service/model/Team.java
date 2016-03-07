package barqsoft.footballscores.service.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Angelo Rüggeberg <s3xy4ngc@googlemail.com>
 */

public class Team {

    private String shortName;
    private String name;
    private String squadMarketValue;
    private String crestUrl;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSquadMarketValue() {
        return squadMarketValue;
    }

    public void setSquadMarketValue(String squadMarketValue) {
        this.squadMarketValue = squadMarketValue;
    }

    public String getCrestUrl() {

        /**
         * We have to transform SVG images into PNG.
         * https://upload.wikimedia.org/wikipedia/<LANG>/<SVG_PATH>/<IMAGE_NAME>.svg
         * becomes
         * https://upload.wikimedia.org/wikipedia/<LANG>/thumb/<SVG_PATH>/<IMAGE_NAME>.svg/<WIDTH>px-<IMAGE_NAME>.svg.png
         */
        if (crestUrl.endsWith(".svg")) {

            String regex = "(http.*?\\.wikimedia.*wikipedia\\/)([a-z]*)(.*\\/)(.*.svg)";
            Pattern p = Pattern.compile(regex);

            Matcher m = p.matcher(crestUrl);
            if (m.find()) {
                return m.replaceAll("$1$2/thumb$3$4/200px-$4.png");
            }
        }

        return crestUrl;
    }

    public void setCrestUrl(String crestUrl) {
        this.crestUrl = crestUrl;
    }
}
