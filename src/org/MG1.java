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

public class MG1 extends LimitedFiringSource {
	
	
	
	public TypedIOPort Es1;
	public TypedIOPort Es2;
	public TypedIOPort Es3;
	public TypedIOPort lamda;
	public TypedIOPort Coefficient;
	public TypedIOPort NumberOfJob;
	public TypedIOPort Traffic;
	public TypedIOPort P_ofZeroJob;
	public TypedIOPort MeanNumberJob_inTheS;
	public TypedIOPort Variance_NofJob_inTheS;    
	public TypedIOPort MeanNumberJob_inTheQ;
	public TypedIOPort Variance_NofJob_inTheQ;
	public TypedIOPort MeanResponseTime;
	public TypedIOPort Variance_ResponseTime;
	public TypedIOPort MeanWaitingTime;
	public TypedIOPort Variance_WaitingTime;
	public TypedIOPort Idle;
	public TypedIOPort MeanNumberJobServedInOneBusyPeriod;
	public TypedIOPort VarNumberJobServedInOneBusyPeriod;
	public TypedIOPort MeanBusyPeriodDuration;
	public TypedIOPort VarianceOfBusyPeriod;
	
	


	public MG1(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit=1;

		Es1 = new TypedIOPort(this, "Es1", true, false);
		Es2 = new TypedIOPort(this, "Es2", true, false);
		Es3 = new TypedIOPort(this, "Es3", true, false);
		lamda = new TypedIOPort(this, "lambda", true, false);
		Coefficient = new TypedIOPort(this, "Coefficient", true, false);
		NumberOfJob = new TypedIOPort(this, "NumberOfJob", true, false);
		
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		P_ofZeroJob= new TypedIOPort(this, "Prob_ofZeroJob", false, true);
		MeanNumberJob_inTheS= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		Variance_NofJob_inTheS= new TypedIOPort(this, "Variance_NofJob_inTheS", false, true);
		MeanNumberJob_inTheQ= new TypedIOPort(this, "MeanNumberJob_inTheQueue", false, true);
		Variance_NofJob_inTheQ= new TypedIOPort(this, "Variance_NofJob_inTheQ", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		Variance_ResponseTime= new TypedIOPort(this, "Variance_ResponseTime", false, true);
		MeanWaitingTime= new TypedIOPort(this, "MeanWaitingTime", false, true);
		Variance_WaitingTime= new TypedIOPort(this, "Variance_WaitingTime", false, true);
		Idle= new TypedIOPort(this, "Idle", false, true);
		MeanNumberJobServedInOneBusyPeriod= new TypedIOPort(this, "MeanNumberJobServedInOneBusyPeriod", false, true);
		VarNumberJobServedInOneBusyPeriod= new TypedIOPort(this, "VarNumberJobServedInOneBusyPeriod", false, true);
		MeanBusyPeriodDuration= new TypedIOPort(this, "MeanBusyPeriodDuration", false, true);
		VarianceOfBusyPeriod= new TypedIOPort(this, "VarianceOfBusyPeriod", false, true);
		
		
		Es1.setTypeEquals(BaseType.DOUBLE);
		Es2.setTypeEquals(BaseType.DOUBLE);
		Es3.setTypeEquals(BaseType.DOUBLE);
		NumberOfJob.setTypeEquals(BaseType.INT);
		//output.setTypeEquals(BaseType.DOUBLE);
		lamda.setTypeEquals(BaseType.DOUBLE);
		Coefficient.setTypeEquals(BaseType.DOUBLE);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		P_ofZeroJob.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		Variance_NofJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheQ.setTypeEquals(BaseType.DOUBLE);
		Variance_NofJob_inTheQ.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		Variance_ResponseTime.setTypeEquals(BaseType.DOUBLE);
		MeanWaitingTime.setTypeEquals(BaseType.DOUBLE);

		Variance_WaitingTime.setTypeEquals(BaseType.DOUBLE);
		Idle.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJobServedInOneBusyPeriod.setTypeEquals(BaseType.DOUBLE);
		VarNumberJobServedInOneBusyPeriod.setTypeEquals(BaseType.DOUBLE);
		MeanBusyPeriodDuration.setTypeEquals(BaseType.DOUBLE);
		VarianceOfBusyPeriod.setTypeEquals(BaseType.DOUBLE);
		}

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		double lambda=Double.valueOf((lamda.get(0)).toString());
		double E1=Double.valueOf((Es1.get(0)).toString());
		double E2=Double.valueOf((Es2.get(0)).toString());
		double E3=Double.valueOf((Es3.get(0)).toString());
		double Coef=Double.valueOf((Coefficient.get(0)).toString());
		double VarS = E2 - math.pow(E1, 2);
		int n = Integer.valueOf((NumberOfJob.get(0)).toString());
		double P = lambda * E1;
		if(P>1) JOptionPane.showMessageDialog(null, "The system is not stable");
		
		double Prob_ofZeroJob=1-P;
		double MeanNJS = P + ((math.pow(P, 2) * (1 + math.pow(Coef, 2)) / 2 * (1-P)));
		double VarNJS= MeanNJS + (math.pow(lambda, 2)  * VarS) + (math.pow(lambda, 3) * E3 / (3*(1-P))) + ( math.pow(lambda, 4)* math.pow(E2, 2) / (4* math.pow(1-P, 2)));
		double MeanNJQ = (P*P) * ((1+math.pow(Coef, 2))/(2*(1-P)));
		double VarNJQ = VarNJS - P + (P*P);
		double MeanRT=MeanNJS/lambda;
		double VarRT=VarS+(lambda * E3/(3*(1-P)))+(lambda*lambda*E2*E2 / 4*(math.pow(1-P, 2)));
		double MeanWT=P*E1*(1+math.pow(Coef, 2))/(2*(1-P));
		double VarWT=VarRT - VarS;
		double idle = 1 - math.exp(-lambda);
		double MeanNJServedBP=1/(1-P);
		double VarNJServedBP = P*(1-P) + lambda*lambda * E2/math.pow(1-P, 3);
		double MeanBusyPDuration=E1/(1-P);
		double VarBusyPDuration = E2/math.pow(1-P, 3) - math.pow(E1, 2)/math.pow(1-P, 2);
		
		
		Traffic.send(0, new DoubleToken(P));
		P_ofZeroJob.send(0, new DoubleToken(Prob_ofZeroJob));
		Variance_NofJob_inTheS.send(0, new DoubleToken(VarNJS));
		MeanNumberJob_inTheS.send(0, new DoubleToken(MeanNJS));
		MeanNumberJob_inTheQ.send(0, new DoubleToken(MeanNJQ));
		Variance_NofJob_inTheQ.send(0, new DoubleToken(VarNJQ));
		MeanResponseTime.send(0, new DoubleToken(MeanRT));
		Variance_ResponseTime.send(0, new DoubleToken(VarRT));
		MeanWaitingTime.send(0, new DoubleToken(MeanWT));
		Variance_WaitingTime.send(0, new DoubleToken(VarWT));
		Idle.send(0, new DoubleToken(idle));
		MeanNumberJobServedInOneBusyPeriod.send(0, new DoubleToken(MeanNJServedBP));
		VarNumberJobServedInOneBusyPeriod.send(0, new DoubleToken(VarNJServedBP));
		MeanBusyPeriodDuration.send(0, new DoubleToken(MeanBusyPDuration));
		VarianceOfBusyPeriod.send(0, new DoubleToken(VarBusyPDuration));
		
	}
}
