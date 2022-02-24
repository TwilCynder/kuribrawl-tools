package testPackage;

import UI.PathChooser;
import UI.Window;

import gamedata.GameData;
import gamedata.RessourcePath;
import gamedata.exceptions.InvalidRessourcePathException;

import java.awt.EventQueue;
import java.nio.file.Path;

public class Test {
    public static void main(String[] args) {
        System.out.println("Test Java VSC !");

        GameData gd;

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);

					PathChooser chooser = new PathChooser(PathChooser.Mode.DIRECTORY, ".");
					Path selected = chooser.showDialog(frame);

					if (selected != null){
						GameData gd = new RessourcePath(selected).parseGameData();

						System.out.println(selected);
	
						frame.setGameData(gd);
					}

				} catch (InvalidRessourcePathException e){
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}