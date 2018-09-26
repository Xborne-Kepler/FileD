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

public class GGm extends LimitedFiringSource {
	public TypedIOPort Es1;
	public TypedIOPort lamda;
	public TypedIOPort Mu;
	public TypedIOPort Et;
	public TypedIOPort NumberOfJob;
	public TypedIOPort NumberOfServer;
	public TypedIOPort Traffic;
	public TypedIOPort MeanNumberJob_inTheService;
	public TypedIOPort MeanNumberJob_inTheSystem;
	public TypedIOPort Variance_NofJob_inTheS;    
	public TypedIOPort MeanResponseTime;
	public TypedIOPort NumberOfJobInTheQ;
	
	


	public GGm(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit=1;

		Es1 = new TypedIOPort(this, "MeanServiceTimePerJob", true, false);
		lamda = new TypedIOPort(this, "lambda", true, false);
		Et = new TypedIOPort(this, "MeanInterrivalTime", true, false);
		Mu = new TypedIOPort(this, "ServiceRate", true, false);
		NumberOfServer= new TypedIOPort(this, "NumberOfServer", true, false);
		NumberOfJob = new TypedIOPort(this, "NumberOfJob", true, false);
		
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		MeanNumberJob_inTheSystem= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		MeanNumberJob_inTheService= new TypedIOPort(this, "MeanNumberJob_inTheService", false, true);
		Variance_NofJob_inTheS= new TypedIOPort(this, "Variance_NofJob_inTheS", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		
		Es1.setTypeEquals(BaseType.DOUBLE);
		Et.setTypeEquals(BaseType.DOUBLE);
		Mu.setTypeEquals(BaseType.DOUBLE);
		NumberOfJob.setTypeEquals(BaseType.INT);
		NumberOfServer.setTypeEquals(BaseType.INT);
		lamda.setTypeEquals(BaseType.DOUBLE);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheSystem.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheService.setTypeEquals(BaseType.DOUBLE);
		Variance_NofJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		
				}
	
	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		double lambda=Double.valueOf((lamda.get(0)).toString());
		double mu=Double.valueOf((Mu.get(0)).toString());
		double MInterTime=Double.valueOf((Et.get(0)).toString());
		double E1=Double.valueOf((Es1.get(0)).toString());
		int n = Integer.valueOf((NumberOfJob.get(0)).toString());
		int m = Integer.valueOf((NumberOfServer.get(0)).toString());
		
		double P = lambda / (m*mu);
		if(P>=1) JOptionPane.showMessageDialog(null, "The system is not stable");
		double MeanNJobInTheService = m*P;
		//double P_ofNJob=(math.exp(-P)/fact(n))*math.pow(P, n);
		double MeanNJS = P;
		double VarNJS= P;
		double MeanRT=E1;
		
		
		Traffic.send(0, new DoubleToken(P));
		Variance_NofJob_inTheS.send(0, new DoubleToken(VarNJS));
		//MeanNumberJob_inTheS.send(0, new DoubleToken(MeanNJS));
		NumberOfJobInTheQ.send(0, new DoubleToken(0));
		MeanResponseTime.send(0, new DoubleToken(MeanRT));
	}

}
