package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	static ArrayList<String> variables = new ArrayList<String>();
	static ArrayList<String> events = new ArrayList<String>();
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		
		int nameSuggestion = 1;

		
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			
			if(content instanceof VariableDefinition) {
				VariableDefinition variable = (VariableDefinition) content;
				variables.add(variable.getName());
				// System.out.println("belső változó - "+variable.getName());
			}
			
			if(content instanceof EventDefinition) {
				EventDefinition event = (EventDefinition) content;
				events.add(event.getName());
				// System.out.println("bemenő esemény- "+event.getName());
			}			
					
			if(content instanceof State) {
				State state = (State) content;
				
				// 2.3) a kiinduló állapot tranzícióinak bejárásával a  célállapotok kiírása
				for (Transition t: state.getOutgoingTransitions()) {
					State outgoing = (State) t.getTarget();
					// System.out.println(state.getName()+" -> "+outgoing.getName());
					
				}
				
				// 2.4) kimenő él nélküli állapotok kiírása
				if (state.getOutgoingTransitions().isEmpty()) {
					System.out.println("'"+state.getName()+"' - csapdaállapot ");
				}
				
				// 2.5) név nélküli állapotok névjavaslata
				if (state.getName().isEmpty()) {
					state.setName("State_"+nameSuggestion);
					System.out.println("név nélküli állapot átnevezve: '"+state.getName()+"'");
					nameSuggestion++;
				}
			}
		}
		
		codeAutomatization();
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	// 4.4)
	public static void printVariables() {
		//System.out.println("public static void print(IExampleStatemachine s) {");
		for (int i = 0; i < variables.size(); i++) {
			char firstLetter = Character.toUpperCase(variables.get(i).charAt(0));
			String withCapitalLetter = firstLetter + variables.get(i).substring(1);
		    System.out.println("	System.out.println(\""+ firstLetter + " = \" + s.getSCInterface().get" + withCapitalLetter + "());");
		}
		//System.out.println("}");
	}
	
	// 4.5)
	public static void codeAutomatization() {
		System.out.println("public static void main(String[] args) throws IOException {");
		System.out.println("	ExampleStatemachine s = new ExampleStatemachine();");
		System.out.println("	Scanner scanner = new Scanner(System.in);");
		System.out.println("	s.setTimer(new TimerService());");
		System.out.println("	RuntimeService.getInstance().registerStatemachine(s, 200);");
		System.out.println("	s.init();");
		System.out.println("	s.enter();");
		System.out.println("	s.runCycle();");
		printVariables();
		
		System.out.println("	boolean shouldExit = false;");
		System.out.println("	while (!shouldExit) {");
		System.out.println("		String input = scanner.nextLine();");
		System.out.println("		switch (input) {");
		System.out.println("		case \"exit\":");
		System.out.println("			shouldExit = true;");
		System.out.println("			break;");
		for (int i = 0; i < events.size(); i++) {
			System.out.println("		case \"" + events.get(i) + "\":");
			System.out.println("			s.raise" + Character.toUpperCase(events.get(i).charAt(0)) + events.get(i).substring(1) + "();");
			System.out.println("			s.runCycle();");
			System.out.println("			break;");
		}
		System.out.println("		}");
		
		printVariables();
		
		System.out.println("	}");
		System.out.println("	scanner.close();");
		System.out.println("	System.exit(0);");
		System.out.println("}");
	}
}
