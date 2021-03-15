package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

import java.util.ArrayList;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

public class Generator {

	
	static ArrayList<String> variables = new ArrayList<String>();
	static ArrayList<String> events = new ArrayList<String>();
	
	static void processModel() {

		ModelManager manager = new ModelManager();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			
			if(content instanceof VariableDefinition) {
				VariableDefinition variable = (VariableDefinition) content;
				variables.add(variable.getName());
				System.out.println("belső változó - "+variable.getName());
			}
			
			if(content instanceof EventDefinition) {
				EventDefinition event = (EventDefinition) content;
				events.add(event.getName());
				System.out.println("bemenő esemény - "+event.getName());
			}							
		}
	}
	
	public static void printVariables() {
		System.out.println("public static void print(IExampleStatemachine s) {");
		for (int i = 0; i < variables.size(); i++) {
			char firstLetter = Character.toUpperCase(variables.get(i).charAt(0));
			String withCapitalLetter = firstLetter + variables.get(i).substring(1);
		    System.out.println("	System.out.println(\""+ firstLetter + " = \" + s.getSCInterface().get" + withCapitalLetter + "());");
		}
		System.out.println("}");
	}
	
	public static void handleInput() {
		System.out.println("boolean shouldExit = false;");
		System.out.println("while (!shouldExit) {");
		System.out.println("	String input = scanner.nextLine();");
		System.out.println("switch (input) {");
		System.out.println("case \"exit\":");
		System.out.println("shouldExit = true;");
		System.out.println("break;");
		for (int i = 0; i < events.size(); i++) {
			System.out.println("case \"" + events.get(i) + "\":");
			
			System.out.println("s.raise" + Character.toUpperCase(events.get(i).charAt(0)) + events.get(i).substring(1) + "();");
			System.out.println("s.runCycle();");
			System.out.println("break;");
		}
		System.out.println("	}");
		
		printVariables();
		System.out.println("}");
		System.out.println("scanner.close();");
		System.out.println("System.exit(0);");
	}
	

	
	public static void main(String[] args) throws IOException {
	// modell beolvasasa
		processModel();
		handleInput();
		
		
		
	}

}
