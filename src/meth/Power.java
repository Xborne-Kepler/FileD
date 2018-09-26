package meth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

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

public class Power extends LimitedFiringSource {

	public Parameter Erreur;
	public TypedIOPort inputCII;
	public TypedIOPort inputsz;
	public TypedIOPort Pi;
	
	public Power(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		// TODO Auto-generated constructor stub
		inputCII = new TypedIOPort(this, "inputCii", true, false);
		Pi = new TypedIOPort(this, "Pi", false, true);
		inputsz = new TypedIOPort(this, "inputSz", true, false);
		
		Erreur= new Parameter(this, "Erreur");
		Erreur.setTypeEquals(BaseType.DOUBLE);
		
		inputsz.setMultiport(true);
		inputCII.setMultiport(true);
		inputCII.setAutomaticTypeConversion(true);
		output.setTypeEquals(BaseType.DOUBLE);
		Pi.setTypeEquals(BaseType.DOUBLE);
	}
		static double[] Calcule(Hashtable<Integer, Hashtable<Integer, Double>> ht1, int v,double[] b, double[] nv) {
			
			for (int key = 0; key < v; key++) {
					for (Integer key2 : ht1.get(key).keySet()) {

						nv[key] += ht1.get(key).get(key2) * b[key2];
					}
				}
				double S = 0;
				for (int i = 0; i < b.length; i++) {
					b[i] = nv[i];
					S += Math.abs(b[i]);
					System.out.print(nv[i]+" | ");
				}System.out.println();
				
				for (int i = 0; i < b.length; i++) {
					b[i] = b[i] / S;
				}
			
			
			return nv;
		}
		
		static Hashtable<Integer, Hashtable<Integer, Double>> transforme(double[][] P) {
			Hashtable<Integer, Hashtable<Integer, Double>> ht1 = new Hashtable<Integer, Hashtable<Integer, Double>>();
			for (int i = 0; i < P.length; i++) {
				Hashtable<Integer, Double> t1 = new Hashtable<Integer, Double>();
				for (int j = 0; j < P.length; j++) {
					if (P[j][i] != 0)
						t1.put(j, P[j][i]);
				}
				ht1.put(i, t1);
			}
			return ht1;

		}
		
		
		@Override
		public void fire() throws IllegalActionException {
			// TODO Auto-generated method stub
			super.fire();
			
			
			int v = 0;
			Hashtable<Integer, Hashtable<Integer, Double>> ht1 = new Hashtable<Integer, Hashtable<Integer, Double>>();
			double erreur = Double.valueOf(Erreur.getExpression());
			String url = ((StringToken) inputCII.get(0)).toString();
			// remove the quotes
			url = url.substring(1, url.length() - 1);
			File file = new File(url);
			String url2 = ((StringToken) inputsz.get(0)).toString();
			// remove the quotes
			url2 = url2.substring(1, url2.length() - 1);
			File file2 = new File(url2);
			
			if (file.exists() != true || file2.exists() != true)
				JOptionPane.showMessageDialog(null, "Fichier introuvable");
			else {
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = br.readLine();
				while (line != null) {
					Hashtable<Integer, Double> t1 = new Hashtable<Integer, Double>();
					String[] Q = line.split("\\s+");
					for (int i = 2; i < Q.length; i += 2)
						t1.put(Integer.parseInt(Q[i + 1]), Double.valueOf(Q[i]));
					ht1.put(Integer.parseInt(Q[0]), t1);
					line = br.readLine();
				}

				br = new BufferedReader(new FileReader(file2));
				line = br.readLine();
				line = br.readLine();
				String[] Q = line.split("\\s+");
				v = Integer.parseInt(Q[1]);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			v=ht1.size();
			double[] b = new double[v];
			double c1 = 1 / Double.valueOf(v);
			java.util.Arrays.fill(b, c1);
			double[] nv = new double[v];
			nv=Calcule(ht1,v,b,nv);
			for(int i=0;i<nv.length;i++) {
				Pi.send(0, new DoubleToken(nv[i]));
			}
			}
		}
		
	}