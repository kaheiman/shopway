/**
 * Created by chfong on 27/5/2017.
 *
 */
public class Main {
    public static void main(String[] args){
        DataSource.getInstance().loadTable();
        Clustering.getInstance().cluster();
    }
}
