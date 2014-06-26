package baofeng.tool.pt;

import org.apache.tools.ant.types.FileSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static String piaHome;
    private static String v11;


    public static void main(String[] args) throws Exception {
        logger.info("begin install people tools");

        init(args);

        installDatabase();
        copyPT();
        installPIA();


        logger.info("people tools installed");
    }

    private static void installPIA() throws Exception {
        logger.info("begin install PIA");

        int jslPort = Integer.parseInt(v11) * 10000 + Integer.valueOf(v2) * 10 + 2;

        int httpPort = jslPort + 2;

        String repText = "PIA_HOME=" + piaHome + "\n" +
                "PS_CFG_HOME=" + piaHome + "\n" +
                "DOMAIN_NAME=peoplesoft\n" +
                "SERVER_TYPE=weblogic\n" +
                "BEA_HOME=C:/oracle/wls1212\n" +
                "USER_ID=system\n" +
                "USER_PWD=Passw0rd\n" +
                "USER_PWD_RETYPE=Passw0rd\n" +
                "INSTALL_ACTION=CREATE_NEW_DOMAIN\n" +
                "DOMAIN_TYPE=NEW_DOMAIN\n" +
                "INSTALL_TYPE=SINGLE_SERVER_INSTALLATION\n" +
                "WEBSITE_NAME=ps\n" +
                "JSL_PORT=" + jslPort + "\n" +
                "HTTP_PORT=" + httpPort + "\n" +
                "HTTPS_PORT=443\n" +
                "AUTH_DOMAIN=\n" +
                "WEB_PROF_NAME=DEV\n" +
                "WEB_PROF_PWD=PTWEBSERVER\n" +
                "WEB_PROF_PWD_RETYPE=PTWEBSERVER\n" +
                "IGW_USERID=administrator\n" +
                "IGW_PWD=Passw0rd\n" +
                "IGW_PWD_RETYPE=Passw0rd\n" +
                "APPSRVR_CONN_PWD=Passw0rd\n" +
                "APPSRVR_CONN_PWD_RETYPE=Passw0rd\n" +
                "REPORTS_DIR=C:/psreports\n";

        Path saved = saveToFile(repText);

        String setupCommand = piaHome + "\\SETUP\\PsMpPIAInstall\\setup.bat  -i silent -DRES_FILE_PATH=" + saved.toAbsolutePath();
        logger.debug("pia setup command:{}", setupCommand);

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", setupCommand);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        while ((line = br.readLine()) != null) {
            logger.debug(line);
        }


        logger.info("PIA installed");
    }

    static Path saveToFile(String password) {
        Path file = Paths.get(piaHome + "\\setup.rep");
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(password);
        } catch (IOException x) {
            Main.logger.error("{}", x);
        }
        logger.debug("saveToFile:{}", file);
        return file;
    }

    private static void copyPT() throws IOException {

        if (ptCopied()) {
            logger.info("ptCopied, skip. can use ptcopy instead");
            return;
        }

        CopyTask copyTask = new CopyTask();
        copyTask.setTodir(new File(piaHome));

        FileSet fileSet = new FileSet();
        String src = "v:\\build\\pt\\pt" + v1 + "\\" + v1 + "-" + v2 + "-" + v3 +
                "\\debug\\WINX86\\" + lic + v1 + "-" + v2 + "-" + v3 + "-" + "debug";

        logger.info("begin copy from'{}' to '{}'", src, piaHome);
        fileSet.setDir(new File(src));
        fileSet.setIncludes("jre\\**,SETUP\\PsMpPIAInstall\\**,SETUP\\PsTestFramework\\**,actional\\**,ACTIVEX\\**,Apps\\**,APPSERV\\**,BIN\\CLIENT\\**,BIN\\SERVER\\WINX86\\**,CLASS\\**,DATA\\**,dict\\**,pgpsdk302\\**,secvault\\**,TUXEDO\\**,utility\\**,psconfig.bat");
        copyTask.addFileset(fileSet);
        copyTask.execute();
        logger.info("copy done");
    }

    private static boolean ptCopied() {
        File file = new File(piaHome);
        return file.exists();
    }


    private static void installDatabase() throws Exception {

        if (dbInstalled()) {
            logger.info("dbInstalled, skip");
            return;
        }


        unzipDatabase();

        orattach();
    }

    private static boolean dbInstalled() throws Exception {

        String OracleServiceName = "OracleService" + dbName;

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
        v11 = String.valueOf(v1.charAt(2));
        v2 = paras[2];
        v3 = paras[3];

        dbName = lic + v11 + v2 + v3;
        dbName = dbName.toUpperCase();
        piaHome = "D:\\" + lic + v1 + "-" + v2 + "-" + v3 + "-" + "debug";


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


    private static void unzipDatabase() {
        String destination = "c:\\" + dbName;
        logger.debug(destination);

        String source = buildSource();

        logger.debug(source);

        logger.info("begin unzipDatabase, may take time...");

        ExpandTask expander = new ExpandTask();
        expander.setSrc(new File(source));
        expander.setDest(new File(destination));
        expander.execute();

        logger.info("unzipDatabase done.");

    }

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
