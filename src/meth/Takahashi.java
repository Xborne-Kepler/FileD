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

public class Takahashi extends LimitedFiringSource {

	
	public Parameter attrib;
	public TypedIOPort inputCII;
	public TypedIOPort inputTarjan;
	public TypedIOPort outNc;
	
	public Takahashi(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		// TODO Auto-generated constructor stub
		inputCII = new TypedIOPort(this, "inputCii", true, false);
		outNc = new TypedIOPort(this, "Pi Value", false, true);
		inputTarjan = new TypedIOPort(this, "inputTarjan", true, false);
		
		attrib= new Parameter(this, "Erreur");
		attrib.setTypeEquals(BaseType.DOUBLE);
		
		
		inputCII.setMultiport(true);
		inputCII.setAutomaticTypeConversion(true);
		
		inputCII.setMultiport(true);
		inputCII.setAutomaticTypeConversion(true);
		output.setTypeEquals(BaseType.DOUBLE);
		outNc.setTypeEquals(BaseType.DOUBLE);
		
	}
	static double[] Calcule(Hashtable<Integer, Hashtable<Integer, Double>> ht1, int[] indicefin, double EPS,
			double[] sig, double[] b, double[] nv) {
		double erreur = 0, erreurMax = 0.0001;
		do {
			for (int key = b.length - 1; key >= 0; key--) {
					int a = 0;
					int i = 0;
					boolean found = false;
					while (found != true) {
						if (key <= indicefin[i]) {
							a = i;
							found = true;
						} else
							i++;
					}
					double A1 = 0, A2 = 0;
					for (Integer key2 : ht1.get(key).keySet()) {

						// System.out.print(key2+" | ");
						if (key2 < key)
							A1 += (ht1.get(key).get(key2) * nv[key2] * sig[a]);
						else if (key2 > key)
							A2 += (ht1.get(key).get(key2) * b[key2] * sig[a]);

					}
					if (ht1.get(key).get(key) != null)
						nv[key] = (A1 + A2) / (1 - ht1.get(key).get(key));
					else
						nv[key] = (A1 + A2);
				}
			double S1 = 0;
			for (int i = 0; i < b.length; i++)
				S1 += Math.abs(nv[i]);

			for (int i = 0; i < b.length; i++) {
				nv[i] = nv[i] / S1;
			}

			for (int i = 0; i < b.length; i++) {

				erreur = Math.abs(nv[i] - b[i]);
				if (i == 0 || erreur > erreurMax)
					erreurMax = erreur;

				b[i] = nv[i];
			}

		} while (erreurMax > EPS);
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

	static double[] lecture2(double[] nv, double[] b, double[] sig, int[] indice,
			Hashtable<Integer, Hashtable<Integer, Double>> ht1, double err) {
		
		nv = Calcule(ht1, indice, err, sig, b, nv);
		return nv;
	}

	static double[] lectureF(String fichier, String fichier2, int[] indice, int iteration, double[] sig,
			Hashtable<Integer, Hashtable<Integer, Double>> ht1) {
		int v = 0;
		File f = new File(fichier);
		File f2 = new File(fichier2);
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
		}
		double[] b = new double[v];
		double c1 = 1 / Double.valueOf(v);
		java.util.Arrays.fill(b, c1);
		double[] nv = new double[v];
		
		nv = Calcule(ht1, indice, 0.00000001, sig, b, nv);
		return nv;
	}

	public static double[] GTH(double[][] P, int M) {
		double[] Pi = new double[M];
		// **************** GTH ************************

		for (int n = M - 1; n >= 1; n--) {
			double S = 0;
			for (int j = 0; j <= n - 1; j++) {
				S = S + P[n][j];

			}

			for (int i = 0; i <= n - 1; i++) {
				P[i][n] = P[i][n] / S;
			}
			for (int i = 0; i <= n - 1; i++) {
				for (int j = 0; j <= n - 1; j++) {
					P[i][j] = P[i][j] + (P[i][n] * P[n][j]);

				}
			}

		}

		Pi[0] = 1;
		double TOT = 1;

		for (int j = 1; j <= M - 1; j++) {
			Pi[j] = P[0][j];
			for (int k = 1; k <= j - 1; k++) {
				Pi[j] = Pi[j] + Pi[k] * P[k][j];
			}

			TOT = TOT + Pi[j];

		}

		for (int j = 0; j <= M - 1; j++) {

			Pi[j] = Pi[j] / TOT;

		}

		return Pi;
	}

	public static double[][][] construction(double[][][] p1, int[] taille) {

		double[][][] B = new double[taille.length][][];

		/*
		 * 1 ere partie : la construction B = | O | I2 | | I1 | P |
		 * 
		 */

		for (int c = 0; c < taille.length; c++) {
			int n = taille[c];
			B[c] = new double[2 * n][2 * n];
			// *********** O **************
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++)
					B[c][i][j] = 0;
			}
			// *********** I1 **************
			for (int i = n; i < 2 * n; i++) {
				for (int j = 0; j < n; j++) {
					if (i == j + n)
						B[c][i][j] = 1;
					else
						B[c][i][j] = 0;
				}
			}
			// *********** I2 **************
			for (int i = 0; i < n; i++) {
				for (int j = n; j < 2 * n; j++) {
					if (i == j - n)
						B[c][i][j] = 1;
					else
						B[c][i][j] = 0;
				}
			}
			// *********** P **************
			int a = 0;
			for (int i = n; i < 2 * n; i++) {
				int f = 0;
				for (int j = n; j < 2 * n; j++) {
					B[c][i][j] = p1[c][a][f];
					f++;
				}
				a++;
			}

		}

		return B;
	}

	public static double[][][] ValeurInter(double[][][] B, int l,int c, int[] TailleB) {

		double[][][] t = new double[TailleB.length][][];
		t[c] = new double[l][l];

			double I = 1 / (1 - B[c][l][l]);
			//System.out.println(" I "+I);
			for (int i = 0; i < l; i++) {
				for (int j = 0; j < l; j++) {
					t[c][i][j] = I * B[c][i][l] * B[c][l][j];
					
				}
			}
		
		return t;
	}

	public static double[][][] Reduction(double[][][] B, int[] TailleB) {
		double[][][] Bnew = new double[TailleB.length][][];
<<<<<<< HEAD
		// 2 eme partie : la reduction
=======
>>>>>>> 235a5d4cdd7f2f67cabf0b6b1c33a6c069379952
		for (int c = 0; c < TailleB.length; c++) {
				int L = B[c].length - 1;
				
				int n=TailleB[c];
				
				while (L >= n) {
					double[][][] t = ValeurInter(B,L, c, TailleB);
					for (int i = 0; i <= L - 1; i++) {
						for (int j = 0; j <= L - 1; j++) {
							B[c][i][j] = B[c][i][j] + t[c][i][j];
						}
					}
					L--;
				}

				Bnew[c] = new double[TailleB[c]][TailleB[c]];
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						Bnew[c][i][j] = B[c][i][j];
					}
				}
		}
<<<<<<< HEAD
			return Bnew;
=======
		return Bnew;
>>>>>>> 235a5d4cdd7f2f67cabf0b6b1c33a6c069379952

	}
	
	
	
	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		double Err =  Double.valueOf(attrib.getExpression());
		Hashtable<Integer, Hashtable<Integer, Double>> ht1 = new Hashtable<Integer, Hashtable<Integer, Double>>();
		int w=0;
		int[] TailleB = null;
		int[] IndiceDebut = null;
		int[] IndiceFin = null;
		String url = ((StringToken) inputCII.get(0)).toString();
		// remove the quotes
		url = url.substring(1, url.length() - 1);
		File file = new File(url);

		String url2 = ((StringToken) inputTarjan.get(0)).toString();
		// remove the quotes
		url2 = url2.substring(1, url2.length() - 1);
		File f2 = new File(url2);

		if (file.exists() != true && f2.exists() !=true)
			JOptionPane.showMessageDialog(null, "Fichier introuvable");
		else {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(f2));
			String line = br.readLine();
			String[] Q1= line.split("\\s+");
			System.out.println(Q1[0]);
			w=Integer.parseInt(Q1[0]);
			int n = Integer.valueOf(line);
			TailleB = new int[n];
			IndiceDebut = new int[n];
			IndiceFin = new int[n];
			line = br.readLine();
			int i = 0;
			while (line != null) {
				String[] Q = line.split("\\s+");
				TailleB[i]=	Integer.valueOf(Q[1]);
				IndiceDebut[i] = Integer.valueOf(Q[2]);
				IndiceFin[i] =  Integer.valueOf(Q[2]) + Integer.valueOf(Q[1]) -1;
				line = br.readLine();
				i++;
			}

			BufferedReader br1 = new BufferedReader(new FileReader(file));
			String line2 = br1.readLine();
			while (line2 != null) {
				Hashtable<Integer, Double> t1 = new Hashtable<Integer, Double>();
				String[] Q = line2.split("\\s+");
				for (int d = 2; d < Q.length; d += 2)
					t1.put(Integer.parseInt(Q[d + 1]), Double.valueOf(Q[d]));
				ht1.put(Integer.parseInt(Q[0]), t1);
				line2 = br1.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int somTailleB = 0;
		for (int i = 0; i < TailleB.length; i++)
			somTailleB += TailleB[i];
		int n = somTailleB;
		int m = 10000;
		double[] P_ancien = new double[n];
		double[] Pi = new double[n];
		double[][] Si = new double[n][m];
		double[][] A = new double[TailleB.length][TailleB.length];
		double erreur = 0, erreurMax = 0;
		double[] sig = new double[TailleB.length];
		double[] GS = new double[n];
		double[] Z = new double[n];
		
		double[][][] P1 = new double[w][][];
		for (int i = 0; i < TailleB.length; i++)
			P1[i] = new double[TailleB[i]][TailleB[i]];

		for (int d = 0; d < TailleB.length; d++) {
			int a = 0;
			for (int i = IndiceDebut[d]; i <= IndiceFin[d]; i++) {
				int b = 0;
				for (int j = IndiceDebut[d]; j <= IndiceFin[d]; j++) {
					//System.out.println(ht1.get(j).get(i));
					if(ht1.get(j).get(i)!=null)
					P1[d][a][b] = ht1.get(j).get(i);
					else P1[d][a][b] =0;
					System.out.print(P1[d][a][b]+" | ");
					b++;
				}System.out.println();
				a++;
			}
		}

		//ht1 = transforme(P);
		double[][][] B1 = construction(P1, TailleB);
		double[][][] Bnew1 = Reduction(B1, TailleB);

		/*
		 * for(int i=0;i<B1.length;i++) for(int j=0;j<B1.length;j++)
		 * System.out.print(B1[0][i][j]+" | "); System.out.println();
		 */
		// ************* 1 *****************

		double p = 1 / Double.valueOf(n);
		for (int i = 0; i < n; i++) {
			P_ancien[i] = p;
		}
		int c = 1;

		// ************** 2 *****************
		for (int i = 0; i < TailleB.length; i++) {
			double sum = 0;
			for (int j = IndiceDebut[i]; j <= IndiceFin[i]; j++) {
				sum += Math.abs(P_ancien[j]);

			}

			for (int j = IndiceDebut[i]; j <= IndiceFin[i]; j++) {
				Si[j][c - 1] = P_ancien[j] / sum;
			}
		}
		do {
			// ************** 3 *****************

			for (int i = 0; i < TailleB.length; i++) {
				for (int j = 0; j < TailleB.length; j++) {
					double h = 0;
					for (int p1 = IndiceDebut[i]; p1 <= IndiceFin[i]; p1++) {
						for (int p2 = IndiceDebut[j]; p2 <= IndiceFin[j]; p2++) {
							if(ht1.get(p2).get(p1)!=null)
							h = h + (Si[p1][c - 1] * ht1.get(p2).get(p1));
						}
					}
					A[i][j] = h;
				}
			}

			// ************** 4 *****************

			sig = GTH(A, TailleB.length);
			//// double[] sig= {0.22333333333333336, 0.27666666666666667 , 0.5};

			// ************** 5 *****************

			int count = 0;
			double S1 = 0;
			GS = lecture2(Pi, P_ancien, sig, IndiceFin, ht1,Err);

			for (int l = 0; l < TailleB.length; l++) {
				S1 = 0;
				for (int i = 0; i < TailleB[l]; i++) {
					double V = 0;
					int b = IndiceDebut[l];
					for (int j = 0; j < TailleB[l]; j++) {
						V = V + (GS[b] * Bnew1[l][j][i]);
						b++;
					}
					Z[count] = V;
					// System.out.println(Z[count]+" | ");
					S1 += Math.abs(Z[count]);
					count++;
				}

				for (int t = IndiceDebut[l]; t <= IndiceFin[l]; t++) {
					Si[t][c] = Z[t] / S1;

				}
			}

			// **************** 6 *****************
			 System.out.println("ETAPE 6 : ");
			for (int i = 0; i < TailleB.length; i++) {
				for (int j = IndiceDebut[i]; j <= IndiceFin[i]; j++) {
					Pi[j] = Si[j][c] * sig[i];
					System.out.print(Pi[j] + " | ");
				}
				System.out.println();
			}
			// System.out.println("***************************");
			for (int i = 0; i < n; i++) {
				P_ancien[i] = Pi[i];

			}
			c++;
		} while (c < 100);
		for (int i = 0; i < Pi.length; i++) {
			outNc.send(0, new DoubleToken(Pi[i]));
				
		}
		}
		
	}
	
}
