package baofeng.tool.pt;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author baxue
 */
public class Main {

    public static final String USAGE = "input like:pt-854-903-r1";
    final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static String lic;
    private static String v1;
    private static String v2;
    private static String v3;
    private static String dbName;


    private static Set<String> licSet = new HashSet<String>(3);
    private static Map<String, String> licMap = new HashMap<String, String>(3);


    public static void main(String[] args) throws Exception {
        logger.info("begin install people tools");

        init(args);

        installDatabase();
//        copyPT();


        logger.info("people tools installed");
    }

    private static void copyPT() throws IOException {

        /**
         * jre
         SETUP\PsMpPIAInstall
         SETUP\PsTestFramework
         actional
         ACTIVEX
         Apps
         APPSERV
         BIN\CLIENT
         BIN\SERVER\WINX86
         CLASS
         DATA
         dict
         pgpsdk302
         secvault
         TUXEDO
         utility
         psconfig.bat
         */
        Path source = Paths.get("v:\\build\\pt\\pt855\\855-101-I1\\debug\\WINX86\\pt855-101-I1-debug\\SETUP\\PsMpPIAInstall\\");
        Path target = Paths.get("D:\\pt855-101-I1-debug\\SETUP\\PsMpPIAInstall\\");
        Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
    }

    private static void installDatabase() throws ZipException, IOException, InterruptedException {
        unzip();

        orattach();
    }

    private static void init(String[] args) {

        if (args.length == 0) {
            logger.error(USAGE);
            System.exit(1);
        }

        logger.debug(args[0]);
        String[] paras = args[0].split("-");
        if (paras.length != 4) {
            logger.error(USAGE);
            System.exit(1);
        }

        lic = paras[0];
        v1 = paras[1];
        v2 = paras[2];
        v3 = paras[3];

        dbName = lic + v1.charAt(v1.length() - 1) + v2 + v3;
        dbName = dbName.toUpperCase();

        licSet.add("qe");
        licSet.add("pt");
        licSet.add("fs");

        licMap.put("qe", "dmo");
        licMap.put("pt", "sys");
        licMap.put("fs", "dmo");

        if (!licSet.contains(lic.toLowerCase())) {
            logger.error("only support:{}", licSet);
            System.exit(1);
        }
    }

    private static void orattach() throws IOException, InterruptedException {
        logger.info("begin orattach");
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "start", "orattach.bat", dbName);
        processBuilder.directory(new File("c:\\orattach\\scripts\\"));
        processBuilder.start();

    }


    private static void unzip() throws ZipException {
        String destination = "c:\\" + dbName;
        logger.debug(destination);

        String source = buildSource();

        logger.debug(source);

        ZipFile zipFile = new ZipFile(source);
        logger.info("begin unzip, may take time...");
        zipFile.extractAll(destination);
        logger.info("unzip done.");

    }

    //v:\relops\PTDatabase\qedmo\qe855-101i1\qedmo_855_101i1_ORAU.zip
    //v:\relops\PTDatabase\ptsys\PT855-101R2\PTSYS_855_101R2_ORAU.zip
    //v:\relops\PTDatabase\ptdmo\pt855-101r2\ptdmo_855_101r2_ORAU.zip
    private static String buildSource() {
        StringBuilder source = new StringBuilder("v:\\relops\\PTDatabase\\");

        String postfix = licMap.get(lic);
        source.append(lic).append(postfix).append('\\')
                .append(lic).append(v1).append("-").append(v2).append(v3).append('\\')
                .append(lic).append(postfix).append("_").append(v1).append("_").append(v2).append(v3).append("_ORAU.zip");

        return source.toString();
    }
}
