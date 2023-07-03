package main;

import UI.Window;

import java.awt.EventQueue;

public class Main {
    public static void main(String[] args) {
        System.out.println("Test Java VSC !");

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);

					frame.openResourcePathDialogue();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}