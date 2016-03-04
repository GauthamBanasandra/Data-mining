/*
Name - Gautham B A
USN - 1PI13CS060
Class - 6th sem A section
 */
import weka.classifiers.trees.BFTree;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by gauth_000 on 22-Feb-16.
 */
public class DM06Gautham_B_A
{
    public static void main(String[] args)
    {
        String path = "C:\\Users\\gauth_000\\OneDrive\\Documents\\5th sem\\Data-mining\\Assignment_6\\bank-data2.arff";
        Random random = new Random();
        try
        {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(path);
            // Training set
            Instances trainingSet = dataSource.getDataSet();

            // Test set
            HashSet<Instance> testSet = new HashSet<Instance>();

            // Get 10 random tuples
            while (testSet.size() <= 10)
                if (random.nextBoolean())
                {
                    int rand = random.nextInt(trainingSet.numInstances());
                    if (!testSet.contains(trainingSet.instance(rand)))
                    {
                        testSet.add(trainingSet.instance(rand));
                        trainingSet.delete(rand);
                    }
                }

            // Set PEP as class attribute
            trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

            J48 j48 = new J48();

            // Build J48 tree
            j48.buildClassifier(trainingSet);

            BFTree bfTree=new BFTree();

            // Build BF tree
            bfTree.buildClassifier(trainingSet);

            // Printing the trees
            System.out.println(j48);
            System.out.println(bfTree);

            // Classify the test set
            System.out.println("\nClass\tC4.5 prediction\tGini prediction");
            for (Instance aTestSet : testSet)
            {
                System.out.print(aTestSet.classAttribute().value((int) aTestSet.classValue()) + "\t\t\t");
                System.out.print(aTestSet.classAttribute().value((int) j48.classifyInstance(aTestSet))+"\t\t\t\t");
                System.out.println(aTestSet.classAttribute().value((int) bfTree.classifyInstance(aTestSet)));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}