package it.unito.model;

import it.zero11.microsim.annotation.ModelParameter;
import it.zero11.microsim.engine.AbstractSimulationManager;
import it.zero11.microsim.event.EventGroup;
import it.zero11.microsim.event.EventListener;
import it.zero11.microsim.event.SystemEventType;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ApplicationsModel extends AbstractSimulationManager implements EventListener {

	private final static Logger log = Logger.getLogger(ApplicationsModel.class);

	private List<Worker> workerList;
	private List<Vacancy> vacancyList, openVacancyList;
	private List<Application> applicationList;
	
	public enum Processes {
		OpenVacancies,
		PrintJobQueues;
	}
	
	// Stop
	@ModelParameter
	private Integer endTime = 100;

	// Vacancies
	@ModelParameter
	private Integer vacancyDuration = 30;

	@ModelParameter
	private Integer newVacanciesPerPeriod = 30;
	
	// Workers
	@ModelParameter
	private Integer nWorkers = 1000;
	
	@ModelParameter
	private Integer applicationsPerPeriod = 1;
	
	public void buildObjects() {
	
		workerList = new ArrayList<Worker>();
		vacancyList = new ArrayList<Vacancy>();
		openVacancyList = new ArrayList<Vacancy>();
		applicationList = new ArrayList<Application>();
		
		//agentList = (List<Agent>) DatabaseUtils.loadTable(Agent.class);
		for (int i=0; i<nWorkers; i++){
			Worker worker = new Worker();
			workerList.add(worker);
		}
	}
	
	public void buildSchedule() {
		EventGroup eventGroup = new EventGroup();
		eventGroup.addCollectionEvent(workerList, Worker.Processes.ResetEmploymentStatus);
		eventGroup.addEvent(this, Processes.OpenVacancies);
		// all workers apply because all workers are reset to unemployed at the beginning of the period
		eventGroup.addCollectionEvent(workerList, Worker.Processes.Apply);
		eventGroup.addCollectionEvent(openVacancyList, Vacancy.Processes.Select, false);
//		eventGroup.addEvent(this, Processes.PrintJobQueues);

		getEngine().getEventList().schedule(eventGroup, 0, 1);
//		getEngine().getEventList().schedule(new SingleTargetEvent(this, Processes.End), endTime);
		getEngine().getEventList().scheduleSystem(endTime, 0, getEngine(), SystemEventType.Stop);
	}
	
	public void onEvent(Enum<?> type) {
		switch ((Processes) type) {
		
		case OpenVacancies:
			for (int i=0; i<newVacanciesPerPeriod; i++) {
				Vacancy vacancy = new Vacancy();
				vacancyList.add(vacancy);
				openVacancyList.add(vacancy);
			}
			break;
			
		case PrintJobQueues:
			printAvgJobQueue();
			break;
		
		}
	}
	
	private void printAvgJobQueue() {

		double avgJobQueue = 0;
		for(Vacancy vac : openVacancyList) {
			avgJobQueue += vac.getVacancyApplicationListSize();
		}
		avgJobQueue /= (double)openVacancyList.size();
		System.out.println(avgJobQueue);
		
	}

	public List<Vacancy> getVacancyList () {
		return vacancyList;
	}
	
	// used by the Collector through reflection
	public int getVacancyNumber () {
		return openVacancyList.size();
	}
	
	// used by the Collector through reflection
	public int getWorkerPerVacancyNumber() {		//Should we make this only unemployed workers per vacancy?  Currently workerList size is constant
		return (int)((double)workerList.size() / (double)openVacancyList.size());
	}
	
	// used by the Collector through reflection
	public double getUnemployedPerVacancyNumber() {		//Should we make this only unemployed workers per vacancy?  Currently workerList size is constant
		int unemployedCount = 0;
		for(Worker worker : workerList) {
			if(!worker.getEmployed()) {
				unemployedCount++;
			}
		}
		return ((double)unemployedCount / (double)openVacancyList.size());
	}
	
	public List<Vacancy> getOpenVacancyList() {
		return openVacancyList;
	}

	public List<Worker> getWorkerList () {
		return workerList;
	}
	
	public Integer getVacancyDuration() {
		return vacancyDuration;
	}

	public void setVacancyDuration(Integer vacancyDuration) {
		this.vacancyDuration = vacancyDuration;
	}

	public Integer getNewVacanciesPerPeriod() {
		return newVacanciesPerPeriod;
	}

	public void setNewVacanciesPerPeriod(Integer newVacanciesPerPeriod) {
		this.newVacanciesPerPeriod = newVacanciesPerPeriod;
	}

	public Integer getnWorkers() {
		return nWorkers;
	}

	public void setnWorkers(Integer nWorkers) {
		this.nWorkers = nWorkers;
	}

	public int getApplicationsPerPeriod () { 
		return applicationsPerPeriod;
	}
	
	public void setApplicationsPerPeriod(Integer applicationsPerPeriod) {
		this.applicationsPerPeriod = applicationsPerPeriod;
	}

	public List<Application> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<Application> applicationList) {
		this.applicationList = applicationList;
	}
	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}
}	