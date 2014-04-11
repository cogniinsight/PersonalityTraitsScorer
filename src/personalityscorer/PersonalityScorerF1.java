/*
 * This work by Firoj Alam is licensed under a Creative Commons Attribution-NonCommercial 4.0 International License.
 * Permissions beyond the scope of this license may be available by sending an email to firojalam@gmail.com.
 * http://creativecommons.org/licenses/by-nc/4.0/deed.en_US
 * 
 */


package personalityscorer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculates precision, recall and f-measure, designed specifically to measures personality traits.
 * The annotation labels for personality traits might be 2 or 3.
 * @author Firoj Alam
 */
public class PersonalityScorerF1 {
    private ArrayList classNames;         
    private ArrayList preList = new ArrayList();
    private ArrayList reList = new ArrayList();
    private ArrayList f1List = new ArrayList();
    private ArrayList accList = new ArrayList();    
    
    
    /**
     * Default constructor
     */
    public PersonalityScorerF1() {
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
            int i=1;
            while ((str = fileRead.readLine()) != null) {
                String[] strArr = str.split("\\s+");
                String refID = strArr[0];
                char [] refLabArr = strArr[1].toCharArray();
                String predID = strArr[5];
                char [] predLabArr = strArr[6].toCharArray();
                if(!refID.equals(predID) && refLabArr.length!=predLabArr.length){
                    System.err.println("\nPlease check instance ids and labels in your input file at line "+i );
                }else{
                    char result[] = new char[refLabArr.length + predLabArr.length];
                    System.arraycopy(refLabArr, 0, result, 0, refLabArr.length);
                    System.arraycopy(predLabArr, 0, result, refLabArr.length, predLabArr.length);
                    instanceList.add(result);
                }
                i = i+1;
            }//end read file
            fileRead.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Please check your input file. Check that whether it is exist or not.");
            Logger.getLogger(PersonalityScorerF1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PersonalityScorerF1.class.getName()).log(Level.SEVERE, null, ex);
        }                
        return instanceList;
    }

    /**
     * Compute the results and print to the standard output.
     * @param list 
     */
    public void compute(ArrayList list){
        try {
            char[] tmp = (char[]) list.get(0);
            int length = tmp.length;
            length = length / 2;
            StringBuilder result = new StringBuilder();
            String[] arr = {"Extra", "Neuro", "Agree", "Cons", "Open"};
            double pre = 0.0, re = 0.0, f1 = 0.0;
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            System.out.println("Class\tP\tR\tF1");
            for (int col = 0; col < length; col++) {
                int colPred = col + length;
                String str = computePreReF1(list, col, colPred);
                System.out.println(arr[col] + "\t" + str);
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
     * Reads the reference and predicted labels from the multidimensional array-list then compute precision,recall and f-measure
     * @param list - reference and predicted labels list
     * @param colRef - index of the reference label
     * @param colPred - index of the predicted label
     * @return string containing the value of precision, recall and f-measure
     */
    private String computePreReF1(ArrayList list, int colRef, int colPred) {
        double tp=0,fp=0,fn=0,tn=0;
        String str="";
        this.classNames = new ArrayList();
        int[][] m_ConfusionMatrix = null;
        for (int row = 0; row < list.size(); row++) {
            char[] inst = (char[]) list.get(row);
            if (!this.classNames.contains(inst[colRef])) {
                classNames.add(inst[colRef]);
            }
        }
        if(this.classNames.size()==2){
            for (int row = 0; row < list.size(); row++) {
                char[] inst = (char[]) list.get(row);
                char ac = inst[colRef];
                char pred = inst[colPred];
                if (ac == pred && pred != 'o') {
                    tp++;
                } else if (ac != pred && pred != 'o') {
                    fp++;
                } else {
                    fn++;
                }
            }//end for loop        
        }else if(this.classNames.size()==3){
            for (int row = 0; row < list.size(); row++) {
                char[] inst = (char[]) list.get(row);
                char ac = inst[colRef];
                char pred = inst[colPred];
                if (ac == pred && pred != 'o') {
                    tp++;
                } else if (ac != pred && pred != 'o') {
                    fp++;
                } else if(ac != 'o' && pred == 'o'){
                    fn++;
                }
            }//end for loop                
        }
        try{
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            double pre = (tp / (tp + fp));
            double re = (tp / (tp + fn));
            double f1 = 2 * ((pre * re) / (pre + re));
            double acc = tp / (tp + fp + fn);
            preList.add(pre);
            reList.add(re);
            f1List.add(f1);
            accList.add(acc);
            str = twoDForm.format(pre) + "\t" + twoDForm.format(re) + "\t" + twoDForm.format(f1);        
        }catch(Exception ex){
            System.err.println("Problem in precision, recall and f-measure calculation.");
        }
        return str;
    }
    
    
}
