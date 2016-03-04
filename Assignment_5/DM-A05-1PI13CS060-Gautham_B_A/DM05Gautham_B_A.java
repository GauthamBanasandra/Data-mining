/*
* Name : Gautham B A
* USN : 1PI13CS060
* Class : 6th sem
* Section : A
*/

import weka.associations.Apriori;
import weka.associations.FPGrowth;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by gauth_000 on 07-Feb-16.
 */
public class DM05Gautham_B_A
{
    public static void main(String[] args)
    {
        try
        {
            // Path to the .arff file
            String path = "C:\\Users\\gauth_000\\OneDrive\\Documents\\5th sem\\Data-mining\\Assignment_5\\Constained association mining\\data\\supermarket.arff";
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(path);

            // Deleting 'total' attribute.
            Instances instances = dataSource.getDataSet();
            instances.deleteAttributeAt(instances.numAttributes() - 1);

            // Using FPGrowth algorithm.
            FPGrowth fpGrowth = new FPGrowth();

            // Choosing 'lift' as the metric.
            SelectedTag tag = new SelectedTag(1, Apriori.TAGS_SELECTION);
            fpGrowth.setMetricType(tag);

            // Setting lift to 1.2
            fpGrowth.setMinMetric(1.2);

            // Setting minimum support to 0.3
            fpGrowth.setLowerBoundMinSupport(0.3);

            // Call to build association rules.
            fpGrowth.buildAssociations(instances);

            List<FPGrowth.AssociationRule> rules = fpGrowth.getAssociationRules();
            ArrayList<Rule> validRules = new ArrayList<Rule>();

            for (FPGrowth.AssociationRule rule : rules)
            {
                boolean valid = false;

                // Checking for vegetables in premise.
                for (FPGrowth.BinaryItem item : rule.getPremise())
                    if (item.toString().equals("vegetables=t"))
                    {
                        valid = true;
                        break;
                    }

                // Checking for absence of vegetables in consequence.
                if (valid)
                    for (FPGrowth.BinaryItem item : rule.getConsequence())
                        if (!item.toString().equals("vegetables=t"))
                        {
                            valid = true;
                            break;
                        }

                if (valid)
                    validRules.add(new Rule
                            (
                                    rule.getPremise().toString(),
                                    rule.getConsequence().toString(),
                                    rule.getTotalSupport(),
                                    rule.getMetricValue(),
                                    (double) rule.getPremiseSupport() / rule.getConsequenceSupport()
                            ));
            }

            // Sorting the rules based on confidence.
            Collections.sort(validRules, new RuleComparator());

            // Printing the output.
            for (Rule rule : validRules)
                System.out.println(rule.toString() + "\n");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

// Class representing each rule.
class Rule
{
    private String premise, consequence;
    private double totalSupport, lift, confidence;

    public Rule(String premise, String consequence, double totalSupport, double lift, double confidence)
    {
        this.premise = premise;
        this.consequence = consequence;
        this.totalSupport = totalSupport;
        this.lift = lift;
        this.confidence = confidence;
    }

    public double getConfidence()
    {
        return confidence;
    }

    @Override
    public String toString()
    {
        DecimalFormat format = new DecimalFormat("0.00");

        return "Premise: " + premise.replaceAll("\\[|\\]|=t", "") + '\n' +
                "Consequence: " + consequence.replaceAll("\\[|\\]|=t", "") + '\n' +
                "TotalSupport: " + totalSupport +
                "\nLift: " + format.format(lift) +
                "\nConfidence: " + format.format(confidence);
    }
}

// Comparator for sorting in descending order.
class RuleComparator implements Comparator<Rule>
{
    @Override
    public int compare(Rule o1, Rule o2)
    {
        if (o1.getConfidence() > o2.getConfidence())
            return -1;
        else
            return 1;
    }
}