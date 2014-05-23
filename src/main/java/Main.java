import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;

/**
 * Created by baxue on 5/23/2014.
 */
public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static String lic;
    private static String v1;
    private static String v2;
    private static String v3;


    /**
     * pt-854-903-r1
     *
     * @param args
     */
    public static void main(String[] args) {
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


        unzip();
        logger.info("done");
    }

    /**
     * v:\relops\PTDatabase\fsdmo\FS854-903R1\FSDMO_854_903R1_ORAU.zip
     * v:\relops\PTDatabase\ptsys\PT854-903R1\PTSYS_854_903R1_ORAU.zip
     */
    private static void unzip() {
        //PT4903R1
        String destination = "c:\\" + lic + v1.charAt(v1.length() - 1) + v2 + v3;
        destination = destination.toUpperCase();
        logger.debug(destination);

        String source = "v:\\relops\\PTDatabase\\";

        if (lic.equalsIgnoreCase("pt")) {
            source += "ptsys\\PT" + v1 + "-" + v2 + v3 + "\\PTSYS_" + v1 + "_" + v2 + v3 + "_ORAU.zip";
        } else if (lic.equalsIgnoreCase("fs")) {
            source += "fsdmo\\FS" + v1 + "-" + v2 + v3 + "\\FSDMO" + v1 + "_" + v2 + v3 + "_ORAU.zip";
        }

        logger.debug(source);

        try {
            ZipFile zipFile = new ZipFile(source);
            logger.info("begin unzip, may take time...");
            zipFile.extractAll(destination);
            logger.info("unzip done.");
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
