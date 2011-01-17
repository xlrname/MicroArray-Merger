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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Armin Töpfer (xlrname@gmail.com)
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
