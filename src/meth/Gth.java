package meth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.LimitedFiringSource;
import ptolemy.data.DoubleToken;
import ptolemy.data.StringToken;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class Gth extends LimitedFiringSource {

	private native void grassmanTH(String A, String B, String c);
	
	public TypedIOPort input;
	public TypedIOPort out;
	static int execution = 1;

	// Declare a native method grassmanTH() that receives 2 arguments (Filename
	// and extension ) and returns a file
	
	// -Djava.library.path="C:\Users\Altec\source\repos\Projet3\x64\Debug"

	
	
	public Gth(CompositeEntity container, String name) throws NameDuplicationException, IllegalActionException {
		super(container, name);
		
		
		_firingCountLimit=1;
		
		input = new TypedIOPort(this, "input", true, false);
		
		out = new TypedIOPort(this, "out", false, true);
		
		input.setMultiport(true);
		input.setAutomaticTypeConversion(true);
		
		output.setTypeEquals(BaseType.DOUBLE);
		out.setTypeEquals(BaseType.DOUBLE);
		input.setTypeEquals(BaseType.STRING);
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void fire() throws IllegalActionException {
		// TODO Auto-generated method stub
		super.fire();
		
		String fichier = ((StringToken) input.get(0)).toString();
		fichier = fichier.substring(1, fichier.length() - 1);
		File f = new File("Projet3.dll");
		try {
		System.load(f.getAbsolutePath());
		if(f.exists()!=true) JOptionPane.showMessageDialog(null, "Fichier Dll introuvable");
		String a = fichier+".Rii";
		String b = fichier+".sz";
		String c = fichier+".pi";
		
		if (execution == 1 ) new Gth(_container, "GTH").grassmanTH(a, b, c);
		execution ++;	
			File file = new File(c);
			if (file.exists() != true)
				JOptionPane.showMessageDialog(null, "Fichier introuvable");
			else {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line ="";
				line = br.readLine();
				Vector<Double> T = new Vector<Double>();;
				while(line!=null){
					T.add(Double.valueOf(line));
					line= br.readLine();
				}
				for(int i=1; i<=T.size();i++) out.send(0, new DoubleToken(T.get(i-1).doubleValue()));
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NameDuplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	
}
