var mysql = require("mysql");

var con = mysql.createConnection({
	host: "iot.cwfqbsb3eks0.us-east-1.rds.amazonaws.com",
	user: "iot_user",
	password: "resu_toi",
	port: 3306,
	database: "iot",
	multipleStatements: true
});


con.connect(function(err) {
	if (err) {
		console.log('mysql server connect error');
		return;
	}
	console.log('mysql server connect success');
});

module.exports = con;