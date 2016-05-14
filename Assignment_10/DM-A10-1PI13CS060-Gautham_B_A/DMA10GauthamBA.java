/*
* Name - Gautham B A
* USN - 1PI13CS060
*/
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.DBSCAN;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gauth_000 on 21-Mar-16.
 */
public class DMA10GauthamBA
{
    // Data structure of a row
    private static class Tuple
    {
        public int x, y, r, g, b;

        public Tuple()
        {
        }

        public Tuple(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public Tuple(int x, int y, int r, int g, int b)
        {
            this.x = x;
            this.y = y;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public void setR(int r)
        {
            this.r = r;
        }

        public void setX(int x)
        {
            this.x = x;
        }

        public void setY(int y)
        {
            this.y = y;
        }

        public void setG(int g)
        {
            this.g = g;
        }

        public void setB(int b)
        {
            this.b = b;
        }

        @Override
        public String toString()
        {
            return x+","+y+","+r+","+g+","+b;
        }
    }

    // Computes RGB values given a pixel
    public static void compute_rgb(int pixel, Tuple tuple)
    {
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        tuple.setR(red);
        tuple.setG(green);
        tuple.setB(blue);
    }

    // Digitizes the buffered image object in the form (x, y, r, g, b)
    public static ArrayList<Tuple> digitize_image(BufferedImage bufferedImage)
    {
        ArrayList<Tuple> tuples = new ArrayList<>();

        for (int i = 0; i < bufferedImage.getWidth(); ++i)
            for (int j = 0; j < bufferedImage.getHeight(); ++j)
            {
                Tuple tuple = new Tuple(i, j);
                compute_rgb(bufferedImage.getRGB(i, j), tuple);
                tuples.add(tuple);
            }

        return tuples;
    }

    // Creating the schema for clustering
    public static FastVector create_schema()
    {
        // Declare two numeric attributes
        Attribute xCoordinate = new Attribute("x");
        Attribute yCoordinate = new Attribute("y");
        Attribute red = new Attribute("r");
        Attribute green = new Attribute("g");
        Attribute blue = new Attribute("b");

        // Declare the feature vector
        FastVector fvWekaAttributes = new FastVector(5);
        fvWekaAttributes.addElement(xCoordinate);
        fvWekaAttributes.addElement(yCoordinate);
        fvWekaAttributes.addElement(red);
        fvWekaAttributes.addElement(green);
        fvWekaAttributes.addElement(blue);

        return fvWekaAttributes;
    }

    public static void main(String[] args)
    {
        String path = "C:\\Users\\gauth_000\\OneDrive\\Documents\\6th sem\\Data-mining\\Assignment_10\\dm-a10.gif";
        try
        {
            File file = new File(path);
            BufferedImage bufferedImage = ImageIO.read(file);

            ArrayList<Tuple> rawData = digitize_image(bufferedImage);
            FastVector schema = create_schema();

            // Create an empty training set
            Instances trainingSet = new Instances("Rel", schema, 100);

            // add the instances
            for (Tuple tuple : rawData)
                trainingSet.add(get_instance(schema, tuple));

            // Start clustering
            DBSCAN dbscan = new DBSCAN();
            dbscan.setEpsilon(0.4);
            dbscan.setMinPoints(1);
            dbscan.setDatabase_Type("weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase");
            dbscan.setDatabase_distanceType("weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject");
            dbscan.buildClusterer(trainingSet);

            HashMap<Integer, Tuple> centroids = new HashMap<>();
            HashMap<Integer, ArrayList<Instance>> clusters = cluster_instances(trainingSet, dbscan);

            // Compute the centroid of each cluster and save it
            for (Integer key : clusters.keySet())
                centroids.put(key, compute_avg(clusters.get(key)));

            // Remove the noise in the image and write it to a file
            write_image(bufferedImage,centroids, clusters);


            ClusterEvaluation clusterEvaluation = new ClusterEvaluation();
            clusterEvaluation.setClusterer(dbscan);
            clusterEvaluation.evaluateClusterer(trainingSet);

            System.out.println("Number of pixels="+rawData.size());
            System.out.println("Number of clusters="+clusterEvaluation.getNumClusters());
            System.out.println("Cluster no.\tsize\t\tavg colour(RGB)");
            for (Integer key : centroids.keySet())
                System.out.println(key + "\t\t\t" + clusters.get(key).size()+"\t\t\t"+ centroids.get(key).r+", "+centroids.get(key).g+", "+centroids.get(key).b);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Write the buffered image to file with the corresponding centroid values
    public static void write_image(BufferedImage bufferedImage, HashMap<Integer, Tuple> centroids, HashMap<Integer, ArrayList<Instance>> clusters)
    {
        for(Integer key:clusters.keySet())
        {
            Tuple avgVal=centroids.get(key);
            for(Instance instance:clusters.get(key))
            {
                int rgb= avgVal.r;
                rgb=(rgb<<8)+avgVal.g;
                rgb=(rgb<<8)+avgVal.b;

                String[] row=instance.toString().split(",");
                bufferedImage.setRGB(Integer.parseInt(row[0]), Integer.parseInt(row[1]), rgb);
            }
        }

        File output=new File("output.jpg");
        try
        {
            ImageIO.write(bufferedImage, "jpg", output);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // Compute the mean of a given cluster
    public static Tuple compute_avg(ArrayList<Instance> data)
    {
        Tuple avgTuple = new Tuple();

        for (Instance instance : data)
        {
            String[] row = instance.toString().split(",");
            avgTuple.x += Double.parseDouble(row[0]);
            avgTuple.y += Double.parseDouble(row[1]);
            avgTuple.r += Double.parseDouble(row[2]);
            avgTuple.g += Double.parseDouble(row[3]);
            avgTuple.b += Double.parseDouble(row[4]);
        }
        avgTuple.x /= data.size();
        avgTuple.y /= data.size();
        avgTuple.r /= data.size();
        avgTuple.g /= data.size();
        avgTuple.b /= data.size();

        return avgTuple;
    }

    // Cluster the instances of the whole dataset.
    public static HashMap<Integer, ArrayList<Instance>> cluster_instances(Instances instances, DBSCAN dbscan)
    {
        HashMap<Integer, ArrayList<Instance>> clusters = new HashMap<>();

        try
        {
            for (int i = 0; i < instances.numInstances(); ++i)
            {
                Instance instance = instances.instance(i);
                int cluster = dbscan.clusterInstance(instance);
                if (clusters.containsKey(cluster))
                    clusters.get(cluster).add(instance);
                else
                {
                    ArrayList<Instance> clusterList = new ArrayList<>();
                    clusterList.add(instance);
                    clusters.put(cluster, clusterList);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return clusters;
    }

    // Get an instance object from a tuple
    public static Instance get_instance(FastVector fvWekaAttributes, Tuple tuple)
    {
        Instance instance = new Instance(5);
        instance.setValue((Attribute) fvWekaAttributes.elementAt(0), tuple.x);
        instance.setValue((Attribute) fvWekaAttributes.elementAt(1), tuple.y);
        instance.setValue((Attribute) fvWekaAttributes.elementAt(2), tuple.r);
        instance.setValue((Attribute) fvWekaAttributes.elementAt(3), tuple.g);
        instance.setValue((Attribute) fvWekaAttributes.elementAt(4), tuple.b);

        return instance;
    }
}