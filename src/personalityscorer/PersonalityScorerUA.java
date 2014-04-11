/*
 * This work by Firoj Alam is licensed under a Creative Commons Attribution-NonCommercial 4.0 International License.
 * Permissions beyond the scope of this license may be available by sending an email to firojalam@gmail.com.
 * http://creativecommons.org/licenses/by-nc/4.0/deed.en_US
 * 
 */
package personalityscorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author firojalam
 */
public class PersonalityScorerUA {

    private ArrayList classNames;

    /**
     * Default constructor
     */    
    public PersonalityScorerUA() {
    }

    /**
     * Reads the instances from the input file, then reads the values from the lines of the file. Returns an multidimensional array containing the reference and predicted labels.
     * The format of the input file is the following. Contains reference file-id, reference labels followed 
     * by three tokens, then again predicted file-id, predicted labels followed by four tokens.
     * @return the value of instanceList
     */
    public ArrayList readInstanceList(String fileName) {
        ArrayList instanceList = new ArrayList();

        try {
            BufferedReader fileRead = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String str = "";
            int i = 1;
            while ((str = fileRead.readLine()) != null) {
                String[] strArr = str.split("\\s+");
                String refID = strArr[0];
                char[] refLabArr = strArr[1].toCharArray();
                String predID = strArr[5];
                char[] predLabArr = strArr[6].toCharArray();
                if (!refID.equals(predID) && refLabArr.length != predLabArr.length) {
                    System.err.println("\nPlease check instance ids and labels in your input file at line " + i);
                } else {
                    char result[] = new char[refLabArr.length + predLabArr.length];
                    System.arraycopy(refLabArr, 0, result, 0, refLabArr.length);
                    System.arraycopy(predLabArr, 0, result, refLabArr.length, predLabArr.length);
                    instanceList.add(result);
                }
                i = i + 1;
            }//end read file
            fileRead.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Please check your input file. Check that whether it is exist or not.");
            Logger.getLogger(PersonalityScorerUA.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PersonalityScorerUA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instanceList;
    }
    
    /**
     * Compute the results and print to the standard output.
     * @param list 
     */
    public void compute(ArrayList list) {
        try {
            char[] tmp = (char[]) list.get(0);
            int length = tmp.length;
            length = length / 2;
            StringBuilder result = new StringBuilder();
            String[] arr = {"Extra", "Neuro", "Agree", "Cons", "Open"};
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            ArrayList preList = new ArrayList();
            ArrayList reList = new ArrayList();
            ArrayList f1List = new ArrayList();
            System.out.println("Class\tP(Avg)\tR(Avg)\tF1(Avg)");
            for (int col = 0; col < length; col++) {
                double pre = 0.0, re = 0.0, f1 = 0.0;
                int colPred = col + length;
                int[][] m_ConfusionMatrix = makeConfusionMatrix(list, col, colPred);
                for (int i = 0; i < this.classNames.size(); i++) {
                    pre += this.precision(i, m_ConfusionMatrix);
                    re += this.recall(i, m_ConfusionMatrix);
                    f1 += this.fMeasure(i, m_ConfusionMatrix);
                }
                double P = pre / classNames.size();
                double R = pre / classNames.size();
                double F = pre / classNames.size();
                preList.add(P);
                reList.add(R);
                f1List.add(F);
                System.out.println(arr[col] + "\t" + twoDForm.format(P) + "\t" + twoDForm.format(R) + "\t" + twoDForm.format(F));
            }
            double meanPre = calMean(preList);
            double meanRe = calMean(reList);
            double meanF1 = calMean(f1List);
            System.out.println("Avg\t" + twoDForm.format(meanPre) + "\t" + twoDForm.format(meanRe) + "\t" + twoDForm.format(meanF1));
        } catch (Exception ex) {
            System.out.println("Please check the format of your input file.");
        }
        
    }

    /**
     * Calculate the mean with respect to the number of elements. This is
     * defined as<p/>
     * <
     * pre>
     * sum ---------------------- number elements
     * </pre>
     *
     * @return the mean
     */
    public double calMean(ArrayList list) {
        double sum = 0.0, n = 0.0, mean = 0.0;
        for (int i = 0; i < list.size(); i++) {
            sum += Double.parseDouble(list.get(i).toString());
        }
        double meanPre = sum / list.size();
        return meanPre;
    }

    /**
     * Calculate the precision with respect to a particular class. This is
     * defined as<p/>
     * <
     * pre>
     * correctly classified positives
     * ------------------------------
     *  total predicted as positive
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the precision
     */
    public double precision(int classIndex, int[][] m_ConfusionMatrix) {

        double correct = 0, total = 0;
        for (int i = 0; i < this.classNames.size(); i++) {
            if (i == classIndex) {
                correct += m_ConfusionMatrix[i][classIndex];
            }
            total += m_ConfusionMatrix[i][classIndex];
        }
        if (total == 0) {
            return 0;
        }
        return correct / total;
    }

    /**
     * Calculate the recall with respect to a particular class. This is defined
     * as<p/>
     * <
     * pre>
     * correctly classified positives
     * ------------------------------
     *      total positives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the recall
     */
    public double recall(int classIndex, int[][] m_ConfusionMatrix) {

        double correct = 0, total = 0;
        for (int j = 0; j < this.classNames.size(); j++) {
            if (j == classIndex) {
                correct += m_ConfusionMatrix[classIndex][j];
            }
            total += m_ConfusionMatrix[classIndex][j];
        }
        if (total == 0) {
            return 0;
        }
        return correct / total;
    }

    /**
     * Calculate the F-Measure with respect to a particular class. This is
     * defined as<p/>
     * <
     * pre>
     * 2 * recall * precision ---------------------- recall + precision
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the F-Measure
     */
    public double fMeasure(int classIndex, int[][] m_ConfusionMatrix) {

        double precision = precision(classIndex, m_ConfusionMatrix);
        double recall = recall(classIndex, m_ConfusionMatrix);
        if ((precision + recall) == 0) {
            return 0;
        }
        return 2 * precision * recall / (precision + recall);
    }

    /**
     * Reads the reference and predicted labels from the multidimensional array-list then compute precision,recall and f-measure
     * @param list - reference and predicted labels list
     * @param colRef - index of the reference label
     * @param colPred - index of the predicted label
     * @return int[][] confusion matrix containing tp,fp, tn, fn
     */
    private int[][] makeConfusionMatrix(ArrayList list, int colRef, int colPred) {
        this.classNames = new ArrayList();
        int[][] m_ConfusionMatrix = null;
        for (int row = 0; row < list.size(); row++) {
            char[] inst = (char[]) list.get(row);
            if (!this.classNames.contains(inst[colRef])) {
                classNames.add(inst[colRef]);
            }
        }
        m_ConfusionMatrix = new int[classNames.size()][classNames.size()];
        for (int row = 0; row < list.size(); row++) {
            char[] inst = (char[]) list.get(row);
            char ac = inst[colRef];
            char pred = inst[colPred];

                for (int r = 0; r < this.classNames.size(); r++) {
                    if (classNames.get(r).equals(ac)) {
                        for (int col = 0; col < this.classNames.size(); col++) {
                            if (classNames.get(col).equals(pred)) {
                                m_ConfusionMatrix[r][col] += 1;
                                break;
                            }
                        }
                    }
                }//end for loop updating confusion matrix                                    

        }
        return m_ConfusionMatrix;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String fName = "/Users/firojalam/Study_PhD_projects/Personality_traits_WCPR14/scorers-wcpr14/myp_fabio-nvrda2cmlf.txt";
        PersonalityScorerUA app1 = new PersonalityScorerUA();        
        ArrayList list = app1.readInstanceList(fName);
        app1.compute(list);
        PersonalityScorerF1 app2 = new PersonalityScorerF1();        
        list = app2.readInstanceList(fName);
        app2.compute(list);
        
    }

}
