package main;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import algorithm.PathGenerator;
import algorithm.TabuSearchRunner;

public class Main {
	
	

	public static void main(String[] args) {
		PathGenerator pg = new PathGenerator("Triangle");
		pg.generateNecessaryFilesToUseToAnalysis();
		
//		List<Set<Integer>> pathsList = pg.getCFGAsArray();
//		System.out.println(pathsList.toString());
		
		TabuSearchRunner runner = new TabuSearchRunner();
		try {
			runner.setTestClass("Triangle");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Method currentMethod = runner.getMethodInClass("checkTriangle", int.class, int.class, int.class);
		runner.run(currentMethod, null, 11, 11, 12);
		System.out.println(runner.getExecutionPath().toString());
	}
}
