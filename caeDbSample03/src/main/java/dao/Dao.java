package dao;
import java.util.*;
import java.sql.*;

/**
 * <h3>simple sample class for database access object</h3>
 * @author nakano@cc.kumamoto-u.ac.jp
 * @version 2021-07-07 (from 2019-05)
 */
public class Dao {
	private String dbDriver = null;
	private String dbUri = null;
	private Properties dbProps = null;

	/**
	 * <h5>Constructor</h5>
	 * read parameters for database access from external file sys.properties /
	 * 外部ファイル sys.properties からパラメータを得る
	 */
	public Dao() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("sys");
			dbDriver = bundle.getString("driver");
			dbUri = bundle.getString("uri");
			dbProps = new Properties();
			dbProps.put("user", bundle.getString("user"));
			dbProps.put("password", bundle.getString("password"));
			dbProps.put("characterEncoding", "UTF8"); // need for multibyte chars. / 日本語等ではこれが必要
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * getAll() method
	 * get all data from "comment" table
	 */
	/**
	 * <h5>Get all data / 全てのデータを取得</h5>
	 * @return all data / 全データ
	 */
	public ArrayList<LinkedHashMap<String,Object>> getAll() {
		ArrayList<LinkedHashMap<String,Object>> ret = new ArrayList<LinkedHashMap<String,Object>>();
		final String queryStr = "select * from comment order by last desc";
		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;
		try {
			// Instantiation of mySQL jdbc driver. / mySQLのjdbcドライバのインスタンス化
			Class.forName(dbDriver).getDeclaredConstructor().newInstance();
//			System.out.println("Class.forName(dbDriver).newInstance().toString()="+Class.forName(dbDriver).newInstance().toString());
			conn = DriverManager.getConnection(dbUri, dbProps);
			conn.setAutoCommit(true);
			state = conn.createStatement();
			rs = state.executeQuery(queryStr); // executing query / queryの実行
			ArrayList<String> key = new ArrayList<String>();
			// obtain column name / カラム名の取得
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				key.add(rs.getMetaData().getColumnName(i)); // add column names to keys / カラム名のリストをキーに追加
			}
			// obtain query results / queryの結果を得る
			while (rs.next()) {
				LinkedHashMap<String,Object> line = new LinkedHashMap<String,Object>();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					// The following conversion is required because javax.json does not support java.sql.Timestamp.
					// javax.jsonがjava.sql.Timestampをサポートしていないため以下で変換している
					if(rs.getObject(i).getClass().equals(java.sql.Timestamp.class)) {
						line.put(key.get(i-1), rs.getObject(i).toString());
					} else {
						line.put(key.get(i-1), rs.getObject(i));
					}
				}
				ret.add(line);
			}
			//for exception
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				rs.close();
				state.close();
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
//		System.out.println("ret=" + ret.toString());
		return ret;
	}
	
	/**
	 * <h5>add a line to "comment" table / "comment"テーブルに1行データを追加</h5>
	 * @param line
	 * @return ret number of added lines / 追加行数
	 */
	public int addLine(LinkedHashMap<String,Object> line) {
		int ret = -1;
		Connection conn = null;
		Statement state = null;
		try {
			// Instantiation of mySQL jdbc driver. / mySQLのjdbcドライバのインスタンス化
			Class.forName(dbDriver).getDeclaredConstructor().newInstance(); 
//			System.out.println("Class.forName(dbDriver).newInstance().toString()="+Class.forName(dbDriver).newInstance().toString());
			conn = DriverManager.getConnection(dbUri, dbProps);
			conn.setAutoCommit(true);
			state = conn.createStatement();
			PreparedStatement pStr = null;
			final String sStr = "insert into comment (name, text, ip, last) values (?, ?, ?, ?)";
			pStr = conn.prepareStatement(sStr);
			pStr.setString(1, line.get("name").toString());
			pStr.setString(2, line.get("text").toString());
			pStr.setString(3, line.get("ip").toString());
			pStr.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
			pStr.execute();
			ret = pStr.getUpdateCount();
			//for exception
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				state.close();
				conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * <h5>main method for check / チェック用mainメソッド</h5>
	 * @param args
	 */
	public static void main(String[] args) {
		Dao dao = new Dao();
		LinkedHashMap<String,Object> line = new LinkedHashMap<String,Object>();
		line.put("name", "なまえ");
		line.put("text", "テキスト");
		line.put("ip", "133.95.1.1");
		line.put("last", String.format("%1$tF %1$tT", Calendar.getInstance()));
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