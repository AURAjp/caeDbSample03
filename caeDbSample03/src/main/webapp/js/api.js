/**
 * JavaScript for caeDbSample03
 * @version 20210707 @since 20190704
 * @author nakano@cc.kumamoto-u.ac.jp
 */

const uriForFile = "http://localhost:8080/caeDbSample03/";

/**
 * jQuery: html(このファイルの末尾まで)が読み込まれた後で実行されるプロセス
 */
$(function() {
	showAllFootprints();
	$('#myForm').submit(function(event) {
		// HTMLでの送信をキャンセル
		event.preventDefault();
		submitDataGet();
		//	submitDataPost();
	});
})

/**
	* <h5>REST型のAPIで同期的にJSONP型データを取得する</h5>
	* JSONP型のWeb APIで全てのデータを取得し，下部に表示する．
	*/
function showAllFootprints() {
	let uri = "api/v1/getAll?callback=?";
	if (location.href.substr(0, 4) == "file") {
		uri = uriForFile + uri;
	}
	$.getJSON(uri, function(data) {
		$('#allFootprints').children().remove();
		for (const key in data) {
			$('#allFootprints').append(
				'<div style="margin: 5px; border:1px solid #ff0000; background-color: #ffeeee;">'
				+ data[key]['id'] + ' <b>' + data[key]['name'] + '</b> ('
				+ (new Date(data[key]['last']).toLocaleString()) + ' from ' + data[key]['ip'] + ')<br>'
				+ '<div style="background-color: #ffffff; color: #666666;">'
				+ data[key]['text'] + '</div></div>\n');
		}
	});
}

/**
 * <h5>Web APIで1つのデータをGETで保存する (2)</h5>
 * (クロスドメインで動作する)
 */
function submitDataGet(addInfo) {
	console.log("addInfo" + addInfo);
	let uri = "api/v1/writeData?callback=?";
	if (location.href.substr(0, 4) == "file") {
		uri = uriForFile + uri;
	}
	// http://localhost:8080/caeDbSample03/api/v1/writeData?callback=jQuery112403590900840136737_1628691044451&name=vcxz&text=vzxcv&_=1628691044453
	console.log("URL" + uri);
	let parm = {}; // new Array()はダメ！ http://bobobo.bona.jp/blog/?p=16
	parm["company"] = escape($('#myForm [name=company]').val());
	parm["text"] = escape($('#myForm [name=text]').val());
	if (addInfo != null) parm["text"] = parm["text"] + "(" + addInfo + ")";
	$.getJSON(uri, parm, function(data) {
		console.log("submitDataGet:" + JSON.stringify(data));
		showAllFootprints();
	});
}