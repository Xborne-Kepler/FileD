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

public class MMmB extends LimitedFiringSource {
	
	
	public TypedIOPort mu;
	public TypedIOPort lamda;
	public TypedIOPort NumberOfServer;
	public TypedIOPort NumberOfJob;
	public TypedIOPort NumberOfBuffer;
	public TypedIOPort Traffic;
	public TypedIOPort P_ofZeroJob;
	public TypedIOPort P_ofNJob_inTheS;
	public TypedIOPort P_ofQueueing;
	public TypedIOPort MeanNumberJob_inTheS;

	public TypedIOPort Utilisation;
	public TypedIOPort MeanNumberJob_inQ;
	public TypedIOPort EffectiveArrivalRate_inTheS;

	public TypedIOPort MeanResponseTime;
	public TypedIOPort ProbOfFullS;
	
	public TypedIOPort LossRate;
	public TypedIOPort MeanWaitingTime;

	
	public static int fact(int n) {
	    int produit = 1;
	    for (int i=1; i<=n; i++)
	        produit *= i;
	    return produit;
	}
	

	public MMmB(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit=1;

		mu = new TypedIOPort(this, "Mu", true, false);
		lamda = new TypedIOPort(this, "lambda", true, false);
		NumberOfServer = new TypedIOPort(this, "NumberOfServer", true, false);
		NumberOfJob = new TypedIOPort(this, "NumberOfJob", true, false);
		NumberOfBuffer = new TypedIOPort(this, "NumberOfBuffer", true, false);
		NumberOfServer.setTypeEquals(BaseType.INT);
		NumberOfJob.setTypeEquals(BaseType.INT);
		Traffic = new TypedIOPort(this, "Traffic_intencity", false, true);
		P_ofZeroJob= new TypedIOPort(this, "Prob_ofZeroJob", false, true);
		P_ofNJob_inTheS= new TypedIOPort(this, "Prob_ofNJob_inTheSystem", false, true);
		MeanNumberJob_inTheS= new TypedIOPort(this, "MeanNumberJob_inTheSystem", false, true);
		Utilisation= new TypedIOPort(this, "Utilisation", false, true);
		MeanNumberJob_inQ= new TypedIOPort(this, "MeanNumberJob_inQueue", false, true);
		MeanResponseTime= new TypedIOPort(this, "MeanResponseTime", false, true);
		MeanWaitingTime= new TypedIOPort(this, "MeanWaitingTime", false, true);
		EffectiveArrivalRate_inTheS = new TypedIOPort(this, "EffectiveArrivalRate_inTheS", false, true);
		LossRate= new TypedIOPort(this, "LossRate", false, true);
		ProbOfFullS= new TypedIOPort(this, "ProbOfFullS", false, true);
		
		
		ProbOfFullS.setTypeEquals(BaseType.DOUBLE);
		mu.setTypeEquals(BaseType.DOUBLE);
		//output.setTypeEquals(BaseType.DOUBLE);
		lamda.setTypeEquals(BaseType.DOUBLE);
		NumberOfBuffer.setTypeEquals(BaseType.INT);
		Traffic.setTypeEquals(BaseType.DOUBLE);
		P_ofZeroJob.setTypeEquals(BaseType.DOUBLE);
		P_ofNJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inTheS.setTypeEquals(BaseType.DOUBLE);
		Utilisation.setTypeEquals(BaseType.DOUBLE);
		MeanNumberJob_inQ.setTypeEquals(BaseType.DOUBLE);
		MeanResponseTime.setTypeEquals(BaseType.DOUBLE);
		MeanWaitingTime.setTypeEquals(BaseType.DOUBLE);
		EffectiveArrivalRate_inTheS.setTypeEquals(BaseType.DOUBLE);
		LossRate.setTypeEquals(BaseType.DOUBLE);
	}

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		double lambda=Double.valueOf((lamda.get(0)).toString());
		double Mu=Double.valueOf((mu.get(0)).toString());
		int m = Integer.valueOf((NumberOfServer.get(0)).toString());
		int n = Integer.valueOf((NumberOfJob.get(0)).toString());
		int B=Integer.valueOf((NumberOfBuffer.get(0)).toString());
		if(B<m) JOptionPane.showMessageDialog(null, "Nombre de buffer doit être supérieure ou égale au nombre de serveurs");
		double P = lambda/(m*Mu);
		double sommation_int=0;
		for(int i=1;i<=m-1;i++) sommation_int= sommation_int + ((math.pow(m*P, i))/fact(i));
		double Pinit= 1 + ((math.pow(m*P, m)*(1-math.pow(P, B-m+1)))/(fact(m)*(1-P))) + sommation_int;
		double P0=0;
		
		if(m==1) {
			if(P==1) P0=1/B+1;
			else P0= (1-P)/(1-math.pow(P, B+1));
		}
		else P0=math.pow(Pinit, -1);
		double[] Pi=new double[B+1];
		if(n<m && n>=0) Pi[n] = P0 * (Math.pow(m*P, n))/fact(n);
		else if(n>=m && n<=B) Pi[n] =P0*(Math.pow(P, n)*Math.pow(m, m)) / fact(m);
		
		double Es=0;
		double Eq=0;
		if(m==1) {
			Es=(P/1-P)-(((B+1)*math.pow(P, B+1))/(1-math.pow(P, B+1)));
			Eq=(P/1-P)-((P*(1+B*math.pow(P, B)))/(1-math.pow(P, B+1)));
		} 
		else {

			for(int i=1;i<=B;i++) Es=Es+(i*Pi[i]);
			for(int i=m+1;i<=B;i++) Eq=Eq+((i-m)*Pi[i]);
		
		}
		double effective = lambda*(1-Pi[B]);
		double Average_Utilisation= P*(1-Pi[B]);
		double MResponseTime=Es/(lambda*(1-Pi[B]));
		double MeanWT=Eq/(lambda*(1-Pi[B]));
		double lossRate = lambda*Pi[B];
		
		double calc=0;
		if(B==m) {
			for(int i=0;i<=m;i++) calc=calc+(math.pow(m*P, i)/fact(i));
				
			Pi[m]=(math.pow(m*P, m)/fact(m))/(calc);
		}
		
		Traffic.send(0, new DoubleToken(P));
		P_ofZeroJob.send(0, new DoubleToken(P0));
		P_ofNJob_inTheS.send(0, new DoubleToken(Pi[n]));
		MeanNumberJob_inTheS.send(0, new DoubleToken(Es));
		MeanNumberJob_inQ.send(0, new DoubleToken(Eq));
		EffectiveArrivalRate_inTheS.send(0, new DoubleToken(effective));
		Utilisation.send(0, new DoubleToken(Average_Utilisation));
		
		MeanResponseTime.send(0, new DoubleToken(MResponseTime));
		MeanWaitingTime.send(0, new DoubleToken(MeanWT));
		LossRate.send(0, new DoubleToken(lossRate));
		ProbOfFullS.send(0, new DoubleToken(Pi[m]));
		
	}
	

}
