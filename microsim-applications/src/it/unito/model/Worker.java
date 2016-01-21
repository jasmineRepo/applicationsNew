package it.unito.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import microsim.data.db.PanelEntityKey;
import microsim.engine.SimulationEngine;
import microsim.event.EventListener;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Worker implements EventListener {

	public static int idCounter = 0;
	
	public enum Processes {
		Apply,
		ResetEmploymentStatus;
	}
	
	@Transient
	private ApplicationsModel model;
	
	@Transient
	private List<Application> workerApplicationList;
	
	@Id
	private PanelEntityKey id;
	
	private Boolean employed;
	
	public Worker () {
		model = (ApplicationsModel) SimulationEngine.getInstance().getManager(ApplicationsModel.class.getCanonicalName());
		id = new PanelEntityKey();
		id.setId((long) idCounter++); 
		employed = false;
		workerApplicationList = new ArrayList<Application>();
	}
	
	public void onEvent(Enum<?> type) {
		switch ((Processes) type) {
		
		case Apply:
			apply();
			break;
		
		case ResetEmploymentStatus:
			if(employed) resetEmploymentStatus();
			break;
		}
	}
	
	public void apply () {	
		// Worker has n=applicationsPerPeriod shots.
		// If selected vacancy was already applied to, shot is not wasted
		// Worker might end up shooting less than n shots if there are not enough open vacancies
		
		// shuffle vacancy list
		Collections.shuffle( model.getOpenVacancyList() );
		int applicationsSent=0;
		int firstVacancySampled = SimulationEngine.getRnd().nextInt( model.getOpenVacancyList().size() );
		int j = firstVacancySampled;
		while ( applicationsSent < model.getApplicationsPerPeriod() ) {
			Vacancy vacancy = model.getOpenVacancyList().get(j);
//			boolean hasAlreadyApplied = false;		//Can prevent applying twice for the same job
			for (Application application : workerApplicationList) {
				if (application.getVacancyId() == vacancy.getId()) {
//					hasAlreadyApplied = true;
					break;
				}
			}
			
//			if (! hasAlreadyApplied) {		//Can prevent applying twice for the same job if not commented out
				Application application = new Application(this, vacancy);
				workerApplicationList.add(application); // the application itself notifies its existence to the model
				applicationsSent ++;
//			}
			j ++;
			if (j == model.getOpenVacancyList().size()) j = 0;
			if (j == firstVacancySampled) break;
		}
	}
	
	public void resetEmploymentStatus() {
		setEmployed(false);
		workerApplicationList.clear();
	}
	
	public void setEmployed(Boolean emp) {
		employed = emp;
	}

	public Long getAgentId() {
		return id.getId();
	}

	public Boolean getEmployed() {
		return employed;
	}
	
	public int getEmployedAsInt() {
		return (employed ? 1 : 0) * 100;
	}

	public List<Application> getWorkerApplicationList() {
		return workerApplicationList;
	}

}
