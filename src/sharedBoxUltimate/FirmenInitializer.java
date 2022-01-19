package sharedBoxUltimate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import models.Abteilung;
import models.Firma;
import models.Mitarbeiter;

public class FirmenInitializer {
	
	public static HashMap<Firma, FirmaController> firmen = new HashMap<Firma, FirmaController>();
	
	public FirmenInitializer() {
		
	}
	
	public void readFirmen() {
		File[] dirs = new File("Server").listFiles(File::isDirectory);
		for (File datei : dirs) {
			Firma temp = new Firma(datei.getName());
			FirmaController tempcont = new FirmaController(temp);
			firmen.put(temp, tempcont);
			
			File[] mitarbeiterDirs = new File("Server/" + datei.getName() + "/Mitarbeiter").listFiles(File::isDirectory);
			for(File mitarbeiterFile : mitarbeiterDirs) {
				Mitarbeiter temp2;
				temp2 = parseMitarbeiterFile(new File(mitarbeiterFile.getPath() + "/userinfo.csv"));
				temp.addUser(temp2);
			}
			
			File[] abteilungDirs = new File("Server/" + datei.getName() + "/Abteilungen").listFiles(File::isDirectory);
			for(File abteilungFile : abteilungDirs) {
				Abteilung abt = new Abteilung(abteilungFile.getName());
				temp.addAbteilung(abt);
			}
		}
	}
	private Mitarbeiter parseMitarbeiterFile(File datei) {
		BufferedReader reader;
		Mitarbeiter mit = null;
		try {
			reader = new BufferedReader(new FileReader(datei));
			String rawLine = reader.readLine();
			reader.close();
			String[] arr = rawLine.split(",");
			mit = new Mitarbeiter(Integer.parseInt(arr[0]),arr[1], arr[2], arr[3]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mit;
	}
	public void printFirmen() {
		for(Firma a : firmen.keySet()) {
			System.out.println(a.getName());
		}
	}
	public static void createFirma(String name) {
		File firmaDir = new File("Server/" + name);
		if(!firmaDir.exists()) {
			firmaDir.mkdir();
			new File(firmaDir.getPath() + "/Mitarbeiter").mkdir();
			new File(firmaDir.getPath() + "/Abteilungen").mkdir();
			Firma neu = new Firma(name);
			firmen.put(neu, new FirmaController(neu));
		}
	}
	public Firma getFirmaByName(String name) {
		Set<Firma> bla = FirmenInitializer.firmen.keySet();
		for(Firma blabla : bla) {
			if(blabla.getName().equalsIgnoreCase(name)) {
				return blabla;
			}
		}
		return null;
	}
	public FirmaController getFirmaControllerByName(String name) {
		Set<Firma> bla = FirmenInitializer.firmen.keySet();
		for(Firma blabla : bla) {
			if(blabla.getName().equalsIgnoreCase(name)) {
				return FirmenInitializer.firmen.get(blabla);
			}
		}
		return null;
	}
}