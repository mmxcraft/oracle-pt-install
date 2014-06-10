package baofeng.tool.wifi;

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

/**
 * @author baxue
 * @date 2014/6/10.
 */
public class PersistantManager {

    public static final String PASSWORD_TXT = "password.txt";
    final static Logger logger = LoggerFactory.getLogger(PersistantManager.class);

    static String getSavedPassword() {
        String savedPassword = null;
        Path file = Paths.get(PASSWORD_TXT);
        if (!file.toFile().exists()) return "";

        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            savedPassword = reader.readLine();
        } catch (IOException x) {
            logger.error("{}", x);
        }

        logger.debug("get saved password:{}", savedPassword);
        return savedPassword;
    }

    static void saveToFile(String password) {
        Path file = new File(PASSWORD_TXT).toPath();
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedWriter writer = Files.newBufferedWriter(file, charset)) {
            writer.write(password);
        } catch (IOException x) {
            Main.logger.error("{}", x);
        }
        logger.debug("saveToFile:{}", file);
    }
}
