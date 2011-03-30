/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Armin Töpfer (xlrname@gmail.com),
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 */
package microarray;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Armin Töpfer (xlrname@gmail.com)
 */
public class MicroArray {

    private Map<String, List<Entry>> entryMap = new HashMap<String, List<Entry>>();

    /**
     * @param args the command line arguments
     */
    public MicroArray(File args) {
        try {
            FileInputStream fstream = new FileInputStream(args);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] split = strLine.split("\t");
                try {
                    double p = Double.parseDouble(split[3]);
                    double coef = Double.parseDouble(split[1]);
                    if (split[10].startsWith("cg") && p < 0.05 && (coef >= 1 || coef <= -1)) {
                        List<Entry> entryList = null;
                        if (entryMap.containsKey(split[10])) {
                            entryList = entryMap.get(split[10]);
                        } else {
                            entryList = new LinkedList<Entry>();
                            entryMap.put(split[10], entryList);
                        }
                        entryList.add(new Entry(split[0], split[1], split[10]));
                    }
                } catch (NumberFormatException e) {
                }
            }
            in.close();
        } catch (Exception e) {
        }
        int i = 0;
        for (String gname : entryMap.keySet()) {
            if (entryMap.get(gname).size() >= 2) {
                i++;
            }
        }
    }

    public Object[][] toObjectArray() {
        int r = 0;
        for (String gname : entryMap.keySet()) {
            if (entryMap.get(gname).size() >= 2) {
                r++;
            }
        }
        Object[][] o = new Object[r][7];
        int i = 0;
        for (List<Entry> le : this.entryMap.values()) {
            int s = le.size();
            if (s >= 2) {
                o[i][0] = le.get(0).getGname();
                Statistics coef = new Statistics();
                Statistics a = new Statistics();
                for (Entry e : le) {
                    coef.update(e.getCoef());
                    a.update(e.getA());
                }
                o[i][1] = coef.getMean();
                o[i][2] = coef.getStandardDeviation();
                o[i][3] = a.getMean();
                o[i][4] = a.getStandardDeviation();
                o[i][5] = le.size();
                o[i][6] = CGAnnotation.getInstance().getDef(o[i][0].toString());
                i++;
            }
        }
        return o;
    }
}
