package meth;

import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.data.DoubleToken;
import ptolemy.data.StringToken;
import ptolemy.data.expr.Parameter;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class Bottleneck extends LimitedFiringSource {

	
	public TypedIOPort inputS;
	public TypedIOPort inputV;
	public TypedIOPort Courbe1;
	public TypedIOPort Courbe2;
	public TypedIOPort Courbe3;
	public TypedIOPort Courbe4;
	public TypedIOPort Courbe5;
	public TypedIOPort Courbe6;
	public TypedIOPort outt;
	public Parameter Type;
	public Parameter Nclient;
	public Parameter Z;
	
	public Bottleneck(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		// TODO Auto-generated constructor stub
		
		
		inputS = new TypedIOPort(this, "inputS", true, false);
		inputV = new TypedIOPort(this, "inputV", true, false);
		outt = new TypedIOPort(this, "type", false, true);
		Courbe1 = new TypedIOPort(this, "Courbe1", false, true);
		Courbe2 = new TypedIOPort(this, "Courbe2", false, true);
		Courbe3 = new TypedIOPort(this, "Courbe3", false, true);
		Courbe4 = new TypedIOPort(this, "Courbe4", false, true);
		Courbe5 = new TypedIOPort(this, "Courbe5", false, true);
		Courbe6 = new TypedIOPort(this, "Courbe6", false, true);
		
		
		inputS.setMultiport(true);
		inputS.setAutomaticTypeConversion(true);
		inputV.setMultiport(true);
		inputV.setAutomaticTypeConversion(true);
		
		outt.setTypeEquals(BaseType.DOUBLE);
		Courbe1.setTypeEquals(BaseType.DOUBLE);
		Courbe2.setTypeEquals(BaseType.DOUBLE);
		Courbe3.setTypeEquals(BaseType.DOUBLE);
		Courbe4.setTypeEquals(BaseType.DOUBLE);
		Courbe5.setTypeEquals(BaseType.DOUBLE);
		Courbe6.setTypeEquals(BaseType.DOUBLE);
		
		
		inputS.setTypeEquals(BaseType.STRING);
		inputV.setTypeEquals(BaseType.STRING);
		
		Type = new Parameter(this, "Type of model");
        Type.setTypeEquals(BaseType.INT);
        Z = new Parameter(this, "Think time");
        Z.setTypeEquals(BaseType.DOUBLE);
        Nclient= new Parameter(this, "Nombre de client");
        Nclient.setTypeEquals(BaseType.DOUBLE);
		
	}

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		
		String ValeurS = ((StringToken) inputS.get(0)).toString();
		String ValeurV = ((StringToken) inputV.get(0)).toString();
		ValeurS = ValeurS.substring(1, ValeurS.length() - 1);
		ValeurV = ValeurV.substring(1, ValeurV.length() - 1);
		
		String[] Q = ValeurS.split("/");
		String[] V = ValeurV.split("/");
		int k=Q.length;
		double[] s=new double[k];
		double[] v=new double[k];
		
		for(int i=0;i<Q.length;i++){ s[i]=Double.valueOf(Q[i]);
		v[i]=Double.valueOf(V[i]);
		//out.send(0, new DoubleToken(s[i]));
		}
		double Xmax = 0,D = 0;
		double[] U= new double[k];
		double[] T= new double[k];
		double[] Di=new double[k];
		String typ=Type.getExpression();
		//typ = typ.substring(1, typ.length() - 1);
		int type = Integer.parseInt(typ);
		outt.send(0, new DoubleToken(type));
		int Nbclient = Integer.parseInt(Nclient.getExpression());
		System.out.println(type);
		String z= Z.getExpression();
		double Z= Double.valueOf(z);
		double Dmax=s[0]*v[0];
		for(int j=0;j<k;j++){
			Di[j]=s[j]*v[j];
			if(Di[j]>Dmax) {
				Dmax=Di[j];
				Xmax=1/Dmax;
			}
			D+=Di[j];
			
			
		}
		if(type==1){
				for(int j=0;j<Nbclient;j++) {
					
					double courb=j/D;
					double p=1/D;
					double n=j*D;
					double m=j*Dmax;
					Courbe1.send(0, new DoubleToken(courb));
					Courbe2.send(0, new DoubleToken(Xmax));
					Courbe3.send(0, new DoubleToken(p));
					Courbe4.send(0, new DoubleToken(D));
					Courbe5.send(0, new DoubleToken(n));
					Courbe6.send(0, new DoubleToken(m));
				
				}
			}
		else if(type==2){
			for(int j=0;j<Nbclient;j++) {
				double p= j/(D+Z);
				double n= j/((j*D)+Z);
				double l= j*D;
				double m= (j*Dmax)-Z;
				Courbe1.send(0, new DoubleToken(p));
				Courbe2.send(0, new DoubleToken(Xmax));
				Courbe3.send(0, new DoubleToken(n));
				Courbe4.send(0, new DoubleToken(D));
				Courbe5.send(0, new DoubleToken(l));
				Courbe6.send(0, new DoubleToken(m));
			}
		}
		else if(type==3){
			for(int j=0;j<Nbclient;j++) {
			Courbe1.send(0, new DoubleToken(Xmax));
			Courbe4.send(0, new DoubleToken(Dmax));
			}
		}
		
	}
	

}
