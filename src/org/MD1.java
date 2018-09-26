package org;

import javax.swing.JOptionPane;

import org.python.modules.math;

import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.data.DoubleToken;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class MD1 extends LimitedFiringSource {

	public TypedIOPort Es1;
	public TypedIOPort lamda;
	public TypedIOPort NumberOfJob;
	public TypedIOPort Traffic;
	public TypedIOPort P_ofNJob;
	public TypedIOPort MeanNumberJob_inTheS;
	public TypedIOPort Variance_NofJob_inTheS;    
	public TypedIOPort MeanNumberJob_inTheQ;
	public TypedIOPort Variance_NofJob_inTheQ;
	public TypedIOPort MeanResponseTime;
	public TypedIOPort Variance_ResponseTime;
	public TypedIOPort MeanWaitingTime;
	public TypedIOPort Variance_WaitingTime;
	public TypedIOPort ProbNJobServedInOneBusyPeriod;
	


	public MD1(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit=1;

		Es1 = new TypedIOPort(this, "Es1", true, false);
		lamda = new TypedIOPort(this, "lambda", true, false);
		NumberOfJob = new TypedIOPort(this, "NumberOfJob", true, false);
		
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		P_ofNJob= new TypedIOPort(this, "Prob_ofNJob", false, true);
		MeanNumberJob_inTheS= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		Variance_NofJob_inTheS= new TypedIOPort(this, "Variance_NofJob_inTheS", false, true);
		MeanNumberJob_inTheQ= new TypedIOPort(this, "MeanNumberJob_inTheQueue", false, true);
		Variance_NofJob_inTheQ= new TypedIOPort(this, "Variance_NofJob_inTheQ", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		Variance_ResponseTime= new TypedIOPort(this, "Variance_ResponseTime", false, true);
		MeanWaitingTime= new TypedIOPort(this, "MeanWaitingTime", false, true);
		Variance_WaitingTime= new TypedIOPort(this, "Variance_WaitingTime", false, true);
		ProbNJobServedInOneBusyPeriod= new TypedIOPort(this, "ProbNJobServedInOneBusyPeriod", false, true);
			
		
		Es1.setTypeEquals(BaseType.DOUBLE);
		NumberOfJob.setTypeEquals(BaseType.INT);
		//output.setTypeEquals(BaseType.DOUBLE);
		lamda.setTypeEquals(BaseType.DOUBLE);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		P_ofNJob.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		Variance_NofJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheQ.setTypeEquals(BaseType.DOUBLE);
		Variance_NofJob_inTheQ.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		Variance_ResponseTime.setTypeEquals(BaseType.DOUBLE);
		MeanWaitingTime.setTypeEquals(BaseType.DOUBLE);

		Variance_WaitingTime.setTypeEquals(BaseType.DOUBLE);
		ProbNJobServedInOneBusyPeriod.setTypeEquals(BaseType.DOUBLE);
		}
	
	public static int fact(int n) {
	    int produit = 1;
	    for (int i=1; i<=n; i++)
	        produit *= i;
	    return produit;
	}
	

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		double lambda=Double.valueOf((lamda.get(0)).toString());
		double E1=Double.valueOf((Es1.get(0)).toString());
		
		int n = Integer.valueOf((NumberOfJob.get(0)).toString());
		double P = lambda * E1;
		if(P>1) JOptionPane.showMessageDialog(null, "The system is not stable");
		
		double Prob_ofNJob=0;
		if(n==0) Prob_ofNJob=1-P;
		else if(n==1) Prob_ofNJob=(1-P)*(math.exp(P)-1);
		else {
			double sommation=0;
			for(int j = 0; j<=n;j++) sommation = sommation + (((math.exp(j*P) * math.pow(-1, n-j) * math.pow(j*P, n-j-1)*((j*P)+n-j)))/fact(n-j));
			Prob_ofNJob=(1-P) * sommation;
		}
		
		double MeanNJS = P + ((math.pow(P, 2) * (1 + math.pow(P, 2)) / (2 * (1-P))));
		double VarNJS= MeanNJS + (math.pow(P, 3)/(3*(1-P))) + (math.pow(P, 4)/ (4* math.pow(1-P, 2)));
		double MeanRT=E1 + (P*E1)/(2*(1-P));
		double VarRT=P*(math.pow(E1, 2)/(3*(1-P))) + (P*P*E1*E1) / (4*(math.pow(1-P, 2)));
		
		double MeanNJQ = (P*P)/(2*(1-P));
		double VarNJQ =  math.pow(P, 2)+(math.pow(P, 2)/(2*(1-P))) + (math.pow(P, 3)/(3*(1-P))) + (math.pow(P, 4)/(4*math.pow(1-P, 2)));
		double MeanWT=P*E1/(2*(1-P));
		double VarWT=VarRT;
		double MeanNJServedBP=1/(1-P);
		double probServingNJobBusyP = (math.pow(n*P, n-1) / fact(n)) * math.exp(-n*P);
		
		Traffic.send(0, new DoubleToken(P));
		P_ofNJob.send(0, new DoubleToken(Prob_ofNJob));
		Variance_NofJob_inTheS.send(0, new DoubleToken(VarNJS));
		MeanNumberJob_inTheS.send(0, new DoubleToken(MeanNJS));
		MeanNumberJob_inTheQ.send(0, new DoubleToken(MeanNJQ));
		Variance_NofJob_inTheQ.send(0, new DoubleToken(VarNJQ));
		MeanResponseTime.send(0, new DoubleToken(MeanRT));
		Variance_ResponseTime.send(0, new DoubleToken(VarRT));
		MeanWaitingTime.send(0, new DoubleToken(MeanWT));
		Variance_WaitingTime.send(0, new DoubleToken(VarWT));
		ProbNJobServedInOneBusyPeriod.send(0, new DoubleToken(probServingNJobBusyP));
		}

}
