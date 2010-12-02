package microarray;

/**
 *
 * @author XLR
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
