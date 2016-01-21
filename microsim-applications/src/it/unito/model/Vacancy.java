package it.unito.model;

import it.unito.model.enums.ApplicationOutcome;
import microsim.data.db.PanelEntityKey;
import microsim.engine.SimulationEngine;
import microsim.event.EventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Vacancy implements EventListener {

	public static int idCounter = 0;
	
	public enum Processes {
		Select;		
	}
	
	@Id
	private PanelEntityKey id;
	
	@Transient
	private ApplicationsModel model;
	
	@Column(name="date_opened")
	private Long dateOpened;
	@Column(name="date_closed")
	private Long dateClosed;
	
	private Long hiredWorkerId;

	@Transient
	private List<Application> vacancyApplicationList;
	
	public Vacancy () {
		model = (ApplicationsModel) SimulationEngine.getInstance().getManager(ApplicationsModel.class.getCanonicalName());
		id = new PanelEntityKey();
		id.setId((long) idCounter++);
		dateOpened = (long)SimulationEngine.getInstance().getTime();
		hiredWorkerId = null;
		vacancyApplicationList = new ArrayList<Application>();
	}

	public void onEvent(Enum<?> type) {
		switch ((Processes) type) {
		case Select:
			if (
					model.getEngine().getTime() == dateOpened + model.getVacancyDuration() -1 &&
					vacancyApplicationList.size()>0 
					)
				selectApplicant();
			break;
		}
	}
	
	public void addApplication (Application application) {
		vacancyApplicationList.add(application);
	}
	
	public void removeApplication (Application application) {
		vacancyApplicationList.remove(application);
	}
	
	public void selectApplicant() {	
		Collections.shuffle(vacancyApplicationList);

		// hire first applicant still on the market
		for (Application application : vacancyApplicationList) {
			if ( application.getOutcome().equals(ApplicationOutcome.StillOpen) ) {
				if (hiredWorkerId == null) {
					hiredWorkerId = application.getWorkerId();
					application.notifySuccessful();
				}
				else application.notifyUnsuccessful();
			}
		}

		dateClosed = (long)SimulationEngine.getInstance().getTime();
		model.getOpenVacancyList().remove(this);
	}

	public long getId() {
		return id.getId();
	}
	
	public boolean getOpen() {
		return (dateClosed == null);
	}
	
	// used by the Collector through reflection
	public int getVacancyApplicationListSize() {
		return vacancyApplicationList.size();
	}

}
