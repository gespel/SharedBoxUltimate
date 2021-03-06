/**
 * The most important controller class. It controlls the Mitarbeiter model.
 */
package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import models.Abteilung;
import models.Mitarbeiter;
import sharedBoxUltimate.Logger;

public class MitarbeiterController {
	Mitarbeiter model;
	public MitarbeiterController(Mitarbeiter model) {
		this.model = model;
	}
	/**
	 * uploads a file to the relative path of the current user
	 * @param in
	 * @param dest
	 */
	public void uploadFile(File in, String dest) {
		long fs = 0;
		try{
		fs = Files.size(in.toPath())/(1024*1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(fs > 5) {
			JOptionPane.showMessageDialog(null, "Die Datei überschreitet die Maximalgröße von 5 MB.", "Dateigröße Überschritten", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(countSize(Paths.get(model.getUserPath()))/(1024*1024) + fs > 10) {
			JOptionPane.showMessageDialog(null, "Der Upload würde die Speicherplatzbegrenzung von 10 MB überschreiten.", "Speicherplatzbegrenzung Überschritten", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try {
			Files.copy(in.toPath(), new File(model.getUserPath() + "/" + dest + "/" + in.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
			Logger.log(this.model, "Uploaded " + in.getName(), new File(model.getUserPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * delets a file at the given path. This path is relative to the current user path
	 * @param name
	 */
	public void deleteFileByName(String name) {
		File del = new File(model.getUserPath() + "/" + name);
		if(del.isDirectory()) {
			deleteFolder(del);
			Logger.log(this.model, "Deleted " + name, new File(model.getUserPath()));
		}
		else {
			del.delete();
			Logger.log(this.model, "Deleted " + name, new File(model.getUserPath()));
	
		}
	}
	/**
	 * recursive function to delete a folder with all its contents
	 *
	 * @param folder
	 */
	public void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files != null) {
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	/**
	 * Copys a file from the give src path to the dest path. Both paths will be relative
	 * @param src
	 * @param dest
	 */
	public void copyFileByName(String src, String dest) {
		File srcFile = new File(model.getUserPath() + "/" + src);
		File destFile = new File(model.getUserPath() + "/" + dest);
		try {
			if(srcFile.isDirectory()) {
				Files.copy(srcFile.toPath(), destFile.toPath());
				for(File f : srcFile.listFiles()) {
					Files.copy(f.toPath(), new File(model.getUserPath() + "/" + dest + "/" + f.getName()).toPath());
					Logger.log(this.model, "Copied " + srcFile.getPath() + " to " + destFile.getPath(), new File(destFile.getParent()));
				}
			}
			else {
				Files.copy(srcFile.toPath(), destFile.toPath());
				Logger.log(this.model, "Copied " + srcFile.getPath() + " to " + destFile.getPath(), new File(destFile.getParent()));
			}
		} catch (FileAlreadyExistsException e) {
			System.out.println("Die Datei existiert bereits! Überspringe...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Same as copy but a moving function
	 * @param src
	 * @param dest
	 */
	public void moveFileByName(String src, String dest) {
		File srcFile = new File(model.getUserPath() + "/" + src);
		File destFile = new File(model.getUserPath() + "/" + dest);
		try {
			Files.move(srcFile.toPath(), destFile.toPath());
			Logger.log(this.model, "Copied " + srcFile.getPath() + " to " + destFile.getPath(), new File(destFile.getParent()));
		} catch (FileAlreadyExistsException e) {
			System.out.println("Die Datei existiert bereits! Überspringe...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * same as move but to the same absolute path
	 * @param src
	 * @param name
	 */
	public void renameFile(String src, String name) {
		File srcFile = new File(model.getUserPath() + "/" + src);
		File destFile = new File(srcFile.getParent() + "/" + name);
		try {
			Files.move(srcFile.toPath(), destFile.toPath());
			Logger.log(this.model, "Copied " + srcFile.getPath() + " to " + destFile.getPath(), new File(destFile.getParent()));
		} catch (FileAlreadyExistsException e) {
			System.out.println("Die Datei existiert bereits! Überspringe...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * uploads a directory with all its contents to the dest path
	 * @param in
	 * @param dest
	 */
	public void uploadDir(File in, String dest) {
		try {
			Files.walk(Paths.get(in.getAbsolutePath())).forEach(source -> { Path destination = Paths.get(model.getUserPath() + "/" + dest + "/" + in.getName(), source.toString().substring(in.getAbsolutePath().length()));
				try {
					Files.copy(source, destination);
					Logger.log(model, "Uploaded " + source.toFile().getAbsolutePath() + " to " + destination.toFile().getAbsolutePath(), new File(model.getUserPath() + "/" + dest));
				} catch(FileAlreadyExistsException e) {
					System.out.println("Dieser Ordner existiert bereits!");
				} catch(IOException e) {
					e.printStackTrace();
				} 
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Creates a directory at the given dest path
	 * @param dest
	 */
	public void mkdir(String dest) {
		File destFile = new File(model.getUserPath() + "/" + dest);
		if(!destFile.exists()) {
			destFile.mkdir();
		}
		else {
			System.out.println("Ziel existert bereits!");
		}
	}
	
	/**
	 * returns all userfiles in a Set of files
	 * @param s
	 * @return
	 */
	public Set<File> getUserFiles(String s) {
		File userDir = new File(model.getUserPath() + "/" + s);
		Set<File> out = new HashSet<File>();
		for(File f : userDir.listFiles()) {
			out.add(f);
		}
		return out;
	}
	
	/**
	 * returns all files of the given Abteilung at the specified path
	 * @param a
	 * @param s
	 * @return
	 */
	public Set<File> getAbteilungFiles(Abteilung a, String s) {
		Set<File> out = new HashSet<File>();
		File abteilungDir = new File("Server/" + model.getFirmaName() + "/Abteilungen/" + a.getName() + "/" + s);
		for(File f : abteilungDir.listFiles()) {
			out.add(f);
		}
		return out;
	}
	/**
	 * returns all shared files
	 * @return
	 */
	public Set<File> getSharedFiles() {
		BufferedReader reader;
		Set<File> out = new HashSet<File>();
		try {
			reader = new BufferedReader(new FileReader(new File("Server/" + model.getFirmaName() + "/Mitarbeiter/" + model.getName() + "/geteilte.txt")));
			String currentLine;
		    while((currentLine = reader.readLine()) != null) {
		    	File tmp = new File(currentLine);
		    	if(tmp.exists()) {
		    		for(File f : tmp.listFiles()) {
		    			out.add(f);
		    		}
		    	}
		    }
		    reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
	    } catch (IOException e) {
			e.printStackTrace();
		}

	    return out;
	}
	/**
	 * Just for debugging purposes
	 * @param s
	 * @return
	 */
	public String getUserFilesAsString(String s) {
		String out = "";
		File userDir = new File(model.getUserPath() + s);
		for(File f : userDir.listFiles()) {
			out += f.getName() + " "; 
		}
		return out;
	}
	/**
	 * retuns Abteilung instance given its name
	 * @param name
	 * @return
	 */
	public Abteilung getAbteilungByName(String name) {
		for(Abteilung x : model.getAbteilungen()) {
			if(x.getName().equals(name)) {
				return x;
			}
		}
		return null;
	}
	/**
	 * Adds a new Abteilung to the User and creates the corrosponding file structure
	 * @param in
	 */
	public void addAbteilung(Abteilung in) {
		File config = new File("Server/" + model.getFirmaName() + "/Mitarbeiter/" + model.getName() + "/abteilung.csv"); //Hier gehts weiter
		BufferedWriter bw = null;
		BufferedReader reader = null;
		boolean isNew = true;

		try {
			reader = new BufferedReader(new FileReader(config));
			String rawLine = reader.readLine();
			if(rawLine != null) {
				String[] arr = rawLine.split(",");
				for(int i = 0; i < arr.length; i++) {
					if(arr[i].equals(in.getName())) {
						isNew = false;
					}
				}
			}
			reader.close();
			if(isNew) {
				FileWriter fw = null;
				try {
					fw = new FileWriter(config);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				bw = new BufferedWriter(fw);
				bw.write(rawLine + "," + in.getName());
				bw.close();
			}
			model.addAbteilung(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * NOT FINISHED. Supposed to remove the Abteilung from the Mitarbeiter
	 * @param in
	 */
	public void removeAbteilung(Abteilung in) {
		model.removeAbteilung(in);
	}
	/**
	 * Used to create the sharing link between two users.
	 * @param dst
	 */
	public void shareDirectory(Mitarbeiter dst) {
		File config = new File("Server/" + model.getFirmaName() + "/Mitarbeiter/" + dst.getName() + "/geteilte.txt");
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(config);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		bw = new BufferedWriter(fw);
		try {
			bw.write(model.getUserPath() + "\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * setters that use the updateUser() to keep the size low ;)
	 * @param in
	 */
	public void setOp(boolean in) {
		updateUser(model.getId(), model.getName(), model.getVorname(), model.getEmail(), model.getPasswort(), model.getUserPath(), in);
		model.setOp(in);
	}
	public void setId(int in) {
		updateUser(in, model.getName(), model.getVorname(), model.getEmail(), model.getPasswort(), model.getUserPath(), model.isOp());
		model.setId(in);
	}
	public void setName(String in) {
		updateUser(model.getId(), in, model.getVorname(), model.getEmail(), model.getPasswort(), model.getUserPath(), model.isOp());
		model.setName(in);
	}
	public void setEmail(String in) {
		updateUser(model.getId(), model.getName(), model.getVorname(), in, model.getPasswort(), model.getUserPath(), model.isOp());
		model.setEmail(in);
	}
	public void setPasswort(String in) {
		updateUser(model.getId(), model.getName(), model.getVorname(), model.getEmail(), in, model.getUserPath(), model.isOp());
		model.setPasswort(in);
	}
	public void setVorname(String in) {
		updateUser(model.getId(), model.getName(), in, model.getEmail(), model.getPasswort(), model.getUserPath(), model.isOp());
		model.setVorname(in);
	}
	
	/**
	* Returns the sum of the size of all Files in a given directory.
	* @param dir
	* @return s
	*/
	public long countSize(Path dir) {
		long s = 0;
		for(File i : dir.toFile().listFiles()) {
			if(Files.isRegularFile(i.toPath())) {
				try {
					s += Files.size(i.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
			else if(Files.isDirectory(i.toPath())) {
				s += countSize(i.toPath());
			}
		}
		return s;
	}
	
	/**
	 * updates the Userfile and the current instance of Mitarbeiter
	 * @param id
	 * @param name
	 * @param vorname
	 * @param email
	 * @param passwort
	 * @param userPath
	 * @param op
	 */
	private void updateUser(int id, String name, String vorname, String email, String passwort, String userPath, boolean op) {
		File useri = new File("Server/" + model.getFirmaName() + "/Mitarbeiter/" + model.getName() + "/userinfo.csv");
		useri.delete();
		useri = new File("Server/" + model.getFirmaName() + "/Mitarbeiter/" + model.getName() + "/userinfo.csv");
		try {
			//useri.createNewFile();
			BufferedWriter bw = null;
			FileWriter fw = null;
			fw = new FileWriter(useri);
			bw = new BufferedWriter(fw);
			if(op == true) {
				bw.write(id + "," + name + "," + vorname + "," + email + "," + passwort + "," + "Server/" + model.getFirmaName() + "/Mitarbeiter/" + name + "/Files" + ",true");
			}
			else {
				bw.write(id + "," + name + "," + vorname + "," + email + "," + passwort + "," + "Server/" + model.getFirmaName() + "/Mitarbeiter/" + name + "/Files" + ",false");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
