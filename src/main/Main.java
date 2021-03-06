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
    private static Class testClass;
	private final static ArrayList testData = new ArrayList<String>();
	static DataStructure Data = new DataStructure(3, 4, 5);
	private static <T> T getData() {
		return (T) Data;
	}
	
    private static void newTrace() {
        try {
            testClass.getDeclaredMethod("newTrace", null).invoke(null, null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    private static void checkdata(List<Set<Integer>> pathsList, TabuSearchRunner runner,  DataStructure data) {
    	int i = 0;
		
		for (Set<Integer> pathList : pathsList) {
			if (pathList.size() == 0 ) continue;
			Object bestSolution = getData();
			
			double fitnessValue = 0.0;
			
			while ( i<2 ) {
				runner.newTrace();	
				Class dataClass = getData().getClass();
				
				List<Object> listObjects = runner.getAllGetterValueMethodFrom(dataClass, getData());
				Method currentMethod = runner.getMethodInClass("checkTriangle", int.class, int.class, int.class);
                runner.run(currentMethod, null, listObjects.toArray(new Object[listObjects.size()]));
    			
                Gson gson = new Gson();
//                String stringData = gson.toJson(getData());
				
                double fitnessValue1 = runner.calculateFitnessValue(runner.getExecutionPath(), pathList);
                
                if (fitnessValue1 > fitnessValue) {
					bestSolution = getData();
					
					fitnessValue = fitnessValue1;
					
					if (fitnessValue1 == 1) {
						break;
					}
				} 
//                
                checkdata(pathsList, runner, new DataStructure(Data.getA() + 1, Data.getB(), Data.getC()));
                checkdata(pathsList, runner, new DataStructure(Data.getA(), Data.getB() + 1, Data.getC()));
                checkdata(pathsList, runner, new DataStructure(Data.getA(), Data.getB(), Data.getC() + 1));
               
                i++;
			}
			testData.add(new Gson().toJson(bestSolution));
			System.out.println(fitnessValue);
			System.out.println("TEST DATA: " + testData);
		}
    }
	

	public static void main(String[] args) {
		
		PathGenerator pg = new PathGenerator("Triangle");
		pg.generateNecessaryFilesToUseToAnalysis();
		
		List<Set<Integer>> pathsList = pg.getCFGAsArray();
		System.out.println(pathsList.toString());
		
		TabuSearchRunner runner = new TabuSearchRunner();
		try {
			runner.setTestClass("Triangle");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		checkdata(pathsList, runner, getData());
		
		
//		Method currentMethod = runner.getMethodInClass("checkTriangle", int.class, int.class, int.class);
//		runner.run(currentMethod, null, 11, 11, 12);
//		System.out.println(runner.getExecutionPath().toString());
		
		
	}
}
