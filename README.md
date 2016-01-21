# applicationsNew
Microsim-applications by Matteo Richiardi and Michele Sonnessa (2014) 
Update to make compatible with JAS-mine by Ross Richardson (2016)

Workers sending out applications to vacancies: an example of many-to-many relationships (many vacancies applied for by any worker, many applications received by any vacancy) and how to persist them in the database, with linked tables that facilitate subsequent analysis.

Features:

    persists both periodic information (the characteristics of the population in each period, one record per worker per period) and one-off summary data (the complete history of each application / vacancy, one record per application / vacancy saved when simulation ends)

The main() method is in the ApplicationsStart.java class in the it.unito.experiment package.  
Run this class as a Java Application, and the JAS-mine GUI will launch.
To build the simulation, click on the 'Build simulation model' button (the second from the left), which should bring up some empty charts. 
To run the simulation, click on the 'Start simulation' button, which is the next button on the right. 

For more information on this simulation, see https://sites.google.com/site/jasminesimulation/demo/applications
