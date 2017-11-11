import Exceptions.IncorrectInputException;
import Exceptions.NotIndividualSymbolException;
import Exceptions.ProbabilityGreaterThanOneException;
import Exceptions.NiceTryException;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Main {

    /**
     * I made these data types global because they are being initialized in one method and
     * used in another. Some are also used across multiple methods. Bearing these things in
     * mind, it was wiser for me to use global variables than restructure all of my code.
     */
    public static double entropy;
    public static double huffAvg;
    public static double arithAvg;
    public static double excessEntropy;

    public static long huffTime;
    public static long arithTime;

    public static int huffBinLength;
    public static int arithBinLength;

    public static StringBuilder huffDispl;
    public static StringBuilder arithDispl;
    public static StringBuilder statDispl;

    /*************************************  HUFFMAN ************************************************\

    /**
     * The following method takes the array of symbols and their respected binary codes and writes out
     * the binary code for the message.
     * @param msg
     * @param huffAr
     * @return
     */
    public static String writeHuffBinary(String msg, ArrayList<Symbol> huffAr) {
        StringBuilder output = new StringBuilder();
        for (int j = 0; j < msg.length(); j++) { //for the string of characters
            for (int k = 0; k < huffAr.size(); k++) { //for the characters and their probabilities
                if (huffAr.get(k).getC().equals(msg.substring(j,j+1))) {
                    output.append(huffAr.get(k).getBinary());
                }
            }
        }
        return output.toString();
    }

    /**
     * The following code takes the array of two collections of symbols and their probabilities & breaks
     * them down further and further recursively until there are no collections and each symbol is on its own
     * in the array, all while appending a 0 or a 1 to the current symbols binary code.
     * @param huffAr
     * @return
     */
    public static ArrayList<Symbol> encodeHuffman(ArrayList<Symbol> huffAr) {
        for (int k = 0; k < huffAr.size(); k++) {
            if (huffAr.get(k).getC().length() > 1) {
                String curBin = huffAr.get(k).getBinary();
                Symbol symb1 = new Symbol(huffAr.get(k).getC().substring(0,1), 0.0); //Probability no longer matters
                symb1.setBinary(curBin + "0");
                huffAr.add(symb1);

                Symbol symb2 = new Symbol(huffAr.get(k).getC().substring(1), 0.0); //arbitrary probability
                symb2.setBinary(curBin + "1");
                huffAr.add(symb2);

                huffAr.remove(k);

                encodeHuffman(huffAr);
            }
        }
        return huffAr;
    }

    /**
     * The following method takes the binary message created using the Huffman coding method and
     * the array of symbols and their respected codes and creates a substring of binary that gets
     * bigger and bigger until a symbol is found that has binary corresponding to it. It then returns
     * the decoded message!
     * @param binMsg
     * @param huffKey
     * @return
     */
    public static String decodeHuffMsg(String binMsg, ArrayList<Symbol> huffKey) {
        int msgInd = 0;
        StringBuilder ogMsg = new StringBuilder();
        StringBuilder subBinMsg = new StringBuilder();
        while (msgInd < binMsg.length()) {
            subBinMsg.append(binMsg.charAt(msgInd));
            for (int uhuh = 0; uhuh < huffKey.size(); uhuh++) {
                if (subBinMsg.toString().equals(huffKey.get(uhuh).getBinary())) {
                    ogMsg.append(huffKey.get(uhuh).getC());
                    subBinMsg = new StringBuilder();
                }
            }
            msgInd++;
        }
        return ogMsg.toString();
    }

    /**
     * This takes the input array of symbols and their respected probabilities and combines them
     * to create 2 collections of symbols and summed probabilities
     * @param symbAr
     */
    public static void setUpHuffman(String msg, ArrayList<Symbol> symbAr) {
        ArrayList<Symbol> sorted = selectionSort(symbAr); // probabilities list
        ArrayList<Symbol> shortened = sorted; //modified huffman probabilities list
        huffDispl = new StringBuilder();
        huffDispl.append("Huffman\n\nOriginal Message: " +msg +"\n");
        huffDispl.append("Amount of symbols in Message: " +msg.length() +"\n");
        while (shortened.size() > 2) { //there are n - 2 steps to complete the first step of Huffman coding
            shortened = selectionSort(shortened); //sort them in order of size
            String intermSymbols = shortened.get(shortened.size()- 1).getC() + shortened.get(shortened.size()- 2).getC();
            if (shortened.get(shortened.size()- 1).getC().length() > 1) {
                intermSymbols = shortened.get(shortened.size()- 2).getC() + shortened.get(shortened.size()- 1).getC();
            }
            double prob1 = shortened.get(shortened.size()- 2).getProb();
            double prob2 = shortened.get(shortened.size()- 1).getProb();
            BigDecimal bigProb1 = BigDecimal.valueOf(prob1);
            BigDecimal bigProb2 = BigDecimal.valueOf(prob2);
            BigDecimal bigSum = bigProb1.add(bigProb2);
            double newProb = bigSum.doubleValue();
            shortened.remove(shortened.size() - 2);
            shortened.remove(shortened.size() - 1);
            Symbol newSymb = new Symbol(intermSymbols, newProb);
            shortened.add(newSymb);
        }
        shortened = selectionSort(shortened);
        shortened.get(0).setBinary("0");
        shortened.get(1).setBinary("1");

        ArrayList<Symbol> huffCoded = encodeHuffman(shortened);
        huffDispl.append("Binary for each Symbol: ");
        System.out.print("Binary for each Symbol: ");
        for (int o = 0; o < huffCoded.size(); o++) {
            huffDispl.append(huffCoded.get(o).symbBinStr() +" ");
            System.out.print(huffCoded.get(o).symbBinStr() +" ");
        }
        huffDispl.append("\n");
        System.out.println();
        huffDispl.append("Huffman message in binary: ");
        String huffBinaryMsg = writeHuffBinary(msg, huffCoded);
        huffDispl.append(huffBinaryMsg +"\n");
        try {
            BigDecimal l = BigDecimal.valueOf((double)(huffBinaryMsg.length()));
            l = l.divide(BigDecimal.valueOf((double)(msg.length())), 5, RoundingMode.HALF_UP);
            huffAvg = l.doubleValue();
            huffBinLength = huffBinaryMsg.length();
            huffDispl.append("Huffman amount of digits: " +huffBinaryMsg.length() +"\n");
            System.out.println("Huffman amount of digits: " +huffBinaryMsg.length());
            huffDispl.append("Huffman digits per symbol: " +huffAvg +"\n");
            System.out.println("Huffman digits per symbol: " +huffAvg);
            huffDispl.append("Huffman Binary Message: " +huffBinaryMsg +"\n");
            System.out.println("Huffman Binary Message: " +huffBinaryMsg);
        } catch (ArithmeticException e) {
            JOptionPane.showMessageDialog(null, "Error calculating Huffman digits per symbol: " +e.getMessage());
        }
        String decodedMsg = decodeHuffMsg(huffBinaryMsg, huffCoded);
        huffDispl.append("Huffman Decoded Message: " +decodedMsg +"\n");
        System.out.println("Huffman Decoded Message: " +decodedMsg);
    }

    /************************************* HELPER METHODS **********************************************\
     The following methods are used in multiple parts of my program and thus are the helpers! I needed to
     use big decimal for a lot of my double computations so I created these for quick & accurate computation.
     */

    public static double addBig(double a, double b) {
        BigDecimal aBig = BigDecimal.valueOf(a);
        BigDecimal bBig = BigDecimal.valueOf(b);
        BigDecimal sum = bBig.add(aBig);
        return sum.doubleValue();
    }

    public static double subBig(double a, double b) {
        BigDecimal aBig = BigDecimal.valueOf(a);
        BigDecimal bBig = BigDecimal.valueOf(b);
        BigDecimal sub = aBig.subtract(bBig);
        return sub.doubleValue();
    }

    public static double divBig(double a, double b) {
        BigDecimal aBig = BigDecimal.valueOf(a);
        BigDecimal bBig = BigDecimal.valueOf(b);
        BigDecimal div = aBig.divide(bBig);
        return div.doubleValue();
    }

    public static double multBig(double a, double b) {
        BigDecimal aBig = BigDecimal.valueOf(a);
        BigDecimal bBig = BigDecimal.valueOf(b);
        BigDecimal multiplied = aBig.multiply(bBig);
        return multiplied.doubleValue();
    }

    /**
     * In this method, I'm sorting the symbols based on their probabilities from highest
     * to lowest while using a selection sort for speedy sorting for the small data sets!
     * @param symbAr
     * @return
     */
    public static ArrayList<Symbol> selectionSort(ArrayList<Symbol> symbAr) {
        ArrayList<Symbol> sortedSymbAr = new ArrayList<>();
        try {
            int size = symbAr.size();
            for (int j = 0; j < size; j++) { //for each
                int curMaxInd = 0;
                for (int k = 0; k < symbAr.size(); k++) {
                    if (symbAr.get(k).getProb() > symbAr.get(curMaxInd).getProb())
                        curMaxInd = k;
                }
                Symbol newSymb = symbAr.get(curMaxInd);
                symbAr.remove(curMaxInd);
                sortedSymbAr.add(newSymb);
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "You messed up in the sorting: " +e.getMessage());
        }
        return sortedSymbAr;
    }

    /**
     * The following method takes the array of Arithmetic symbols and their values, iterates through them,
     * and modifies each one based on the new values as the program goes step by step into the process.
     * This was being used to both encode and decode for Arithmetic so I made it its own method.
     * @param low
     * @param ind
     * @param ogArray
     * @return
     */
    public static ArrayList<ArithmeticSymbol> modifyArray(double low, int ind, ArrayList<ArithmeticSymbol> ogArray) {
        double curProbInd = low;
        for (int g = 0; g < ogArray.size(); g++) { //modify symbol values for new range, low value and high value

            if (g < 1)
                ogArray.get(g).setLow(curProbInd);
            else
                ogArray.get(g).setLow(ogArray.get(g-1).getHigh());
            double multiplied = multBig(ogArray.get(g).getProb(), ogArray.get(ind).getRange());
            curProbInd = addBig(curProbInd, multiplied);

            ogArray.get(g).setRange(multiplied);

            ogArray.get(g).setHigh(addBig(ogArray.get(g).getRange(),ogArray.get(g).getLow()));
        }
        return ogArray;
    }

    public static double calcEntropy(String msg, ArrayList<Symbol> arr) {
        double entropy = 0.0;
        for (int i = 0; i < msg.length(); i++) {
            for (int j = 0; j < arr.size(); j++) {
                if (msg.substring(i, i+1).equals(arr.get(j).getC())) {
                    /**
                     * The following is essentially performing this function using BigDecimals:
                     * entropy += (0.0 - arr.get(j).getProb()) * (Math.log(arr.get(j).getProb()) / Math.log(2));
                     */
                    BigDecimal log2 = BigDecimal.valueOf(Math.log(arr.get(j).getProb()));
                    log2 = log2.divide(BigDecimal.valueOf(Math.log(2)), 5, RoundingMode.HALF_UP);
                    BigDecimal multiplied = BigDecimal.valueOf(subBig(0.0, arr.get(j).getProb()));
                    multiplied = multiplied.multiply(BigDecimal.valueOf(log2.doubleValue()));
                    entropy = addBig(multiplied.doubleValue(), entropy);
                }
            }
        }
        System.out.println("Entropy: " +entropy +" digits per symbol");
        return entropy;
    }

    /*****************************************  ARITHMETIC *********************************************\

    /**
     * The following method takes the original unmodified arithmetic array, the end of data symbol, and the tag,
     * and goes through the arithmetic process of finding the original message. It was created to keep going until
     * the last symbol is found and can get into some really small values!
     * @param eod
     * @param tag
     * @param ogArithArray
     * @return
     */
    public static String decodeArithMsg(String eod, double tag, ArrayList<ArithmeticSymbol> ogArithArray) {
        StringBuilder ogMsg = new StringBuilder();
        String symStr = "";
        double low = 0.0;
        System.out.print("OG ARITHMETIC MESSAGE: ");
        while (!symStr.equals(eod)) {
            for (int ah = 0; ah < ogArithArray.size(); ah++) {
                if (tag > ogArithArray.get(ah).getLow() && tag < ogArithArray.get(ah).getHigh()) {
                    low = ogArithArray.get(ah).getLow();
                    symStr = ogArithArray.get(ah).getC();
                    ogMsg.append(symStr);
                    System.out.print(symStr);
                    ogArithArray = modifyArray(low, ah, ogArithArray);
                    ah = ogArithArray.size(); //go to the next character in the string
                }
            }
        }
        System.out.println();
        return ogMsg.toString();
    }

    /**
     * This method creates the tag using the binary tag. I've found my code is 100% accurate in decoding the
     * message when the tag is set to the high value.
     * @param encodedTag
     * @return
     */
    public static double geTag(String encodedTag) {
        double tag = 0.0, low = 0.0, mid = 0.5, high = 1.0;
        for (int oy = 0; oy < encodedTag.length(); oy++) {
            if (Integer.parseInt(encodedTag.substring(oy, oy + 1)) == 1) { //if next digit is 1
                low = mid;
                mid = addBig(divBig(subBig(high,low), 2), low); // essentially (high - low) / 2 + low
                tag = high; //
            }
            else { //if next digit is 0
                high = mid;
                mid = addBig(divBig(subBig(high,low), 2), low); // essentially (high - low) / 2 + low
                tag = high;
            }
        }
        return tag;
    }

    /**
     * The following method creates the tag used to decode the original message. it essentially recurses
     * deeper and deeper into a symbol's range, adding 0s or 1s based on the low and high range values of
     * the last input symbol in the message, until it reaches a point where the range is no longer within the
     * low and high value, and thus the resulting binary is the tag!
     *
     * @param lowRange
     * @param highRange
     * @param low
     * @param high
     * @return
     */
    public static String encodeTag(double lowRange, double highRange, double low, double high) {
        StringBuilder binary = new StringBuilder();
        if (high < highRange || low > lowRange) {
            return "";
        }
        else {
            double half = ((high - low) / 2) + low;
            if (lowRange >= half) {
                binary.append("1");
                System.out.print(binary.toString());
                binary.append(encodeTag(lowRange, highRange, half, high));
            }
            else {
                binary.append("0");
                System.out.print(binary.toString());
                binary.append(encodeTag(lowRange, highRange, low, half));
            }
            return binary.toString();
        }
    }

    /**
     * This method creates a random message based on the user input symbols. I wanted to make the
     * percentage of times each symbol came up coincide with the actual probability, but realized
     * this would not work for small probabilities nor for arithmetic consistently as arithmetic
     * requires an End-Of-Data symbol.
     * @param eod
     * @param arr
     * @return
     */
    public static String makeRandomMsg(String eod, ArrayList<Symbol> arr) {
        StringBuilder randMsg = new StringBuilder();
        int length = (int)(Math.random() * 8 + 3), indToAdd = 0;
        for (int s = 0; s < length; s++) {
            indToAdd = (int)(Math.random() * (arr.size()-1));
            while (arr.get(indToAdd).getC().equals(eod)) {
                indToAdd = (int)(Math.random() * (arr.size()-1));
            }
            randMsg.append(arr.get(indToAdd).getC());
        }
        randMsg.append(eod);
        return randMsg.toString();
    }

    /**
     * This method is the mastermind of the whole Arithmetic operation. Using the parameters end-of-data,
     * the message, the arraylist of symbols used for encoding, and the arraylist of symbols used for decoding,
     * this method sets up the arithmetic array of symbols, finds the low and high range values for last
     * symbol of the message, encodes the tag based on the low and high values, decodes the tag, and uses it to
     * decode and find the original message!
     * @param eod
     * @param str
     * @param arithArray
     * @param arithArray2
     */
    public static void setUpArithmetic(String eod, String str, ArrayList<ArithmeticSymbol> arithArray, ArrayList<ArithmeticSymbol> arithArray2) {
        double[] rangeTag = new double[2];
        double low = 0.0, high = 1.0; //start in double for values, use BigDecimal for computation
        arithDispl = new StringBuilder();
        arithDispl.append("Arithmetic\n\n" +"Original Message: " +str +"\n");
        arithDispl.append("Amount of symbols in Message: " +str.length() +"\n");
        for (int j = 0; j < str.length(); j++) { //for the string of characters
            for (int k = 0; k < arithArray.size(); k++) { //for the characters and their probabilities
                if (arithArray.get(k).getC().equals(str.substring(j,j+1))) {
                    low = arithArray.get(k).getLow();
                    high = arithArray.get(k).getHigh();
                    arithArray = modifyArray(low, k, arithArray);
                    k = arithArray.size(); //go to the next character in the string
                }
            }
        }
        System.out.println("Low: " +low +" and high: " +high);
        rangeTag[0] = low;
        rangeTag[1] = high;
        arithDispl.append("Low: " +low +", high: " +high +", and range: " +subBig(high, low) +"\n");
        String encodedTag = encodeTag(rangeTag[0], rangeTag[1], 0, 1);
        arithDispl.append("Arithmetic message in binary: " +encodedTag +"\n");
        System.out.println();
        try {
            BigDecimal l = BigDecimal.valueOf((double)(encodedTag.length()));
            l = l.divide(BigDecimal.valueOf((double)(str.length())), 5, RoundingMode.HALF_UP);
            arithAvg = (double)(encodedTag.length()) / (double)(str.length());
            excessEntropy = addBig(2.0 / (double)(str.length()), entropy);
            arithBinLength = encodedTag.length();
            arithDispl.append("Arithmetic amount of digits: " +encodedTag.length() +"\n");
            System.out.println("Arithmetic amount of digits: " +encodedTag.length());
            arithDispl.append("Arithmetic digits per symbol: " +arithAvg +"\n");
            System.out.println("Arithmetic digits per symbol: " +arithAvg);
            arithDispl.append("entropy + (2/n), arithmetic average: " +excessEntropy +", " +arithAvg +"\n");
            System.out.println("entropy + excess: " +excessEntropy +" vs " +arithAvg);
        } catch (ArithmeticException e) {
            JOptionPane.showMessageDialog(null, "Error calculating Arithmetic digits per symbol: " +e.getMessage());
        }
        double tag = geTag(encodedTag);
        System.out.println("Arithmetic decoded tag: " + tag);
        String ogMsg = decodeArithMsg(eod, tag, arithArray2);
        arithDispl.append("Arithmetic Decoded Message: " +ogMsg +"\n");
    }


    /**************************************** Main Methods *********************************************\

    /**
     * This method is responsible for all the nice GUIs you read from the Huffman, to the Arithmetic,
     * to the statistics.
     */
    public static void makeSomeGUIs() {
        BigDecimal bigHuffPer = BigDecimal.valueOf(huffAvg);
        bigHuffPer = bigHuffPer.divide(BigDecimal.valueOf(entropy), 5, RoundingMode.HALF_UP);
        double huffPer = multBig(bigHuffPer.doubleValue(), 100.0);

        BigDecimal bigArithPer = BigDecimal.valueOf(arithAvg);
        bigArithPer = bigArithPer.divide(BigDecimal.valueOf(entropy), 5, RoundingMode.HALF_UP);
        double arithPer = multBig(bigArithPer.doubleValue(), 100.0);

        JOptionPane.showMessageDialog(null, arithDispl.toString());
        System.out.println("Arithmetic elapsed time in nanoseconds: " + arithTime);
        statDispl = new StringBuilder();
        statDispl.append("Stats\n\n");
        statDispl.append("In terms of using the least amount of bits,\n");
        if (arithBinLength > huffBinLength) {
            statDispl.append("Huffman reigns supreme with only " +huffBinLength +" bits used\n" +
                    "while Arithmetic used " +arithBinLength +" bits.\n");
        }
        else if (arithBinLength == huffBinLength) {
            statDispl.append("It's a tie! both used only " +huffBinLength +" bits.\n");
        }
        else {
            statDispl.append("Arithmetic takes the W with only " +arithBinLength +" bits used.\n" +
                    "while Huffman used " +huffBinLength +" bits\n");
        }
        statDispl.append("If we prioritize elapsed time,\n");
        if (arithTime > huffTime) {
            statDispl.append("Huffman took only " +huffTime +" nanoseconds\n" +
                    "while Arithmetic took " +arithTime +" nanoseconds.\n");
        }
        else if (arithTime == huffTime) {
            statDispl.append("This is truly an anomoly. Both have an elapsed\n" +
                    "time of " +arithTime +" nanoseconds.\nIncredible!");
        }
        else {
            statDispl.append("Arithmetic took only " +arithTime +" nanoseconds\n" +
                    "while Huffman took " +huffTime +" nanoseconds.\n");
        }
        statDispl.append("Huffman got " +huffPer +"% of entropy (digits per symbol in message).\n");
        statDispl.append("Arithmetic got " +arithPer +"% of entropy (digits per symbol in message)");
        BigDecimal bigPerForIdeal = BigDecimal.valueOf(arithAvg);
        bigPerForIdeal = bigPerForIdeal.divide(BigDecimal.valueOf(excessEntropy), 5, RoundingMode.HALF_UP);
        double perForIdeal = bigPerForIdeal.doubleValue();
        if (perForIdeal <= 1) {
            statDispl.append(",\nmaking it still the an ideal implementation since it doesn't exceed" +
                    " more than entropy + 2 / n.\n");
        }
        else {
            statDispl.append(",\nmaking it not the ideal implementation since it exceeds" +
                    " entropy + 2 / n.\n");
        }
        statDispl.append("Thank you!");
        JOptionPane.showMessageDialog(null, statDispl.toString());
    }

    /**
     * This method provides all the functionality to the program. It takes the user input and processes it,
     * checking for any errors or bad inputs before converting them into Symbol & ArithmeticSymbol objects
     * and storing them in their respected arrays, and runs the Huffman & Arithmetic coding.
     * @param rawInput
     */
    public static void breakItDown(String rawInput) {
        ArrayList<Symbol> symbAr = new ArrayList<>(); //The main array for Huffman
        ArrayList<ArithmeticSymbol> arithSymbAr = new ArrayList<>(); //the array being processed by arithmetic
        ArrayList<ArithmeticSymbol> arithSymbAr2 = new ArrayList<>(); //the array that keeps it's values until the end
        String[] splitSymbols = rawInput.split(",");
        double arithProbInd = 0.0;
        double totalProb = 0.0;
        String eod = "";
        int eodInd = (int)(Math.random() * (splitSymbols.length - 1));
        try {
            if (splitSymbols.length < 3) {
                throw new NiceTryException();
            }
            for (int i = 0; i < splitSymbols.length; i++) {
                boolean symbGood = false, probGood = false;
                String[] splitSymbol = splitSymbols[i].split(" ");
                if (splitSymbol.length < 2) {
                    throw new IncorrectInputException();
                }
                else {
                    String symb = "";
                    double prob = 0.0;
                    if (splitSymbol[0].length() != 1) {
                        throw new NotIndividualSymbolException();
                    }
                    else {
                        symb = splitSymbol[0].substring(0, 1); //takes first character
                        if (i == eodInd) {
                            eod = symb;
                            System.out.println("EOD: " + eod);
                        }
                        symbGood = true;
                    }
                    if (symbGood) {
                        prob = Double.parseDouble(splitSymbol[1]);
                        if ((totalProb += prob) > 1)
                            throw new ProbabilityGreaterThanOneException();
                        probGood = true;
                    }
                    if (symbGood && probGood) {
                        Symbol newSymb = new Symbol(symb, prob);
                        symbAr.add(newSymb);
                        /**
                         * I had to copy everything manually in order to have an array to be modified
                         * and an original array to use as a key. This was the first way I found that
                         * worked.
                         */
                        ArithmeticSymbol newArithSymb = new ArithmeticSymbol();
                        ArithmeticSymbol newArithSymb2 = new ArithmeticSymbol();
                        newArithSymb.setC(symb);
                        newArithSymb2.setC(symb);
                        newArithSymb.setProb(prob);
                        newArithSymb2.setProb(prob);

                        if (arithSymbAr.size() < 1) {
                            newArithSymb.setLow(arithProbInd);
                            newArithSymb2.setLow(arithProbInd);
                        }
                        else {
                            newArithSymb.setLow(arithSymbAr.get(i - 1).getHigh());
                            newArithSymb2.setLow(arithSymbAr2.get(i - 1).getHigh());
                        }
                        arithProbInd = addBig(arithProbInd, prob);

                        newArithSymb.setHigh(addBig(newArithSymb.getLow(), prob));
                        newArithSymb2.setHigh(addBig(newArithSymb.getLow(), prob));
                        newArithSymb.setRange(subBig(newArithSymb.getHigh(), newArithSymb.getLow()));
                        newArithSymb2.setRange(subBig(newArithSymb2.getHigh(), newArithSymb2.getLow()));
                        arithSymbAr.add(newArithSymb);
                        arithSymbAr2.add(newArithSymb2);

                        if (i == (splitSymbols.length - 1)) {

                            String message = makeRandomMsg(eod, symbAr);
                            System.out.println("Random Message: " +message);

                            JOptionPane.showMessageDialog(null, "The randomly generated message is \'" +message
                                    +"\' with the eod symbol being \'" +eod +"\'");

                            entropy = calcEntropy(message, symbAr);
                            long hStart = System.nanoTime();
                            setUpHuffman(message, symbAr);
                            long hEnd = System.nanoTime();
                            JOptionPane.showMessageDialog(null, huffDispl.toString());
                            huffTime = hEnd - hStart;
                            System.out.println("Huffman elapsed time in nanoseconds: " + huffTime);
                            long aStart = System.nanoTime();
                            setUpArithmetic(eod, message, arithSymbAr, arithSymbAr2);
                            long aEnd = System.nanoTime();
                            arithTime = aEnd - aStart;
                            makeSomeGUIs();
                        }
                    }
                }
            }
        } catch (IncorrectInputException e) {
            JOptionPane.showMessageDialog(null, "input cannot be processed, check to make sure" +
                    " format for input is <a .5,b .25,c .25>");
        } catch (NotIndividualSymbolException e) {
            JOptionPane.showMessageDialog(null, "My program detected you input multiple symbols into one" +
                    ", please make sure format for input is <a .5,b .25,c .25>");
        } catch (ProbabilityGreaterThanOneException e) {
            JOptionPane.showMessageDialog(null, "The Probability can\'t add up to greater than 1");
        }
        catch (NiceTryException e) {
            JOptionPane.showMessageDialog(null, "Nice try, but you need to input at least 3 symbols" +
                    " to see my code actually do something!");
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error running program: " +e.getMessage());
        }

    }



    public static void input() {
        //String userInput = "a 0.4,b 0.2,c .2,d .1,e .05,f .05";
        String oGPane = JOptionPane.showInputDialog("Please input source symbols and their respected probabilities in this format: \'a 0.3,b 0.2,c 0.2,d 0.1\'");
        //oGPane = userInput;
        if (oGPane == null || oGPane.length() < 2) {
            JOptionPane.showMessageDialog(null, "Well, thanks anyway.");
        }
        else {
            breakItDown(oGPane);
        }
    }

    public static void main(String[] args) {
        input();
    }
}