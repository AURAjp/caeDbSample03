package rest;
import java.util.LinkedHashMap;

import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.JSONP;

import dao.Dao;

/**
 *  <h3>データベースアクセスを伴うRestサンプル</h3>
 *  @version 2021-07-07 (from 2019-07-03)
 *  @author nakano@cc.kumamoto-u.ac.jp
 *  Bug: callback名を指定するとJSONPではなくJSONで返してしまう．<br />
 *  
 */
@Path("/v1")
public class RestServiceV1 {
	Dao dao = new Dao();

	/**
	 * <h5>全データの取得 (JSON/JSONP)</h5>
	 * @param callback : 自動で入るcallback関数名 
	 * @return 全てのデータ
	 */
	@Path("getAll")
	@Produces({"application/x-javascript", "application/json"})
	@JSONP(queryParam = "callback")
    @GET
    public String getAll(@QueryParam("callback") String callback) {
       return Json.createArrayBuilder(dao.getAll()).build().toString();
	}

	/**
	 * <h5>1つデータを書き込む(GET版)</h5>
	 * @param callback : 自動で入るcallback関数名
	 * @param name : "name"データ
	 * @param text : "text"データ
	 * @param request : httpリクエストパラメータ
	 * @return : 受信データ＋IPアドレス，myname
	 * This version supports JSONP. 
	 */
	@Path("writeData")
	@Produces({"application/x-javascript", "application/json"})
	@JSONP(queryParam = "callback")
    @GET
    public String writeData(
            @QueryParam("callback") String callback,
            @QueryParam("company") String company,
    		@QueryParam("name") String name,
    		@QueryParam("price") String price,
    		@Context HttpServletRequest request) {

		LinkedHashMap<String,Object> var = new LinkedHashMap<>();
		var.put("company", escapeHtml(convertToOiginal(company)));
		var.put("name"   , escapeHtml(convertToOiginal(name   )));
		var.put("price"  , escapeHtml(convertToOiginal(price  )));
		dao.addLine(var);
		return Json.createObjectBuilder(var).build().toString();
 	}

	/**
	 * <h5>Unicodeを含むWebエンコードされた文字列から元の文字列に変換する ("%u3042" -> "あ", "%20" -> " ")</h5>
	 * 参考サイト: http://qiita.com/sifue/items/039846cf8415efdc5c92
	 * @param unicode
	 * @return decoded string
	 */
	private static String convertToOiginal(String unicode)
	{
	    String[] codeStrs = unicode.split("%");
	    String encodedText = codeStrs[0];
  		int[] codePoints = new int[1];
	    for(int i = 1; i < codeStrs.length; i++) {
	    	if(codeStrs[i].startsWith("u")) {
	    		codePoints[0] = Integer.parseInt(codeStrs[i].substring(1, 5), 16);
	    		encodedText += new String(codePoints, 0, codePoints.length);
	    		if(codeStrs[i].length() > 5) {
	    			encodedText += codeStrs[i].substring(5,codeStrs[i].length());
	    		}
	    	} else {
	    		codePoints[0] = Integer.parseInt(codeStrs[i].substring(0, 2), 16);
	    		encodedText += new String(codePoints, 0, codePoints.length);
	    		if(codeStrs[i].length() > 2) {
	    			encodedText += codeStrs[i].substring(2,codeStrs[i].length());
	    		}
	    	}
	    }
	    return encodedText;
	}

	/**
	 * escape HTML code / HTMLコードをエスケープする (& → &amp;, < → &gt; ...)
	 * @param s code
	 * @return escaped code
	 */
	private static String escapeHtml(String s) {
		return s.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;");
	}

}