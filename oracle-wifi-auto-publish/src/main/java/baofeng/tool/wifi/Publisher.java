package baofeng.tool.wifi;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author baxue
 * @date 2014/6/10.
 */
public class Publisher {
    public static final int RETRY_TIMES = 10;
    final static Logger logger = LoggerFactory.getLogger(Publisher.class);

    static void publish(String password) throws Exception {

        System.setProperty("http.proxyHost", "146.56.234.217");
        System.setProperty("http.proxyPort", "80");

        String url = "http://2.oracle-bj-wifi.appspot.com/?p=" + password;
        httpGet(url, RETRY_TIMES);

        url = "http://baofeng.im/p/" + password;
        httpGet(url, RETRY_TIMES);
    }

    private static void httpGet(String url, int retryTimes) throws Exception {
        try {
            Jsoup.connect(url).get();
        } catch (IOException e) {
            logger.error("url:{},times:{},e:{}", url, retryTimes, e);
            if (retryTimes > 0) {
                Thread.sleep(1000 * 60 * 5);
                httpGet(url, --retryTimes);
            }
        }
        logger.debug("publish:{}", url);
    }
}
