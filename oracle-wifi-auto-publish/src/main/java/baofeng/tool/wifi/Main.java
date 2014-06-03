package baofeng.tool.wifi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author baxue
 * @date 2014/5/27.
 */
public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static final String LOGIN_URI_PREFIX = "https://login.oracle.com";

    private static String agent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

    private static String wifiURI = "https://gmp.oracle.com/captcha/files/airespace_pwd_apac.txt?_dc=1401161841661";
    private static String sso_id;
    private static String sso_pass;


    public static void main(String[] args) throws Exception {

        sso_id = System.getProperty("sso_id");
        sso_pass = System.getProperty("sso_pass");

        if (sso_id == null || sso_pass == null) {
            logger.error("must use -Dsso_id=abc -Dsso_pass=bcd");
            System.exit(1);
        }

        Timer timer = new java.util.Timer(false);

        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    doTask();
                } catch (IOException e) {
                    logger.error("{}", e);
                }
            }
        };
        timer.schedule(task, new Date(), 1000 * 60 * 60 * 8);

    }

    private static void doTask() throws IOException {
        String password = getPasswordInternal();
        System.setProperty("http.proxyHost", "146.56.234.217");
        System.setProperty("http.proxyPort", "80");
        Jsoup.connect("http://2.oracle-bj-wifi.appspot.com/?p=" + password).get();
    }

    private static String getPasswordInternal() throws IOException {
        logger.debug("wifiURI\n{}", wifiURI);
        Document doc = Jsoup.connect(wifiURI).userAgent(agent).get();

        Elements form = doc.select("form");
        String ssoURI = form.attr("action");
        Elements inputs = form.select("input");


        Map<String, String> params = new HashMap<String, String>(inputs.size());
        for (Element input : inputs) {
            params.put(input.attr("name"), input.attr("value"));
        }


        logger.debug("ssoURI\n{}", ssoURI);
        doc = Jsoup.connect(ssoURI).userAgent(agent).data(params).post();

        form = doc.select("form[name=MLoginForm]");
        ssoURI = form.attr("action");
        inputs = form.select("input");
        params = new HashMap<String, String>(inputs.size());
        for (Element input : inputs) {
            params.put(input.attr("name"), input.attr("value"));
        }


        params.put("ssousername", sso_id);
        params.put("password", sso_pass);

        logger.debug("ssoURI\n{}", ssoURI);
        doc = Jsoup.connect(LOGIN_URI_PREFIX + ssoURI).userAgent(agent).data(params).post();
        logger.debug(doc.outerHtml());
        Elements body = doc.select("body");
        String text = body.text();
        text = text.substring(text.indexOf("Generated") - 9);
        logger.info(text);
        return text.substring(0, 8);
    }
}
