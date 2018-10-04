package org;

import org.python.modules.math;

import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.data.DoubleToken;
import ptolemy.data.StringToken;
import ptolemy.data.expr.Parameter;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class MM1 extends LimitedFiringSource {

	

	public TypedIOPort mu;
	public TypedIOPort lamda;
	public TypedIOPort etat;
	public TypedIOPort Traffic;
	public TypedIOPort P_ofZeroJob;
	public TypedIOPort P_ofNJob_inTheS;
	public TypedIOPort MeanNumberJob_inTheS;
	public TypedIOPort Variance;
	public TypedIOPort P_ofNJob_inTheQ;
	public TypedIOPort MeanNumberJob_inQ;
	public TypedIOPort Variance_inTheQ;
	public TypedIOPort Cumulative_DF_ofResponseTime;
	public TypedIOPort MeanResponseTime;
	public TypedIOPort Var_ResponseTime;
	public TypedIOPort QPercentile_ResponseTime;
	public TypedIOPort Cumulative_DF_ofWaintingTime;
	public TypedIOPort MeanWaitingTime;
	public TypedIOPort Var_WaitingTime;
	public TypedIOPort QPercentile_WaitingTime;
	public TypedIOPort P_FindingK_orMore_JobinTheS;
	public TypedIOPort P_Serving_kJob_ineOneBusyP;
	public TypedIOPort Mean_Numb_JobServedInOneBusyP;
	public TypedIOPort Mean_Busy_Periode_Duration;
	public TypedIOPort Var_ofTheBusyPeriode;
	public TypedIOPort Var_ofNumbJobServedInOneBusyPeriode;
	
	
	
	
	public MM1(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		// TODO Auto-generated constructor stub
		
		_firingCountLimit=1;

		mu = new TypedIOPort(this, "Mu", true, false);
		lamda = new TypedIOPort(this, "lambda", true, false);
		etat = new TypedIOPort(this, "etat", true, false);
		
		etat.setTypeEquals(BaseType.INT);
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		P_ofZeroJob= new TypedIOPort(this, "Prob_ofZeroJob", false, true);
		P_ofNJob_inTheS= new TypedIOPort(this, "Prob_ofNJob_inTheSystem", false, true);
		MeanNumberJob_inTheS= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		Variance= new TypedIOPort(this, "Variance", false, true);
		P_ofNJob_inTheQ= new TypedIOPort(this, "P_ofNJob_inTheQueue", false, true);
		MeanNumberJob_inQ= new TypedIOPort(this, "MeanNumberJob_inQueue", false, true);
		Variance_inTheQ= new TypedIOPort(this, "Variance_inTheQueue", false, true);
		Cumulative_DF_ofResponseTime= new TypedIOPort(this, "Cumulative_DF_ofResponseTime", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		Var_ResponseTime= new TypedIOPort(this, "Var_ResponseTime", false, true);
		QPercentile_ResponseTime= new TypedIOPort(this, "90Percentile_ResponseTime", false, true);
		Cumulative_DF_ofWaintingTime= new TypedIOPort(this, "Cumulative_DF_ofWaintingTime", false, true);
		MeanWaitingTime= new TypedIOPort(this, "MeanWaitingTime", false, true);
		Var_WaitingTime= new TypedIOPort(this, "Var_WaitingTime", false, true);
		QPercentile_WaitingTime= new TypedIOPort(this, "90Percentile_WaitingTime", false, true);
		P_FindingK_orMore_JobinTheS= new TypedIOPort(this, "P_FindingK_orMore_JobinTheSysteme", false, true);
		P_Serving_kJob_ineOneBusyP= new TypedIOPort(this, "P_Serving_kJob_ineOneBusyPeriode", false, true);
		Mean_Numb_JobServedInOneBusyP= new TypedIOPort(this, "Mean_Numb_JobServedInOneBusyPeriode", false, true);
		Mean_Busy_Periode_Duration= new TypedIOPort(this, "Mean_Busy_Periode_Duration", false, true);
		Var_ofTheBusyPeriode= new TypedIOPort(this, "Var_ofTheBusyPeriode", false, true);
		Var_ofNumbJobServedInOneBusyPeriode= new TypedIOPort(this, "Var_ofNumbJobServedInOneBusyPeriode", false, true);
		
		mu.setTypeEquals(BaseType.DOUBLE);
		//output.setTypeEquals(BaseType.DOUBLE);
		lamda.setTypeEquals(BaseType.DOUBLE);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		P_ofZeroJob.setTypeEquals(BaseType.DOUBLE);
		P_ofNJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		Variance.setTypeEquals(BaseType.DOUBLE);
		P_ofNJob_inTheQ.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inQ.setTypeEquals(BaseType.DOUBLE);
		Variance_inTheQ.setTypeEquals(BaseType.DOUBLE);
		Cumulative_DF_ofResponseTime.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		Var_ResponseTime.setTypeEquals(BaseType.DOUBLE);
		QPercentile_ResponseTime.setTypeEquals(BaseType.DOUBLE);
		Cumulative_DF_ofWaintingTime.setTypeEquals(BaseType.DOUBLE);
		MeanWaitingTime.setTypeEquals(BaseType.DOUBLE);
		Var_WaitingTime.setTypeEquals(BaseType.DOUBLE);
		QPercentile_WaitingTime.setTypeEquals(BaseType.DOUBLE);
		P_FindingK_orMore_JobinTheS.setTypeEquals(BaseType.DOUBLE);
		P_Serving_kJob_ineOneBusyP.setTypeEquals(BaseType.DOUBLE);
		Mean_Numb_JobServedInOneBusyP.setTypeEquals(BaseType.DOUBLE);
		Mean_Busy_Periode_Duration.setTypeEquals(BaseType.DOUBLE);
		Var_ofTheBusyPeriode.setTypeEquals(BaseType.DOUBLE);
		Var_ofNumbJobServedInOneBusyPeriode.setTypeEquals(BaseType.DOUBLE);

	}
	
	int combinaison(int n,int p) {
	    int[] t = new int[n+1]; //Tableau de n elements
	    t[0] = 1;
	    for (int i = 1; i <= n; i++) {
	        t[i] = 1;
	        for (int j = i - 1; j >= 1; j--) //On part de le fin pour ne pas ecraser les valeurs.
	            t[j] = t[j] + t[j - 1]; //On fait les calculs necessaires.
	    }
	    return t[p]; //On renvoie la valeur recherchee.
	}

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		
		double lambda=Double.valueOf((lamda.get(0)).toString());
		double Mu=Double.valueOf((mu.get(0)).toString());
		int k = Integer.valueOf((etat.get(0)).toString());
		double P = lambda/Mu; 
		double P0= 1-P;
		double Pi = (1-P)* (Math.pow(P, k));
		double Es= P/(1-P);
		double VARs = P/(Math.pow((1-P), 2));
		double Prob=0;
		if(k==0) Prob = 1 - (Math.pow(P,2));
		else Prob = (1-P)*(Math.pow(P,k+1));
		double Eq= (Math.pow(P,2))/(1-P);
		double r= (1/Mu*(1-P)) * Math.log(10);
		
		double VARq=(P*P) * (1+P-(P*P))/(Math.pow((1-P), 2));
		double Frt=1-Math.exp(-r*Mu*(1-P));
		double MResponseTime=(1/Mu)/(1-P);
		double VARresponseTime=(1/(Mu*Mu))/(Math.pow((1-P),2));
		double qPercentileRT= MResponseTime* Math.log((10));
		double NPercentileRT= 2.3 * MResponseTime;
		double w= (1/Mu*(1-P)) * Math.log(100*P/10);
		
		double Fwt= 1-(P* Math.exp(-Mu*w*(1-P)));
		double MeanWT=P*(1/Mu)/(1-P);
		double VARwt=(2-P)*P/((Mu*Mu)*(Math.pow((1-P),2)));
		double qPercentileWT= (MeanWT/P) * Math.log(100*P/10);
		if(qPercentileWT<0) qPercentileWT=0;
		double NPercentileWT=(MeanWT/P) * Math.log(10*P);
		if(NPercentileWT<0) NPercentileWT=0;
		double Pfinding=(Math.pow(P,k));
		double Pserving= (1/k)* combinaison((2*k)-2, k-1) * (Math.pow(P, k-1)/math.pow(1+P, 2*k-1));
		double Pserving_one_busy_periode = 1/(1-P);
		double Vserving_one_busy_periode = P*(1+P)/(math.pow(1-P, 3));
		double Mean_busy_periode_duration = 1/(Mu*(1-P));
		double Var_busy_periode_periode= (1/(math.pow(Mu, 2)*math.pow(1-P, 3))) - (1/(math.pow(Mu, 2)*math.pow(1-P, 2)));
		Traffic.send(0, new DoubleToken(P));
		P_ofZeroJob.send(0, new DoubleToken(P0));
		P_ofNJob_inTheS.send(0, new DoubleToken(Pi));
		MeanNumberJob_inTheS.send(0, new DoubleToken(Es));
		Variance.send(0, new DoubleToken(VARs));
		P_ofNJob_inTheQ.send(0, new DoubleToken(Prob));
		MeanNumberJob_inQ.send(0, new DoubleToken(Eq));
		Variance_inTheQ.send(0, new DoubleToken(VARq));
		Cumulative_DF_ofResponseTime.send(0, new DoubleToken(Frt));
		MeanResponseTime.send(0, new DoubleToken(MResponseTime));
		Var_ResponseTime.send(0, new DoubleToken(VARresponseTime));
		QPercentile_ResponseTime.send(0, new DoubleToken(NPercentileRT));
		Cumulative_DF_ofWaintingTime.send(0, new DoubleToken(Fwt));
		MeanWaitingTime.send(0, new DoubleToken(MeanWT));
		Var_WaitingTime.send(0, new DoubleToken(VARwt));
		QPercentile_WaitingTime.send(0, new DoubleToken(NPercentileWT));
		P_FindingK_orMore_JobinTheS.send(0, new DoubleToken(Pfinding));
		P_Serving_kJob_ineOneBusyP.send(0, new DoubleToken(Pserving));
		Mean_Numb_JobServedInOneBusyP.send(0, new DoubleToken(Pserving_one_busy_periode));
		Mean_Busy_Periode_Duration.send(0, new DoubleToken(Mean_busy_periode_duration));
		Var_ofTheBusyPeriode.send(0, new DoubleToken(Var_busy_periode_periode));
		Var_ofNumbJobServedInOneBusyPeriode.send(0, new DoubleToken(Vserving_one_busy_periode));
	}

}
