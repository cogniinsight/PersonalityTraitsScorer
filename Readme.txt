To run the PersonalityScorer from the command line please check the following options:

usage: java -jar <PersonalityScorer.jar>
with the following options:

 -f          please use this option to get f measures.
 -i <FILE>   please use a file containing reference and predicted labels.
 -n          please use this option to get root mean square error.
 -u          please use this option to get un-weighted average.
e.g., 
java -jar PersonalityScorer.jar -i myp_fabio-nvrda2cmlf.txt -u -f
OR
java -jar PersonalityScorer.jar -i myp_fabio-numeric.txt -n

Please keep in mind that the input format for numeric and class predictions
is different. Therefore, when you select -n option you might not select
other two options (i.e., -f -u) and vice-versa.
