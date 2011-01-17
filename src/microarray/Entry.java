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

/**
 *
 * @author Armin Töpfer (xlrname@gmail.com)
 */
public class Entry {

    private String a;
    private String coef;
    private String gname;

    public Entry(String a, String coef, String gname) {
        this.a = a;
        this.coef = coef;
        this.gname = gname;
    }

    public double getA() {
        return Double.valueOf(a);
    }

    public double getCoef() {
        return Double.valueOf(coef);
    }

    public String getGname() {
        return gname;
    }
}
