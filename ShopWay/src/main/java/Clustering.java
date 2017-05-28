import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.DBSCANClusterer;
import org.apache.commons.math3.stat.clustering.EuclideanIntegerPoint;

import java.util.List;

/**
 * Created by chfong on 28/5/2017.
 *
 */
public class Clustering {
    private static Clustering ourInstance = new Clustering();

    public static Clustering getInstance() {
        return ourInstance;
    }

    private Clustering() {
    }

    public void cluster(){
        DBSCANClusterer mDBSCANCluster_users = new DBSCANClusterer(1, 3);
        DBSCANClusterer mDBSCANCluster_items = new DBSCANClusterer(10, 1);
        List<Cluster<EuclideanIntegerPoint>> clusters_users =
                mDBSCANCluster_users.cluster(DataSource.getInstance().getClusterPoints_users());
        for(Cluster<EuclideanIntegerPoint> cluster: clusters_users)
            System.out.println(cluster.getPoints().size());
//        List<Cluster<EuclideanIntegerPoint>> clusters_items =
//                mDBSCANCluster_items.cluster(DataSource.getInstance().getClusterPoints_items());
//        System.out.println(clusters_items.size());
    }
}
