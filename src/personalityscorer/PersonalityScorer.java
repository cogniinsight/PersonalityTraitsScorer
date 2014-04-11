/*
 * This work by Firoj Alam is licensed under a Creative Commons Attribution-NonCommercial 4.0 International License.
 * Permissions beyond the scope of this license may be available by sending an email to firojalam@gmail.com.
 * http://creativecommons.org/licenses/by-nc/4.0/deed.en_US
 * 
 */


package personalityscorer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Calculates precision, recall and f-measure, designed specifically to measures personality traits.
 * It uses two different methods to calculate these measures. 
 * @see PersonalityScorerF1
 * @see PersonalityScorerUA
 * The format of the input file is the following. Contains reference file-id, reference labels followed 
 * by three tokens, then again predicted file-id, predicted labels followed by four tokens:
 * 8cc4acc3bb9b04d1c4a4b4d35ca514ee	nnynn	-	-	-	8cc4acc3bb9b04d1c4a4b4d35ca514ee	ynnnn	-	-	-	-
 * 504862f12f8c000ae4358a10d657e8f5	yyyyy	-	-	-	504862f12f8c000ae4358a10d657e8f5	ynyyy	-	-	-	-
 * 2609216746274fd785c35801fb53e9d8	ynnyy	-	-	-	2609216746274fd785c35801fb53e9d8	ynynn	-	-	-	-
 * The above format would be different for numeric prediction i.e., to calculate root mean square error.
 * @author Firoj Alam
 */
public class PersonalityScorer {

    /**
     * Default constructor
     */
    public PersonalityScorer() {
    }

    /**
     * Checks whether a file is exist or not.
     * @param file a file to check its existence
     */
    public void checkFileExistance(String file) {
        try {
            File f = new File(file);
            if (!f.exists()) {
                System.out.println(f + " file does not exist. Please check the label file.");
                System.exit(0);
            }
        } catch (Exception ex) {
            System.out.println(" Problem in reading the file.");
            System.exit(0);
        }
    }    
    /**
     * Parse the commands with specified format
     * @param args command line arguments
     * @return a hashmap containing arguments and their values
     */
    public HashMap parseCommands(String[] args) {
        HashMap cmdTable = new HashMap();
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();        
        // create the parser
        CommandLineParser parser = new GnuParser();
        // create the Options
        Option labelFile = OptionBuilder.withArgName("FILE")
                .hasArg()
                .withDescription("please use a file containing reference and predicted labels.")
                .create("i");
        Option ua = OptionBuilder.withArgName("un-weighted average")
                .withDescription("please use this option to get un-weighted average.")
                .create("u");
        Option f1 = OptionBuilder.withArgName("f-measures")
                .withDescription("please use this option to get f measures.")
                .create("f");
        Option n = OptionBuilder.withArgName("RMSE")
                .withDescription("please use this option to get root mean square error.")
                .create("n");

        Options options = new Options();
        options.addOption(labelFile);        
        options.addOption(ua);
        options.addOption(f1);
        options.addOption(n);
        String usageString = "java -jar <PersonalityScorer.jar>";
        String header = "with the following options:\n\n";
        String footer = "e.g., java -jar PersonalityScorer.jar -i myp_fabio-nvrda2cmlf.txt -u -f"
                + "\nOR\njava -jar PersonalityScorer.jar -i myp_fabio-numeric.txt -n\n\n"
                +"Please keep in mind that the input format for numeric and class predictions is different."
                +" Therefore, when you select -n option you might not select other two options (i.e., -f -u) and vice-versa.\n\n";
        try {
            // parse the command line arguments
            CommandLine cmds = parser.parse(options, args);
            if(cmds.hasOption("i")){
                this.checkFileExistance(cmds.getOptionValue("i"));
                cmdTable.put("i", cmds.getOptionValue("i"));
            }else{
                formatter.printHelp(usageString, header, options, footer);
                System.exit(0);
            }
            if(cmds.hasOption("n")){
                cmdTable.put("n", cmds.getOptionValue("n"));
                return cmdTable;
            }
            if(cmds.hasOption("u") && cmds.hasOption("f")){
                cmdTable.put("u", cmds.getOptionValue("u"));
                cmdTable.put("f", cmds.getOptionValue("f"));
            }else if(cmds.hasOption("u")){
                cmdTable.put("u", cmds.getOptionValue("u"));
            }else if (cmds.hasOption("f")){
                cmdTable.put("f", cmds.getOptionValue("f"));
            }else if (!cmds.hasOption("f") & !cmds.hasOption("f")){
                cmdTable.put("u", cmds.getOptionValue("u"));
            }            
        } catch (ParseException exp) {
            // Something went wrong
            formatter.printHelp(usageString, header, options, footer);
            System.err.println("Please check the options. " + exp.getMessage()+"\n");
            System.exit(0);
        }
        return cmdTable;
    }        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        PersonalityScorer obj = new PersonalityScorer();
        HashMap cmdTable = obj.parseCommands(args);
        String labelFile = cmdTable.get("i").toString();
        
//        String fName = "/Users/firojalam/Study_PhD_projects/Personality_traits_WCPR14/scorers-wcpr14/myp_fabio-nvrda2cmlf.txt";
//        PersonalityScorerUA app1 = new PersonalityScorerUA();        
//        ArrayList list = app1.readInstanceList(fName);
//        app1.compute(list);
//        PersonalityScorerF1 app2 = new PersonalityScorerF1();        
//        list = app2.readInstanceList(fName);
//        app2.compute(list);
        if(cmdTable.containsKey("n")){
            PersonalityNumericScorer app1 = new PersonalityNumericScorer();
            ArrayList list = app1.readInstanceList(labelFile);
            app1.compute(list); 
            System.out.println("");
        }else if(cmdTable.containsKey("u") && cmdTable.containsKey("f")){
            PersonalityScorerUA app1 = new PersonalityScorerUA();
            ArrayList list = app1.readInstanceList(labelFile);
            app1.compute(list); 
            System.out.println("");
            PersonalityScorerF1 app2 = new PersonalityScorerF1();
            list = app2.readInstanceList(labelFile);
            app2.compute(list);            
        }else if(cmdTable.containsKey("u")){
            PersonalityScorerUA app1 = new PersonalityScorerUA();
            ArrayList list = app1.readInstanceList(labelFile);
            app1.compute(list);                    
        }else if(cmdTable.containsKey("f")){
            PersonalityScorerF1 app2 = new PersonalityScorerF1();
            ArrayList list = app2.readInstanceList(labelFile);
            app2.compute(list);            
        }
    }//end main
}
