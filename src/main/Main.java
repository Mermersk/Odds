package main;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Spliterator;

import org.jsoup.nodes.Element;

import scraper.Scraper;
public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scraper varpa = new Scraper("singapore");
		//varpa.setCountry("Norway");
		
		System.out.println(varpa.connectLeague());
		varpa.connectAllPastResults();
		varpa.connectHomeTeam();
		
		varpa.getHomeOrderlyTable();
		System.out.println("\n");
		varpa.getAwayOrderlyTable();
		
		
		ArrayList<String> home_leikir = varpa.getHomePlayedMatches();
		ArrayList<String> away_leikir = varpa.getAwayPlayedMatches();
		//System.out.println(away_leikir);
	
		int whitespace_counter = 0;
		ArrayList<String> future_matches = varpa.connectFutureResults(2);
		ArrayList<String> fm_home_team = new ArrayList<String>(); //ft = future_matches
		ArrayList<String> fm_away_team = new ArrayList<String>();
		for (int i = 0; i < future_matches.size(); i++) {
			if (future_matches.get(i).contains(" pp")) {
				future_matches.remove(i);
			}
			for (int v = 0; v < future_matches.get(i).length(); v++) {
				if (future_matches.get(i).charAt(v) == ' ') {
					whitespace_counter = whitespace_counter + 1;
				} else {
					whitespace_counter = 0;
				}
				if (whitespace_counter >= 2) {
					System.out.println("TRIR");
					fm_home_team.add(future_matches.get(i).substring(0, v-1));
					fm_away_team.add(future_matches.get(i).substring(v+1, future_matches.get(i).length()));
				}
			}
		}
		
		System.out.println(future_matches);
		System.out.println(fm_home_team);
		System.out.println(fm_away_team);
		
		for (int i = 0; i < fm_home_team.size(); i++) {
		
		//varpa.connectHomeTeam();
		//varpa.getHomePlayedMatches();
			
		varpa.setHomeTeam(fm_home_team.get(i).trim());
		varpa.setAwayTeam(fm_away_team.get(i).trim());

		
		int home_team = varpa.calculateHomeStrength();
		int away_team = varpa.calculateAwayStrength();
		double home_team_best = varpa.getWorstAndBestScore().first(); //first er home team
		double away_team_best = varpa.getWorstAndBestScore().second(); //Second er away team
		
		double one_percent_home = (home_team_best*2)/100;
		double one_percent_away = (away_team_best*2)/100;
		
		double home_team_percentage = getPercentage(home_team, one_percent_home);
		double away_team_percentage = getPercentage(away_team, one_percent_away);
	
		System.out.println(varpa.getHomeTeam() + " " + home_team + " | " + varpa.getAwayTeam() + " " + away_team + "\n");
		
		
		System.out.println(home_team_best + "  " + away_team_best);
		System.out.println(one_percent_home + "  " + one_percent_away);
		System.out.println(home_team_percentage+"%" + "  " + away_team_percentage+"%");
		System.out.println(100/home_team_percentage + "  " + 100/away_team_percentage);
		String s_odds = stabilizeOdds(100/home_team_percentage, 100/away_team_percentage);
		System.out.println(s_odds);
		varpa.clean(); //Endurstilla alla lista og þannig annars bætist bara við þá þegar komið er mað leik nr2 og so on
		}
		
		
	}
	
	public static int getPercentage(int team_score, double one_percent) {
		int team_percent = 0;
		double team_counter = 0;
		if (team_score == 0) {
			return 111;
		}
		
		if (team_score > 0) {
			team_percent = team_percent + 50;
			while (team_counter < team_score) {
				team_counter = team_counter + one_percent;
				team_percent = team_percent + 1;
			}
		}
		
		if (team_score < 0) {
			team_percent = team_percent - 50;
			while (team_counter > team_score) {
				team_counter = team_counter - one_percent;
				team_percent = team_percent + 1;
			}
		}
		
		return Math.abs(team_percent);
		
	}
	
	public static String stabilizeOdds(double home_odds, double away_odds) {
		
		double s_home_odds = home_odds;
		double s_away_odds = away_odds;
		
		if (s_home_odds == 0 || s_away_odds == 0) {
			return "O i odds gengur ekki";
		}
		
		while (s_home_odds + s_away_odds <= 4) {
			s_home_odds += 0.01;
			s_away_odds += 0.01;
		}
		
		while (s_home_odds + s_away_odds > 4) {
			s_home_odds -= 0.01;
			s_away_odds -= 0.01;
		}
		
		
		return "Stablized odds| Home Team: " + s_home_odds + " Away Team: " + s_away_odds;
	}

}

