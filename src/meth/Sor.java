package meth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;

import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.data.DoubleToken;
import ptolemy.data.StringToken;
import ptolemy.data.expr.Parameter;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class Sor extends LimitedFiringSource {

	public Parameter Erreur;
	//public Parameter AscDesc;
	public Parameter Alpha;
	public TypedIOPort inputCII;
	public Parameter Sigma;
	public TypedIOPort Pi;
	
	public Sor(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		// TODO Auto-generated constructor stub
		inputCII = new TypedIOPort(this, "inputCii", true, false);
		
		Pi = new TypedIOPort(this, "Pi", false, true);
	//	inputsz = new TypedIOPort(this, "inputSz", true, false);
		
		Erreur= new Parameter(this, "Erreur");
		Erreur.setTypeEquals(BaseType.DOUBLE);
		Alpha= new Parameter(this, "Alpha");
		Alpha.setTypeEquals(BaseType.DOUBLE);
		
		//AscDesc= new TypedIOPort(this, "AscDesc", true, false);
		//AscDesc.setTypeEquals(BaseType.STRING);
		Sigma= new Parameter(this, "Sigma");
		Sigma.setTypeEquals(BaseType.DOUBLE);
		
		
		//inputsz.setMultiport(true);
		inputCII.setMultiport(true);
		inputCII.setAutomaticTypeConversion(true);
		output.setTypeEquals(BaseType.DOUBLE);
		Pi.setTypeEquals(BaseType.DOUBLE);
	
	}
	
static double[] CalculePower(Hashtable<Integer, Hashtable<Integer, Double>> ht1, int v,double[] b, double[] nv) {
		
		for (int key = 0; key < v; key++) {
				for (Integer key2 : ht1.get(key).keySet()) {

					nv[key] += ht1.get(key).get(key2) * b[key2];
				}
			}
			double S = 0;
			for (int i = 0; i < b.length; i++) {
				b[i] = nv[i];
				S += Math.abs(b[i]);
			}
			for (int i = 0; i < b.length; i++) {
				b[i] = b[i] / S;
			}
		
		
		return nv;
	}

	static double[] Calcule(Hashtable<Integer, Hashtable<Integer, Double>> ht1,double sig, double EPS, int v,double[] b, double[] nv) {
		double erreur = 0, erreurMax = 0.00001;
		do {
			
			for (int key = v-1; key >= 0; key--) {
				double A1 = 0, A2 = 0;
				for (Integer key2 : ht1.get(key).keySet()) {

					// System.out.print(key2+" | ");
					if (key2 < key)
						A1 += (ht1.get(key).get(key2) * nv[key2]);
					else if (key2 > key)
						A2 += (ht1.get(key).get(key2) * b[key2]);

				}
				if (ht1.get(key).get(key) != null)
					nv[key] = (A1 + A2) / (1 - ht1.get(key).get(key));
				else
					nv[key] = (A1 + A2);
			}
			double S1=0;
			for (int i = 0; i < b.length; i++) S1 += Math.abs(nv[i]);
		
			for (int i = 0; i < b.length; i++) {
				nv[i] = Math.abs(nv[i] / S1);
			}
			double S = 0;
			for (int i = 0; i < b.length; i++) {
				
				erreur = Math.abs(nv[i] - b[i]);
				if (i == 0 || erreur > erreurMax)
							erreurMax = erreur;
				
				System.out.print(nv[i] + " | ");
				b[i] = nv[i];
				S += Math.abs(b[i]);
				
			}
			for (int i = 0; i < b.length; i++) {
				b[i] = b[i] / S;
			}

		} while (erreurMax > EPS);
		return nv;
	}

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		double w = Double.valueOf(Alpha.getExpression());
		double erreur = 0, erreurMax = 0.01;
		Hashtable<Integer, Hashtable<Integer, Double>> ht1 = new Hashtable<Integer, Hashtable<Integer, Double>>();
		double EPS = Double.valueOf(Erreur.getExpression());
		String url = ((StringToken) inputCII.get(0)).toString();
		// remove the quotes
		url = url.substring(1, url.length() - 1);
		// remove the quotes
		File f = new File(url);
		if (f.exists() != true)
			JOptionPane.showMessageDialog(null, "Fichier introuvable");
		else {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = br.readLine();
				while (line != null) {
					Hashtable<Integer, Double> t1 = new Hashtable<Integer, Double>();
					String[] Q = line.split("\\s+");
					for (int i = 2; i < Q.length; i += 2)
						t1.put(Integer.parseInt(Q[i + 1]), Double.valueOf(Q[i]));
					ht1.put(Integer.parseInt(Q[0]), t1);
					line = br.readLine();
				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//ht1 = transforme(P);
			

			int v=ht1.size();
			double[] b = new double[v];
			double[] nv = new double[v];
			double[] Nouv = new double[v];
			
			double sig = 0.22335;
			//int v=ht1.size();
			double[] b1 = new double[v];
			double c1 = 1 / Double.valueOf(v);
			java.util.Arrays.fill(b1, c1);
			nv=Calcule(ht1,sig,EPS,v,b1,nv);
			java.util.Arrays.fill(b, c1);
			//for(int i=0;i<v.size();i++) b[i]=v.elementAt(i);
			b=CalculePower(ht1, v, b, Nouv);
			
			for(int i=0;i<v;i++) {
				Nouv[i] = (b[i] * w )+ ((1-w)*nv[i]);
				System.out.print(Nouv[i] + " | ");
				Pi.send(0, new DoubleToken(Nouv[i]));
			}
		}
	}
	
}
