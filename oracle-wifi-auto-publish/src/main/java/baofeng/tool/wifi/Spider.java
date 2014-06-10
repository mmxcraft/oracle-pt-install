package baofeng.tool.wifi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author baxue
 * @date 2014/6/10.
 */
public class Spider {

    public static final String LOGIN_URI_PREFIX = "https://login.oracle.com";
    private static final String agent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";
    private static final String wifiURI = "https://gmp.oracle.com/captcha/files/airespace_pwd_apac.txt?_dc=1401161841661";
    static String sso_id;
    static String sso_pass;
    static String currentPassword;
    private static Date generatedAt;

    static void fetchPassword() throws Exception {
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

        Main.logger.debug("currentPassword:{},generatedAt:{}", currentPassword, generatedAt);
    }

    private static Date getDate(String timeString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy z", Locale.US);
        return dateFormat.parse(timeString);
    }

}
