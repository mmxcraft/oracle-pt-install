package baofeng.tool.pt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by baxue on 6/26/2014.
 */
public class InstallInit {
    public static final String USAGE = "input like:pt-854-903-r1-debug";
    final static Logger logger = LoggerFactory.getLogger(InstallInit.class);
    public static String lic;
    public static String v1;
    public static String v2;
    public static String v3;
    public static String dbName;
    public static Map<String, String> licMap = new HashMap<String, String>(3);
    public static String piaHome;
    public static String v11;
    public static String debug;
    private static Set<String> licSet = new HashSet<String>(3);
    private static boolean inited = false;


    public static void main(String[] args) {

        if (args.length == 0) {
            logger.error(USAGE);
            System.exit(1);
        }

        if (inited) return;

        logger.info("your input:{}", args[0]);
        String[] paras = args[0].split("-");
        if (paras.length != 5) {
            logger.error(USAGE);
            System.exit(1);
        }

        lic = paras[0];
        v1 = paras[1];
        v11 = String.valueOf(v1.charAt(2));
        v2 = paras[2];
        v3 = paras[3];
        debug = paras[4];

        dbName = lic + v11 + v2 + v3;
        dbName = dbName.toUpperCase();
        piaHome = "D:\\" + lic + v1 + "-" + v2 + "-" + v3 + "-" + debug;
        piaHome = piaHome.toUpperCase();


        licSet.add("qe");
        licSet.add("pt");
        licSet.add("fs");

        licMap.put("qe", "dmo");
        licMap.put("pt", "sys");
        licMap.put("fs", "dmo");

        if (!licSet.contains(lic.toLowerCase())) {
            logger.error("only support:{}", licSet);
            System.exit(1);
        }
        inited = true;
    }
}
