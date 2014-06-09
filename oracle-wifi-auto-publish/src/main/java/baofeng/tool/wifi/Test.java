package baofeng.tool.wifi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author baxue
 * @date 2014/6/9.
 * Iec3Bf7i Generated at Sun Jun 8 19:00:02 2014 UTC
 */
public class Test {

    final static Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception {
        String timeString = "Sun Jun 8 19:00:02 2014 UTC";
        Date date = getDate(timeString);
        logger.debug("{}", date);
    }

    private static Date getDate(String timeString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy z", Locale.US);
        return dateFormat.parse(timeString);
    }
}
