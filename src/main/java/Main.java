import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author baxue
 */
public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static String lic;
    private static String v1;
    private static String v2;
    private static String v3;
    private static String dbName;


    /**
     * pt-854-903-r1
     */
    public static void main(String[] args) throws Exception {
        logger.info("begin");

        if (args.length == 0) {
            logger.error("input like:pt-854-903-r1");
            return;
        }

        logger.debug(args[0]);
        String[] paras = args[0].split("-");
        if (paras.length != 4) {
            logger.error("input like:pt-854-903-r1");
            return;
        }


        lic = paras[0];
        v1 = paras[1];
        v2 = paras[2];
        v3 = paras[3];

        dbName = lic + v1.charAt(v1.length() - 1) + v2 + v3;
        dbName = dbName.toUpperCase();
        //PT4903R1

        unzip();

        orattach();

        logger.info("done");
    }

    private static void orattach() throws IOException, InterruptedException {
        logger.info("begin orattach");
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "start", "orattach.bat", dbName);
        processBuilder.directory(new File("c:\\orattach\\scripts\\"));
        processBuilder.start();

    }

    /**
     * v:\relops\PTDatabase\fsdmo\FS854-903R1\FSDMO_854_903R1_ORAU.zip
     * v:\relops\PTDatabase\ptsys\PT854-903R1\PTSYS_854_903R1_ORAU.zip
     */
    private static void unzip() throws ZipException {
        String destination = "c:\\" + dbName;
        logger.debug(destination);

        String source = "v:\\relops\\PTDatabase\\";

        if (lic.equalsIgnoreCase("pt")) {
            source += "ptsys\\PT" + v1 + "-" + v2 + v3 + "\\PTSYS_" + v1 + "_" + v2 + v3 + "_ORAU.zip";
        } else if (lic.equalsIgnoreCase("fs")) {
            source += "fsdmo\\FS" + v1 + "-" + v2 + v3 + "\\FSDMO" + v1 + "_" + v2 + v3 + "_ORAU.zip";
        }

        logger.debug(source);

        ZipFile zipFile = new ZipFile(source);
        logger.info("begin unzip, may take time...");
        zipFile.extractAll(destination);
        logger.info("unzip done.");

    }
}
