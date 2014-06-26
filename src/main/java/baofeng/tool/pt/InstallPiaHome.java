package baofeng.tool.pt;

import org.apache.tools.ant.types.FileSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by baxue on 6/26/2014.
 */
public class InstallPiaHome {

    final static Logger logger = LoggerFactory.getLogger(InstallPiaHome.class);

    public static void main(String[] args) throws IOException {

        InstallInit.main(args);

        if (ptCopied()) {
            logger.info("ptCopied, skip. If copy more files we can use ptcopy");
            return;
        }

        CopyTask copyTask = new CopyTask();
        copyTask.setTodir(new File(InstallInit.piaHome));

        FileSet fileSet = new FileSet();
        String src = "v:\\build\\pt\\pt" + InstallInit.v1 + "\\" + InstallInit.v1 + "-" + InstallInit.v2 + "-" + InstallInit.v3 +
                "\\" + InstallInit.debug + "\\WINX86\\" + InstallInit.lic + InstallInit.v1 + "-" + InstallInit.v2 + "-" + InstallInit.v3 + "-" + InstallInit.debug;

        logger.info("Begin copy from\n{}\nto\n{}", src, InstallInit.piaHome);
        fileSet.setDir(new File(src));
        fileSet.setIncludes("jre\\**,SETUP\\PsMpPIAInstall\\**,SETUP\\PsTestFramework\\**,actional\\**,ACTIVEX\\**,Apps\\**,APPSERV\\**,BIN\\CLIENT\\**,BIN\\SERVER\\WINX86\\**,CLASS\\**,DATA\\**,dict\\**,pgpsdk302\\**,secvault\\**,TUXEDO\\**,utility\\**,psconfig.bat");
        copyTask.addFileset(fileSet);
        copyTask.execute();
        logger.info("copy done");
    }

    private static boolean ptCopied() {
        File file = new File(InstallInit.piaHome);
        return file.exists();
    }
}
