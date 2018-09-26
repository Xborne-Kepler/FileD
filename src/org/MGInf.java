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

public class MGInf extends LimitedFiringSource {
	public TypedIOPort Es1;
	public TypedIOPort lamda;
	public TypedIOPort NumberOfJob;
	public TypedIOPort Traffic;
	public TypedIOPort ProbOfNJob;
	public TypedIOPort ProbOfZeroJob;
	public TypedIOPort MeanNumberJob_inTheS;
	public TypedIOPort Variance_NofJob_inTheS;    
	public TypedIOPort MeanResponseTime;
	public TypedIOPort NumberOfJobInTheQ;
	
	


	public MGInf(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit=1;

		Es1 = new TypedIOPort(this, "Es1", true, false);
		lamda = new TypedIOPort(this, "lambda", true, false);
		NumberOfJob = new TypedIOPort(this, "NumberOfJob", true, false);
		
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		ProbOfNJob = new TypedIOPort(this, "Prob_ofNJob_inTheSystem", false, true);
		ProbOfZeroJob =  new TypedIOPort(this, "Prob_ofZeroJob_inTheSystem", false, true);
		MeanNumberJob_inTheS= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		Variance_NofJob_inTheS= new TypedIOPort(this, "Variance_NofJob_inTheS", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		
		Es1.setTypeEquals(BaseType.DOUBLE);
		NumberOfJob.setTypeEquals(BaseType.INT);
		lamda.setTypeEquals(BaseType.DOUBLE);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		ProbOfNJob.setTypeEquals(BaseType.DOUBLE);
		ProbOfZeroJob.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		Variance_NofJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		
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
		double P_ZeroJob = math.exp(-P);
		double P_ofNJob=(math.exp(-P)/fact(n))*math.pow(P, n);
		double MeanNJS = P;
		double VarNJS= P;
		double MeanRT=E1;
		
		
		Traffic.send(0, new DoubleToken(P));
		ProbOfZeroJob.send(0, new DoubleToken(P_ZeroJob));
		ProbOfNJob.send(0, new DoubleToken(P_ofNJob));
		Variance_NofJob_inTheS.send(0, new DoubleToken(VarNJS));
		MeanNumberJob_inTheS.send(0, new DoubleToken(MeanNJS));
		NumberOfJobInTheQ.send(0, new DoubleToken(0));
		MeanResponseTime.send(0, new DoubleToken(MeanRT));
	}

}
