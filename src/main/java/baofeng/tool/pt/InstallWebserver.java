package baofeng.tool.pt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by baxue on 6/26/2014.
 */
public class InstallWebserver {

    final static Logger logger = LoggerFactory.getLogger(InstallWebserver.class);

    public static void main(String args[]) throws Exception {

        InstallInit.main(args);

        logger.info("Begin install Webserver");

        if (piaInstalled()) {
            logger.info("Webserver Installed, skip");
            return;
        }

        int jslPort = Integer.parseInt(InstallInit.v11) * 10000 + Integer.valueOf(InstallInit.v2) * 10 + 2;

        int httpPort = jslPort + 2;

        String repText = "PIA_HOME=" + InstallInit.piaHome + "\n" +
                "PS_CFG_HOME=" + InstallInit.piaHome + "\n" +
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

        String setupCommand = InstallInit.piaHome + "\\SETUP\\PsMpPIAInstall\\setup.bat  -i silent -DRES_FILE_PATH=" + saved.toAbsolutePath();
        logger.debug("pia setup command:{}", setupCommand);

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", setupCommand);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File("d:/"));

        Process process = processBuilder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        while ((line = br.readLine()) != null) {
            logger.debug(line);
        }


        logger.info("Webserver installed");
    }

    private static boolean piaInstalled() {
        File file = new File(InstallInit.piaHome + "\\webserv");
        return file.exists();
    }

    static Path saveToFile(String password) {
        Path file = Paths.get(InstallInit.piaHome + "\\setup.rep");
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(password);
        } catch (IOException x) {
            logger.error("{}", x);
        }
        logger.debug("saveToFile:{}", file);
        return file;
    }
}
