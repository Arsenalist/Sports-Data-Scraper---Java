package com.arsenalist.sportsdata.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.arsenalist.sportsdata.parsers.ExpectedElementNotFound;


public class ESPNNBAPlayByPlayParser {
	
	private String gameId;
	private Document document;	

	public ESPNNBAPlayByPlayParser(String gameId) {
		this.gameId = gameId;
		this.init();
	}

	private void init() {
		try {
			this.document = Jsoup.connect("http://scores.espn.go.com/nba/playbyplay?gameId=" + this.gameId + "&period=0").get();
		} catch (Exception e) {
			throw new RuntimeException("Could not connect to the play by play page for " + gameId, e);
		}
	}

	public String getHomeTeam() throws ExpectedElementNotFound {
		Elements playByPlay = this.document.getElementsByAttributeValueMatching("class", "mod-data");
		if (playByPlay == null || playByPlay.isEmpty()) {
			throw new ExpectedElementNotFound("Could not retrieve home team");
		}
		return playByPlay.get(0).children().get(0).children().get(1).children().get(3).text();
	}	

	public String getPlayByPlayAsCsvString() throws ExpectedElementNotFound {
		Elements playByPlay = this.document.getElementsByAttributeValueMatching("class", "mod-data");		
		if (playByPlay == null || playByPlay.isEmpty()) {
			throw new ExpectedElementNotFound("Could not find play-by-play table");
		}
		StringBuffer sb = new StringBuffer();
		Elements theadsAndTbodies = playByPlay.get(0).children();
		for (Element theadOrTbody : theadsAndTbodies) {
			for (Element tr : theadOrTbody.children()) {
				Elements tds = tr.children();
				String row = "";
				for (Element td : tds) {
					String text = StringUtils.trimToEmpty(td.text());
					text = text.replace("\u00a0", "");
					if (StringUtils.isNotBlank(text)) {							
						row = row + text + ",";
					} 
				}
				row = StringUtils.removeEnd(row, ",");
				if (StringUtils.isNotBlank(row)) {
					sb.append(row);
					sb.append("\n");
				}									
			}
		}
		return StringUtils.chomp(sb.toString());
	}
}
