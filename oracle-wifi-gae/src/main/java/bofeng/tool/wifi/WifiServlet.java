package bofeng.tool.wifi;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author baxue
 * @date 2014/6/3.
 */
public class WifiServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(WifiServlet.class.getName());


    private String storedPassword;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String incomingPassword = req.getParameter("p");
        logger.info("incomingPassword:" + incomingPassword);
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
                msg.addRecipient(Message.RecipientType.TO, address);
                msg.setSubject("Oracle Beijing Wifi" + new Date());
                msg.setText(storedPassword);
                Transport.send(msg);

            } catch (AddressException e) {
                logger.severe(e.toString());
            } catch (MessagingException e) {
                logger.severe(e.toString());
            }

        }

    }

}
