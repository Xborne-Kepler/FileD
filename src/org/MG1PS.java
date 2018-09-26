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

public class MG1PS extends LimitedFiringSource {

	public TypedIOPort Es1;
	public TypedIOPort lamda;
	public TypedIOPort NumberOfJob;
	public TypedIOPort Traffic;
	public TypedIOPort MeanNumberJob_inTheS;
	public TypedIOPort Variance_NofJob_inTheS;    
	public TypedIOPort MeanResponseTime;
	public TypedIOPort ProbNJob;
	
	


	public MG1PS(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit=1;

		Es1 = new TypedIOPort(this, "Es1", true, false);
		lamda = new TypedIOPort(this, "lambda", true, false);
		NumberOfJob = new TypedIOPort(this, "NumberOfJob", true, false);
		
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		ProbNJob = new TypedIOPort(this, "Prob_ofNJob_inTheSystem", false, true);
		MeanNumberJob_inTheS= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		Variance_NofJob_inTheS= new TypedIOPort(this, "Variance_NofJob_inTheS", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		
		Es1.setTypeEquals(BaseType.DOUBLE);
		NumberOfJob.setTypeEquals(BaseType.INT);
		lamda.setTypeEquals(BaseType.DOUBLE);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		ProbNJob.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		Variance_NofJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		
				}

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		double lambda=Double.valueOf((lamda.get(0)).toString());
		double E1=Double.valueOf((Es1.get(0)).toString());
		int n = Integer.valueOf((NumberOfJob.get(0)).toString());
		double P = lambda * E1;
		if(P>=1) JOptionPane.showMessageDialog(null, "The system is not stable");
		double P_ofNJob=(1-P)*math.pow(P, n);
		double MeanNJS = P / (1-P);
		double VarNJS= P / math.pow(1-P, 2);
		double MeanRT=E1/ (1-P);
		
		
		Traffic.send(0, new DoubleToken(P));
		ProbNJob.send(0, new DoubleToken(P_ofNJob));
		Variance_NofJob_inTheS.send(0, new DoubleToken(VarNJS));
		MeanNumberJob_inTheS.send(0, new DoubleToken(MeanNJS));
		MeanResponseTime.send(0, new DoubleToken(MeanRT));
	}

}
