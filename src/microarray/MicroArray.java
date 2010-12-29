/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import sort.SortManager;

/**
 *
 * @author XLR
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
        System.out.println(i);
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
                if (le.get(0).getGname().equals("cg1109")) {
                    System.out.println("a");
                }
                double coefSum = 0;
                double aSum = 0;
                for (Entry e : le) {
                    coefSum += e.getCoef();
                    aSum += e.getA();
                }
                double coefMean = coefSum / s;
                o[i][1] = coefMean;
                double coefSD = 0;
                for (Entry e : le) {
                    coefSD += Math.pow((e.getCoef() - coefMean), 2);
                }
                o[i][2] = coefSD;
                double aMean = aSum / s;
                o[i][3] = aMean;
                double aSD = 0;
                for (Entry e : le) {
                    aSD += Math.pow((e.getA() - aMean), 2);
                }
                o[i][4] = aSD;
                o[i][5] = le.size();
                o[i][6] = CGAnnotation.getInstance().getDef(o[i][0].toString());
                i++;
            }
        }
        
//        SortManager.sort(twodime, column, order)
        return o;
    }
}
