package match;

public class Match implements Comparable<Match> {
	
	private String team_a; //Nafn á liðunum
	private String team_b;
	private String league;
	
	private double team_a_p; //p = prosent
	private double team_b_p;
	
	private double difference;
	
	public Match(String team_a, String team_b, double tap, double tbp, String deild) {
		
		this.team_a = team_a;
		this.team_b = team_b;
		this.league = deild;
		
		this.team_a_p = tap;
		this.team_b_p = tbp;
		
		this.difference = Math.abs(team_a_p - team_b_p);
		
	}
	
	public double getDiff() {
		return this.difference;
	}
	
	public double getHomeTPerc() {
		return this.team_a_p;
	}
	
	public double getAwayTPerc() {
		return this.team_b_p;
	}
	
	public String toString() {
		return team_a + ": " + String.valueOf(team_a_p) + "  " + team_b + ": " + String.valueOf(team_b_p) + "  Diff: " + String.valueOf(difference) + "   " + league;
	}

	@Override
	public int compareTo(Match other) {
		
		return Double.compare(this.difference, other.difference);
	}
}
