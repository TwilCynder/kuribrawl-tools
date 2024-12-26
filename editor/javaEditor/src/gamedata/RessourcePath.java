//TODO : BACKUP PDT SAUVEGARDE
package gamedata;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.awt.Image;

import javax.imageio.ImageIO;
import KBUtil.StringHelper;
import gamedata.Animation.Defaultness;
import gamedata.EntityAnimation.EntityAnimationDefaultness;
import gamedata.exceptions.GameDataException;
import gamedata.exceptions.InvalidRessourcePathException;
import gamedata.exceptions.RessourceException;
import gamedata.exceptions.TransparentGameDataException;
import gamedata.exceptions.WhatTheHellException;
import gamedata.parsers.AnimationParser;
import gamedata.parsers.DescriptorReader;

public class RessourcePath {
    private Path path;
    private LinkOption symlinks_behavior;

    public RessourcePath(Path path)throws InvalidRessourcePathException{
        this.path = path;
        this.symlinks_behavior = LinkOption.NOFOLLOW_LINKS;
        if (!Files.exists(this.path, symlinks_behavior)){
            throw new InvalidRessourcePathException("Path does not exist");
        }

        if (!Files.isDirectory(this.path, symlinks_behavior)){
            throw new InvalidRessourcePathException("Path is not a directory");
        }
    }

    @Deprecated
    public RessourcePath(String pathname) throws InvalidRessourcePathException{
        this(getRessourcePath(pathname));
    }

    /**
     * Return the Path at which the ressources directory is located.
     * CANNOT RETURN NULL.
     * @return
     */
    public Path getPath(){
        return path;
    }

    public static Path getRessourcePath(String pathname) throws InvalidRessourcePathException {
        try {
            return Paths.get(pathname);
        } catch (InvalidPathException e){
            throw new InvalidRessourcePathException("Invalid path", e);
        }
    }

    public static Path getRessourcePathOrNull(String pathname) throws InvalidRessourcePathException{
        return (pathname == null) ? null : getRessourcePath(pathname);
    }

    @SuppressWarnings("unused")
    private InputStream fileStream(String name) throws IOException, InvalidPathException{
        return Files.newInputStream(path.resolve(name), symlinks_behavior);
    }

    private InputStream fileStream(Path path) throws IOException, InvalidPathException{
        return Files.newInputStream(this.path.resolve(path), symlinks_behavior);
    }

    private File getFile(String name) {
        return path.resolve(name).toFile();
    }

    public Path resolvePath(Path name){
        return path.resolve(name);
    }

    public Path resolvePath(String name){
        return path.resolve(name);
    }

    public boolean exists(Path name){
        return Files.exists(resolvePath(name), symlinks_behavior);
    }

    static private Path toPath(String name){
        return name == null ? null : Paths.get(name);
    }

    private BufferedReader fileReader(String filename) throws IOException, InvalidPathException, NoSuchFileException {
        return fileReader_(path.resolve(filename));
    }

    private BufferedReader fileReader(Path filepath) throws IOException, InvalidPathException, NoSuchFileException {
        return fileReader_(path.resolve(filepath));
    }

    private BufferedReader fileReader_(Path fullpath) throws IOException, InvalidPathException, NoSuchFileException{
        fullpath = fullpath.toAbsolutePath();
        return Files.newBufferedReader(fullpath);
    }

    private BufferedWriter fileWriter(String filename) throws IOException, InvalidPathException, NoSuchFileException {
        return fileWriter_(path.resolve(filename));
    }

    private BufferedWriter fileWriter(Path filepath) throws IOException, InvalidPathException, NoSuchFileException {
        return fileWriter_(path.resolve(filepath));
    }

    private BufferedWriter fileWriter_(Path fullpath) throws IOException, InvalidPathException, NoSuchFileException{
        fullpath = fullpath.toAbsolutePath();
        return Files.newBufferedWriter(fullpath);
    }

    private static void fullFrameHurtbox(Frame frame, EntityFrame entity_frame){
        entity_frame.fullFrameHurtbox(frame);
    }

    public Image loadImage(String filename)throws IOException {
        return ImageIO.read(resolvePath(filename).toFile());
    }

    public Image loadImage(Path name) throws IOException {
        return ImageIO.read(resolvePath(name).toFile());
    }

    /**
     * Adds a new animation to the given Champion based on basic information.
     * Handles loading the image.
     * @param champion
     * @param animName name of the anim to create
     * @param nbFrames number of frames in the new animation
     * @param source_filename source image filename (will be kept as property of the animation AND used to load the image right now)
     * @param descriptor_filename descriptor filename : may be null
     * @return the created EntityAnimation (which was already added to the champion)
     * @throws RessourceException if the image couldn't be opened
     */
    public <A extends Animation> A addAnimation(AnimationPool<A> domain, String animName, int nbFrames, String source_filename, String descriptor_filename) throws RessourceException{
        try {
            Image source = loadImage(source_filename);
            return domain.addAnimation(animName, source, nbFrames, source_filename, descriptor_filename);
        } catch (IOException e){
            throw new RessourceException("Couldn't read source image file " + source_filename, e);
        } catch (InvalidPathException e){
            throw new RessourceException("Descriptor path is invalid : " + descriptor_filename, e);
        } catch (NullPointerException e){
            throw new RessourceException("Null filename", e);
        }
    }

    /**
     * Adds a new animation to the specified GameData based on basic information from the text files.
     * Handles the interpretation of these informations (loading the given image file, parsing the tag, etc)
     */
    public Animation addAnimation(GameData gd, String tag, int nbFrames, String source_filename, String descriptor_filename) throws RessourceException{
        String[] tagSplit = StringHelper.split(tag, "/");

        if (tagSplit.length < 2 ) { 
            //only one field : invalid
            throw new RessourceException("Ill-formed animation file info : tag should contain at least 2 non-empty fields separated by / : (" + tag + ")");
        }

        if (tagSplit.length < 3) {
            //only two fields : assume champio
            if (!GameData.isValidIdentifier(tagSplit[0])) throw new RessourceException("Invalid domain name : " + tagSplit[0]);
            if (!GameData.isValidIdentifier(tagSplit[1])) throw new RessourceException("Invalid animation name : " + tagSplit[1]);
        
            if (tagSplit[0].startsWith("$"))
                return addAnimation(gd.tryStage(tagSplit[0].substring(1)), tagSplit[1], nbFrames, source_filename, descriptor_filename);
            return addAnimation(gd.tryChampion(tagSplit[0]), tagSplit[1], nbFrames, source_filename, descriptor_filename);
        }

        //3 fields : parse 

        if (!GameData.isValidIdentifier(tagSplit[1])) throw new RessourceException("Invalid domain name : " + tagSplit[0]);
        if (!GameData.isValidIdentifier(tagSplit[2])) throw new RessourceException("Invalid animation name : " + tagSplit[1]);

        switch (tagSplit[0]){
            case "Stage", "S" :
                return addAnimation(gd.tryStage(tagSplit[1]), tagSplit[2], nbFrames, source_filename, descriptor_filename);
            case "Champion", "Champ", "C":  
                return addAnimation(gd.tryChampion(tagSplit[1]), tagSplit[2], nbFrames, source_filename, descriptor_filename);
            default:
                throw new RessourceException("Unknown domain type : " + tagSplit[0]);
        }
    }

    private void parseAnimation(GameData gd, String file, String info) throws RessourceException, WhatTheHellException{
        String[] fields;

        fields = StringHelper.split(info, " ");

        if (fields.length < 2){
            throw new RessourceException("Ill-formed animation file info, should contain a tag and infos separated by spaces : " + info);
        }

        if (fields[1].endsWith(".dat")){ //animation has a descriptor
            parseAnimationDescriptor(gd, fields[0], file, fields[1]);
        } else { //no
            AnimationParser.parseAnimationShortDescriptor(gd, fields, this, file);
        }

    }

    private void parseAnimationDescriptor(GameData gd, String tag, String source_filename, String descriptor_filename) throws RessourceException{
        try {
            AnimationParser.parseAnimationDescriptor(gd, tag, this, source_filename, descriptor_filename, fileReader(descriptor_filename));
        } catch (FileNotFoundException ex){
            throw new RessourceException("Could not open file : " + descriptor_filename);
        }  catch (IOException ex) {
            throw new RessourceException("Error while reading descriptor file : " + descriptor_filename, ex);
        }
        
    }

    private void parseChampionDescriptor(Champion c, String filename) throws RessourceException, WhatTheHellException {
        if (filename == null) throw new RessourceException("null descriptor filename for champion " + c.getName());

        try (DescriptorReader reader = new DescriptorReader(fileReader(filename))){
            String line = reader.readLine();
            c.setDisplayName(line);
        }  catch (IOException e){
            throw new RessourceException("Couldn't descriptor file " + filename, e);
        }
    }

    public void parseChampion(GameData gd, String file, String info) throws RessourceException, WhatTheHellException{
        Champion c = gd.tryChampion(info, file);
        c.setDescriptorFilename(file);
        parseChampionDescriptor(c, file);
    }

    public void parseStage(GameData gd, String file, String info) throws RessourceException, WhatTheHellException{
        System.out.println("PARSE STAGE");
        Stage s = gd.tryStage(info, file);
        s.setDescriptorFilename(file);
        parseStageDescriptor(s, file);
    }

    private void parseStageDescriptor(Stage s, String filename)  throws RessourceException, WhatTheHellException {
        if (filename == null) throw new RessourceException("null descriptor filename for champion " + s.getName());

        try (DescriptorReader reader = new DescriptorReader(fileReader(filename))){
            String line = reader.readLine();
            s.setDisplayName(line);
        }  catch (IOException e){
            throw new RessourceException("Couldn't descriptor file " + filename, e);
        }
    }

    private void finalizeGameData(GameData gd){
        for (var champ : gd.getChampions()){
            for (var eanim : champ.getAnimations()){
                eanim.finalize(champ, gd);
            }
        }
    }

    private static final String listFilename = "project_db.txt";
    private static final Path listPath = Paths.get(listFilename);

    public GameData parseGameData() throws IOException, RessourceException, WhatTheHellException, InvalidRessourcePathException {
        try (DescriptorReader reader = new DescriptorReader(fileReader(listPath))){
            String file, info;
            String[] split;
            int line = 1;
    
            GameData gd = new GameData();
    
            while (reader.ready()){
                file = reader.readLine();
                info = reader.readLine();
    
                if (info == null) throw new RessourceException("filename without file info in files list file (" + listFilename + ")");
    
                split = info.split(":");
    
                if (split.length != 2) throw new RessourceException("File info line should contain exactly 1 : separator", listFilename, line + 1);
                line += 2;
    
                switch (split[0]){
                    case "L":
                    case "I":
                    case "X":
                        gd.addOtherFile(file, info);
                        break;
                    case "A":
                        parseAnimation(gd, file.trim(), split[1]);
                        break;
                    case "C":
                        parseChampion(gd, file.trim(), split[1]);
                        break;
                    case "S":
                        parseStage(gd, file.trim(), split[1]);
                        break;
                    
                }
            }

            finalizeGameData(gd);

            return gd;
        } catch (NoSuchFileException e){
            throw new RessourceException("The specified directory is not a valid ressource path : does not contain a project_db.txt file.", e);
        }

    }

    private static void writeString(BufferedWriter writer, String str) throws IOException{
        writer.write(str, 0, str.length());
    }

    /**
     * Class used to define code to be ran if information required to save data is missing.   
     * Each method is called for a specific missing info. If a call to these methods returns false, an exception is raised.
    */
    public static abstract class MissingInfoListener {
        public boolean missingAnimationDescriptor(RessourcePath r, Animation anim, NamedAnimationPool<?> c){return false;}
        public boolean missingChampionDescriptor(RessourcePath r, Champion c){return false;}
    }

    /**
     * Obtains the descriptor filename of a champion, calling the right method of a specified MissingInfoListener if it can't be found
     * TODO : actually use it when we can manipulate the descriptor filename of champions
     */
    private String getChampionDescriptorFilename(Champion c, MissingInfoListener mil) throws TransparentGameDataException{
        String toWrite = c.getDescriptorFilename();

        if (toWrite != null){   //if the info isn't missing in the first place
            return toWrite;     //just return it
        } else if (mil != null && mil.missingChampionDescriptor(this, c)){  //if is is, do we have a MIL and did its method supposedly succeed ?
            toWrite = c.getDescriptorFilename(); //if so, we test the info again
            if (toWrite != null) return toWrite;
        }   //if the info is actually still missing OR the method indicated that it failed OR we didn't even have a MIL

        throw new TransparentGameDataException("Champion " + c.getName() + " does not have a descriptor file. Please set one."); //just raise an exception
    }

    /**
     * Obtains the descriptor filename of an EntityAnimation, calling the right method of a specified MissingInfoListener if it can't be found
     */
    private String getAnimationDescriptorFilename(Animation anim, NamedAnimationPool<?> c, MissingInfoListener mil) throws TransparentGameDataException{
        String toWrite = anim.getDescriptorFilename();

        if (toWrite != null){   //if the info isn't missing in the first place
            return toWrite;     //just return it
        } else if (mil != null && mil.missingAnimationDescriptor(this, anim, c)){  //if is is, do we have a MIL and did its method supposedly succeed ?
            toWrite = anim.getDescriptorFilename(); //if so, we test the info again
            if (toWrite != null) return toWrite;
        }   //if the info is actually still missing OR the method indicated that it failed OR we didn't even have a MIL
        
        throw new TransparentGameDataException("Animation" + anim.getName() + " of " + c.getEntityDesignation() + " does not have a descriptor file but needs one. Please set one."); //just raise an exception
    }

    public void saveGameData(GameData gd, MissingInfoListener mil) throws GameDataException, TransparentGameDataException, IOException{
        try (BufferedWriter listWriter = fileWriter(listPath)){
            String toWrite;
            for (var file : gd.getOtherFiles()){
                System.out.println(file.getKey() + "    " + file.getValue());
                writeString(listWriter, file.getKey()); listWriter.newLine();
                writeString(listWriter, file.getValue()); listWriter.newLine();
            }

            for (Stage s : gd.getStages()){
                System.out.println("Writing champion " + s.getDisplayName() + " " + s.getDescriptorFilename());
                
                writeString(listWriter, s.getDescriptorFilename()); listWriter.newLine();
                writeString(listWriter, "S:" + s.getName()); listWriter.newLine();

                for (Animation anim : s){
                    writeString(listWriter, anim.getSourceFilename()); listWriter.newLine();
                    writeString(listWriter, "A:" + "Stage/" + s.getName() + "/" + anim.getName() + " ");

                    if (anim.areFramesDefault()){
                        writeString(listWriter, "" + anim.getNbFrames() + " " + anim.getSpeed());
                    } else {
                        toWrite = getAnimationDescriptorFilename(anim, s, mil);
                        writeString(listWriter, toWrite);

                        try (BufferedWriter descriptorWriter = fileWriter(anim.getDescriptorPath())){
                            writeString(descriptorWriter, anim.generateDescriptor());
                        }
                    }
                    listWriter.newLine();
                }
            }

            for (Champion c : gd){
                System.out.println("Writing champion " + c.getDisplayName() + " " + c.getDescriptorFilename());

                toWrite = getChampionDescriptorFilename(c, mil);

                writeString(listWriter, c.getDescriptorFilename()); listWriter.newLine();
                writeString(listWriter, "C:" + c.getName()); listWriter.newLine();

                for (EntityAnimation anim : c){
                    System.out.println("Writing animation " + anim.getName());

                    writeString(listWriter, anim.getSourceFilename()); listWriter.newLine();
                    writeString(listWriter, "A:" + c.getName() + "/" + anim.getName() + " ");

                    Defaultness defaultness = anim.getFramesDefaultness();
                    if (defaultness.needDescriptor()){

                        toWrite = getAnimationDescriptorFilename(anim, c, mil);
                        writeString(listWriter, toWrite);

                        try (BufferedWriter descriptorWriter = fileWriter(anim.getDescriptorPath())){
                            writeString(descriptorWriter, anim.generateDescriptor());
                        }
                    } else {
                        writeString(listWriter, "" + anim.getNbFrames() + " " + anim.getSpeed());
                        if (defaultness == EntityAnimationDefaultness.DEFAULT_CBOX){
                            writeString(listWriter, " c");
                        }
                    }


                    listWriter.newLine();
                }
            }
        }
    }

    public void saveGameData(GameData gd) throws GameDataException, TransparentGameDataException, IOException{
        saveGameData(gd, null);
    }

    private void copyFiles(Path origin, List<String> files) throws IOException {
        for (String filename : files){
            Files.copy(origin.resolve(filename), path.resolve(filename));
        }
    }

    public void copyUnmodifiedFiles(RessourcePath origin, GameData gd) throws IOException {
        copyFiles(origin.getPath(), gd.getUnmodifiedFilenames());
    }

    public void saveGameDataFrom(RessourcePath origin, GameData gd, MissingInfoListener mil) throws IOException, GameDataException, TransparentGameDataException{
        saveGameData(gd);
    }

    public void saveGameDataFrom(RessourcePath origin, GameData gd) throws IOException, GameDataException, TransparentGameDataException{
        saveGameData(gd);
    }

    public void saveAsArchive(List<String> files, Path dest) throws IOException {
        Files.createDirectories(dest.getParent());
        System.out.println(dest.getParent());
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(dest, symlinks_behavior, StandardOpenOption.CREATE))){
            zipFile(listPath, zos);
            for (String file : files){
                if (file == null) continue;
                zipFile(toPath(file), zos);
            }
        }
    }

    private static final int READ_BUFFER = 4096;
    public void zipFile(Path path, ZipOutputStream zos) throws IOException {
        System.out.println(path);
        zos.putNextEntry(new ZipEntry(path.toString()));

        BufferedInputStream bis = new BufferedInputStream(fileStream(path));

        byte[] buffer = new byte[READ_BUFFER];
        int read;

        while ((read = bis.read(buffer)) != -1){
            zos.write(buffer, 0, read);
        }
        zos.closeEntry();
    }

    public void restoreArchive(Path archivePath) throws IOException {
        System.out.println("Restoring " + archivePath);
        ZipInputStream zis = new ZipInputStream(Files.newInputStream(archivePath));
        for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()){
            System.out.println("Unzipping " + entry);
            Path dest = resolvePath(entry.getName());

            if (dest.startsWith(getPath())){
                System.out.println("VALID");
            }
        }
    }
}
