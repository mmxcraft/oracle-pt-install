package baofeng.tool.wifi;

import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author baxue
 * @date 2014/5/27.
 */
public class Main {

    public static final String LOGIN_URI_PREFIX = "https://login.oracle.com";
    public static final String PASSWORD_TXT = "password.txt";
    final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String agent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

    private static final String wifiURI = "https://gmp.oracle.com/captcha/files/airespace_pwd_apac.txt?_dc=1401161841661";
    private static String sso_id;
    private static String sso_pass;
    private static String currentPassword;
    private static Date generatedAt;


    public static void main(String[] args) throws Exception {

        sso_id = System.getProperty("sso_id");
        sso_pass = System.getProperty("sso_pass");

        if (sso_id == null || sso_pass == null) {
            logger.error("must use -Dsso_id=abc -Dsso_pass=bcd");
            System.exit(1);
        }

        while (true) {

            try {
                doTask();
            } catch (Exception e) {
                logger.debug("e:{}", e);
            }
        }
    }

    private static void doTask() throws Exception {

        getPasswordAndTimeInternal();

        String savedPassword = getSavedPassword();
        logger.debug("savedPassword:{}", savedPassword);

        Date nextDay = DateUtils.addDays(generatedAt, 1);

        if (!currentPassword.equals(savedPassword)) {
            logger.debug("got new password:{}", currentPassword);

            saveToFile(currentPassword);
            publish(currentPassword);
            /*
            * Generated at Sun Jun 8 19:00:02 2014 UTC
            * next=Generated+24hour
            * sleep=next-current
            * */
            logger.debug("sleep to:{}", nextDay);
            Thread.sleep(nextDay.getTime() - System.currentTimeMillis());

        } else {
            logger.debug("still is old password:{}, will try after 1 hour", currentPassword);
            Thread.sleep(1000 * 60 * 60);
        }
    }

    private static String getSavedPassword() {
        String savedPassword = null;
        Path file = Paths.get(PASSWORD_TXT);
        if (!file.toFile().exists()) return "";

        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            savedPassword = reader.readLine();
        } catch (IOException x) {
            logger.error("{}", x);
        }
        return savedPassword;
    }

    private static void publish(String password) throws IOException {

        System.setProperty("http.proxyHost", "146.56.234.217");
        System.setProperty("http.proxyPort", "80");

        String url = "http://2.oracle-bj-wifi.appspot.com/?p=" + password;
        Jsoup.connect(url).get();
        logger.debug("publish:{}", url);

        url = "http://baofeng.im/?p=" + password;
        Jsoup.connect(url).get();
        logger.debug("publish:{}", url);
    }

    private static void saveToFile(String password) {
        Path file = new File(PASSWORD_TXT).toPath();
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(password);
        } catch (IOException x) {
            logger.error("{}", x);
        }
        logger.debug("saveToFile:{}", file);
    }

    private static void getPasswordAndTimeInternal() throws Exception {
        Document doc = Jsoup.connect(wifiURI).userAgent(agent).get();

        Elements form = doc.select("form");
        String ssoURI = form.attr("action");
        Elements inputs = form.select("input");


        Map<String, String> params = new HashMap<>(inputs.size());
        for (Element input : inputs) {
            params.put(input.attr("name"), input.attr("value"));
        }

        doc = Jsoup.connect(ssoURI).userAgent(agent).data(params).post();

        form = doc.select("form[name=MLoginForm]");
        ssoURI = form.attr("action");
        inputs = form.select("input");
        params = new HashMap<>(inputs.size());
        for (Element input : inputs) {
            params.put(input.attr("name"), input.attr("value"));
        }


        params.put("ssousername", sso_id);
        params.put("password", sso_pass);

        doc = Jsoup.connect(LOGIN_URI_PREFIX + ssoURI).userAgent(agent).data(params).post();
        Elements body = doc.select("body");
        String text = body.text();


        text = text.substring(text.indexOf("Generated") - 9);

        generatedAt = getDate(text.substring(text.indexOf("Generated at ") + 13));
        currentPassword = text.substring(0, 8);

        logger.debug("currentPassword:{},generatedAt:{}", currentPassword, generatedAt);
    }

    private static Date getDate(String timeString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy z", Locale.US);
        return dateFormat.parse(timeString);
    }
}
