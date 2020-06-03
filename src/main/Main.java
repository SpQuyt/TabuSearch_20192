package main;

import java.util.List;
import java.util.Set;

import algorithm.PathGenerator;

public class Main {

	public static void main(String[] args) {
		PathGenerator pg = new PathGenerator("Triangle");
		pg.generateNecessaryFilesToUseToAnalysis();
		
		List<Set<Integer>> niggalist = pg.getCFGAsArray();
		System.out.println(niggalist.toString());
	}
}
