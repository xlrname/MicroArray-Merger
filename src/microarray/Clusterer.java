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
public class Clusterer {

    private Map<String, List<File>> clusterDataMap;
    private List<String> genes = new LinkedList<String>();
    private List<String> experiments;
    private Map<String, List<String>> experimentToGenes = new HashMap<String, List<String>>();

    public Clusterer(Map<String, List<File>> clusterDataMap, List<String> experiments) {
        this.clusterDataMap = clusterDataMap;
        this.experiments = experiments;
        for (String s : experiments) {
            this.experimentToGenes.put(s, new LinkedList<String>());
        }
    }

    public String getClusters() {
        Map<String, Map<String, List<Double>>> resultMap = new HashMap<String, Map<String, List<Double>>>();
        Map<String, List<Map<String, List<Double>>>> map = new HashMap<String, List<Map<String, List<Double>>>>();
        for (String experiment : clusterDataMap.keySet()) {
            Map<String, List<Double>> geneToFoldMap = this.parseList(experiment, clusterDataMap.get(experiment));
            if (!map.containsKey(experiment)) {
                map.put(experiment, new LinkedList<Map<String, List<Double>>>());
            }
            map.get(experiment).add(geneToFoldMap);
        }
        for (String experiment : map.keySet()) {
            Map<String, Integer> geneCount = new HashMap<String, Integer>();
            Map<String, List<Double>> sumMap = new HashMap<String, List<Double>>();
            List<Map<String, List<Double>>> l = map.get(experiment);
            for (Map<String, List<Double>> m : l) {
                for (String gene : m.keySet()) {
                    if (!this.genes.contains(gene)) {
                        this.genes.add(gene);
                    }
                    if (!geneCount.containsKey(gene)) {
                        geneCount.put(gene, 1);
                    } else {
                        geneCount.put(gene, geneCount.get(gene) + 1);
                    }
                    if (!sumMap.containsKey(gene)) {
                        sumMap.put(gene, m.get(gene));
                    } else {
                        sumMap.get(gene).addAll(m.get(gene));
                    }
                }
            }
            resultMap.put(experiment, sumMap);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Gene").append("\t");
        for (String experiment : experiments) {
            sb.append(experiment).append("\t\t\t");
        }
        sb.append("\r\n\t");
        for (String experiment : experiments) {
            sb.append("mean\tsd\t\t");
        }
        sb.append("\r\n");
        for (String gene : genes) {
            int geneCount = 0;
            for (List<String> l : this.experimentToGenes.values()) {
                if (l.contains(gene)) {
                    geneCount++;
                }
            }
            if (geneCount >= 2) {
                sb.append(gene);
                for (String experiment : experiments) {
                    sb.append("\t");
                    if (resultMap.get(experiment).get(gene) != null) {
                        List<Double> roms = resultMap.get(experiment).get(gene);
                        Statistics statistics = new Statistics();
                        double sum = 0;
                        int i = 0;
                        for (double d : roms) {
                            statistics.update(d);
                        }
//                        sum /= i;
//                        double sd = 0;
//                        for (double d : roms) {
//                            sd += Math.pow((d - sum), 2);
//                        }
                        sb.append(Math.round(statistics.getMean() * 100) / 100.0).append("\t");
                        sb.append(Math.round(statistics.getStandardDeviation() * 100) / 100.0);
                        sb.append("\t");
                        for (double d : roms) {
                            sb.append(d).append(";");
                        }
                    } else {
                        sb.append(0);
                    }
                }
                sb.append("\r\n");
            }
        }


        return sb.toString().replaceAll("\\.", ",");
    }
    

    private Map<String, List<Double>> parseList(String experiment, List<File> get) {
        Map<String, List<Double>> genesToFoldMap = new HashMap<String, List<Double>>();
        for (File f : get) {
            this.parseFile(experiment, f, genesToFoldMap);
        }
        return genesToFoldMap;
    }

    private void parseFile(String experiment, File f, Map<String, List<Double>> map) {
        try {
            FileInputStream fstream = new FileInputStream(f);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            boolean content = false;
            int ratioI = 0;
            int nameI = 0;
            int f532 = 0;
            int g635 = 0;
            int flag = 0;
            List<String> genes = this.experimentToGenes.get(experiment);
            while ((strLine = br.readLine()) != null) {
                String[] split = strLine.split("\t");
                if (!content && split[0].equals("\"Block\"")) {
                    content = true;
                    for (int i = 0; i < split.length; i++) {
                        if (split[i].equals("\"Ratio of Medians (635/532)\"")) {
                            ratioI = i;
                        } else if (split[i].equals("\"F532 Median\"")) {
                            f532 = i;
                        } else if (split[i].equals("\"G635 Median\"")) {
                            g635 = i;
                        } else if (split[i].equals("\"Name\"")) {
                            nameI = i;
                        } else if (split[i].equals("\"Flags\"")) {
                            flag = i;
                        }
                    }
                    continue;
                }
                if (content) {
                    try {
                        String name = split[nameI].replaceAll("\"", "");
                        double f532m = Double.parseDouble(split[f532]);
                        double g635m = Double.parseDouble(split[g635]);
                        double rom = Double.parseDouble(split[ratioI]);
                        if (name.startsWith("cg") && split[flag].equals("0")) {
                            if (!genes.contains(name)) {
                                genes.add(name);
                            }
                            List<Double> entryList = null;
                            if (map.containsKey(name)) {
                                entryList = map.get(name);
                            } else {
                                entryList = new LinkedList<Double>();
                                map.put(name, entryList);
                            }
                            entryList.add(rom);
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
