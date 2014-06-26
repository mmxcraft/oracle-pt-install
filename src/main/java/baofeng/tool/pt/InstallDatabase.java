package baofeng.tool.pt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by baxue on 6/26/2014.
 */
public class InstallDatabase {
    final static Logger logger = LoggerFactory.getLogger(InstallDatabase.class);

    public static void main(String[] args) throws Exception {
        InstallInit.main(args);

        if (dbInstalled()) {
            logger.info("dbInstalled, skip");
            return;
        }

        unzipDatabase();

        orattach();
    }

    private static boolean dbInstalled() throws Exception {

        String OracleServiceName = "OracleService" + InstallInit.dbName;

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "sc query " + OracleServiceName);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        while ((line = br.readLine()) != null) {
            if (line.contains(OracleServiceName)) {
                logger.debug(line);
                return true;
            }
        }

        return false;
    }

    private static void orattach() throws IOException, InterruptedException {
        logger.info("Begin orattach");
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "start", "orattach.bat", InstallInit.dbName);
        processBuilder.directory(new File("c:\\orattach\\scripts\\"));
        processBuilder.start();

    }

    private static void unzipDatabase() {
        String destination = "c:\\" + InstallInit.dbName;

        String source = buildSource();

        logger.debug("from:{}", source);
        logger.debug("zip to:{}", destination);


        logger.info("Begin unzipDatabase, may take time...");

        ExpandTask expander = new ExpandTask();
        expander.setSrc(new File(source));
        expander.setDest(new File(destination));
        expander.execute();

        logger.info("unzipDatabase done.");

    }

    //v:\relops\PTDatabase\ptdmo\pt855-101r2\ptdmo_855_101r2_ORAU.zip
    private static String buildSource() {
        StringBuilder source = new StringBuilder("v:\\relops\\PTDatabase\\");

        String postfix = InstallInit.licMap.get(InstallInit.lic.toLowerCase());
        source.append(InstallInit.lic).append(postfix).append('\\')
                .append(InstallInit.lic).append(InstallInit.v1).append("-").append(InstallInit.v2).append(InstallInit.v3).append('\\')
                .append(InstallInit.lic).append(postfix).append("_").append(InstallInit.v1).append("_").append(InstallInit.v2).append(InstallInit.v3).append("_ORAU.zip");

        return source.toString();
    }
}
