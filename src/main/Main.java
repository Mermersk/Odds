package main;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.jsoup.nodes.Element;

import scraper.Scraper;
public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scraper varpa = new Scraper("denmark2", "Vendsyssel");
		//varpa.setCountry("Norway");
		
		System.out.println(varpa.connectLeague());
		//System.out.println(varpa.getElementTable());
		//System.out.println(varpa.getElementTable());
		String[] home = {};
		String[] away = {};
		varpa.connectAllResults();
		varpa.connectTeam();
		
		//ArrayList<Element> fal_tafla = varpa.getOrderlyTable();
		varpa.getAwayOrderlyTable();
		varpa.getHomeOrderlyTable();
	
		//for (int i = 0; i < fal_tafla.size(); i++) {
		//System.out.println(varpa.getOrderlyTable());
		//}
		ArrayList<String> leikir = varpa.getHomePlayedMatches();
		System.out.println(leikir.size());
		for (int i = 0; i < leikir.size(); i++) {
			//System.out.println(leikir.get(i));
		}
		//varpa.getAwayOrderlyTable();
		//for (int i = 0; i < 10; i++) {
		//varpa.setTeam(home[i]);
		
		int home_team = varpa.calculateHomeStrength();
		int away_team = varpa.calculateAwayStrength("Esbjerg");
		double home_team_best = varpa.getWorstAndBestScore().first(); //first er home team
		double away_team_best = varpa.getWorstAndBestScore().second(); //Second er away team
		
		double one_percent_home = (home_team_best*2)/100;
		double one_percent_away = (away_team_best*2)/100;
		
		double home_team_percentage = getPercentage(home_team, one_percent_home);
		double away_team_percentage = getPercentage(away_team, one_percent_away);
	
		System.out.println("Home team: " + home_team + " | Away team: " + away_team + "\n");
		//}
		
		System.out.println(home_team_best + "  " + away_team_best);
		System.out.println(one_percent_home + "  " + one_percent_away);
		System.out.println(home_team_percentage+"%" + "  " + away_team_percentage+"%");
		System.out.println(100/home_team_percentage + "  " + 100/away_team_percentage);
	
		//varpa.calculateAwayStrength("MSV Duisburg");
	}
	
	public static int getPercentage(int team_score, double one_percent) {
		int team_percent = 0;
		double team_counter = 0;
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

}
