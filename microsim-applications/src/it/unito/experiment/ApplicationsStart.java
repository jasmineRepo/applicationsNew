package it.unito.experiment;

import it.unito.model.ApplicationsModel;
import it.zero11.microsim.engine.ExperimentBuilder;
import it.zero11.microsim.engine.SimulationEngine;
import it.zero11.microsim.gui.shell.MicrosimShell;

public class ApplicationsStart implements ExperimentBuilder {

	public static void main(String[] args) {
		boolean showGui = true;
				
		ApplicationsStart experimentBuilder = new ApplicationsStart();
		final SimulationEngine engine = SimulationEngine.getInstance();
		MicrosimShell gui = null;
		if (showGui) {
			gui = new MicrosimShell(engine);		
			gui.setVisible(true);
		}
		
		engine.setExperimentBuilder(experimentBuilder);
			
		engine.setup();	
	}
	
	public void buildExperiment(SimulationEngine engine) {
		ApplicationsModel model = new ApplicationsModel();
		ApplicationsCollector collector = new ApplicationsCollector(model);
		ApplicationsObserver observer = new ApplicationsObserver(model, collector);
				
		engine.addSimulationManager(model);
		engine.addSimulationManager(collector);
		engine.addSimulationManager(observer);		
	}	
}	