import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

import java.text.DecimalFormat;
import java.util.ArrayList;

/*
 * Created by gauth_000 on 17-Jan-16.
*/

public class DM02Gautham_B_A
{
    public static void main(String[] args)
    {
        class StatData
        {
            private double minimum, maximum, mean, stddev;

            public StatData(double minimum, double maximum, double mean, double stddev)
            {
                this.minimum = minimum;
                this.maximum = maximum;
                this.mean = mean;
                this.stddev = stddev;
            }

            public double getMinimum()
            {
                return minimum;
            }

            public double getMaximum()
            {
                return maximum;
            }

            public double getMean()
            {
                return mean;
            }

            public double getStddev()
            {
                return stddev;
            }
        }

        try
        {
            DecimalFormat format = new DecimalFormat("0.00");
            DataSource dataSource = new DataSource("C:\\Users\\gauth_000\\OneDrive\\Documents\\5th sem\\Data-mining\\Assignment_2\\DataPre-processing\\src\\bank-data.csv");

            Instances instances = dataSource.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1);

            // deleting the attribute "id".
            instances.deleteAttributeAt(0);

            ArrayList<StatData> statistics = new ArrayList<StatData>();

            // now, the attribute at index 0 is "age"
            // age statistics.
            AttributeStats attributeStats = instances.attributeStats(0);
            Stats ageStats = attributeStats.numericStats;
            statistics.add(new StatData(ageStats.min, ageStats.max, ageStats.mean, ageStats.stdDev));

            // income statistics.
            attributeStats = instances.attributeStats(3);
            Stats incomeStats = attributeStats.numericStats;
            statistics.add(new StatData(incomeStats.min, incomeStats.max, incomeStats.mean, incomeStats.stdDev));

            double productSum = 0;
            for (int i = 0; i < instances.numInstances(); ++i)
                productSum += (Double.parseDouble(instances.instance(i).toString().split(",")[0]) - ageStats.mean) *
                        (Double.parseDouble(instances.instance(i).toString().split(",")[3]) - incomeStats.mean);

            double covariance = productSum / instances.numInstances();
            double correlation = covariance / (ageStats.stdDev * incomeStats.stdDev);
            System.out.println("age:\t\t\t\t\t\t\tincome:");
            System.out.println("min :" + format.format(statistics.get(0).getMinimum()) + "\t\t\t\t\t\t" + "min :" + format.format(statistics.get(1).getMinimum()));
            System.out.println("max :" + format.format(statistics.get(0).getMaximum()) + "\t\t\t\t\t\t" + "max :" + format.format(statistics.get(1).getMaximum()));
            System.out.println("mean :" + format.format(statistics.get(0).getMean()) + "\t\t\t\t\t\t" + "mean :" + format.format(statistics.get(1).getMean()));
            System.out.println("stddev. :" + format.format(statistics.get(0).getStddev()) + "\t\t\t\t\t" + "stddev. :" + format.format(statistics.get(1).getStddev()) + "\n");
            System.out.println("covariance : " + format.format(covariance));
            System.out.println("correlation: " + format.format(correlation) + "\n");

            Discretize filter = new Discretize();
            // applying filter on column 4 - income.
            filter.setAttributeIndices("4");

            // setting the number of bins
            filter.setBins(4);

            filter.setInputFormat(instances);
            Instances output = Filter.useFilter(instances, filter);

            // get the cut points for column 4.
            double[] cutPoints = filter.getCutPoints(3);
            AttributeStats outputStats = output.attributeStats(3);

            int[] frequencies = outputStats.nominalCounts;

            // frequency of each range - use nominalCountStats.
            for (int i = 0; i < cutPoints.length; ++i)
                System.out.println("cutpoint " + i + " : " + format.format(cutPoints[i]) + "\tfrequency : " + frequencies[i]);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}