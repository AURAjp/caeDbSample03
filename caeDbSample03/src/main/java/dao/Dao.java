package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * <h3>simple sample class for database access object</h3>
 * @author nakano@cc.kumamoto-u.ac.jp
 * @version 2021-07-07 (from 2019-05)
 */
public class Dao {

    private String     dbUri   = null;
    private Properties dbProps = null;

    /**
     * <h5>Constructor</h5>
     * 外部ファイル sys.properties からパラメータを得る
     */
    public Dao() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("sys");
            dbUri = bundle.getString("uri");
            dbProps = new Properties();
            dbProps.put("user"    , bundle.getString("user"));
            dbProps.put("password", bundle.getString("password"));
            dbProps.put("characterEncoding", "UTF8"); // 日本語等ではこれが必要
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * <h5>全てのデータを取得</h5>
     * @return 全データ
     */
    public ArrayList<LinkedHashMap<String,Object>> getAll() {

        ArrayList<LinkedHashMap<String,Object>> ret = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUri, dbProps)) {
            conn.setAutoCommit(true);
            try (Statement state = conn.createStatement()) {
                final String SQL = "SELECT * FROM product ORDER BY id DESC;";
                try (ResultSet rs = state.executeQuery(SQL)) {
                    List<String> key = new ArrayList<>();
                    // カラム名の取得
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        // カラム名のリストをキーに追加
                        key.add(rs.getMetaData().getColumnName(i));
                    }
                    // queryの結果を得る
                    while (rs.next()) {
                        LinkedHashMap<String,Object> line = new LinkedHashMap<>();
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            // javax.jsonがjava.sql.Timestampをサポートしていないため以下で変換している
                            if(rs.getObject(i).getClass().equals(Timestamp.class)) {
                                line.put(key.get(i-1), rs.getObject(i).toString());
                            } else {
                                line.put(key.get(i-1), rs.getObject(i));
                            }
                        }
                        ret.add(line);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ret;
    }
    
    /**
     * <h5> "product"テーブルに1行データを追加</h5>
     * @param line
     * @return ret 追加行数
     */
    public int addLine(LinkedHashMap<String,Object> line) {
        int ret = -1;
        try (Connection conn = DriverManager.getConnection(dbUri, dbProps)) {
            conn.setAutoCommit(true);
            final String SQL = "INSERT INTO product (company, name, price, last) VALUES (?, ?, ?, ?);";
            try (PreparedStatement pStr = conn.prepareStatement(SQL)) {
                pStr.setString(1, line.get("company").toString());
                pStr.setString(2, line.get("name").toString());
                pStr.setInt(3, Integer.valueOf((String) line.get("price")));
                pStr.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pStr.execute();
                ret = pStr.getUpdateCount();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    /**
     * <h5>チェック用mainメソッド</h5>
     * @param args
     */
    public static void main(String[] args) {
        Dao dao = new Dao();
        LinkedHashMap<String,Object> line = new LinkedHashMap<>();
        line.put("company", "calbee");
        line.put("name"   , "potato chips");
        line.put("price"     , "123");
        line.put("last"   , String.format("%1$tF %1$tT", Calendar.getInstance()));
        System.out.println("add = " + dao.addLine(line));
        ArrayList<LinkedHashMap<String,Object>>  all = dao.getAll();
        for(LinkedHashMap<String,Object> l : all) {
            for(String key : l.keySet()) {
                System.out.print(key+":"+l.get(key)+", ");
            }
            System.out.println("");
        }
    }
    
}