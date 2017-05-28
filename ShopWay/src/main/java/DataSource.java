import org.apache.commons.math3.stat.clustering.EuclideanIntegerPoint;

import java.io.*;
import java.util.*;

/**
 * Created by chfong on 27/5/2017.
 *
 */
public class DataSource {
    private static DataSource ourInstance = new DataSource();

    public static DataSource getInstance() {
        return ourInstance;
    }

    private DataSource() {}

    private Map<String, List<String>> pid_uid = new HashMap<>();
    private Map<String, List<String>> uid_oid = new HashMap<>();

    private Map<String, List<String>> oid_pid = new HashMap<>();
    private Map<String, List<String>> uid_pid = new HashMap<>();

    int size_i;
    int size_j;
    int size_i_t;
    int size_i_v;
    private int[][] table_train;
    private int[][] table_verify;

    void loadTable(){
        loadOID();
        loadPID();
        combineUID_PID();
        reduceSizeOfUID_PID();
        revertUID_PID();
        reduceSizeOfPID_UID();
        revertPID_UID();
        convertTable();
    }
    List<EuclideanIntegerPoint> getClusterPoints_items(){
        List<EuclideanIntegerPoint> items = new ArrayList<>();
        for(int j=0; j<size_j; j++){
            int[] item = new int[size_i_t];
            for(int i=0; i<size_i_t; i++){
                item[i] = table_train[i][j];
            }
            items.add(new EuclideanIntegerPoint(item));
        }
        return items;
    }
    List<EuclideanIntegerPoint> getClusterPoints_users(){
        List<EuclideanIntegerPoint> users = new ArrayList<>();
        for(int i=0; i<size_i_t; i++){
            int[] user = new int[size_j];
            for(int j=0; j<size_j; j++){
                user[j] = table_train[i][j];
            }
            users.add(new EuclideanIntegerPoint(user));
        }
        return users;
    }
    public void exportFile(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size_i_t; i++){
            for(int j = 0; j < size_j; j++){
                builder.append(table_train[i][j]);
                if(j < size_j - 1)
                    builder.append(",");
            }
            builder.append("\n");//append new line at the end of the row
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("table_train.csv"));
            writer.write(builder.toString());//save the string representation of the board
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private void loadOID(){
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("orders.csv"));
            String line;
            while((line=br.readLine())!=null){
                String str[] = line.split(",");
                if(!uid_oid.containsKey(str[0])){
                    List<String> oid = new ArrayList<>();
                    oid.add(str[1]);
                    uid_oid.put(str[0],oid);
                } else {
                    uid_oid.get(str[0]).add(str[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadPID(){
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("order_products.csv"));
            String line;
            while((line=br.readLine())!=null){
                String str[] = line.split(",");
                if(!oid_pid.containsKey(str[0])){
                    List<String> pid = new ArrayList<>();
                    pid.add(str[1]);
                    oid_pid.put(str[0],pid);
                } else {
                    oid_pid.get(str[0]).add(str[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void combineUID_PID(){
        for(Map.Entry<String, List<String>> entry: uid_oid.entrySet()){
            String uid = entry.getKey();
            List<String> pids = new ArrayList<>();
            List<String> oids = entry.getValue();
            for (String oid : oids) {
                List<String> _pids = oid_pid.get(oid);
                if(_pids!=null) {
                    pids.addAll(_pids);
                }
            }
            uid_pid.put(uid, pids);
        }
        uid_oid.clear();
        oid_pid.clear();
    }
    private void reduceSizeOfUID_PID(){
        List<String> uid_toBeRemove = new ArrayList<>();
        for(Map.Entry<String, List<String>> entry: uid_pid.entrySet()){
            if(entry.getValue().size()<=18) uid_toBeRemove.add(entry.getKey());
        }
        for (String uid : uid_toBeRemove) {
            uid_pid.remove(uid);
        }
        countUID_PID();
    }
    private void revertUID_PID(){
        for(Map.Entry<String, List<String>> entry: uid_pid.entrySet()){
            String uid = entry.getKey();
            List<String> pids = entry.getValue();
            for (String pid : pids) {
                if(!pid_uid.containsKey(pid)) {
                    List<String> uids = new ArrayList<>();
                    uids.add(uid);
                    pid_uid.put(pid, uids);
                } else {
                    pid_uid.get(pid).add(uid);
                }
            }
        }
    }
    private void reduceSizeOfPID_UID() {
        List<String> pid_toBeRemove = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : pid_uid.entrySet()) {
            if (entry.getValue().size() <= 32) pid_toBeRemove.add(entry.getKey());
        }
        for (String pid : pid_toBeRemove) {
            pid_uid.remove(pid);
        }
        countPID_UID();
    }
    private void revertPID_UID(){
        uid_pid.clear();
        for(Map.Entry<String, List<String>> entry: pid_uid.entrySet()){
            String pid = entry.getKey();
            List<String> uids = entry.getValue();
            for (String uid : uids) {
                if(!uid_pid.containsKey(uid)) {
                    List<String> pids = new ArrayList<>();
                    pids.add(pid);
                    uid_pid.put(uid, pids);
                } else {
                    uid_pid.get(uid).add(pid);
                }
            }
        }
        countUID_PID();
    }
    private void countUID_PID(){
        int n_pids = 0;
        for(Map.Entry<String, List<String>> entry: uid_pid.entrySet()){
            n_pids += entry.getValue().size();
        }
        n_pids /= uid_pid.size();
        System.out.println(uid_pid.size());
        System.out.println(n_pids);
    }
    private void countPID_UID(){
        int n_uids = 0;
        for (Map.Entry<String, List<String>> entry : pid_uid.entrySet()) {
            n_uids += entry.getValue().size();
        }
        n_uids /= pid_uid.size();
        System.out.println(pid_uid.size());
        System.out.println(n_uids);
    }
    private void convertTable(){
        List<String> uids_stack = new ArrayList<>();
        List<String> pids_stack = new ArrayList<>();
        uids_stack.addAll(uid_pid.keySet());
        pids_stack.addAll(pid_uid.keySet());
        size_i = uids_stack.size();
        size_j = pids_stack.size();
        int[][] table = new int[size_i][size_j];
        for(Map.Entry<String, List<String>> entry: uid_pid.entrySet()){
            List<String> pids = entry.getValue();
            String uid = entry.getKey();
            int i = uids_stack.indexOf(uid);
            for(String pid: pids){
                int j = pids_stack.indexOf(pid);
                table[i][j] = 1;
            }
        }
        size_i_t = (int) (size_i*0.8);
        size_i_v = size_i - size_i_t;

        System.out.println(size_j);
        System.out.println(size_i_t);
        System.out.println(size_i_v);
        table_train = new int[size_i_t][size_j];
        table_verify = new int[size_i_v][size_j];
        for(int j=0; j<size_j; j++){
            for(int i=0; i<size_i_t; i++){
                table_train[i][j] = table[i][j];
            }
            for(int i=0; i<size_i_v; i++){
                table_verify[i][j] = table[i+size_i_t][j];
            }
        }
    }
}
