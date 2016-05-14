/*
Name - Gautham B A
USN - 1PI13CS060
Section - 'A'
 */
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.InterquartileRange;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gauth_000 on 19-Apr-16.
 */
public class Outlier
{
    public static void main(String[] args)
    {
        try
        {
            final String path = "./data/winequality-white.csv";
            File file = new File(path);
            CSVLoader csvLoader = new CSVLoader();
            csvLoader.setSource(file);

            Instances instances = new Instances(csvLoader.getDataSet());
            final int numAttributes = instances.numAttributes();

            InterquartileRange interquartileRange = new InterquartileRange();
            String[] options = {"-R", "first-last", "-O", "3.0", "-E", "6.0", "-P"};
            interquartileRange.setOptions(options);
            interquartileRange.setInputFormat(instances);

            // Applying IQR
            Instances instancesOutliers = Filter.useFilter(instances, interquartileRange);

            // Displaying attributes and the corresponding number of outliers
            System.out.printf("%-20s\t%-20s\n", "Attribute", "Outliers");
            for (int i = 0, j = 0; j < numAttributes; i += 2, ++j)
            {
                AttributeStats attributeStats = instancesOutliers.attributeStats(numAttributes + i);
                System.out.printf("%-20s\t%-20s\n", instancesOutliers.attribute(j).name(), attributeStats.nominalCounts[1]);
            }

            interquartileRange.setDetectionPerAttribute(false);
            interquartileRange.setInputFormat(instances);
            Instances cumulativeOutliers = Filter.useFilter(instances, interquartileRange);

            // Outliers and their description
            HashMap<String, ArrayList<String>> outliersDict = new HashMap<>();
            for (int i = 0; i < cumulativeOutliers.numInstances(); i++)
                if (cumulativeOutliers.instance(i).stringValue(cumulativeOutliers.attribute(12)).equals("yes"))
                {
                    ArrayList<String> outlierAttributes = new ArrayList<>();
                    for (int j = 12; j < numAttributes * 2; j += 2)
                        if (instancesOutliers.instance(i).stringValue(instancesOutliers.attribute(j)).equals("yes"))
                            outlierAttributes.add(instancesOutliers.attribute(j).name());
                    outliersDict.put(instances.instance(i).toString(), outlierAttributes);
                }

            System.out.printf("\n%-60s\t%-20s\n", "Outliers", "Descriptive label");
            for (String key : outliersDict.keySet())
                System.out.printf("%-60s\t%-20s\n", key, outliersDict.get(key));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}