package it.unito.model;

import it.unito.model.enums.ApplicationOutcome;
import microsim.data.db.PanelEntityKey;
import microsim.engine.SimulationEngine;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Application {
	@Id
	private PanelEntityKey id;
	
	@Column(name="vacancy_id")
	private Long vacancyId;
	@Transient
	private Vacancy vacancy;
	
	@Column(name="worker_id")
	private Long workerId;
	@Transient
	private Worker worker;	
	
	@Enumerated(EnumType.STRING)
	private ApplicationOutcome outcome;
	
	@Transient
	private ApplicationsModel model;
	
	private Long dateOpened;
	
	private Long dateClosed;
	
	public Application() {
		super();
	}

	// applications are send by workers and notified to vacancies
	public Application(Worker worker, Vacancy vacancy) {
		super();
		setWorker(worker);
		setVacancy(vacancy);
		this.vacancy = vacancy;
		dateOpened = (long)SimulationEngine.getInstance().getTime();
		outcome = ApplicationOutcome.StillOpen;
		vacancy.addApplication(this);
		model = (ApplicationsModel) SimulationEngine.getInstance().getManager(ApplicationsModel.class.getCanonicalName());
		model.getApplicationList().add(this);
	}
	
	public void notifySuccessful() {
		outcome = ApplicationOutcome.Successful;
		dateClosed = (long)SimulationEngine.getInstance().getTime();
		worker.setEmployed(true);
		
		// notify all other vacancies involved that the worker is not on the market anymore
		for (Application application : worker.getWorkerApplicationList()) 
			if (! application.equals(this) && application.getOutcome().equals(ApplicationOutcome.StillOpen)) {
				application.setOutcome(ApplicationOutcome.FoundOtherJob);
				application.setDateClosed( (long)SimulationEngine.getInstance().getTime() );
			}
	}
	
	public void notifyUnsuccessful() {
		outcome = ApplicationOutcome.Unsuccesful;
		dateClosed = (long)SimulationEngine.getInstance().getTime();
	}
	
	public long getDateOpened() {
		return dateOpened;
	}

	public void setDateOpened(long openingDate) {
		this.dateOpened = openingDate;
	}

	public long getDateClosed() {
		return dateClosed;
	}

	public void setDateClosed(long closingDate) {
		this.dateClosed = closingDate;
	}
	
	public boolean getOpen() {
		return (dateClosed == null);
	}

	public ApplicationOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(ApplicationOutcome outcome) {
		this.outcome = outcome;
	}

	public Vacancy getVacancy() {
		return vacancy;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
		this.workerId = worker.getAgentId();
	}

	public void setVacancy(Vacancy vacancy) {
		this.vacancy = vacancy;
		this.vacancyId = vacancy.getId();
	}

	public Long getVacancyId() {
		return vacancyId;
	}

	public Long getWorkerId() {
		return workerId;
	}
	
}
