package org;

import ptolemy.actor.lib.LimitedFiringSource;
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
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class MMm extends LimitedFiringSource {
	
	
	public TypedIOPort mu;
	public TypedIOPort lamda;
	public TypedIOPort NumberOfServer;
	public TypedIOPort NumberOfJob;
	public TypedIOPort Traffic;
	public TypedIOPort P_ofZeroJob;
	public TypedIOPort P_ofNJob_inTheS;
	public TypedIOPort P_ofQueueing;
	public TypedIOPort MeanNumberJob_inTheS;
	public TypedIOPort Variance;
	public TypedIOPort Utilisation;
	public TypedIOPort MeanNumberJob_inQ;
	public TypedIOPort Variance_inTheQ;
	public TypedIOPort Cumulative_DF_ofResponseTime;
	public TypedIOPort MeanResponseTime;
	public TypedIOPort Var_ResponseTime;
	
	public TypedIOPort Cumulative_DF_ofWaintingTime;
	public TypedIOPort MeanWaitingTime;
	public TypedIOPort Var_WaitingTime;
	public TypedIOPort QPercentile_WaitingTime;
	
	
	public static int fact(int n) {
	    int produit = 1;
	    for (int i=1; i<=n; i++)
	        produit *= i;
	    return produit;
	}
	
	public MMm(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		// TODO Auto-generated constructor stub
		_firingCountLimit=1;

		mu = new TypedIOPort(this, "Mu", true, false);
		lamda = new TypedIOPort(this, "lambda", true, false);
		NumberOfServer = new TypedIOPort(this, "NumberOfServer", true, false);
		NumberOfJob = new TypedIOPort(this, "NumberOfJob", true, false);
		NumberOfServer.setTypeEquals(BaseType.INT);
		NumberOfJob.setTypeEquals(BaseType.INT);
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		P_ofZeroJob= new TypedIOPort(this, "Prob_ofZeroJob", false, true);
		P_ofNJob_inTheS= new TypedIOPort(this, "Prob_ofNJob_inTheSystem", false, true);
		MeanNumberJob_inTheS= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		Variance= new TypedIOPort(this, "Variance", false, true);
		Utilisation= new TypedIOPort(this, "Utilisation", false, true);
		MeanNumberJob_inQ= new TypedIOPort(this, "MeanNumberJob_inQueue", false, true);
		Variance_inTheQ= new TypedIOPort(this, "Variance_inTheQueue", false, true);
		Cumulative_DF_ofResponseTime= new TypedIOPort(this, "Cumulative_DF_ofResponseTime", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		Var_ResponseTime= new TypedIOPort(this, "Var_ResponseTime", false, true);
		
		Cumulative_DF_ofWaintingTime= new TypedIOPort(this, "Cumulative_DF_ofWaintingTime", false, true);
		MeanWaitingTime= new TypedIOPort(this, "MeanWaitingTime", false, true);
		Var_WaitingTime= new TypedIOPort(this, "Var_WaitingTime", false, true);
		QPercentile_WaitingTime= new TypedIOPort(this, "90Percentile_WaitingTime", false, true);
		
		mu.setTypeEquals(BaseType.DOUBLE);
		//output.setTypeEquals(BaseType.DOUBLE);
		lamda.setTypeEquals(BaseType.DOUBLE);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		P_ofZeroJob.setTypeEquals(BaseType.DOUBLE);
		P_ofNJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		Variance.setTypeEquals(BaseType.DOUBLE);
		Utilisation.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inQ.setTypeEquals(BaseType.DOUBLE);
		Variance_inTheQ.setTypeEquals(BaseType.DOUBLE);
		Cumulative_DF_ofResponseTime.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		Var_ResponseTime.setTypeEquals(BaseType.DOUBLE);
		
		Cumulative_DF_ofWaintingTime.setTypeEquals(BaseType.DOUBLE);
		MeanWaitingTime.setTypeEquals(BaseType.DOUBLE);
		Var_WaitingTime.setTypeEquals(BaseType.DOUBLE);
		QPercentile_WaitingTime.setTypeEquals(BaseType.DOUBLE);
	}

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		double lambda=Double.valueOf((lamda.get(0)).toString());
		double Mu=Double.valueOf((mu.get(0)).toString());
		int m = Integer.valueOf((NumberOfServer.get(0)).toString());
		int n = Integer.valueOf((NumberOfJob.get(0)).toString());
	
		double P = lambda/(m*Mu);
		if(P>1)stopFire();
		double sommation_int=0;
		for(int i=1;i<=m-1;i++) sommation_int= sommation_int + ((math.pow(m*P, i))/fact(i));
		double Pinit= 1 + (math.pow(m*P, m)/(fact(m)*(1-P))) + sommation_int;
		double P0=math.pow(Pinit, -1);
		double Pi=0;
		if(n<m) Pi = P0 * (Math.pow(m*P, n))/fact(n);
		else Pi=P0*(Math.pow(P, n)*Math.pow(m, m)) / fact(m);
		double ProbOfQueueing = (Math.pow(m * P, m) / (fact(m) * (1-P))) * P0;
		
		double Es= m*P + (P*ProbOfQueueing/(1-P));
		double VARs = m*P + (P*ProbOfQueueing*((1+P-(P*ProbOfQueueing)/(Math.pow((1-P), 2)))+m));
		
		double Eq= P*ProbOfQueueing/(1-P);
		double r= (1/Mu*(1-P)) * Math.log(10);
		
		double VARq=(P*ProbOfQueueing) * (1+P-(P*ProbOfQueueing))/(Math.pow((1-P), 2));
		double Frt=0;
		if(P==((m-1)/m) && r>0) Frt = 1-Math.exp(-r*Mu)* -1*ProbOfQueueing*Mu*r*Math.exp(-r*Mu);
		else Frt= 1-Math.exp(-r*Mu)* -1*(ProbOfQueueing/(1-m+(m*P))) *Math.exp(-m*Mu*(1-P)*r) * Math.exp(-r*Mu);
		double MResponseTime=(1/Mu)*(1+ (ProbOfQueueing/m*(1-P)));
		double VARresponseTime=(1/(Mu*Mu))*(1 + (ProbOfQueueing*(2-ProbOfQueueing))/(m*m)*(Math.pow((1-P),2)));
		
		double w= (1/(m*Mu*(1-P))) * Math.log(100*ProbOfQueueing/10);
		if (w<0) w=0;
		double Fwt= 1-(ProbOfQueueing* Math.exp(-m*Mu*w*(1-P)));
		double MeanWT=ProbOfQueueing/(m*Mu*(1-P));
		double VARwt=(2-ProbOfQueueing)*ProbOfQueueing/((m*m)*(Mu*Mu)*(Math.pow((1-P),2)));
		double NPercentileWT=(MeanWT/ProbOfQueueing) * Math.log(10*ProbOfQueueing);
		if(NPercentileWT<0) NPercentileWT=0;
		
		Traffic.send(0, new DoubleToken(P));
		P_ofZeroJob.send(0, new DoubleToken(P0));
		P_ofNJob_inTheS.send(0, new DoubleToken(Pi));
		MeanNumberJob_inTheS.send(0, new DoubleToken(Es));
		Variance.send(0, new DoubleToken(VARs));
		Utilisation.send(0, new DoubleToken(P));
		MeanNumberJob_inQ.send(0, new DoubleToken(Eq));
		Variance_inTheQ.send(0, new DoubleToken(VARq));
		Cumulative_DF_ofResponseTime.send(0, new DoubleToken(Frt));
		MeanResponseTime.send(0, new DoubleToken(MResponseTime));
		Var_ResponseTime.send(0, new DoubleToken(VARresponseTime));
		
		Cumulative_DF_ofWaintingTime.send(0, new DoubleToken(Fwt));
		MeanWaitingTime.send(0, new DoubleToken(MeanWT));
		Var_WaitingTime.send(0, new DoubleToken(VARwt));
		QPercentile_WaitingTime.send(0, new DoubleToken(NPercentileWT));
		
		
	}
	

}
