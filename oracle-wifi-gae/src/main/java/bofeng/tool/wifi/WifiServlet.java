package bofeng.tool.wifi;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author baxue
 * @date 2014/6/3.
 */
public class WifiServlet extends HttpServlet {
    public static final String mailList = "xbaofeng@gmail.com,29994106@qq.com," +
            "2938187216@qq.com,brian.sheng@oracle.com," +
            "1469253517@qq.com,hao.z.zhang@oracle.com," +
            "63007025@qq.com,yonghao.bai@oracle.com," +
            "759942710@qq.com,young.liu@oracle.com,";
    private static final Logger logger = Logger.getLogger(WifiServlet.class.getName());
    private static String storedPassword;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String incomingPassword = req.getParameter("p");
        logger.info("incomingPassword:" + incomingPassword);
        logger.info("storedPassword:" + storedPassword);
        if (incomingPassword != null
                && !incomingPassword.equals(storedPassword)
                && !"".equals(incomingPassword)) {

            logger.info("new pass set:" + incomingPassword);
            storedPassword = incomingPassword;

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            try {
                Message msg = new MimeMessage(session);
                InternetAddress address = new InternetAddress("xbaofeng@gmail.com", "Baofeng Xue");
                msg.setFrom(address);
                msg.addRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(mailList));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                msg.setSubject("Oracle Beijing Wifi-" + dateFormat.format(new Date()));
                msg.setText(storedPassword);
                Transport.send(msg);

            } catch (MessagingException e) {
                logger.severe(e.toString());
            }

        } else {
            logger.info("parameters error or password no change.");
        }

    }

}
