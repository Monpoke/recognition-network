/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello;

import hello.recognition.RecognitionClassifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.util.Zip4jUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ImporterDataImpl implements ImporterData, InitializingBean, Runnable, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(ImporterData.class);

    private Thread thread;

    @Value("${batch.importer.directory}")
    private String directoryIN;

    @Value("${batch.importer.tmp}")
    private String directoryTMP;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("After set...");

        thread = new Thread(this);
        thread.setName("importer-data");
        thread.start();
    }

    @Override
    public void run() {

        try {
            // Creates a instance of WatchService.
            WatchService watcher = FileSystems.getDefault().newWatchService();

            // Registers the logDir below with a watch service.
            Path logDir = Paths.get(directoryIN);
            logDir.register(watcher, ENTRY_CREATE);
            log.info("Listening for " + directoryIN + " for changes.");

            // Monitor the logDir at listen for change notification.
            while (true) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (ENTRY_CREATE.equals(kind)) {

                        // on creation, start
                        Path path = (Path) event.context();

                        log.info("A file has been detected... Sleeping 5s before processing");
                        Thread.sleep(1000);

                        processFiles(path);

                    }

                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void destroy() throws Exception {
        thread.interrupt();
    }

    /**
     * Process files.
     *
     * @param file
     */
    public void processFiles(Path file) {
        try {

            String tmpDest = step1_movefiles(file);
            log.info("Extracting to " + tmpDest);

            // extract zip
            String directory = step2_unzip(tmpDest);

            // get file for index
            String indexDirectory = step3_analyse(directory);

            List<String> filesList = step4_readIndexFile(indexDirectory);

            loadClassifier(indexDirectory, filesList);

        } catch (IOException ex) {
            log.error("IO: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     *
     */
    private String step1_movefiles(Path file) throws IOException {
        String filename = directoryIN + file.getFileName().toString();

        String tmpDest = directoryTMP + file.getFileName().toString();

        // move filename to tmp
        log.info("The file will be moved to tmp directory...");
        log.info(filename + " -> " + tmpDest);
        Files.move(Paths.get(filename), Paths.get(tmpDest));

        return tmpDest;
    }

    /**
     * Loads the file.
     *
     * @param tmpDest
     */
    private String step2_unzip(String tmpDest) {

        try {
            File file = new File(tmpDest);
            ZipFile zip = new ZipFile(file);

            if (!zip.isValidZipFile()) {
                log.error("Can't process ZIP: it's not a valid archive.");
                return null;
            }

            Date date = new Date();
            String extractDir = directoryTMP + "tmp-" + date.getTime();

            zip.extractAll(extractDir);

            // extract directory...
            log.info("Well extracted...");

            Files.delete(file.toPath());
            log.info("ZIP deleted...");

            return extractDir;

        } catch (ZipException ex) {
            log.error("Zip exception: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            log.error("IO: " + ex.getMessage());
        }

        return null;
    }

    /**
     * analyse
     *
     * @param directory
     * @return
     */
    private String step3_analyse(String directory) {

        // directory analyse
        // have to read the train image
        File direct = new File(directory);
        if (!direct.isDirectory()) {
            log.error(directory + " is not a tmp directory...");
            return null;
        }

        // index file
        String indexDirec = null;

        //
        File[] files = direct.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            // well done!!
            if (file.isDirectory()) {
                log.info("-> " + file.getAbsolutePath());
                indexDirec = file.getAbsolutePath();
                break;
            }

        }

        // We have now indexfile path
        String indexDirectory = indexDirec + "/";

        return indexDirectory;
    }

    /**
     * index file
     *
     * @param index
     * @return
     */
    private List<String> step4_readIndexFile(String directory) {
        String index = directory + "train_images.txt";
        File indexFile = new File(index);
        if (!indexFile.isFile()) {
            log.error("There is no index file...");
            return null;
        }

        List<String> imgFiles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                imgFiles.add(line);

                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            log.info("Index read!");

            return imgFiles;

        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(ImporterDataImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ImporterDataImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return imgFiles;
    }

    private void loadClassifier(String basedir, List<String> filesList) {

        try {
            RecognitionClassifier recognitionClassifier = new RecognitionClassifier(basedir, filesList);
            recognitionClassifier.start();

            // remove TMP directory
            delete(new File(basedir).getParentFile());
            log.info("Removing tmp dir...");
        } 
        catch(IOException ex){
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        catch (Exception ex) {
            log.error("An exception has been thrown by recognition!! " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

}
