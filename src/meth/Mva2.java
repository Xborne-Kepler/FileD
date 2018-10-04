package meth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kepler.metadata.ParserInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java_cup.parse_action;
import ptolemy.actor.NoRoomException;
import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.actor.parameters.PortParameter;
import ptolemy.actor.util.ActorTypeUtil;
import ptolemy.actor.util.ArrayElementTypeFunction;
import ptolemy.data.ArrayToken;
import ptolemy.data.DoubleToken;
import ptolemy.data.StringToken;
import ptolemy.data.expr.Parameter;
import ptolemy.data.expr.Token;

import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.Attribute;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.InternalErrorException;
import ptolemy.kernel.util.NameDuplicationException;

import ptolemy.kernel.util.Workspace;
import ptolemy.data.IntToken;
import java.util.Vector;

public class Mva2 extends LimitedFiringSource {
	public Parameter combinaison;
	public Parameter outt;
	public Parameter attrib;
	public TypedIOPort input;
	public TypedIOPort outNc;
	public TypedIOPort outX;
	public TypedIOPort outK;
	public PortParameter arrayLength;

	public Mva2(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		_firingCountLimit = 1;

		input = new TypedIOPort(this, "XmlFile", true, false);
		outNc = new TypedIOPort(this, "Nombre_M de Client", false, true);
		outX = new TypedIOPort(this, "throughput ", false, true);
		outK = new TypedIOPort(this, "Combinaisons", false, true);

		outt = new Parameter(this, "classe number");
		combinaison = new Parameter(this, "numero du combinaison de client");
		combinaison.setTypeEquals(BaseType.INT);
		outt.setTypeEquals(BaseType.INT);

		input.setMultiport(true);
		input.setAutomaticTypeConversion(true);
		output.setTypeEquals(BaseType.DOUBLE);
		outNc.setTypeEquals(BaseType.DOUBLE);
		outX.setTypeEquals(BaseType.DOUBLE);
		outK.setTypeEquals(BaseType.STRING);

		// username = new Parameter(this, "username", new DoubleToken());
		// TODO Auto-generated constructor stub

	}

	// Fonction qui retourne le type d'un fichier
	public static String type_fichier(String fichier) {
		fichier = fichier.substring(0, fichier.length());
		int pos = fichier.lastIndexOf(".");
		if (pos > -1) {
			return fichier.substring(pos);

		} else {
			return fichier;
		}

	}

	// fonction qui lit le nombre de server et le nombre de client
	public static int Nombre_De_file(String fichier) throws IOException {

		File f = new File(fichier);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int i = 0;

		String line;
		line = br.readLine();
		while (line.charAt(2) != 'P' && line.charAt(2) != 'p') {
			line = br.readLine();
			i++;
		}
		System.out.println("Nombre de file : " + i);
		return i;

	}

	// fonction qui lit la matrice de transition
	public static double[][] Matrice_de_transition(String file, String type, int M) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));
		double[][] p = new double[M + 1][M + 1];
		String line;

		int i = 0;
		line = br.readLine();
		double[] c = null;
		while (line.charAt(0) != '#') {
			line = br.readLine();
		}

		for (int j = 0; j < i; j++) {
			for (int d = 0; d < i; d++) {
				p[j][d] = 0;
			}
		}
		line = br.readLine();
		while (line != null) {
			String[] Q = line.split(" ");
			int j = Integer.parseInt(Q[0]) - 1;
			int d = Integer.parseInt(Q[1]) - 1;
			p[j][d] = Double.valueOf(Q[Q.length - 1]);
			line = br.readLine();

		}

		return p;

	}

	// Fonction qui lit le service ds servers
	public static double[][] service(String file, int M, int R) throws IOException {
		File f = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(f));

		String line;
		double[][] p = new double[M + 1][R + 1];

		int i = 0;

		line = br.readLine();

		while (line.charAt(2) != 'P' && line.charAt(2) != 'p') {
			String[] Q = line.split(" ");
			for (int a = 0; a < Q.length; a++) {
				for (int h = 0; h < Q[a].length(); h++) {
					if (Q[a].charAt(h) == ',')
						Q[a].replace(',', '.');
				}
			}

			for (int z = 0; z < Q.length; z++) {
				if ((Q[z].charAt(0) == 'M' || Q[z].charAt(0) == 'm')
						&& (Q[z].charAt(1) == 'U' || Q[z].charAt(1) == 'u')) {
					int f1 = 1;
					for (int d = 0; d < R; d++) {
						p[i][d] = Double.valueOf(Q[z + f1]);
						f1++;
						// System.out.println(p[i][d] + " | ");
					}
				}

			}

			line = br.readLine();
			i++;
		}
		return p;
	}

	public static int[] Nbserver(String file, int M) throws IOException {
		File f = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] D = new int[M];
		String line = "";

		line = br.readLine();
		int i = 0;
		while (line.charAt(2) != 'P' && line.charAt(2) != 'p') {
			String[] Q = line.split(" ");
			boolean sort = false;
			int z = 0;
			while (z < Q.length && !sort) {
				if ((Q[z].charAt(0) == 'm' || Q[z].charAt(0) == 'M')
						&& (Q[z].charAt(1) == 'i' || Q[z].charAt(1) == 'I')) {
					D[i] = Integer.parseInt(Q[z + 1]);
					sort = true;
				}
				z++;

			}
			i++;
			line = br.readLine();
		}

		return D;

	}

	public static int[] Population(String file, int R) throws IOException {
		File f = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] D = new int[R];
		String line = "";

		line = br.readLine();
		int i = 0;
		while ((line.charAt(2) != 'P' && line.charAt(2) != 'p') && (line.charAt(3) != 'O' && line.charAt(3) != 'o')
				&& line.charAt(0) != '#') {

			line = br.readLine();
		}

		if ((line.charAt(2) == 'P' || line.charAt(2) == 'p') && (line.charAt(3) == 'O' || line.charAt(3) == 'o')) {

			String[] Q = line.split(" ");
			for (int z = 2; z < Q.length; z++) {
				D[i] = Integer.parseInt(Q[z]);
				i++;
			}

		}

		return D;

	}

	public static int[] Type(String fichier, int M) throws IOException {
		File f = new File(fichier);
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] D = new int[M + 1];
		String line = "";

		line = br.readLine();
		int i = 0;
		while (line.charAt(2) != 'P' && line.charAt(2) != 'p') {
			String[] Q = line.split(" ");

			for (int z = 0; z < Q.length; z++) {
				if ((Q[z].charAt(0) == 't' || Q[z].charAt(0) == 'T') && (Q[z].charAt(1) == 'Y' || Q[z].charAt(1) == 'y')
						&& (Q[z].charAt(2) == 'P' || Q[z].charAt(2) == 'p')
						&& (Q[z].charAt(3) == 'E' || Q[z].charAt(3) == 'e'))
					D[i] = Integer.parseInt(Q[z + 1]);

			}
			i++;
			line = br.readLine();
		}
		return D;
	}

	public static double[][] Visite(String file) throws IOException {
		File f = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(f));
		double[][] D = new double[50][50];
		String line = "";
		line = br.readLine();
		int i = 0;
		while (line.charAt(2) != 'P' && line.charAt(2) != 'p') {
			String[] Q = line.split(" ");

			for (int a = 0; a < Q.length; a++) {
				for (int h = 0; h < Q[a].length(); h++) {
					if (Q[a].charAt(h) == ',')
						Q[a].replace(',', '.');
				}
			}
			String line2 = "";
			
			for (int z = Q.length - 1; z > 7; z--) {
				if (Q[z].charAt(0) == 'V' && Q[z].charAt(1) == 'i') {
					for (int r = z + 1; r < Q.length; r++)
						line2 = line2 + Q[r] + " ";

				} else
					z--;
			}

			String[] Q1 = line2.split(" ");

			for (int d = 0; d < Q1.length; d++) {

				D[i][d] = Double.valueOf(Q1[d]);
			}
			i++;
			line = br.readLine();

		}

		return D;
	}

	public static double[] GTH(double[][] P, int M) {
		double[] Pi = new double[M];
		// **************** GTH ************************

		for (int n = M - 1; n >= 1; n--) {
			double S = 0;
			for (int j = 0; j <= n - 1; j++) {
				S = S + P[n][j];
				System.out.println("S " + S);
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

	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		String url = ((StringToken) input.get(0)).toString();
		// remove the quotes
		url = url.substring(1, url.length() - 1);
		File file = new File(url);

		if (file.exists() != true)
			JOptionPane.showMessageDialog(null, "Fichier introuvable");
		else {
			String type = type_fichier(url);
			int[] Nb = new int[2];
			Nombre_C_S(url, Nb);
			int M = Nb[1]; // nombre de file
			// int[] N = { 2, 1 }; // vecteur de population
			int nbclass = Nb[0];
			if ((type.charAt(1) == 't' && type.charAt(2) == 'g' && type.charAt(3) == 'f'))
				try {
					M = Nombre_De_file(url);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			double[][] mu = new double[M][nbclass];
			double[][] V = new double[M][nbclass];
			int[] typeServer = new int[M];
			int[] NbServer = new int[M];
			int[] N = new int[nbclass];
			if ((type.charAt(1) == 'x' && type.charAt(2) == 'm' && type.charAt(3) == 'l') || (type.charAt(1) == 'j'
					&& type.charAt(2) == 'm' && type.charAt(3) == 'v' && type.charAt(4) == 'a')) {
				xmlrecup(url, V, mu, typeServer, NbServer, N);

			} else if ((type.charAt(1) == 't' && type.charAt(2) == 'g' && type.charAt(3) == 'f')) {
				try {
					int R = 2;
					V = Visite(url);
					NbServer = new int[M];
					NbServer = Nbserver(url, M);
					typeServer = Type(url, M);
					mu = service(url, M, R);
					N = Population(url, R);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				JOptionPane.showMessageDialog(null, "extension du fichier doit etre soit .xml , .jmva ou .tgf ");
			
			NumberFormat fmat = NumberFormat.getNumberInstance();
			fmat.setMaximumFractionDigits(3);
			fmat.setMinimumFractionDigits(3);

			int d = 1; // le nombre de vecteur de population possible
			for (int i = 0; i < N.length; i++)
				d = d * (N[i] + 1);

			Vector<Vector<Integer>> vect = new Vector<Vector<Integer>>(); // vecteur de population
			// construction des vecteurs de populations ( de (0 ,0 , ...,0) au vecteur de
			// population maximal )
			for (int j = 0; j < d; j++) {
				Vector<Integer> vec = new Vector<Integer>();
				int[] vectPop = Int_To_Vecteur(N, j);
				for (int i = 0; i < N.length; i++) {
					vec.add(i, vectPop[i]);
				}
				vect.add(vec);
			}

			/**
			 * Intermediate results: queue length for each service station, class<br>
			 * [station] [class] [Number of vector of population]
			 */
			double[][][] Nc = new double[M][N.length][d];

			/**
			 * Intermediate results: residence time for each service station, class<br>
			 * [station] [class] [Number of vector of population]
			 */
			double[][][] T = new double[M][N.length][d];

			/**
			 * Intermediate results: throughput for each service station, class<br>
			 * [class] [Number of vector of population]
			 */
			double[][] X = new double[N.length][d];

			// double[] V = GTH(P, M);
			String attribut = outt.getExpression();
			String Combinaison = combinaison.getExpression();
			int nmax = 0;
			for (int i = 0; i < M; i++) {
				if (nmax < NbServer[i])
					nmax = NbServer[i];
			}
			double[][][] pMarg = new double[M][nmax + 1][d];

			pMarg[0][0][0] = 1; // Marginals for LD nodes

			for (int i = 0; i < M; i++) {
				for (int j = 1; j <= NbServer[i] - 1; j++) {
					pMarg[0][j][0] = 0;
				}
				for (int c = 0; c < N.length; c++) {
					Nc[i][c][0] = 0;
				}
			}

			// int width = output.getWidth();
			String Ks = "";
			// double[][] Tvalue= new double[y][M];

			for (int i = 0; i < d; i++) {
				String combin = i + " = " + vect.elementAt(i);
				outK.send(0, new StringToken(combin));
				// *** MVA Step 1: Compute mean response times ***

				for (int m = 0; m < M; m++) {
					for (int c = 0; c < N.length; c++) {
						double u = 0;
						if (typeServer[m] == 3) { // Infinite server node
							T[m][c][i] = 1 / mu[m][c];
						} else if (typeServer[m] == 1) { // FIFO exponential, etc.

							int v = valeurK(i, c, N.length, vect);

							for (int s = 0; s < N.length; s++) {
								u = u + Nc[m][s][v];
							}

							T[m][c][i] = (u + 1) * 1 / mu[m][c];

						} else if (typeServer[m] == 2) { // LD

							double p = 0;
							double t = 0;
							int v = valeurK(i, c, N.length, vect);
							for (int s = 0; s < N.length; s++) {
								p = p + Nc[m][s][v];
							}
							for (int j = 0; j <= NbServer[m] - 2; j++)
								t = t + (NbServer[m] - j - 1) * (pMarg[m][j][v]);
							T[m][c][i] = 1 / (mu[m][c] * NbServer[m]) * (1 + p + t);
						}

						// Tvalue[k-1][m]=T[m];

					}

				}

				// *** MVA Step 2: Compute throughputs

				for (int c = 0; c < N.length; c++) {
					double bottom = 0;
					for (int m = 0; m < M; m++) {
						bottom = bottom + V[m][c] * T[m][c][i];
					}
					X[c][i] = vect.elementAt(i).elementAt(c) / bottom; // Overall throughput

					// *** MVA Step 3: Compute mean number of customers ***

					for (int m = 0; m < M; m++) {
						Nc[m][c][i] = X[c][i] * T[m][c][i] * V[m][c];
						// Nvalue[k-1][m]= Nc[c][m];
					} // outNc.send(0, new DoubleToken(Nvalue[k-1][Integer.parseInt(attribut)]));

					// Compute marginals for all load-dependent nodes
				}
				for (int m = 0; m < M; m++) {
					if (typeServer[m] != 3) {

						for (int j = 1; j <= NbServer[m] - 1; j++) {
							double u = 0;
							int v = 0;
							for (int r = 0; r < N.length; r++) {

								v = valeurK(i, r, N.length, vect);
								u = u + (X[r][i] * (V[m][r] / mu[m][r]) * pMarg[m][j - 1][v]);

							}
							pMarg[m][j][i] = (1 / j) * u;
							// System.out.println("pj "+pMarg[m][j][i]);
						}

						double n = 0;
						double B = 0;
						for (int r = 0; r < N.length; r++) {
							n = n + (X[r][i] * (V[m][r] / mu[m][r]));

						}

						for (int j = 1; j <= NbServer[m] - 1; j++) {
							B = B + ((NbServer[m] - j) * pMarg[m][j][i]);

						}
						double p = NbServer[m];
						pMarg[m][0][i] = 1 - (1 / p) * (n + B);
						// System.out.println("p0 "+pMarg[m][0][i]+" n = "+n+ " B= "+B);

					}

				}

				// Output results
				// System.out.println(" " );
				System.out.println("  e = " + vect.elementAt(i));

				for (int m = 0; m < M; m++) {
					for (int c = 0; c < N.length; c++) {

						System.out.println(" m = " + (m + 1) + ":     " + fmat.format(T[m][c][i]) + "   "
								+ fmat.format(X[c][i]) + "    " + fmat.format(Nc[m][c][i]));
						// Ks=Ks+" m = " + (m + 1) + ": " + fmat.format(T[c][m]) + " " +
						// fmat.format(X[c][m])
						// + " " + fmat.format(Nc[c][m])+"\n";
					}
				}

				Ks = Ks + "\n";
			}
			for (int m = 0; m < M; m++) {
				output.send(0, new DoubleToken(T[m][Integer.parseInt(attribut) - 1][Integer.parseInt(Combinaison)]));
				outX.send(0, new DoubleToken(X[Integer.parseInt(attribut) - 1][Integer.parseInt(Combinaison)]));
				outNc.send(0, new DoubleToken(Nc[m][Integer.parseInt(attribut) - 1][Integer.parseInt(Combinaison)]));
			}
		}
	}

	public static int valeurK(int i, int r, int N, Vector<Vector<Integer>> vect) {
		boolean found = false;
		int v = 0;
		int x = 0;
		while (x < i && found == false) {
			int cont = 0;
			if (vect.elementAt(x).elementAt(r) == vect.elementAt(i).elementAt(r) - 1) {
				cont++;
				for (int h = 0; h < N; h++) {
					if (h != r && vect.elementAt(x).elementAt(h) == vect.elementAt(i).elementAt(h)) {
						cont++;
					}
				}
				if (cont == N) {
					v = x;
					found = true;
				}
			}
			x++;
		}
		return v;
	}

	public static int Vecteur_To_Int(int[] vectMax, int[] vect) {
		int j = vect[vect.length - 1];
		for (int i = vect.length - 2; i >= 0; i--) {
			int t = 1;
			for (int e = i + 1; e <= vect.length - 1; e++)
				t = t * (vectMax[e] + 1);
			j = j + (vect[i] * t);
		}

		return j;

	}

	public static int[] Int_To_Vecteur(int[] vectMax, int i) {
		int length = vectMax.length;
		int[] pop = new int[length];

		for (int j = length - 1; j >= 0; j--) {
			pop[j] = i % (vectMax[j] + 1);
			i = (i - pop[j]) / (vectMax[j] + 1);
		}
		return pop;

	}

	public static void xmlrecup(String url, double[][] visite, double[][] service, int[] Type, int[] nombre,
			int[] pop) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);

		try {
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			File fileXML = new File(url);
			Document xml;
			Vector<String> v = new Vector();
			Vector<String> v1 = new Vector();
			Vector<Integer> type = new Vector();
			Vector<Integer> number = new Vector();
			try {
				xml = builder.parse(fileXML);
				Element racine = xml.getDocumentElement();
				DefaultMutableTreeNode root = new DefaultMutableTreeNode(racine.getNodeName());
				createJTree(number, type, v1, v, racine, root);

				for (int i = 0; i < type.size(); i++) {
					Type[i] = type.elementAt(i);
					nombre[i] = number.elementAt(i);
				}
				int s = 0;
				for (int i = 2; i < v1.size(); i++) {
					pop[s] = Integer.parseInt(v1.elementAt(i));
					s++;
				}
				int nbclass = Integer.parseInt(v1.elementAt(0));
				int nbstation = Integer.parseInt(v1.elementAt(1));

				int p = 0;
				for (int j = 0; j < nbstation; j++) {
					for (int i = 0; i < nbclass; i++) {
						service[j][i] = Double.valueOf(v.elementAt(p));
						p++;
					}
					p = p + nbclass;
				}
				// double[][] visite = new double[nbstation][nbclass];
				int d = nbclass;
				for (int j = 0; j < nbstation; j++) {
					for (int i = 0; i < nbclass; i++) {
						visite[j][i] = Double.valueOf(v.elementAt(d));
						d++;
					}
					d = d + nbclass;
				}

			} catch (SAXParseException e) {
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static int[] Nombre_C_S(String url, int[] Nb) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);

		try {
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			File fileXML = new File(url);
			Document xml;
			try {
				Vector<String> v = new Vector();
				Vector<String> v1 = new Vector();
				Vector<Integer> type = new Vector();
				Vector<Integer> number = new Vector();
				xml = builder.parse(fileXML);
				Element racine = xml.getDocumentElement();
				DefaultMutableTreeNode root = new DefaultMutableTreeNode(racine.getNodeName());
				createJTree(number, type, v1, v, racine, root);
				int nbclass = Integer.parseInt(v1.elementAt(0));
				int nbstation = Integer.parseInt(v1.elementAt(1));

				Nb[0] = nbclass;
				Nb[1] = nbstation;
			} catch (SAXParseException e) {
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return Nb;
	}

	public static void createJTree(Vector<Integer> type, Vector<Integer> number, Vector<String> v1, Vector<String> v,
			Node n, DefaultMutableTreeNode treeNode) {

		if (n instanceof Element) {

			// Nous sommes donc bien sur un element de notre document
			// Nous castons l'objet de type Node en type Element
			Element element = (Element) n;

<<<<<<< HEAD
			// nous contrôlons la liste des attributs presents
=======
			// nous contrÃ´lons la liste des attributs presents
>>>>>>> 235a5d4cdd7f2f67cabf0b6b1c33a6c069379952
			if (n.getAttributes() != null && n.getAttributes().getLength() > 0) {

				// nous pouvons recuperer la liste des attributs dans une Map
				NamedNodeMap att = n.getAttributes();
				int nbAtt = att.getLength();
				Node n0 = att.item(0);
				if (n.getNodeName().equals("classes")) {
					String p = n0.getNodeValue();
					// System.out.println(NbClass);
					v1.add(0, p);
				}
				if (n.getNodeName().equals("stations")) {
					v1.add(1, n0.getNodeValue());
				}

				Node nfin = att.item(nbAtt - 1);
				if (n.getNodeName().equals("closedclass")) {
					v1.add(nfin.getTextContent());
				}

				if (n.getNodeName().equals("ldstation")) {
					number.add(Integer.parseInt(nfin.getTextContent()));
					type.add(2);
				}
				if (n.getNodeName().equals("listation")) {
					number.add(1);
					type.add(1);
				}
				if (n.getNodeName().equals("delaystation")) {
					number.add(1);
					type.add(3);
				}
			}

			// Nous allons maintenant traiter les noeuds enfants du noeud en cours de
			// traitement
			int nbChild = n.getChildNodes().getLength();
			// Nous recuperons la liste des noeuds enfants
			NodeList list = n.getChildNodes();

			// nous parcourons la liste des noeuds
			for (int i = 0; i < nbChild; i++) {
				Node n2 = list.item(i);

				// si le noeud enfant est un Element, nous le traitons
				if (n2 instanceof Element) {
					DefaultMutableTreeNode noeud = new DefaultMutableTreeNode(n2.getNodeName());

					// Pour afficher les valeurs des noeuds
					if (n2.getTextContent() != null && n2.getTextContent().trim() != ""
							&& n2.getChildNodes().getLength() == 1) {
						DefaultMutableTreeNode value = new DefaultMutableTreeNode(n2.getTextContent());
						noeud.add(value);
						String[] D = (String.valueOf(noeud.getChildAt(0))).split(";");
						v.add(D[0]);

					}
					// appel recursif a la methode pour le traitement du noeud et de ses enfants
					createJTree(number, type, v1, v, n2, noeud);
					treeNode.add(noeud);

				}
			}
		}
	}

}
