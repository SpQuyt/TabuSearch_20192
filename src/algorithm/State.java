package algorithm;
import java.util.ArrayList;
import java.util.Set;

public class State {
	private DataStructure params;
	private Set executionPath;
	private Set targetPath;
	private String methodName;
	
	public DataStructure getParams() {
		return params;
	}

	private double fitnessValue;
	
	public State(DataStructure params) {
		this.params = params;
	}
	
	public double getFitnessValue() {
		return fitnessValue;
	}
	
	public void setFitnessValue(double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}
	
	public ArrayList<State> getNeighbors(int offset) {
		ArrayList<State> neighborsList = new ArrayList<State>();
		neighborsList.add(
				new State(
						new DataStructure(
								this.params.getA() + offset,
								this.params.getB(),
								this.params.getC()
						)
				)
		);
		neighborsList.add(
				new State(
						new DataStructure(
								this.params.getA(),
								this.params.getB() + offset,
								this.params.getC()
						)
				)
		);
		neighborsList.add(
				new State(
						new DataStructure(
								this.params.getA(),
								this.params.getB(),
								this.params.getC() + offset
						)
				)
		);
		
		return neighborsList;
	}

	public Set getExecutionPath() {
		return executionPath;
	}

	public void setExecutionPath(Set executionPath) {
		this.executionPath = executionPath;
	}

	public Set getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(Set targetPath) {
		this.targetPath = targetPath;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
}
