package testPackage;

import UI.Window;

import gamedata.GameData;
import gamedata.RessourcePath;
import gamedata.exceptions.InvalidRessourcePathException;

import java.awt.EventQueue;

import javax.swing.JFileChooser;

public class Test {
    public static void main(String[] args) {
        System.out.println("Test Java VSC !");

        GameData gd;

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

		try {
			RessourcePath p = new RessourcePath("res");
			gd = p.parseGameData();

			
			
		} catch (InvalidRessourcePathException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);
					
					GameData gd;

					JFileChooser chooser = new JFileChooser((String)null);

					chooser.showOpenDialog(frame);

					frame.setGameData(new GameData());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}