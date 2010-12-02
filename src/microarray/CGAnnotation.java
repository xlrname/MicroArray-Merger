package microarray;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author XLR
 */
public class CGAnnotation {

    private static final CGAnnotation INSTANCE = new CGAnnotation();
    private Map<String, String> cgMap = new HashMap<String, String>();

    public String getDef(String gname) {
        return cgMap.get(gname);
    }

    public static CGAnnotation getInstance() {
        return INSTANCE;
    }

    private CGAnnotation() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/microarray/CG_Annotation.txt")));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] split = strLine.split("\t");
                cgMap.put(split[0], split[1]);
            }
            br.close();
        } catch (Exception e) {
        }
    }
}
