package rest;
import java.util.*;

import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.JSONP;

import dao.Dao;

/**
 *  <h3>Rest sample with database access / データベースアクセスを伴うRestサンプル</h3>
 *  @version 2021-07-07 (from 2019-07-03)
 *  @author nakano@cc.kumamoto-u.ac.jp
 *  Bug: callback名を指定するとJSONPではなくJSONで返してしまう．<br />
 *  If callback name is specified, this returns JSON instead of JSONP.
 *  
 */
@Path("/v1")
public class RestServiceV1 {
	Dao dao = new Dao();

	/**
	 * <h5>Get all data (JSON/JSONP) / 全データの取得 (JSON/JSONP)</h5>
	 * @param callback : automatically filled callback function's name / 自動で入るcallback関数名 
	 * @return all data / 全てのデータ
	 */
	@Path("getAll")
	@Produces({"application/x-javascript", "application/json"})
	@JSONP(queryParam = "callback")
    @GET
    public String getAll(@QueryParam("callback") String callback) {
       return Json.createArrayBuilder(dao.getAll()).build().toString();
	}

	/**
	 * <h5>write one data (GET version) / 1つデータを書き込む(GET版)</h5>
	 * @param callback : automatically filled callback function's name / 自動で入るcallback関数名
	 * @param name : "name" data / "name"データ
	 * @param text : "text" data / "text"データ
	 * @param request : http request parameter / httpリクエストパラメータ
	 * @return : received data plus IP address and my name / 受信データ＋IPアドレス，myname
	 * This version supports JSONP. 
	 */
	@Path("writeData")
	@Produces({"application/x-javascript", "application/json"})
	@JSONP(queryParam = "callback")
    @GET
    public String writeData(@QueryParam("callback") String callback, @QueryParam("name") String name,
    		@QueryParam("text") String text, @Context HttpServletRequest request) {
		LinkedHashMap<String,Object> var = new LinkedHashMap<String,Object>();
		var.put("name", escapeHtml(convertToOiginal(name)));
		var.put("text", escapeHtml(convertToOiginal(text)));
		var.put("ip", request.getRemoteAddr());
		dao.addLine(var);
//		System.out.println("writeData: name=" + var.get("name") + ", text=" + var.get("text") + ", ip="+var.get("ip"));
		return Json.createObjectBuilder(var).build().toString();
 	}

	/**
	 * <h5>convert from web encoded text including unicode to utf8 / <br />
	 * Unicodeを含むWebエンコードされた文字列から元の文字列に変換する ("%u3042" -> "あ", "%20" -> " ")</h5>
	 * 参考サイト: http://qiita.com/sifue/items/039846cf8415efdc5c92
	 * @param unicode
	 * @return decoded string
	 */
	private static String convertToOiginal(String unicode)
	{
	    String[] codeStrs = unicode.split("%");
	    String encodedText = codeStrs[0]; // if not unicode
//	    for(String s: codeStrs) System.out.println(s);
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

	/**
	 * <h5>write one data / 1つデータを書き込む</h5>
	 * !!! NOT USED THIS TIME / 今回は使用せず !!!
	 * @param var : "name" and "text" data from browser / ブラウザから送信された"name"と"text"データ
	 * @param request : http request parameter / httpリクエストパラメータ
	 * @return : received data plus IP address / 受信データ＋IPアドレス
	 * This does not support JSONP.
	 */
	@Path("postData")
	@Produces(MediaType.APPLICATION_JSON)
    @POST
	public String postData(LinkedHashMap<String,Object> var, @Context HttpServletRequest request) {
		var.put("ip", request.getRemoteAddr());
		dao.addLine(var);
//		System.out.println("postData: name=" + var.get("name") + ", text=" + var.get("text") + ", ip="+var.get("ip"));
		return Json.createObjectBuilder(var).build().toString();
	}

}