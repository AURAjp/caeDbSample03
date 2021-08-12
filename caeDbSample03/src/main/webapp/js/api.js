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
  let html = "<table><thead><tr><th>ID</th><th>会社名</th><th>商品名</th><th>価格</th><th>最終更新日</th></tr></thead>";
    for (const key in data) {
      html += '<tbody><tr><td>' + data[key]['id'] + '</td><td>'
  + data[key]['company'] + '</td><td>' + data[key]['name'] + '</td><td>'
  + data[key]['price'] + '</td><td>' + (new Date(data[key]['last']).toLocaleString()) + '</td></tr></tbody>';
    }
  html += '</table>';
  $('#allFootprints').append(html);
  });
}

/**
 * <h5>Web APIで1つのデータをGETで保存する (2)</h5>
 * (クロスドメインで動作する)
 */
function submitDataGet(addInfo) {
  let uri = "api/v1/writeData?callback=?";
  if (location.href.substr(0, 4) == "file") {
    uri = uriForFile + uri;
  }

  let parm = {}; // new Array()はダメ！ http://bobobo.bona.jp/blog/?p=16
  parm["company"] = escape($('#myForm [name=company]').val());
  parm["name"]    = escape($('#myForm [name=name]').val());
  parm["price"]   = escape($('#myForm [name=price]').val());
  if (addInfo != null) { parm["name"] = parm["name"] + "(" + addInfo + ")"; }
  if (addInfo != null) { parm["price"] = parm["price"] + "(" + addInfo + ")"; }
  $.getJSON(uri, parm, data => {
    console.log("submitDataGet:" + JSON.stringify(data));
    showAllFootprints();
  });
}