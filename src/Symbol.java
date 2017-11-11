public class Symbol {

    private String c;
    private double prob;
    private String binary;

    public String getC() {
        return c;
    }

    public double getProb() {
        return prob;
    }

    public String getBinary() {
        return binary;
    }

    public void setC(String c) {
        this.c = c;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public String symbProbStr() {
        return "[" +this.getC() +"," +this.getProb() +"]";
    }

    public String symbBinStr() {
        return "[" +this.getC() +"," +this.getBinary() +"]";
    }

    public Symbol(String c, double prob) {
        this.c = c;
        this.prob = prob;
        this.binary = "";
    }
}