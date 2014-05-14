package com.arsenalist.sportsdata.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.List;
import com.arsenalist.sportsdata.parsers.ExpectedElementNotFound;

public class ESPNNBAScoreboardParser {
	
	private String date;
	private Document document;

	public ESPNNBAScoreboardParser(String date) {
		this.date = date;
		this.init();
	}

	private void init() {
		try {
			this.document = Jsoup.connect("http://scores.espn.go.com/nba/scoreboard?date=" + this.date).get();
		} catch (Exception e) {
			throw new RuntimeException("Could not get the list of games for " + this.date, e);
		}
	}

	public List<String> getGameIds() throws ExpectedElementNotFound {
		List<String> gameIds = new ArrayList<String>();
		Elements games = document.getElementsByAttributeValueContaining("id", "-statusLine1");
		if (games == null || games.isEmpty()) {
			throw new ExpectedElementNotFound("Couldn't find any games to download.");
		}
		for (Element game : games) {
			String gameId = game.attr("id").split("-")[0];
			gameIds.add(gameId);
		}
		return gameIds;
	}

}