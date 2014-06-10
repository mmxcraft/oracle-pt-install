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
    final static Logger logger = LoggerFactory.getLogger(Publisher.class);

    static void publish(String password) throws IOException {

        System.setProperty("http.proxyHost", "146.56.234.217");
        System.setProperty("http.proxyPort", "80");

        String url = "http://2.oracle-bj-wifi.appspot.com/?p=" + password;
        httpGet(url);

        url = "http://baofeng.im/?p=" + password;
        httpGet(url);
    }

    private static void httpGet(String url) throws IOException {
        try {
            Jsoup.connect(url).get();
        } catch (IOException e) {
            logger.error("e:{}", e);
        }
        logger.debug("publish:{}", url);
    }
}
