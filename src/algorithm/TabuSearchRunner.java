package algorithm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabuSearchRunner {
	private Class testClass;
	private List<Set<Integer>> targetPaths;
    private final ArrayList testData = new ArrayList<String>();

	public void setTestClass(String testClassName) throws ClassNotFoundException {
        this.testClass = Class.forName(testClassName);
        PathGenerator generator = new PathGenerator(testClass.getName());
        this.targetPaths = generator.getCFGAsArray();
    }
	
	private List<Set<Integer>> getTargetPaths() {
        return targetPaths;
    }
	
	private static boolean isGetter(Method method){
        if(!method.getName().startsWith("get"))
            return false;
        if(method.getParameterTypes().length != 0)
            return false;
        if(void.class.equals(method.getReturnType()))
            return false;
        return !method.getName().equals("getClass");
    }
	
	public List<Object> getAllGetterValueMethodFrom(Class dataClass, Object instanceObject) {
        List<Object> objects = new ArrayList<>();
        Method[] methods = dataClass.getMethods();

        for(Method method: methods) {
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
	
	public void run(Method method, Object instanceObject, Object... params) {
        method.setAccessible(true);
        try {
            method.invoke(instanceObject, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
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
        List<Integer> executionNodesInTargetPath = new ArrayList<>();
        System.out.println("Execution path: " + executionPath);
        int targetPathSize = targetPath.size();
        for (int i = 0; i < targetPathSize; i++) {
            if (targetPathArray.get(i).equals(executionPathArray.get(i))) {
                executionNodesInTargetPath.add(executionPathArray.get(i));
            } else {
                break;
            }
        }

        return (double) executionNodesInTargetPath.size() / targetPath.size();
	}
	
//	private void runTabu(Method method, ) {
//		// TODO Auto-generated method stub
//
//	}
}
