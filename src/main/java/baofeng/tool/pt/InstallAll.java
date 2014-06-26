package baofeng.tool.pt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author baxue
 */
public class InstallAll {

    final static Logger logger = LoggerFactory.getLogger(InstallAll.class);

    public static void main(String[] args) throws Exception {
        logger.info("Begin install people tools");

        InstallDatabase.main(args);
        InstallPiaHome.main(args);
        InstallWebserver.main(args);

        logger.info("people tools installed");
    }


}
