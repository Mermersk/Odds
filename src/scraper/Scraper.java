package scraper;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Scraper {

	String league;
	Document doc_nett; //Ath: Þetta er home/away taflan
	Document doc_all_results;
	String home_team = "";
	String away_team = "";
	String full_table_home_string;
	String full_table_away_string;
	Element full_table_home;
	Element full_table_away;
	Element ar_table;
	ArrayList<String> past_matches_home = new ArrayList<String>(); //Listi fyrir alla (home og away) leiki sem liðið hefur spilað
	ArrayList<String> past_matches_away = new ArrayList<String>();
	
	ArrayList<String> home_past_matches = new ArrayList<String>(); //Þessi listi er hinsvegar fyrir aðeins alla heimaleiki "home" liðsins
	ArrayList<String> away_past_matches = new ArrayList<String>();
	
	ArrayList<String> orderly_away_table = new ArrayList<String>();
	ArrayList<String> orderly_home_table = new ArrayList<String>();
	
	String soccerstats_ha_adress = "http://www.soccerstats.com/homeaway.asp?league="; //Linkurinn til home/away töflunnar
	String soccerstats_ar_adress = "http://www.soccerstats.com/team_results.asp?league="; //Linkurinn til all results töflunnar
	public Scraper(String deild) {
		league = deild;
	}
	
	public void cleanOrderlyTables() {
		orderly_home_table.clear();
		orderly_away_table.clear();
	}
	
	public void clean() {
		
		
		home_past_matches.clear(); //Þessi listi er hinsvegar fyrir aðeins alla heimaleiki "home" liðsins
		away_past_matches.clear();
	
		doc_nett.clearAttributes(); //Ath: Þetta er home/away taflan
		doc_all_results.clearAttributes();
		home_team = "";
		away_team = "";
		full_table_home_string = "";
		full_table_away_string = "";
		full_table_home.clearAttributes();
		full_table_away.clearAttributes();
		ar_table.clearAttributes();
		
		soccerstats_ha_adress = "http://www.soccerstats.com/homeaway.asp?league="; //Fyrir s loopuna, resetta strenginn
		soccerstats_ar_adress = "http://www.soccerstats.com/team_results.asp?league="; //Linkurinn til all results töflunnar
	}
	
	public void setCountry(String country) {
		league = country;
	}
	
	public String getCountry() {
		return league;
	}
	
	public void setHomeTeam(String lid) {
		home_team = lid;
	}
	
	public String getHomeTeam() {
		return home_team;
	}
	
	public void setAwayTeam(String lid) {
		away_team = lid;
	}
	
	public String getAwayTeam() {
		return away_team;
	}
	
	public String connectLeague() throws IOException {
		soccerstats_ha_adress = soccerstats_ha_adress + league;
		doc_nett = Jsoup.connect(soccerstats_ha_adress).get();
		
		Element ele = doc_nett.body();
		//full_table_home = ele.getElementById("btable");
		full_table_home = ele.select("#btable").get(0); // # merkir id sem við leitum af, þetta id er t.d. svona: id="btable"
		full_table_home_string = full_table_home.text();
		full_table_away = ele.select("#btable").get(1);
		full_table_away_string = full_table_away.text();
		
		return "Data retrieval done for: " + league;
	}
	
	public String connectHomeTeam() {
		if (home_team.isEmpty() == true) {
			return "You must Specify team you are searching for with setTeam function";
		}
		
		Elements one_team_home = full_table_home.getElementsByClass("odd");
		Elements one_team_away = full_table_away.getElementsByClass("odd");
	
		int team_number_home = 0;
		int team_number_away = 0;
		//one_team = full_table_home.getElementsContainingText(team);
		for (int i = 0; i < one_team_home.size(); i++) {
			if (one_team_home.get(i).text().contains(home_team)) {
				team_number_home = i;
			}
			if (one_team_away.get(i).text().contains(home_team)) {
				team_number_away = i;
			}
		}
		return (one_team_home.get(team_number_home).text() + "\n" + one_team_away.get(team_number_away).text());
		
	}
	
	public Element connectAllPastResults() throws IOException {
		soccerstats_ar_adress = soccerstats_ar_adress + league + "&pmtype=bydate";
		doc_all_results = Jsoup.connect(soccerstats_ar_adress).get();
		Element el_ar = doc_all_results.body();
		ar_table = el_ar.select("#btable").get(0); //Nær i töfluna með leiki sem eru núþegar spilaðir
		String ar_table_text = ar_table.text();
		//System.out.println(el_ar);
		return ar_table;
	}
	
	public ArrayList<String> connectFutureResults(int how_many_matches) throws IOException {
		//soccerstats_ar_adress = soccerstats_ar_adress + league + "&pmtype=bydate";
		//doc_all_results = Jsoup.connect(soccerstats_ar_adress).get();
		Element el_ar = doc_all_results.body();
		Element future_matches;
		future_matches = el_ar.select("#btable").get(1); //get(1) skilar toflunni með framtiðarleikjum, en get(0) skilar past matches
		String future_matches_text = future_matches.text();
		//[^a-zA-Z]+   Arrays.asList()
		String[] home_teams;;
		ArrayList<String> matches = new ArrayList<String>(); 
		//future_matches_text.replaceAll(".", " ");
		future_matches_text = future_matches_text.replaceAll("[^a-zA-Z - ]+", "");
		future_matches_text = future_matches_text.substring(3, future_matches_text.length());
		System.out.println(future_matches_text);
		home_teams = future_matches_text.split("   ");
		//System.out.println(Arrays.asList(home_teams));
		for (int i = 0; i < how_many_matches; i++) {
			matches.add(home_teams[i]);
		}
	
		System.out.println(matches);
		return matches;
	}
	
	public ArrayList<String> getHomePlayedMatches() {
		
		Elements ar_table_elements = ar_table.getAllElements();
		
		ArrayList<Element> team_found = new ArrayList();
		for (int i = 2; i < ar_table_elements.size(); i++) {
			
			if (ar_table_elements.get(i).text().contains(home_team) == true) {
				 team_found.add(ar_table_elements.get(i));
			}
		}
		for (int i = 0; i < team_found.size(); i++) {
			
			past_matches_home.add(team_found.get(i).text());
		}
		for (int i = 0; i < past_matches_home.size(); i++) {
			//String kk = past_matches_home.get(i).replaceAll("(\\b-\\b)", "h");
			//past_matches_home.set(i, kk);
			if (past_matches_home.get(i).contains(":") == false) {
				past_matches_home.remove(i);
			}
			
		}
		return past_matches_home;
	}
	
	public ArrayList<String> getAwayPlayedMatches() {
		Elements ar_table_elements = ar_table.getAllElements();
		
		ArrayList<Element> team_found = new ArrayList();
		for (int i = 2; i < ar_table_elements.size(); i++) {
			
			if (ar_table_elements.get(i).text().contains(away_team) == true) {
				 team_found.add(ar_table_elements.get(i));
			}
		}
		for (int i = 0; i < team_found.size(); i++) {
			
			past_matches_away.add(team_found.get(i).text());
		}
		for (int i = 0; i < past_matches_away.size(); i++) {
			if (past_matches_away.get(i).contains(":") == false) {
				past_matches_away.remove(i);
			}
		}
		return past_matches_away;
	}
	
	public ArrayList<String> getAwayOrderlyTable() {
		
		Elements isk = full_table_away.getElementsByClass("odd");
		
		for (int i = 0; i < isk.size(); i++) {
			orderly_away_table.add(isk.get(i).text());
		}
		
		for (int i = 0; i < orderly_away_table.size(); i++) {
			
			String new_format = orderly_away_table.get(i);
			new_format = orderly_away_table.get(i).replaceAll("[^a-zA-Z0-9 ]", ""); //Eyði út öllum punktum, kommum og spesial characters
			orderly_away_table.remove(i);
			orderly_away_table.add(i, new_format);
			
			System.out.println(orderly_away_table.get(i));
			
		}
		return orderly_away_table;
		
	}
	
public ArrayList<String> getHomeOrderlyTable() {

		Elements isk = full_table_home.getElementsByClass("odd");
		
		for (int i = 0; i < isk.size(); i++) {
			orderly_home_table.add(isk.get(i).text());
		}
	
		
		for (int i = 0; i < orderly_home_table.size(); i++) {
			
			String new_format = orderly_home_table.get(i);
			new_format = orderly_home_table.get(i).replaceAll("[^a-zA-Z0-9 ]", "");
			orderly_home_table.remove(i);
			orderly_home_table.add(i, new_format);
		
			
			System.out.println(orderly_home_table.get(i));
			
		}
		
		return orderly_home_table;
	
	}
	
	public int calculateHomeStrength() {
		int win_score;
		int draw_score;
		int loss_score;
		int team_score = 0;
		int team_place_index = 0;
		//Gera lista með past home eða away leiki
		
		Pattern pattern = Pattern.compile(home_team); //Regex
		//Matcher matcher = pattern.matcher("");
		int start_char_pos;
		int midju_strikid = 0;
		for (int i = 0; i < past_matches_home.size(); i++) {
			Matcher matcher = pattern.matcher(past_matches_home.get(i));
			for (int v = 0; v < past_matches_home.get(i).length()-12; v++) { //-12 svo að ég fái ekki siðustu 2 miðjustrikin inn i leitina
				if (past_matches_home.get(i).charAt(v) == '-') {
					midju_strikid = v;
				}
			}
			while (matcher.find()) {
				//System.out.printf("I found the text" +
				//" \"%s\" starting at " +
				//"index %d and ending at index %d.%n",
				//matcher.group(),
				//matcher.start(),
				//matcher.end(),
				start_char_pos  = matcher.start(); // ")"
				if (start_char_pos < midju_strikid) {
					home_past_matches.add(past_matches_home.get(i));
				}
			}
		}
		
		String home_past_matches_results = "";
		Pattern pattern_score = Pattern.compile("(\\d+) - (\\d+)");
		for (int i = 0; i < home_past_matches.size(); i++) {
			Matcher matches_score = pattern_score.matcher(home_past_matches.get(i));
			while (matches_score.find() == true) {
				
				String one_result = matches_score.group();
				int result_a = Character.getNumericValue(one_result.charAt(0));
				int result_b = Character.getNumericValue(one_result.charAt(4));
				if (result_a == result_b) {
					home_past_matches_results = home_past_matches_results + "d";
				}
				if (result_a > result_b) {
					home_past_matches_results = home_past_matches_results + "w";
				}
				if (result_a < result_b) {
					home_past_matches_results = home_past_matches_results + "l";
				}
				
			}
			
			//Eyði út öllum punktum, kommum og spesial characters
			String new_format = home_past_matches.get(i);
			new_format = home_past_matches.get(i).replaceAll("[^a-zA-Z0-9- ]", "");
			home_past_matches.remove(i);
			home_past_matches.add(i, new_format);
			
			System.out.println(home_past_matches.get(i));
		}
		System.out.println(home_past_matches_results);
		// - ((\\w+) (\\w+))
		//
		String opponent = "";
		// - [(\p{L}+) (\p{L}+)]+
		Pattern pattern2 = Pattern.compile(home_team + " - ((\\w+) (\\w+))");
		int matcher_group_index = 1;
		for (int i = 0; i < home_past_matches.size(); i++) {
			Matcher matcher2 = pattern2.matcher(home_past_matches.get(i));
			while (matcher2.find()) {
				System.out.print("I found the text " + matcher2.group(matcher_group_index) + " starting at index " + matcher2.start(matcher_group_index) + " and ending at " + matcher2.end(matcher_group_index) + "\n");
				opponent = matcher2.group(matcher_group_index);
			}
			if (opponent.matches(".*\\d.*")) { //Ef tala er i strengnum(sem gerist ef stærð nafnsins á liðinu er bara 1 orð)
				opponent = opponent.substring(0, opponent.length()-1); // Taka töluna út
			}
			
			for (int v = 0; v < orderly_away_table.size(); v++) {
				if (orderly_away_table.get(v).contains(opponent)) {
					System.out.println(orderly_away_table.get(v));
					team_place_index = v;
				}
			}
			
			
				if (home_past_matches_results.charAt(i) == 'w') {
					team_score = team_score + (orderly_away_table.size() - team_place_index);
				}
				if (home_past_matches_results.charAt(i) == 'l') {
					team_score = team_score - team_place_index - 1;
				}
				if (home_past_matches_results.charAt(i) == 'd') {
					team_score = team_score + (orderly_away_table.size() - team_place_index)/2;
				}
			
			
			//Insert lokareiknings-kóðann
			
			System.out.println(team_score);	
		}
		
		System.out.println(past_matches_home.size());
		
		return team_score;
	}
	
	public int calculateAwayStrength() {
		
		
		int win_score;
		int draw_score;
		int loss_score;
		int team_score = 0;
		int team_place_index = 0;
		System.out.println(away_team);
		Pattern pattern_away = Pattern.compile(away_team); //Regex
		
		int start_char_pos;
		int midju_strikid = 0;
		for (int i = 0; i < past_matches_away.size(); i++) {
			Matcher matcher_away = pattern_away.matcher(past_matches_away.get(i));
			for (int v = 0; v < past_matches_away.get(i).length()-12; v++) { //-12 svo að ég fái ekki siðustu 2 miðjustrikin inn i leitina
				if (past_matches_away.get(i).charAt(v) == '-') {
					midju_strikid = v;
				}
			}
			while (matcher_away.find()) {
				//System.out.printf("I found the text" +
				//" \"%s\" starting at " +
				//"index %d and ending at index %d.%n",
				//matcher.group(),
				//matcher.start(),
				//matcher.end(),
				start_char_pos  = matcher_away.start(); 
				if (start_char_pos > midju_strikid) {
					away_past_matches.add(past_matches_away.get(i));
				}
			}
		}
		

		String away_past_matches_results = "";
		Pattern pattern_score2 = Pattern.compile("(\\d+) - (\\d+)");
		for (int i = 0; i < away_past_matches.size(); i++) {
			Matcher matches_score2 = pattern_score2.matcher(away_past_matches.get(i));
			while (matches_score2.find() == true) {
				
				String one_result2 = matches_score2.group();
				int result_a2 = Character.getNumericValue(one_result2.charAt(0));
				int result_b2 = Character.getNumericValue(one_result2.charAt(4));
				if (result_a2 == result_b2) {
					away_past_matches_results = away_past_matches_results + "d";
				}
				if (result_a2 < result_b2) {
					away_past_matches_results = away_past_matches_results + "w";
				}
				if (result_a2 > result_b2) {
					away_past_matches_results = away_past_matches_results + "l";
				}
				
			}
			//Eyði út öllum punktum, kommum og spesial characters
			String new_format = away_past_matches.get(i);
			new_format = away_past_matches.get(i).replaceAll("[^a-zA-Z0-9- ]", "");
			away_past_matches.remove(i);
			away_past_matches.add(i, new_format);
			
			
			System.out.println(away_past_matches.get(i));
		}
		System.out.println(away_past_matches.size());
		System.out.println(away_past_matches_results);
		
		// team + " - ((\\w+) (\\w+))"
		String opponent = "";
		Pattern pattern2 = Pattern.compile("((\\w+)|(\\w+) (\\w+))[a-zA-Z]+ -"); //(a|b) = Matches either a or b. Matchar annaðhvort bara eitt orð eða 2
		int matcher_group_index = 0;
		for (int i = 0; i < away_past_matches.size(); i++) {
			Matcher matcher2 = pattern2.matcher(away_past_matches.get(i));
			while (matcher2.find()) {
				System.out.print("I found the text " + matcher2.group(matcher_group_index) + " starting at index " + matcher2.start(matcher_group_index) + " and ending at " + matcher2.end(matcher_group_index) + "\n");
				opponent = matcher2.group(matcher_group_index);
				
			}
			if (opponent.matches("(\\w+) -")) { //Ef "-" er i strengnum þá taka það út
				opponent = opponent.substring(0, opponent.length()-2); // Ef "-" er i strengnum þá taka það út(hér ef liðsnafn eru 2 orð)
			}
			if (opponent.matches("(\\w+) (\\w+) -")) { //Ef tala er i strengnum(sem gerist ef stærð nafnsins á liðinu er bara 1 orð)
				opponent = opponent.substring(0, opponent.length()-2); // Taka strikið út
			}
			
			opponent = opponent.replaceAll("[0-9]+", "");
			
			//System.out.println(opponent);
			for (int v = 0; v < orderly_home_table.size(); v++) {
				if (orderly_home_table.get(v).contains(opponent)) {
					System.out.println(orderly_home_table.get(v));
					team_place_index = v;
				}
			}
			
			//for (int b = 0; i < home_past_matches_results.length(); b++) {
				if (away_past_matches_results.charAt(i) == 'w') {
					team_score = team_score + (orderly_home_table.size() - team_place_index);
				}
				if (away_past_matches_results.charAt(i) == 'l') {
					team_score = team_score - team_place_index - 1;
				}
				if (away_past_matches_results.charAt(i) == 'd') {
					team_score = team_score + (orderly_home_table.size() - team_place_index)/2;
				}
			//}
				System.out.println(team_score);
			//Insert lokareiknings-kóðann
		}
		
		System.out.println(team_score);
		
		//for (int i = 0; i < away_past_matches.size(); i++) {
			//System.out.println(away_past_matches.get(i));
		//}
		//System.out.println(away_past_matches.size());
		//System.out.println(past_matches_away.size());
		return team_score;
	}
	
	public Pair<Integer> getWorstAndBestScore() {
		int league_size = orderly_home_table.size() - 1; //Næ í fjölda liða i töflunni
		int pmh_count = home_past_matches.size(); //pmh_count = past_matches_home_count
		int pma_count = away_past_matches.size();
		
		int home_team_best_score = 0; //Best score og worst score er sama tala bara ein i plús og ein i minus
		int away_team_best_score = 0; //Hinsvegar getur scorið breyst eftir liðum því kannski heimaliðið hefur spilað 2 leiki en awayliðið 3 leiki
		for (int i = 0; i < pmh_count; i++){
			home_team_best_score = home_team_best_score + league_size;
			league_size = league_size - 1;
		}
		
		league_size = orderly_away_table.size() - 1; //-1 þar sem að CalculateStrength fyrir ofan er með það þannig
		for (int i = 0; i < pma_count; i++){
			away_team_best_score = away_team_best_score + league_size;
			league_size = league_size - 1;
		}
		//String.valueOf(home_team_best_score) + "   " + String.valueOf(away_team_best_score);
		Pair<Integer> df = new Pair<Integer>(home_team_best_score, away_team_best_score);
		return df;
	}
	
	public String getHomeTable() {
		return full_table_home_string;
	}
	public String getAwayTable() {
		return full_table_away_string;
	}
	public Element getElementTable() {
		return full_table_home;
	}
	
	public class Pair<T> {
	    private final T m_first;
	    private final T m_second;

	    public Pair(T first, T second) {
	        m_first = first;
	        m_second = second;
	    }

	    public T first() {
	        return m_first;
	    }

	    public T second() {
	        return m_second;
	    }
	}
	
}



//Það sem þarf i úteikninginn: fara yfir siðustu home eða away leiki,
//safna saman 3 tölur loss-draw-win, hver af þessari tölu er reiknað út með
//siðustu leiki liðsins safnað i stigagjöf þar sem að staða mótherjans i sinni töflu
//er lagt saman svo eftir hvort það var W-D-L. því ofar sem mótherjinn er i
//töflunni þvi fleiri stig fær hann fyrir sigur-jafntefli eða minus stig fyhrir tap
