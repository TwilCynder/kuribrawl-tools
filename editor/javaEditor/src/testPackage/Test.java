package testPackage;

import UI.PathChooser;
import UI.Window;

import gamedata.GameData;
import gamedata.RessourcePath;
import gamedata.exceptions.InvalidRessourcePathException;
import gamedata.exceptions.RessourceException;

import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JOptionPane;

public class Test {
    public static void main(String[] args) {
        System.out.println("Test Java VSC !");

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);

					PathChooser chooser = new PathChooser(PathChooser.Mode.DIRECTORY, ".");
					Path selected = chooser.openPath(frame);

					if (selected != null){
						GameData gd;
						try {
							RessourcePath originPath = new RessourcePath(selected);
							gd = originPath.parseGameData();
							System.out.println(selected);
							frame.setGameData(gd, originPath);
						} catch (InvalidRessourcePathException | IOException e){
							JOptionPane.showMessageDialog(frame,
							"Could not read selected ressource file : " + e.getMessage(),
							"Inane error",
							JOptionPane.ERROR_MESSAGE);
						} catch (RessourceException e){
							JOptionPane.showMessageDialog(frame,
							"Could not read selected ressource file : " + e.getMessage(),
							"Inane error",
							JOptionPane.ERROR_MESSAGE);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}