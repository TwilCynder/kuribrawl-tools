package testPackage;

import javax.swing.JFrame;

import gamedata.DataFile;
import gamedata.GameData;

public class Test {
    public static void main(String[] args) {
        System.out.println("Test Java VSC !");

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        try (DataFile file = new DataFile("res/data.twl")){
            file.read(new GameData());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}