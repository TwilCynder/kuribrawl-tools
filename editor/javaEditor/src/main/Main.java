package main;

import UI.Window;

import java.awt.EventQueue;

public class Main {
    public static void main(String[] args) {

		String path = args.length > 0 ? args[0] : System.getProperty("user.dir");
        System.out.println("Working Directory = " + path);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);


					frame.openResourcePathDialogue(path);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}