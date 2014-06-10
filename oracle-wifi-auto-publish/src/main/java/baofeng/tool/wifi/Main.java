package baofeng.tool.wifi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author baxue
 * @date 2014/5/27.
 */
public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws Exception {

        Spider.sso_id = System.getProperty("sso_id");
        Spider.sso_pass = System.getProperty("sso_pass");

        if (Spider.sso_id == null || Spider.sso_pass == null) {
            logger.error("must use -Dsso_id=abc -Dsso_pass=bcd");
            System.exit(1);
        }

        for (int i = 0; i < 10; ) {

            try {
                doTask();
            } catch (Exception e) {
                logger.error("e:{}", e);
            }

            logger.debug("password still same, keep getting after 10 minutes, tried times:{}", ++i);
            Thread.sleep(1000 * 60 * 10);
        }
    }

    private static void doTask() throws Exception {

        Spider.fetchPassword();

        String savedPassword = PersistantManager.getSavedPassword();


        if (!Spider.currentPassword.equals(savedPassword)) {
            logger.debug("got new password:{},save and pubish then quit.", Spider.currentPassword);

            PersistantManager.saveToFile(Spider.currentPassword);
            Publisher.publish(Spider.currentPassword);

            System.exit(0);
        }
    }

}
