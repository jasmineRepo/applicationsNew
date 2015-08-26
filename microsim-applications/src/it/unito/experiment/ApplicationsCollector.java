package it.unito.experiment;

import it.unito.model.ApplicationsModel;
import it.unito.model.Vacancy;
import it.unito.model.Worker;
import it.zero11.microsim.data.db.DatabaseUtils;
import it.zero11.microsim.engine.AbstractSimulationCollectorManager;
import it.zero11.microsim.engine.EngineListener;
import it.zero11.microsim.engine.SimulationEngine;
import it.zero11.microsim.engine.SimulationManager;
import it.zero11.microsim.event.EventGroup;
import it.zero11.microsim.event.EventListener;
import it.zero11.microsim.event.SystemEventType;
import it.zero11.microsim.statistics.CrossSection;
import it.zero11.microsim.statistics.functions.MeanArrayFunction;
import it.zero11.microsim.statistics.functions.MultiTraceFunction;

import org.apache.log4j.Logger;

public class ApplicationsCollector extends AbstractSimulationCollectorManager implements EventListener, EngineListener {

	private final static Logger log = Logger.getLogger(ApplicationsCollector.class);
	
	public CrossSection.Integer csQueues, csEmployed;
	public MeanArrayFunction fMeanQueues, fMeanEmployed;
	public MultiTraceFunction.Integer fTraceVacancyNumber;
//	public MultiTraceFunction.Integer fTraceWorkerPerVacancyNumber;
	public MultiTraceFunction.Double fTraceUnemployedPerVacancyNumber;

	public ApplicationsCollector(SimulationManager manager) {
		super(manager);
		SimulationEngine.getInstance().addEngineListener(this);
	}

	public enum Processes {
		Update,
		DumpPeriodicInfo,
		DumpOneOffInfo;
	}

	public void buildObjects() {
		csQueues = new CrossSection.Integer(((ApplicationsModel) getManager()).getOpenVacancyList(), Vacancy.class, "getVacancyApplicationListSize", true);
		csEmployed = new CrossSection.Integer(((ApplicationsModel) getManager()).getWorkerList(), Worker.class, "getEmployedAsInt", true);
		
		fMeanQueues = new MeanArrayFunction(csQueues);
		fMeanEmployed = new MeanArrayFunction(csEmployed);
		fTraceVacancyNumber = new MultiTraceFunction.Integer((ApplicationsModel) getManager(), "getVacancyNumber", true);
//		fTraceWorkerPerVacancyNumber = new MultiTraceFunction.Integer((ApplicationsModel) getManager(), "getWorkerPerVacancyNumber", true);
		fTraceUnemployedPerVacancyNumber = new MultiTraceFunction.Double((ApplicationsModel) getManager(), "getUnemployedPerVacancyNumber", true);
		
	}
	
	public void buildSchedule() {
		EventGroup eventGroup = new EventGroup();
		eventGroup.addEvent(this, Processes.Update);
		eventGroup.addEvent(this, Processes.DumpPeriodicInfo);
		getEngine().getEventList().schedule(eventGroup, 0, 1);							
	
	}
	
	public void onEvent(Enum<?> type) {
		switch ((Processes) type) {
		
		case Update:
			update();
			break;
			
		case DumpPeriodicInfo:
			try {
				DatabaseUtils.snap(DatabaseUtils.getOutEntityManger(), 
						(long) SimulationEngine.getInstance().getCurrentRunNumber(), 
						getEngine().getTime(), 
						((ApplicationsModel) getManager()).getWorkerList());

			} catch (Exception e) {
				log.error(e.getMessage());				
			}
			break;
			
		case DumpOneOffInfo:
			try {
				DatabaseUtils.snap(DatabaseUtils.getOutEntityManger(), 
						(long) SimulationEngine.getInstance().getCurrentRunNumber(), 
						getEngine().getTime(), 
						((ApplicationsModel) getManager()).getVacancyList());

				DatabaseUtils.snap(DatabaseUtils.getOutEntityManger(), 
						(long) SimulationEngine.getInstance().getCurrentRunNumber(), 
						getEngine().getTime(), 
						((ApplicationsModel) getManager()).getApplicationList());
				
			} catch (Exception e) {
				log.error(e.getMessage());				
			}
			break;
		}
	}
	
	public void update()
	{
		fMeanQueues.updateSource();
		fMeanEmployed.updateSource();
		fTraceVacancyNumber.updateSource();
//		fTraceWorkerPerVacancyNumber.updateSource();
		fTraceUnemployedPerVacancyNumber.updateSource();
	}
	

	public double getMeanQueues() {
		return fMeanQueues.getDoubleValue(null);
	}
	
	public double getMeanEmployed() {
		return fMeanEmployed.getDoubleValue(null);
	}

	public void onEngineEvent(SystemEventType event) {
		if (event.equals(SystemEventType.Stop))
			onEvent(Processes.DumpOneOffInfo);		
	}
	
}	