import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gauth_000 on 23-Jan-16.
 */
public class DM03Gautham_B_A
{
    // Function to compute mean
    public static double compute_mean(ArrayList<Double> data)
    {
        double sum = 0;
        for (Double val : data)
            sum += val;
        return sum / data.size();
    }

    // Function that mimics select clause of SQL
    public static ArrayList<Double> select(ArrayList<String> instances, String age, String sex, String region, int params)
    {
        ArrayList<Double> data = new ArrayList<Double>();

        for (String instance : instances)
        {
            String[] tuple = instance.split(",");
            if (params == 3)
                if (tuple[0].equals(age) && tuple[1].equals(sex) && tuple[2].equals(region))
                    data.add(Double.parseDouble(tuple[3]));
            if (params == 2)
            {
                if (tuple[0].equals(age) && tuple[1].equals(sex))
                    data.add(Double.parseDouble(tuple[3]));
                else if (tuple[0].equals(age) && tuple[2].equals(region))
                    data.add(Double.parseDouble(tuple[3]));
                else if (tuple[1].equals(sex) && tuple[2].equals(region))
                    data.add(Double.parseDouble(tuple[3]));
            }
            if (params == 1)
            {
                if (tuple[0].equals(age))
                    data.add(Double.parseDouble(tuple[3]));
                else if (tuple[1].equals(sex))
                    data.add(Double.parseDouble(tuple[3]));
                else if (tuple[2].equals(region))
                    data.add(Double.parseDouble(tuple[3]));
            }
        }

        return data;
    }

    public static void main(String[] args)
    {
        DecimalFormat format = new DecimalFormat("0.00");
        try
        {
            // Specify the absolute path to bank-data.csv file
            String path = "C:\\Users\\gauth_000\\OneDrive\\Documents\\5th sem\\Data-mining\\Assignment_3\\bank-data.csv";
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(path);

            Instances instances = dataSource.getDataSet();

            // Deleting all the attributes except age, sex, income and region
            instances.deleteAttributeAt(0);
            instances.deleteAttributeAt(4);
            instances.deleteAttributeAt(4);
            instances.deleteAttributeAt(4);
            instances.deleteAttributeAt(4);
            instances.deleteAttributeAt(4);
            instances.deleteAttributeAt(4);
            instances.deleteAttributeAt(4);

            // Discretizing age into YOUNG, MIDDLE and OLD
            Discretize filter = new Discretize();
            filter.setAttributeIndices("1");
            filter.setBins(3);
            filter.setInputFormat(instances);
            Instances output = Filter.useFilter(instances, filter);
            double[] cutPoints = filter.getCutPoints(0);

            HashMap<String, ArrayList<Double>> ageMap = new HashMap<String, ArrayList<Double>>();
            HashMap<String, ArrayList<Double>> sexMap = new HashMap<String, ArrayList<Double>>();
            HashMap<String, ArrayList<Double>> regionMap = new HashMap<String, ArrayList<Double>>();

            ageMap.put("YOUNG", new ArrayList<Double>());
            ageMap.put("MIDDLE", new ArrayList<Double>());
            ageMap.put("OLD", new ArrayList<Double>());

            sexMap.put("FEMALE", new ArrayList<Double>());
            sexMap.put("MALE", new ArrayList<Double>());

            regionMap.put("INNER_CITY", new ArrayList<Double>());
            regionMap.put("TOWN", new ArrayList<Double>());
            regionMap.put("RURAL", new ArrayList<Double>());
            regionMap.put("SUBURBAN", new ArrayList<Double>());

            ArrayList<String> data = new ArrayList<String>();
            System.out.println("discretizing age into YOUNG, MIDDLE and OLD");
            for (int i = 0; i < instances.numInstances(); ++i)
            {
                String[] tuple = instances.instance(i).toString().split(",");
                double age = Double.parseDouble(tuple[0]);
                if (age < cutPoints[0])
                    tuple[0] = "YOUNG";
                else if (age < cutPoints[1] && age > cutPoints[0])
                    tuple[0] = "MIDDLE";
                else
                    tuple[0] = "OLD";

                ageMap.get(tuple[0]).add(Double.parseDouble(tuple[3]));
                sexMap.get(tuple[1]).add(Double.parseDouble(tuple[3]));
                regionMap.get(tuple[2]).add(Double.parseDouble(tuple[3]));

                data.add(tuple[0] + "," + tuple[1] + "," + tuple[2] + "," + tuple[3]);
                System.out.format("%6s %10s %15s %10s\n", tuple[0], tuple[1], tuple[2], tuple[3]);
            }

            System.out.println("\ncuboid for n="+args[0]);
            int dimension = Integer.parseInt(args[0]);
            // avg_income
            switch (dimension)
            {
                case 0:
                    System.out.println("0-D cuboid : avg_income");
                    AttributeStats attributeStats = instances.attributeStats(3);
                    Stats incomeStats = attributeStats.numericStats;
                    System.out.println(format.format(incomeStats.mean));
                    break;
                case 1:
                    System.out.println("1-D cuboid : avg_income");
                    // 1-D cuboid : age
                    System.out.println("age");
                    System.out.format("%10s%10s%10s\n", "YOUNG", "MIDDLE", "OLD");
                    System.out.format("%10s%10s%10s\n\n",
                            format.format(compute_mean(ageMap.get("YOUNG"))),
                            format.format(compute_mean(ageMap.get("MIDDLE"))),
                            format.format(compute_mean(ageMap.get("OLD"))));

                    // 2-D cuboid : sex
                    System.out.println("sex");
                    System.out.format("%10s%10s\n", "FEMALE", "MALE");
                    System.out.format("%10s%10s\n\n",
                            format.format(compute_mean(sexMap.get("FEMALE"))),
                            format.format(compute_mean(sexMap.get("MALE"))));

                    // 3-D cuboid : region
                    System.out.println("region");
                    System.out.format("%10s%10s%10s%10s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("%10s%10s%10s%10s\n\n",
                            format.format(compute_mean(regionMap.get("INNER_CITY"))),
                            format.format(compute_mean(regionMap.get("TOWN"))),
                            format.format(compute_mean(regionMap.get("RURAL"))),
                            format.format(compute_mean(regionMap.get("SUBURBAN"))));
                    break;

                case 2:
                    System.out.println("2-D cuboid : avg_income");
                    // 2-D cuboid : age v/s sex
                    System.out.println("age v/s sex");
                    System.out.format("%19s%10s\n", "FEMALE", "MALE");
                    System.out.format("YOUNG%15s%10s\n",
                            format.format(compute_mean(select(data, "YOUNG", "FEMALE", "", 2))),
                            format.format(compute_mean(select(data, "YOUNG", "MALE", "", 2))));
                    System.out.format("MIDDLE%14s%10s\n",
                            format.format(compute_mean(select(data, "MIDDLE", "FEMALE", "", 2))),
                            format.format(compute_mean(select(data, "MIDDLE", "MALE", "", 2))));
                    System.out.format("OLD%17s%10s\n\n",
                            format.format(compute_mean(select(data, "OLD", "FEMALE", "", 2))),
                            format.format(compute_mean(select(data, "OLD", "MALE", "", 2))));

                    // 2-D cuboid : age v/s region
                    System.out.println("age v/s region");
                    System.out.format("%21s%7s%10s%11s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("YOUNG%15s%10s%10s%10s\n",
                            format.format(compute_mean(select(data, "YOUNG", "", "INNER_CITY", 2))),
                            format.format(compute_mean(select(data, "YOUNG", "", "TOWN", 2))),
                            format.format(compute_mean(select(data, "YOUNG", "", "RURAL", 2))),
                            format.format(compute_mean(select(data, "YOUNG", "", "SUBURBAN", 2))));
                    System.out.format("MIDDLE%14s%10s%10s%10s\n",
                            format.format(compute_mean(select(data, "MIDDLE", "", "INNER_CITY", 2))),
                            format.format(compute_mean(select(data, "MIDDLE", "", "TOWN", 2))),
                            format.format(compute_mean(select(data, "MIDDLE", "", "RURAL", 2))),
                            format.format(compute_mean(select(data, "MIDDLE", "", "SUBURBAN", 2))));
                    System.out.format("OLD%17s%10s%10s%10s\n\n",
                            format.format(compute_mean(select(data, "OLD", "", "INNER_CITY", 2))),
                            format.format(compute_mean(select(data, "OLD", "", "TOWN", 2))),
                            format.format(compute_mean(select(data, "OLD", "", "RURAL", 2))),
                            format.format(compute_mean(select(data, "OLD", "", "SUBURBAN", 2))));

                    // 2-D cuboid : sex v/s region
                    System.out.println("sex v/s region");
                    System.out.format("%21s%7s%10s%12s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("FEMALE%14s%10s%10s%10s\n",
                            format.format(compute_mean(select(data, "", "FEMALE", "INNER_CITY", 2))),
                            format.format(compute_mean(select(data, "", "FEMALE", "TOWN", 2))),
                            format.format(compute_mean(select(data, "", "FEMALE", "RURAL", 2))),
                            format.format(compute_mean(select(data, "", "FEMALE", "SUBURBAN", 2))));
                    System.out.format("MALE%16s%10s%10s%10s\n\n",
                            format.format(compute_mean(select(data, "", "MALE", "INNER_CITY", 2))),
                            format.format(compute_mean(select(data, "", "MALE", "TOWN", 2))),
                            format.format(compute_mean(select(data, "", "MALE", "RURAL", 2))),
                            format.format(compute_mean(select(data, "", "MALE", "SUBURBAN", 2))));
                    break;

                case 3:
                    System.out.println("3-D cuboid : avg_income");
                    // 3-D cuboid : age v/s sex v/s region (female)
                    System.out.println("age v/s sex v/s region (female)");
                    System.out.format("%21s%7s%10s%12s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("YOUNG%15s%10s%10s%10s\n",
                            format.format(compute_mean(select(data, "YOUNG", "FEMALE", "INNER_CITY", 3))),
                            format.format(compute_mean(select(data, "YOUNG", "FEMALE", "TOWN", 3))),
                            format.format(compute_mean(select(data, "YOUNG", "FEMALE", "RURAL", 3))),
                            format.format(compute_mean(select(data, "YOUNG", "FEMALE", "SUBURBAN", 3))));
                    System.out.format("MIDDLE%14s%10s%10s%10s\n",
                            format.format(compute_mean(select(data, "MIDDLE", "FEMALE", "INNER_CITY", 3))),
                            format.format(compute_mean(select(data, "MIDDLE", "FEMALE", "TOWN", 3))),
                            format.format(compute_mean(select(data, "MIDDLE", "FEMALE", "RURAL", 3))),
                            format.format(compute_mean(select(data, "MIDDLE", "FEMALE", "SUBURBAN", 3))));
                    System.out.format("OLD%17s%10s%10s%10s\n\n",
                            format.format(compute_mean(select(data, "OLD", "FEMALE", "INNER_CITY", 3))),
                            format.format(compute_mean(select(data, "OLD", "FEMALE", "TOWN", 3))),
                            format.format(compute_mean(select(data, "OLD", "FEMALE", "RURAL", 3))),
                            format.format(compute_mean(select(data, "OLD", "FEMALE", "SUBURBAN", 3))));

                    // 3-D cuboid : age v/s sex v/s region (male)
                    System.out.println("age v/s sex v/s region (male)");
                    System.out.format("%21s%7s%10s%12s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("YOUNG%15s%10s%10s%10s\n",
                            format.format(compute_mean(select(data, "YOUNG", "MALE", "INNER_CITY", 3))),
                            format.format(compute_mean(select(data, "YOUNG", "MALE", "TOWN", 3))),
                            format.format(compute_mean(select(data, "YOUNG", "MALE", "RURAL", 3))),
                            format.format(compute_mean(select(data, "YOUNG", "MALE", "SUBURBAN", 3))));
                    System.out.format("MIDDLE%14s%10s%10s%10s\n",
                            format.format(compute_mean(select(data, "MIDDLE", "MALE", "INNER_CITY", 3))),
                            format.format(compute_mean(select(data, "MIDDLE", "MALE", "TOWN", 3))),
                            format.format(compute_mean(select(data, "MIDDLE", "MALE", "RURAL", 3))),
                            format.format(compute_mean(select(data, "MIDDLE", "MALE", "SUBURBAN", 3))));
                    System.out.format("OLD%17s%10s%10s%10s\n\n",
                            format.format(compute_mean(select(data, "OLD", "MALE", "INNER_CITY", 3))),
                            format.format(compute_mean(select(data, "OLD", "MALE", "TOWN", 3))),
                            format.format(compute_mean(select(data, "OLD", "MALE", "RURAL", 3))),
                            format.format(compute_mean(select(data, "OLD", "MALE", "SUBURBAN", 3))));
                    break;
            }

            // count
            switch (dimension)
            {
                case 0:
                    System.out.println("0-D cuboid : count");
                    AttributeStats attributeStats = instances.attributeStats(3);
                    Stats incomeStats = attributeStats.numericStats;
                    System.out.println((incomeStats.count));
                    break;
                case 1:
                    System.out.println("1-D cuboid : count");
                    // 1-D cuboid : age
                    System.out.println("age");
                    System.out.format("%10s%10s%10s\n", "YOUNG", "MIDDLE", "OLD");
                    System.out.format("%10s%10s%10s\n\n",
                            ageMap.get("YOUNG").size(),
                            ageMap.get("MIDDLE").size(),
                            ageMap.get("OLD").size());

                    // 2-D cuboid : sex
                    System.out.println("sex");
                    System.out.format("%10s%10s\n", "FEMALE", "MALE");
                    System.out.format("%10s%10s\n\n",
                            sexMap.get("FEMALE").size(),
                            sexMap.get("MALE").size());

                    // 3-D cuboid : region
                    System.out.println("region");
                    System.out.format("%10s%10s%10s%10s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("%10s%10s%10s%10s\n\n",
                            regionMap.get("INNER_CITY").size(),
                            regionMap.get("TOWN").size(),
                            regionMap.get("RURAL").size(),
                            regionMap.get("SUBURBAN").size());
                    break;

                case 2:
                    System.out.println("2-D cuboid : count");
                    // 2-D cuboid : age v/s sex
                    System.out.println("age v/s sex");
                    System.out.format("%21s%10s\n", "FEMALE", "MALE");
                    System.out.format("YOUNG%15s%10s\n",
                            select(data, "YOUNG", "FEMALE", "", 2).size(),
                            select(data, "YOUNG", "MALE", "", 2).size());
                    System.out.format("MIDDLE%14s%10s\n",
                            select(data, "MIDDLE", "FEMALE", "", 2).size(),
                            select(data, "MIDDLE", "MALE", "", 2).size());
                    System.out.format("OLD%17s%10s\n\n",
                            select(data, "OLD", "FEMALE", "", 2).size(),
                            select(data, "OLD", "MALE", "", 2).size());

                    // 2-D cuboid : age v/s region
                    System.out.println("age v/s region");
                    System.out.format("%21s%10s%10s%11s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("YOUNG%15s%10s%10s%10s\n",
                            select(data, "YOUNG", "", "INNER_CITY", 2).size(),
                            select(data, "YOUNG", "", "TOWN", 2).size(),
                            select(data, "YOUNG", "", "RURAL", 2).size(),
                            select(data, "YOUNG", "", "SUBURBAN", 2).size());
                    System.out.format("MIDDLE%14s%10s%10s%10s\n",
                            select(data, "MIDDLE", "", "INNER_CITY", 2).size(),
                            select(data, "MIDDLE", "", "TOWN", 2).size(),
                            select(data, "MIDDLE", "", "RURAL", 2).size(),
                            select(data, "MIDDLE", "", "SUBURBAN", 2).size());
                    System.out.format("OLD%17s%10s%10s%10s\n\n",
                            select(data, "OLD", "", "INNER_CITY", 2).size(),
                            select(data, "OLD", "", "TOWN", 2).size(),
                            select(data, "OLD", "", "RURAL", 2).size(),
                            select(data, "OLD", "", "SUBURBAN", 2).size());

                    // 2-D cuboid : sex v/s region
                    System.out.println("sex v/s region");
                    System.out.format("%21s%10s%10s%12s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("FEMALE%14s%10s%10s%10s\n",
                            select(data, "", "FEMALE", "INNER_CITY", 2).size(),
                            select(data, "", "FEMALE", "TOWN", 2).size(),
                            select(data, "", "FEMALE", "RURAL", 2).size(),
                            select(data, "", "FEMALE", "SUBURBAN", 2).size());
                    System.out.format("MALE%16s%10s%10s%10s\n\n",
                            select(data, "", "MALE", "INNER_CITY", 2).size(),
                            select(data, "", "MALE", "TOWN", 2).size(),
                            select(data, "", "MALE", "RURAL", 2).size(),
                            select(data, "", "MALE", "SUBURBAN", 2).size());
                    break;

                case 3:
                    System.out.println("3-D cuboid : count");
                    // 3-D cuboid : age v/s sex v/s region (female)
                    System.out.println("age v/s sex v/s region (female)");
                    System.out.format("%21s%10s%10s%12s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("YOUNG%15s%10s%10s%10s\n",
                            select(data, "YOUNG", "FEMALE", "INNER_CITY", 3).size(),
                            select(data, "YOUNG", "FEMALE", "TOWN", 3).size(),
                            select(data, "YOUNG", "FEMALE", "RURAL", 3).size(),
                            select(data, "YOUNG", "FEMALE", "SUBURBAN", 3).size());
                    System.out.format("MIDDLE%14s%10s%10s%10s\n",
                            select(data, "MIDDLE", "FEMALE", "INNER_CITY", 3).size(),
                            select(data, "MIDDLE", "FEMALE", "TOWN", 3).size(),
                            select(data, "MIDDLE", "FEMALE", "RURAL", 3).size(),
                            select(data, "MIDDLE", "FEMALE", "SUBURBAN", 3).size());
                    System.out.format("OLD%17s%10s%10s%10s\n\n",
                            select(data, "OLD", "FEMALE", "INNER_CITY", 3).size(),
                            select(data, "OLD", "FEMALE", "TOWN", 3).size(),
                            select(data, "OLD", "FEMALE", "RURAL", 3).size(),
                            select(data, "OLD", "FEMALE", "SUBURBAN", 3).size());

                    // 3-D cuboid : age v/s sex v/s region (male)
                    System.out.println("age v/s sex v/s region (male)");
                    System.out.format("%21s%10s%10s%12s\n", "INNER_CITY", "TOWN", "RURAL", "SUBURBAN");
                    System.out.format("YOUNG%15s%10s%10s%10s\n",
                            select(data, "YOUNG", "MALE", "INNER_CITY", 3).size(),
                            select(data, "YOUNG", "MALE", "TOWN", 3).size(),
                            select(data, "YOUNG", "MALE", "RURAL", 3).size(),
                            select(data, "YOUNG", "MALE", "SUBURBAN", 3).size());
                    System.out.format("MIDDLE%14s%10s%10s%10s\n",
                            select(data, "MIDDLE", "MALE", "INNER_CITY", 3).size(),
                            select(data, "MIDDLE", "MALE", "TOWN", 3).size(),
                            select(data, "MIDDLE", "MALE", "RURAL", 3).size(),
                            select(data, "MIDDLE", "MALE", "SUBURBAN", 3).size());
                    System.out.format("OLD%17s%10s%10s%10s\n\n",
                            select(data, "OLD", "MALE", "INNER_CITY", 3).size(),
                            select(data, "OLD", "MALE", "TOWN", 3).size(),
                            select(data, "OLD", "MALE", "RURAL", 3).size(),
                            select(data, "OLD", "MALE", "SUBURBAN", 3).size());
                    break;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}