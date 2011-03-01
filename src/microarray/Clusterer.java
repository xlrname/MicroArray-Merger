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
    private Map<String, Boolean> filenameToCSW;
    private Map<String, List<String>> experimentToGenes = new HashMap<String, List<String>>();

    public Clusterer(Map<String, List<File>> clusterDataMap, List<String> experiments, Map<String, Boolean> filenameToCSW) {
        this.clusterDataMap = clusterDataMap;
        this.experiments = experiments;
        this.filenameToCSW = filenameToCSW;
        for (String s : experiments) {
            this.experimentToGenes.put(s, new LinkedList<String>());
        }
    }

    public String getClusters() {
        Map<String, Map<String, Double>> resultMap = new HashMap<String, Map<String, Double>>();
        Map<String, List<Map<String, Double>>> map = new HashMap<String, List<Map<String, Double>>>();
        for (String experiment : clusterDataMap.keySet()) {
            Map<String, Double> geneToFoldMap = this.parseList(experiment, clusterDataMap.get(experiment));
            if (!map.containsKey(experiment)) {
                map.put(experiment, new LinkedList<Map<String, Double>>());
            }
            map.get(experiment).add(geneToFoldMap);
        }

        for (String experiment : map.keySet()) {
            Map<String, Integer> geneCount = new HashMap<String, Integer>();
            Map<String, Double> sumMap = new HashMap<String, Double>();
            List<Map<String, Double>> l = map.get(experiment);
            for (Map<String, Double> m : l) {
                for (String gene : m.keySet()) {
                    if (!geneCount.containsKey(gene)) {
                        geneCount.put(gene, 1);
                    } else {
                        geneCount.put(gene, geneCount.get(gene) + 1);
                    }
                    if (!sumMap.containsKey(gene)) {
                        sumMap.put(gene, m.get(gene));
                    } else {
                        sumMap.put(gene, sumMap.get(gene) + (m.get(gene)));
                    }
                }
            }
            resultMap.put(experiment, sumMap);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Gene");
        for (String experiment : experiments) {
            sb.append("\t").append(experiment);
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
                        double rom = (double) resultMap.get(experiment).get(gene);
                        sb.append(Math.round(rom * 100) / 100.0);
                    } else {
                        sb.append(0);
                    }
                }
                sb.append("\r\n");
            }
        }


        return sb.toString();
    }

    private Map<String, Double> parseList(String experiment, List<File> get) {
        Map<String, List<Double>> genesToFoldMap = new HashMap<String, List<Double>>();
        for (File f : get) {
            this.parseFile(experiment, f, genesToFoldMap);
        }
        Map<String, Double> geneToFoldMap_mean = new HashMap<String, Double>();
        for (String gene : genesToFoldMap.keySet()) {
            List<Double> l = genesToFoldMap.get(gene);
            double sum = 0.0;
            for (double d : l) {
                sum += Math.log10(d);
            }
            sum /= l.size();
            geneToFoldMap_mean.put(gene, sum);
            if (!genes.contains(gene)) {
                genes.add(gene);
            }
        }
        return geneToFoldMap_mean;
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
