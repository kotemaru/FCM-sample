/**
 * GCM アプリサーバ
 * - API
 * -- ?action=register&userId={ユーザID}&regId={端末ＩＤ}
 * -- ?action=unregister&userId={ユーザID}
 * -- ?action=send&userId={ユーザID}&msg={送信メッセージ}
 */
var TAG = "app:"
var Http = require('http');
var Https = require('https');
var Url = require('url');
var QueryString = require('querystring');

var PORT = 8888;
// for GCM : https://console.developers.google.comで生成したAPIキー。
// for FCM : google-service.jsのAPI-KEYをコピー
var API_KEY = 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx';

var deviceMap = {};
function doService(req, res) {
	var url = Url.parse(req.url);
	req.parsedUrl = url;
	req.params = QueryString.parse(url.query);

	var action = req.params.action;
	var registrationId = req.params.regId;
	var userId = req.params.userId;
	var msg = req.params.msg;

	if ("register" == action) {
		// 端末登録、Androidから呼ばれる。
		deviceMap[userId] = registrationId;
		console.log("register:", userId, registrationId);
		res.end();
	} else if ("unregister" == action) {
		// 端末登録解除、Androidから呼ばれる。
		delete deviceMap[userId];
		console.log("unregister:", userId, registrationId);
		res.end();
	} else if ("send" == action) {
		// メッセージ送信。任意の送信アプリから呼ばれる。
		registrationId = deviceMap[userId];
		var postData = {
			registration_ids : [registrationId],
			data : {
				msg : msg
			}
		};
		requestGCM(postData, function(statusCode, response, err) {
			res.statusCode = statusCode;
			var buff = new Buffer(JSON.stringify(response));
			res.setHeader('Content-Type', 'application/json;charset=utf-8');
			res.setHeader('Content-length', buff.length);
			res.write(buff);
			res.end();
		});

	} else {
		res.statusCode = 500;
		res.end();
	}
}

function requestGCM(postData, callback) {
	var buff = new Buffer(JSON.stringify(postData));
	var requestOpts = {
		method : 'POST',
		host : 'gcm-http.googleapis.com', // for GCM-3.0
		path : '/gcm/send',  // for GCM-3.0
		//host : 'fcm.googleapis.com',  // for FCM
		//path : '/fcm/send',  // fot FCM
		port : 443,
		headers : {
			'Content-length' : buff.length,
			'Connection' : 'close',
			'Content-Type' : 'application/json;charset=utf-8',
			'Authorization' : 'key=' + API_KEY
		}
	};
	console.log("GCM request:", postData);

	var svrReq = Https.request(requestOpts, function(svrRes) {
		var rawBody = "";
		svrRes.on('data', function(chunk) {
			rawBody += chunk;
		});
		svrRes.on('end', function() {
			console.log("GCM response:", rawBody);
			if (svrRes.statusCode == 200) {
				callback(svrRes.statusCode, JSON.parse(rawBody));
			} else {
				callback(svrRes.statusCode, rawBody);
			}
		});
	});
	svrReq.on('error', function(e) {
		callback(500, null, e);
	});

	svrReq.write(buff);
	svrReq.end();
}

// HTTPサーバ作成
var server = Http.createServer();
server.on('request', doService);
server.listen(PORT);

console.log("listen porxy", PORT);
