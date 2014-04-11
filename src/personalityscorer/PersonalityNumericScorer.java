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
 *
 * @author Firoj Alam
 */
public class PersonalityNumericScorer {
    private ArrayList rmseList = new ArrayList();
    public PersonalityNumericScorer() {
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
                String[] refLabArr = strArr[1].split("#");
                String predID = strArr[5];
                String[] predLabArr = strArr[6].split("#");
                if (!refID.equals(predID) && refLabArr.length != predLabArr.length) {
                    System.err.println("\nPlease check instance ids and labels in your input file at line " + i);
                } else {
                    String result[] = new String[refLabArr.length + predLabArr.length];
                    System.arraycopy(refLabArr, 0, result, 0, refLabArr.length);
                    System.arraycopy(predLabArr, 0, result, refLabArr.length, predLabArr.length);
                    instanceList.add(result);
                }
                i = i + 1;
            }//end read file
            fileRead.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Please check your input file. Check that whether it is exist or not.");
            Logger.getLogger(PersonalityNumericScorer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PersonalityNumericScorer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instanceList;
    }

    /**
     * Compute the results and print to the standard output.
     * @param list 
     */
    public void compute(ArrayList list){
        try {
            String[] tmp = (String[]) list.get(0);
            int length = tmp.length;
            length = length / 2;
            StringBuilder result = new StringBuilder();
            String[] arr = {"Extra", "Neuro", "Agree", "Cons", "Open"};
            //my $len = @{$matrix[0]};
            double pre = 0.0, re = 0.0, f1 = 0.0;
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            System.out.println("Class\tRMSE");
            for (int col = 0; col < length; col++) {
                int colPred = col + length;
                String str = computeRMSE(list, col, colPred);
                System.out.println(arr[col] + "\t" + str);
            }
            double meanPre = calMean(rmseList);
            System.out.println("Avg\t" + twoDForm.format(meanPre) + "\t");

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
     * Reads the reference and predicted labels from the multidimensional array-list then compute root-mean square error
     * @param list - reference and predicted labels list
     * @param colRef - index of the reference label
     * @param colPred - index of the predicted label
     * @return string containing the value of precision, recall and f-measure
     */
    private String computeRMSE(ArrayList list, int colRef, int colPred) {
        String str = "";
        try{
        double errorSum=0;
        for (int row = 0; row < list.size(); row++) {
            String[] inst = (String[])list.get(row);
            double ac = Double.parseDouble(inst[colRef]);
            double pred = Double.parseDouble(inst[colPred]);
            errorSum += Math.pow(pred-ac,2);
        }
        double rmse = Math.sqrt(errorSum/list.size());        
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            rmseList.add(rmse);
            str = twoDForm.format(rmse);
        }catch(Exception ex){
            System.err.println("Problem in calculating RMSE. Please check the format of your input file.");
        }
        return str;
    }    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String fName = "/Users/firojalam/Study_PhD_projects/Personality_traits_WCPR14/scorers-wcpr14/myp_fabio-numeric.txt";
        PersonalityNumericScorer app1 = new PersonalityNumericScorer();        
        ArrayList list = app1.readInstanceList(fName);
        app1.compute(list);        
    }
    
}
