package com.arsenalist.sportsdata.drivers;
import org.apache.commons.cli.*;
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
		try {
			CommandLine cmd = initializeCommandLine(args);

			if (cmd.hasOption("g")) {
			    runForGame(cmd.getOptionValue("g"), ".");
			} else {
				String date = cmd.hasOption("d") ? 	cmd.getOptionValue("d") : args[0];
				date = validateDateArgument(date);
				runForDate(date);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static CommandLine initializeCommandLine(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("g", true, "Argument is a game.");
		options.addOption("d", true, "Argument is a date.");
		CommandLineParser parser = new BasicParser();
		return parser.parse( options, args);		

	}

	private static void runForDate(String date) throws Exception {
		List<String> gameIds = null;
		ESPNNBAScoreboardParser scoreboardParser = new ESPNNBAScoreboardParser(date);
		gameIds = scoreboardParser.getGameIds();
		System.out.println("Game IDs to process: " + gameIds);
		for (String gameId : gameIds) {
			runForGame(gameId, "playbyplay_" + date);
		}
	}


	private static void runForGame(String gameId, String folder) throws Exception {
		try {
			System.out.println("Game ID to process: " + gameId);
			ESPNNBAPlayByPlayParser pbp = new ESPNNBAPlayByPlayParser(gameId);
			String homeTeam = pbp.getHomeTeam();
			String pbpString = pbp.getPlayByPlayAsCsvString();
			writeToFile(folder, homeTeam + "_" + gameId + ".csv", pbpString);	
			System.out.println("Successfully processed " + gameId);

		} catch (Exception e) {
			System.out.println("Could not process " + gameId + ": " + e);
		}
	}


	private static String validateDateArgument(String date) throws Exception {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.parse(date);
			return date;
		} catch (Exception e) {
			throw new RuntimeException("Could not parse date - must be supplied as YYYYMMDD format");
		}
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
