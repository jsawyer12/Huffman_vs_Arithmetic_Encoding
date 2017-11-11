public class ArithmeticSymbol {

    private String c;
    private double prob;
    private double range;
    private double low;
    private double high;


    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public String arithSymStr() {
        return "[" +this.getLow() +"," +this.getC() +"," +this.getHigh() + "]";
    }
}