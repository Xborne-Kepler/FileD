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

public class GM1 extends LimitedFiringSource {
	public TypedIOPort mu;
	public TypedIOPort Si;
	public TypedIOPort MeanArrivalTime;
	public TypedIOPort Traffic;
	public TypedIOPort P_ofZeroJob;
	public TypedIOPort P_ofNJob_inTheS;
	public TypedIOPort MeanNumberJob_inTheS;
	public TypedIOPort Variance;
	public TypedIOPort MeanResponseTime;
	public TypedIOPort Var_ResponseTime;
	public TypedIOPort QPercentile_ResponseTime;
	public TypedIOPort MeanWaitingTime;
	public TypedIOPort Var_WaitingTime;
	public TypedIOPort QPercentile_WaitingTime;
	public TypedIOPort P_FindingK_orMore_JobinTheS;
	public TypedIOPort NumberOfJob;
	
	
	
	public GM1(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		// TODO Auto-generated constructor stub
		
		_firingCountLimit=1;

		mu = new TypedIOPort(this, "Mu", true, false);
		Si = new TypedIOPort(this, "Si", true, false);
		MeanArrivalTime = new TypedIOPort(this, "MeanInterArrivalTime", true, false);
		
		MeanArrivalTime.setTypeEquals(BaseType.DOUBLE);
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		P_ofZeroJob= new TypedIOPort(this, "Prob_ofZeroJob", false, true);
		P_ofNJob_inTheS= new TypedIOPort(this, "Prob_ofNJob_inTheSystem", false, true);
		MeanNumberJob_inTheS= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		Variance= new TypedIOPort(this, "Variance", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		Var_ResponseTime= new TypedIOPort(this, "Var_ResponseTime", false, true);
		QPercentile_ResponseTime= new TypedIOPort(this, "90Percentile_ResponseTime", false, true);
		MeanWaitingTime= new TypedIOPort(this, "MeanWaitingTime", false, true);
		Var_WaitingTime= new TypedIOPort(this, "Var_WaitingTime", false, true);
		QPercentile_WaitingTime= new TypedIOPort(this, "90Percentile_WaitingTime", false, true);
		P_FindingK_orMore_JobinTheS= new TypedIOPort(this, "P_FindingK_orMore_JobinTheSysteme", false, true);
		
		NumberOfJob = new TypedIOPort(this, "NumberOfJob", true, false);
		mu.setTypeEquals(BaseType.DOUBLE);
		MeanArrivalTime.setTypeEquals(BaseType.DOUBLE);
		Si.setTypeEquals(BaseType.DOUBLE);
		NumberOfJob.setTypeEquals(BaseType.INT);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		P_ofZeroJob.setTypeEquals(BaseType.DOUBLE);
		P_ofNJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		Variance.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		Var_ResponseTime.setTypeEquals(BaseType.DOUBLE);
		QPercentile_ResponseTime.setTypeEquals(BaseType.DOUBLE);
		MeanWaitingTime.setTypeEquals(BaseType.DOUBLE);
		Var_WaitingTime.setTypeEquals(BaseType.DOUBLE);
		QPercentile_WaitingTime.setTypeEquals(BaseType.DOUBLE);
		P_FindingK_orMore_JobinTheS.setTypeEquals(BaseType.DOUBLE);
		
	}
		@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		
		double si=Double.valueOf((Si.get(0)).toString());
		double Mu=Double.valueOf((mu.get(0)).toString());
		int k = Integer.valueOf((MeanArrivalTime.get(0)).toString());
		int n = Integer.valueOf((NumberOfJob.get(0)).toString());
		double P = 1/(k*Mu); 
		if(P>=1) JOptionPane.showMessageDialog(null, "The system is not stable");
		double P0= 1-P;
		double Pi = (1-si)* (Math.pow(si, n-1)) * P;
		double Es= P/(1-si);
		double VARs = P*(1+si-P)/math.pow(1-si, 2);
		double MResponseTime=1/(Mu*(1-si));
		double VARresponseTime=Math.pow(MResponseTime,2);
		double NPercentileRT= 2.3 * MResponseTime;
		double MeanWT=si/(Mu*(1-si));
		double VARwt=(2-si)*si/((Mu*Mu)*(Math.pow((1-si),2)));
		
		double NPercentileWT=(MeanWT/si) * Math.log(10*si);
		if(NPercentileWT<0) NPercentileWT=0;
		double Pfinding=(Math.pow(si,n) * (1-P))/(1-si);
		
		Traffic.send(0, new DoubleToken(P));
		P_ofZeroJob.send(0, new DoubleToken(P0));
		P_ofNJob_inTheS.send(0, new DoubleToken(Pi));
		MeanNumberJob_inTheS.send(0, new DoubleToken(Es));
		Variance.send(0, new DoubleToken(VARs));
		MeanResponseTime.send(0, new DoubleToken(MResponseTime));
		Var_ResponseTime.send(0, new DoubleToken(VARresponseTime));
		QPercentile_ResponseTime.send(0, new DoubleToken(NPercentileRT));
		MeanWaitingTime.send(0, new DoubleToken(MeanWT));
		Var_WaitingTime.send(0, new DoubleToken(VARwt));
		QPercentile_WaitingTime.send(0, new DoubleToken(NPercentileWT));
		P_FindingK_orMore_JobinTheS.send(0, new DoubleToken(Pfinding));
		}

}
