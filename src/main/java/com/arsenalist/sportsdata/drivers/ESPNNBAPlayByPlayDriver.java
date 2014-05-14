package com.arsenalist.sportsdata.drivers;

import org.apache.commons.lang3.StringUtils;
import java.io.PrintWriter;
import java.io.File;
import com.arsenalist.sportsdata.parsers.ESPNNBAScoreboardParser;
import com.arsenalist.sportsdata.parsers.ESPNNBAPlayByPlayParser;
import com.arsenalist.sportsdata.parsers.ExpectedElementNotFound;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileNotFoundException;

public class ESPNNBAPlayByPlayDriver {
	public static void main(String[] args) throws Exception {
		String date = null;
		try {
			date = parseDateFromArgs(args);
		} catch (Exception e) {
			System.out.println("Could not parse date - must be supplied as an argument in YYYYMMDD format");
			return;
		}


		List<String> gameIds = null;
		try {
			ESPNNBAScoreboardParser scoreboardParser = new ESPNNBAScoreboardParser(date);
			gameIds = scoreboardParser.getGameIds();
		} catch (Exception e) {
			System.out.println("Could not get the list of games for " + date);
			return;
		}
		System.out.println("Game IDs to process: " + gameIds);
		for (String gameId : gameIds) {
			String homeTeam = null;
			String pbpString = null;			
			try {
				ESPNNBAPlayByPlayParser pbp = new ESPNNBAPlayByPlayParser(gameId);
				homeTeam = pbp.getHomeTeam();
				pbpString = pbp.getPlayByPlayAsCsvString();			
			} catch (Exception e) {
				System.out.println("Could not process " + gameId + ": " + e);
				continue;
			}
			System.out.println("Successfully processed " + gameId);
			writeToFile("playbyplay_" + date, homeTeam + "_" + date + ".csv", pbpString);
		}
	}

	private static String parseDateFromArgs(String[] args) throws Exception {
		String date = args[0];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.parse(date);
		return date;
	}

	private static void writeToFile(String folder, String fileName, String contents) throws Exception {
		File dir = new File(folder);
		dir.mkdirs();
		File f = new File(dir, fileName);
		PrintWriter writer = new PrintWriter(f.getAbsolutePath(), "UTF-8");
		writer.print(contents);
		writer.close();
	}
}
