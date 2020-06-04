package main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import javax.xml.crypto.Data;

import algorithm.DataStructure;
import algorithm.PathGenerator;
import algorithm.State;
import algorithm.TabuSearchRunner;

public class Main {
	
	public static void printSolution(State solution) {
		System.out.println("Solution found for method: " + solution.getMethodName());
		System.out.println("   - target path   : " + solution.getTargetPath().toString());
		System.out.println("   - execution path: " + solution.getExecutionPath().toString());
		System.out.println("   - params        : " + new Gson().toJson(solution.getParams()));
		System.out.println("   - fitness value : " + solution.getFitnessValue());
		System.out.println();
	}

	public static void main(String[] args) {
		TabuSearchRunner runner = new TabuSearchRunner();
		try {
			runner.setTestClass("Triangle");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		List<State> solutions = new ArrayList<State>();
		List<Set<Integer>> targetPaths = runner.getTargetPaths();
		for (Set<Integer> targetPath : targetPaths) {
			if (targetPath.size() == 0) continue;
			System.out.println("Finding testcase for path: " + targetPath.toString());
			State initialState = new State(new DataStructure(-1, -1, -1));
			State solution = runner.solve(initialState, "checkTriangle", targetPath);
			solutions.add(solution);
			printSolution(solution);
		}
		
		System.out.println("All solutions: ");
		for (State solution : solutions) {
			printSolution(solution);
		}
	}
}
