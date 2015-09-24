package it.unito.experiment;

import it.zero11.microsim.engine.AbstractSimulationObserverManager;
import it.zero11.microsim.engine.SimulationCollectorManager;
import it.zero11.microsim.engine.SimulationManager;
import it.zero11.microsim.event.CommonEventType;
import it.zero11.microsim.event.EventGroup;
import it.zero11.microsim.event.EventListener;
import it.zero11.microsim.gui.GuiUtils;
import it.zero11.microsim.gui.plot.TimeSeriesSimulationPlotter;
import it.zero11.microsim.statistics.IIntSource;

import org.apache.log4j.Logger;

public class ApplicationsObserver extends AbstractSimulationObserverManager implements EventListener {

	private final static Logger log = Logger.getLogger(ApplicationsObserver.class);

	private TimeSeriesSimulationPlotter queuesPlotter, employedPlotter, vacancyPlotter, unemployedPerVacancyPlotter;//, workersPerVacancyPlotter;
	
	public ApplicationsObserver(SimulationManager manager, SimulationCollectorManager collectorManager) {
		super(manager, collectorManager);
	}
	
	public enum Processes {
	}

	public void buildObjects() {
//		final ApplicationsModel model = (ApplicationsModel) getManager();
		final ApplicationsCollector collector = (ApplicationsCollector) getCollectorManager();
		
		queuesPlotter = new TimeSeriesSimulationPlotter("job queues", "#");
		queuesPlotter.addSeries("", collector.fMeanQueues);
		GuiUtils.addWindow(queuesPlotter, 1050, 0, 400, 400); 
//		GuiUtils.addWindow(queuesPlotter, 1350, 0, 550, 550);
		
		employedPlotter = new TimeSeriesSimulationPlotter("employment rate", "%");
		employedPlotter.addSeries("", collector.fMeanEmployed);
		GuiUtils.addWindow(employedPlotter, 650, 0, 400, 400);
//		GuiUtils.addWindow(employedPlotter, 800, 0, 550, 550);
	
		vacancyPlotter = new TimeSeriesSimulationPlotter("vacancies", "#");
		vacancyPlotter.addSeries("", (IIntSource) collector.fTraceVacancyNumber); 				
		GuiUtils.addWindow(vacancyPlotter, 250, 0, 400, 400);
//		GuiUtils.addWindow(vacancyPlotter, 250, 0, 550, 550);
		
//		workersPerVacancyPlotter = new TimeSeriesSimulationPlotter("workers per vacancy", "#");
//		workersPerVacancyPlotter.addSeries("", (IIntSource) collector.fTraceWorkerPerVacancyNumber);
//		GuiUtils.addWindow(workersPerVacancyPlotter, 1450, 0, 400, 400);

		unemployedPerVacancyPlotter = new TimeSeriesSimulationPlotter("unemployed per vacancy", "#");
		unemployedPerVacancyPlotter.addSeries("", collector.fTraceUnemployedPerVacancyNumber, IIntSource.Variables.Default);
		GuiUtils.addWindow(unemployedPerVacancyPlotter, 1450, 0, 400, 400);
		
	}
	
	public void buildSchedule() {
		EventGroup eventGroup = new EventGroup();
		
		eventGroup.addEvent(queuesPlotter, CommonEventType.Update);
		eventGroup.addEvent(employedPlotter, CommonEventType.Update);
		eventGroup.addEvent(vacancyPlotter, CommonEventType.Update);
//		eventGroup.addEvent(workersPerVacancyPlotter, CommonEventType.Update);
		eventGroup.addEvent(unemployedPerVacancyPlotter, CommonEventType.Update);
		getEngine().getEventList().schedule(eventGroup, 0, 1);
						
		log.debug("Observer schedule created");
		}
		
	
	public void onEvent(Enum<?> type) {
		switch ((Processes) type) {
		}
	}
	
}	