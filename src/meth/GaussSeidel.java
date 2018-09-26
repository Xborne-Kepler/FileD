package meth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.springframework.core.Ordered;

import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.data.DoubleToken;
import ptolemy.data.StringToken;
import ptolemy.data.expr.Parameter;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class GaussSeidel extends LimitedFiringSource {

	public Parameter Erreur;
	//public TypedIOPort AscDesc;
	public TypedIOPort inputCII;
	public Parameter Sigma;
	public TypedIOPort inputsz;
	public TypedIOPort Pi;
	
	public GaussSeidel(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		// TODO Auto-generated constructor stub
		inputCII = new TypedIOPort(this, "inputCii", true, false);
		
		Pi = new TypedIOPort(this, "Pi", false, true);
		inputsz = new TypedIOPort(this, "inputSz", true, false);
		
		Erreur= new Parameter(this, "Erreur");
		Erreur.setTypeEquals(BaseType.DOUBLE);
		//AscDesc= new TypedIOPort(this, "AscDesc", true, false);
		//AscDesc.setTypeEquals(BaseType.STRING);
		Sigma= new Parameter(this, "Sigma");
		Sigma.setTypeEquals(BaseType.DOUBLE);
		
		inputsz.setMultiport(true);
		inputCII.setMultiport(true);
		inputCII.setAutomaticTypeConversion(true);
		output.setTypeEquals(BaseType.DOUBLE);
		Pi.setTypeEquals(BaseType.DOUBLE);
	
		
	}
	static Hashtable<Integer, Hashtable<Integer, Double>> transforme(double[][] P){
		Hashtable<Integer, Hashtable<Integer, Double>> ht1 = new Hashtable<Integer, Hashtable<Integer, Double>>();
		for(int i =0;i<P.length;i++) {
			Hashtable<Integer, Double> t1 = new Hashtable<Integer, Double>();
			for(int j=0;j<P.length;j++) {
				if(P[j][i]!=0) t1.put(j, P[j][i]);
			}ht1.put(i, t1);
		}
		return ht1;
		
	}

	static double[] Calcule(Hashtable<Integer, Hashtable<Integer, Double>> ht1,double sig, double EPS, int v,double[] b, double[] nv) {
		double erreur = 0, erreurMax = 0.00001;
		do {
			
				for (int key = v-1; key >= 0; key--) {
				double A1 = 0, A2 = 0;
				for (Integer key2 : ht1.get(key).keySet()) {

					// System.out.print(key2+" | ");
					if (key2 < key)
						A1 += (ht1.get(key).get(key2) * nv[key2] * sig);
					else if (key2 > key)
						A2 += (ht1.get(key).get(key2) * b[key2] * sig);

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
			
			
			System.out.println();
			System.out.println("**************************");
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
		
	//	String ordre = ((StringToken) AscDesc.get(0)).toString();
		//ordre = ordre.substring(1, ordre.length() - 1);
		double erreur = Double.valueOf(Erreur.getExpression());
		String url = ((StringToken) inputCII.get(0)).toString();
		// remove the quotes
		url = url.substring(1, url.length() - 1);
		String url2 = ((StringToken) inputsz.get(0)).toString();
		// remove the quotes
		url2 = url2.substring(1, url2.length() - 1);
		
		int v = 0;
		Hashtable<Integer, Hashtable<Integer, Double>> ht1 = new Hashtable<Integer, Hashtable<Integer, Double>>();
		
		File f = new File(url);
		File f2 = new File(url2);
		if (f.exists() != true || f2.exists() != true)
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

				br = new BufferedReader(new FileReader(f2));
				line = br.readLine();
				line = br.readLine();
				String[] Q = line.split("\\s+");
				v = Integer.parseInt(Q[1]);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//ht1 = transforme(P);
			double sig=Double.valueOf(Sigma.getExpression());
			//int v=ht1.size();
			double[] b1 = new double[v];
			double c1 = 1 / Double.valueOf(v);
			java.util.Arrays.fill(b1, c1);
			double[] nv = new double[v];
			nv=Calcule(ht1,sig,0.0001,v,b1,nv);
			for(int i=0;i<nv.length;i++) {
			Pi.send(0, new DoubleToken(nv[i]));
		}
		}
	}
	
	}
