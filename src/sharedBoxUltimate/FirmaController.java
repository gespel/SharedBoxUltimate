package sharedBoxUltimate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import models.Abteilung;
import models.Firma;
import models.Mitarbeiter;

public class FirmaController {
	private Firma model;
	
	
	public FirmaController(Firma model) {
		this.model = model;
	}
	public void readFirmaData() {
		
	}
	public void createMitarbeiter(int id, String name, String vorname, String passwort) {
		File mitarbeiterDir = new File("Server/" + model.getName() + "/Mitarbeiter");
		if(!mitarbeiterDir.exists()) {
			mitarbeiterDir.mkdir();
		}
		File userDir = new File("Server/" + model.getName() + "/Mitarbeiter/" + name);
		if(!userDir.exists()) {
			userDir.mkdir();
			new File(userDir.getPath() + "/Files").mkdir();
			
			File userinfo = new File(userDir.getPath() + "/userinfo.csv");
			try {
				userinfo.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				FileWriter writer = new FileWriter(userDir.getPath() + "/userinfo.csv");
				writer.write(id + "," + name + "," + vorname + "," + passwort);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Mitarbeiter neu = new Mitarbeiter(id, name, vorname, passwort);
			model.addUser(neu);
		}
		
	}
	public void createAbteilung(String name) {
		File abteilungDir = new File("Server/" + model.getName() + "/Abteilungen");
		if(!abteilungDir.exists()) {
			abteilungDir.mkdir();
		}
		File groupDir = new File("Server/" + model.getName() + "/Abteilungen/" + name);
		if(!groupDir.exists()) {
			groupDir.mkdir();
			Abteilung neu = new Abteilung(name);
			model.addAbteilung(neu);
		}
	}
}
