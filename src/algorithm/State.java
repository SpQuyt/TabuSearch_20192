package algorithm;
import java.util.ArrayList;

public class State {
	int numsOfParams = 3;
	ArrayList<Integer> params = new ArrayList<Integer>(numsOfParams);
	private double fitnessValue;
	
	public State(int paramItem1, int paramItem2, int paramItem3) {
		this.params.set(0, paramItem1);
		this.params.set(1, paramItem2);
		this.params.set(2, paramItem3);
	}
	
	public double getFitnessValue() {
		return fitnessValue;
	}
	public void setFitnessValue(double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}
	
	public ArrayList<State> getNeighbors() {
		ArrayList<State> neighborsList = new ArrayList<State>(this.numsOfParams);
		neighborsList.add(new State(this.params.get(0) + 1, this.params.get(1), this.params.get(2)));
		neighborsList.add(new State(this.params.get(0), this.params.get(1) + 1, this.params.get(2)));
		neighborsList.add(new State(this.params.get(0), this.params.get(1), this.params.get(2) + 1));
		
		return neighborsList;
	}
	
}
