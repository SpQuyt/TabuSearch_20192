package algorithm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.gson.Gson;

public class TabuSearchRunner {
	private Class testClass;
	private List<Set<Integer>> targetPaths;
	private final ArrayList testData = new ArrayList<String>();
	public static final int MAX_ITERATIONS = 100;
	public static final double ACCEPTABLE_FITNESS_VALUE = 1.0;

	public void setTestClass(String testClassName) throws ClassNotFoundException {
		this.testClass = Class.forName(testClassName);
		PathGenerator generator = new PathGenerator(testClass.getName());
		System.out.println("Generating files ...");
		generator.generateNecessaryFilesToUseToAnalysis();
		this.targetPaths = generator.getCFGAsArray();
		System.out.println("Possible paths: " + this.targetPaths.toString());
	}

	public List<Set<Integer>> getTargetPaths() {
		return targetPaths;
	}

	private static boolean isGetter(Method method) {
		if (!method.getName().startsWith("get"))
			return false;
		if (method.getParameterTypes().length != 0)
			return false;
		if (void.class.equals(method.getReturnType()))
			return false;
		return !method.getName().equals("getClass");
	}

	public List<Object> getAllGetterValueMethodFrom(Class dataClass, Object instanceObject) {
		List<Object> objects = new ArrayList<>();
		Method[] methods = dataClass.getMethods();

		for (Method method : methods) {
			if (isGetter(method)) {
				try {
					objects.add(method.invoke(instanceObject, null));
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return objects;
	}

	public Method getMethodInClass(String methodName, Class<?>... parameterTypes) {
		Method method = null;
		try {
			method = testClass.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			System.out.println("Can not find method " + methodName);
			e.printStackTrace();
		}
		return method;
	}

	public Set runMethod(String methodName, DataStructure data) {
		List<Object> listObjects = this.getAllGetterValueMethodFrom(data.getClass(), data);
		Method method = this.getMethodInClass(methodName, int.class, int.class, int.class);
		Object instanceObject = null;
		method.setAccessible(true);
		newTrace();
		try {
			method.invoke(instanceObject, listObjects.toArray(new Object[listObjects.size()]));
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		Set executionPath = getExecutionPath();
		return executionPath;
	}

	public void newTrace() {
		try {
			testClass.getDeclaredMethod("newTrace", null).invoke(null, null);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public Set getExecutionPath() {
		Method getTraceMethod;
		Set executionPath = new HashSet();
		try {
			getTraceMethod = testClass.getDeclaredMethod("getTrace", null);
			Object path = getTraceMethod.invoke(null, null);
			if (path instanceof Set) {
				executionPath = (Set) path;
			}
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return executionPath;
	}

	public double calculateFitnessValue(Set executionPath, Set targetPath) {
		List<Integer> targetPathArray = new ArrayList<>(targetPath);
		List<Integer> executionPathArray = new ArrayList<>(executionPath);
		double fitnessValue = 1.0;
		for (int checkpoint : targetPathArray) {
			if (!executionPathArray.contains(checkpoint)) {
				fitnessValue = fitnessValue - (1.0/(2 * targetPathArray.size()));
			}
		}
		for (int checkpoint : executionPathArray) {
			if (!targetPathArray.contains(checkpoint)) {
				fitnessValue = fitnessValue - (1.0/(2 * executionPathArray.size()));
			}
		}

		return fitnessValue;
	}
	
	public State getBestNeighbor(State solution, List<State> candidateNeighbors, List<State> tabuList) {
		System.out.print("Checking neighbors: ");
		for (State candidate : candidateNeighbors) {
			System.out.print(new Gson().toJson(candidate.getParams()));
			candidate.setMethodName(solution.getMethodName());
			candidate.setTargetPath(solution.getTargetPath());
			candidate.setExecutionPath(
					this.runMethod(candidate.getMethodName(), candidate.getParams())
			);
			candidate.setFitnessValue(
					this.calculateFitnessValue(candidate.getExecutionPath(), candidate.getTargetPath())
			);
			System.out.print("(" + candidate.getFitnessValue() + ") ");
		}
		System.out.println();
		State bestCandidate = candidateNeighbors.get(0);
		for (State candidate : candidateNeighbors) {
			if (tabuList.contains(candidate)) continue;
			if (candidate.getFitnessValue() > bestCandidate.getFitnessValue()) {
				bestCandidate = candidate;
			}
			if (candidate.getFitnessValue() == bestCandidate.getFitnessValue()) {
				// Choose random 1
				if (Math.random() < 0.5) {
					bestCandidate = candidate;
				}
			}
		}
		if (solution.getFitnessValue() > bestCandidate.getFitnessValue()) {
			int random = new Random().nextInt(candidateNeighbors.size());
			bestCandidate = candidateNeighbors.get(random);
//			return null;
		}
		return bestCandidate;
	}

	public State solve(State initialSolution, String methodName, Set targetPath) {
		
		State currentState = initialSolution;
		currentState.setMethodName(methodName);
		currentState.setTargetPath(targetPath);
		
		currentState.setExecutionPath(
				this.runMethod(currentState.getMethodName(), currentState.getParams())
		);
		currentState.setFitnessValue(
				this.calculateFitnessValue(currentState.getExecutionPath(), currentState.getTargetPath())
		);

		List<State> tabuList = new ArrayList<State>();
		
		int iterationCounter = 0;
		Boolean solutionFound = false;
	 
		//we make a predefined number of iterations
		while(iterationCounter < MAX_ITERATIONS && !solutionFound) {
			System.out.println(iterationCounter + ". Checking solution: " + new Gson().toJson(currentState.getParams()));
			System.out.println("Current fitness value: " + currentState.getFitnessValue());
			
			State bestNeighborFound = null;
			int counter = 1;
			while (bestNeighborFound == null && counter < 10) {
				List<State> candidateNeighbors = currentState.getNeighbors(counter);
				
				//get the best neighbor (lowest f(x) value) AND make sure it is not in the tabu list
				bestNeighborFound = this.getBestNeighbor(currentState, candidateNeighbors, tabuList);
				counter++;
			}
	 
			if (bestNeighborFound == null) {
				System.out.println("Can not find neighbors");
				return currentState;
			}
			
			//we are looking for a minimum in this case
			if (bestNeighborFound.getFitnessValue() >= ACCEPTABLE_FITNESS_VALUE) {
				
				solutionFound = true;
				currentState = bestNeighborFound;
				break;
			}
	 
			//we add it to the tabu list because we considered this item
			tabuList.add(currentState);
	 
			//hop to the next state
			currentState = bestNeighborFound;
	 
			iterationCounter++;
		}
	 
		// solution of the algorithm
		
		if (!solutionFound) {
			System.out.println("Can not find acceptable solution with fitness value >= " + ACCEPTABLE_FITNESS_VALUE);
		}
		
		return currentState;
	}
}
