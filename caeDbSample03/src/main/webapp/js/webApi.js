/**
 * JavaScript for caeDbSample03
 * @version 20210707 @since 20190704
 * @author nakano@cc.kumamoto-u.ac.jp
 */

const uriForFile = "http://localhost:8080/caeDbSample03/";

/**
 * jQuery: Processes after loading html (whole this file)
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
	* <h5>Function: get JSONP data by Restful api / REST型のAPIで同期的にJSONP型データを取得する</h5>
	* Get all data by JSONP type web API and show it at the bottom.<br />
	* JSONP型のWeb APIで全てのデータを取得し，下部に表示する．
	*/
function showAllFootprints() {
	let uri = "api/v1/getAll?callback=?";
	if (location.href.substr(0, 4) == "file") uri = uriForFile + uri;
	$.getJSON(uri, function(data) {
		$('#allFootprints').children().remove();
		for (const key in data) {
			$('#allFootprints').append(
				'<div style="margin: 5px; border:1px solid #ff0000; background-color: #ffeeee;">'
				+ data[key]['id'] + ' <b>' + data[key]['name'] + '</b> ('
				+ (new Date(data[key]['last']).toLocaleString()) + ' from ' + data[key]['ip'] + ')<br />'
				+ '<div style="background-color: #ffffff; color: #666666;">'
				+ data[key]['text'] + '</div></div>\n');
		}
	});
}

/**
 * <h5>Function: save a data (through GET) by Web API (2) / Web APIで1つのデータをGETで保存する (2)</h5>
 * (It can work for crosdomain access / クロスドメインで動作する)
 */
function submitDataGet(addInfo) {
	let uri = "api/v1/writeData?callback=?";
	if (location.href.substr(0, 4) == "file") uri = uriForFile + uri;
	let parm = {}; // should be Object, new Array() can not work / new Array()はダメ！ http://bobobo.bona.jp/blog/?p=16
	parm["name"] = escape($('#myForm [name=name]').val());
	parm["text"] = escape($('#myForm [name=text]').val());
	if (addInfo != null) parm["text"] = parm["text"] + "(" + addInfo + ")";
	$.getJSON(uri, parm, function(data) {
		console.log("submitDataGet:" + JSON.stringify(data));
		showAllFootprints();
	});
}

/**
 * !!! NOT USED THIS TIME / 今回は使用しない !!!
 * <h5>Function: save a data (through POST) by Web API (1) / Web APIで1つのデータをPOSTで保存する (1)</h5>
 * (it can not work for crosdomain access / クロスドメインでは動作しない)
 */
function submitDataPost() {
	let uri = "api/v1/postData";
	if (location.href.substr(0, 4) == "file") uri = uriForFile + uri;
	let parm = {}; // should be Object, new Array() can not work / new Array()はダメ！ http://bobobo.bona.jp/blog/?p=16
	parm["name"] = $('#myForm [name=name]').val();
	parm["text"] = $('#myForm [name=text]').val();
	console.log(JSON.stringify(parm));
	$.ajax({
		url: uri,
		type: "POST", // HTTP POST method / HTTP POSTメソッド
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		async: false, // synchronous communication for refrecting data / 結果を反映させるため同期通信
		data: JSON.stringify(parm),
		success: function(data, status) {
			console.log("submitData:" + JSON.stringify(data));
			showAllFootprints();
		}
	});
}
