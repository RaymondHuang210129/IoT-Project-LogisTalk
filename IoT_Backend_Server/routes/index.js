var express = require('express');
var passport = require('passport');
var bcrypt = require('bcrypt');
var path = require('path');
var ida = require('../ida.js');
var dan = require('../dan.js');
var router = express.Router();

const saltRounds = 12;

async function hashPassword (unhashPassword) {

	var result = await new Promise((resolve, reject) => {
		bcrypt.hash(unhashPassword, saltRounds, function(err, hash) {
			if (err) {
				reject(err);
			}
			resolve(hash);
		});
	});
	return result;
};

/* GET home page. */
router.get('/', (req, res) => res.sendFile(path.resolve('client', 'build', 'index.html')));
router.get('/manage', (req, res) => res.sendFile(path.resolve('client', 'build', 'index.html')));
router.get('/searchOrder', (req, res) => res.sendFile(path.resolve('client', 'build', 'index.html')));
router.get('/customer/*', (req, res) => res.sendFile(path.resolve('client', 'build', 'index.html')));

router.get('/api/v1/orders', function(req, res, next) {

	var db = req.con;

	var sql = "SELECT Orders.orderUID AS id, Orders.status, Orders.address, Orders.establishTime AS orderTime, IF(ISNULL(Orders.driverID), NULL, Drivers.driverName) AS driver, GROUP_CONCAT(Items.itemID,\" \",Items.itemName,\" \",Items.itemPrice,\" \",OrdersItemsXRef.itemNumber) AS itemList, SUM(Items.itemPrice * OrdersItemsXRef.itemNumber) AS total_price " + 
	 		  "FROM ((((Orders " + 
	 		  "INNER JOIN Users ON Users.userID = Orders.userID) " + 
			  "INNER JOIN OrdersItemsXRef ON OrdersItemsXRef.orderID = Orders.orderID) " +
	 		  "INNER JOIN Items ON Items.itemID = OrdersItemsXRef.itemID) " +
			  "LEFT JOIN Drivers ON Drivers.driverID = Orders.driverID) " +
			  "GROUP BY Orders.orderID"


	db.query(sql, function(err, rows) {
		if (err) {
			console.log(err);
			res.json({message: "Something went wrong. Please try again"});
			return;
		}

		let response_json = {
			orders: rows
		}

		let order_list = response_json.orders;

		for(var i = 0; i < order_list.length; i++) {
			let item_list = order_list[i].itemList.split(',');
			var itemList_json_list = [];
			for(var j = 0; j < item_list.length; j++) {
				let temp = item_list[j].split(' ');
				let itemList_json = {
					id: temp[0],
					name: temp[1],
					price: temp[2],
					itemCount: temp[3],
				};
				itemList_json_list.push(itemList_json);
			}
			response_json.orders[i].itemList = itemList_json_list;
		}

		res.setHeader('Access-Control-Allow-Origin', '*');
	    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
	    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');
	    res.setHeader('Access-Control-Allow-Credentials', true);
		res.setHeader('Content-Type', 'application/json');
		res.json(response_json);
	});

});

router.get('/api/v1/manage', function(req, res, next) {
	var db = req.con; 

	db.query('SELECT * FROM Drivers', function(err, rows) {
		if (err) {
			console.log(err);
		}

		for(var i = 0; i < ida.odf_data.length; i++) {
			if(!ida.odf_data[i]["name"]) {
				for(var j = 0; j < rows.length; j++) {
					if(rows[j]["driverID"] == ida.odf_data[i]["driverId"]) {
						ida.odf_data[i]["name"] = rows[j]["driverName"];
					}
				}
			}
		}

		let response_json_list = [];

		for(var k = 0; k < ida.odf_data.length; k++) {
			let json_temp = {};
			json_temp["driverID"] = ida.odf_data[k]["driverId"];
			json_temp["name"] = ida.odf_data[k]["name"];
			json_temp["lat"] = ida.odf_data[k]["lat"];
			json_temp["lng"] = ida.odf_data[k]["long"];
			response_json_list.push(json_temp);
		}

		let response_json = {
			drivers: response_json_list
		}

		console.log(response_json);

		res.setHeader('Access-Control-Allow-Origin', '*');
	    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
	    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');
	    res.setHeader('Access-Control-Allow-Credentials', true);
		res.setHeader('Content-Type', 'application/json');
		res.json(response_json);
	});
});


router.get('/api/v1/search', function(req, res, next) {

	let order_uid = req.query.uid;
	console.log(order_uid);
	var db = req.con;

	var sql = "SELECT Orders.orderUID AS id, Orders.status, Orders.establishTime AS orderTime, Orders.address, IF(ISNULL(Orders.driverID), NULL, Drivers.driverName) AS driver, GROUP_CONCAT(Items.itemID,\" \",Items.itemName,\" \",Items.itemPrice,\" \",OrdersItemsXRef.itemNumber) AS itemList, SUM(Items.itemPrice * OrdersItemsXRef.itemNumber) AS total_price " + 
	 		  "FROM ((((Orders " + 
	 		  "INNER JOIN Users ON Users.userID = Orders.userID) " + 
			  "INNER JOIN OrdersItemsXRef ON OrdersItemsXRef.orderID = Orders.orderID) " +
	 		  "INNER JOIN Items ON Items.itemID = OrdersItemsXRef.itemID) " +
			  "LEFT JOIN Drivers ON Drivers.driverID = Orders.driverID) " +
			  "WHERE Orders.orderUID = ? " +
			  "GROUP BY Orders.orderID"


	db.query(sql, [order_uid], function(err, rows) {
		if (err) {
			console.log(err);
			res.json({message: "Something went wrong. Please try again"});
			return;
		}

		let response_json = {
			orders: rows
		}

		let order_list = response_json.orders;

		for(var i = 0; i < order_list.length; i++) {
			let item_list = order_list[i].itemList.split(',');
			var itemList_json_list = [];
			for(var j = 0; j < item_list.length; j++) {
				let temp = item_list[j].split(' ');
				let itemList_json = {
					id: temp[0],
					name: temp[1],
					price: temp[2],
					itemCount: temp[3],
				};
				itemList_json_list.push(itemList_json);
			}
			response_json.orders[i].itemList = itemList_json_list;
		}

		res.setHeader('Access-Control-Allow-Origin', '*');
	    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
	    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');
	    res.setHeader('Access-Control-Allow-Credentials', true);
		res.setHeader('Content-Type', 'application/json');
		res.json(response_json);
	});

});

var flag = 1;


router.get('/api/customer/order', function(req, res, next) {

	let order_uid = req.query.uid;

	console.log(order_uid);

	var db = req.con;

	var sql = "SELECT Orders.orderUID AS id, Orders.driverID, JSON_OBJECT('lat', Orders.addressLat, 'lng', Orders.addressLong) AS customerAddress, Orders.status, Orders.establishTime AS orderTime, Orders.address, IF(ISNULL(Orders.driverID), NULL, Drivers.driverName) AS driver, GROUP_CONCAT(Items.itemID,\" \",Items.itemName,\" \",Items.itemPrice,\" \",OrdersItemsXRef.itemNumber) AS itemList, SUM(Items.itemPrice * OrdersItemsXRef.itemNumber) AS total_price " + 
	 		  "FROM ((((Orders " + 
	 		  "INNER JOIN Users ON Users.userID = Orders.userID) " + 
			  "INNER JOIN OrdersItemsXRef ON OrdersItemsXRef.orderID = Orders.orderID) " +
	 		  "INNER JOIN Items ON Items.itemID = OrdersItemsXRef.itemID) " +
			  "LEFT JOIN Drivers ON Drivers.driverID = Orders.driverID) " +
			  "WHERE Orders.orderUID = ? " +
			  "GROUP BY Orders.orderID"

	db.query(sql, [order_uid], function(err, rows) {
		if (err) {
			console.log(err);
			res.json({message: "Something went wrong. Please try again"});
			return;
		}

		let response_json = {
			order: rows
		}

		let order_list = response_json.order;

		for(var i = 0; i < order_list.length; i++) {
			let item_list = order_list[i].itemList.split(',');
			var itemList_json_list = [];
			for(var j = 0; j < item_list.length; j++) {
				let temp = item_list[j].split(' ');

				let itemList_json = {
					id: temp[0],
					name: temp[1],
					price: temp[2],
					itemCount: temp[3],
				};
				itemList_json_list.push(itemList_json);
			}
			let customer_address_json = JSON.parse(order_list[i].customerAddress);
			response_json.order[i].customerAddress = customer_address_json;
			response_json.order[i].itemList = itemList_json_list;
		}


		if(rows[0]) {
			if(rows[0]["status"] != 0) {
				let driverAddress_json = {};

				for(var k = 0; k < ida.odf_data.length; k++) {

					if(ida.odf_data[k]["driverId"] == rows[0]["driverID"]) {
						driverAddress_json["lat"] = ida.odf_data[k]["lat"]
						driverAddress_json["lng"] = ida.odf_data[k]["long"]

						k = 100;
					}
				}

				response_json["driverAddress"] = driverAddress_json;
			}
		}
		else {
			console.log(rows);
		}
		


		res.setHeader('Access-Control-Allow-Origin', '*');
	    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
	    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');
	    res.setHeader('Access-Control-Allow-Credentials', true);
		res.setHeader('Content-Type', 'application/json');
		res.json(response_json);
	});


	

});




router.post("/appLogin", function(req, res) {
	passport.authenticate("local-login", function(err, user, info) {
		if (err) {
			res.status(404).json(err);
			return;
		}

		if (user) {
			var db = req.con;
			// generate token in any way
			let token = "testToken";
			db.query('UPDATE Drivers SET driverToken = ? WHERE driverID = ?', [token, user.driverID], function(err, rows) {
				if (err) {
					console.log(err);
					res.status(404).json(err);
					return;
				}
				res.status(200);
				res.json({
					message: "Login Success",
					token: token,
					driverID: user.driverID
				});
			});
		} else {
			res.status(401).json({message: "Login Failure"});
		}
	})(req, res);
});

router.get('/appGetOrders', function(req, res, next){
	let token = req.query.token;
	if(!token) {
		res.json({message: "Not Login yet"});
		return;
	}
	var db = req.con;
	db.query('SELECT * FROM Drivers WHERE driverToken = ?',token , function(err, rows) {
		if (err) {
			console.log(err);
			res.json({message: "Something went wrong. Please try again"});
			return;
		}

		if(rows.length) {
			var sql = "SELECT Orders.orderID, Orders.address,GROUP_CONCAT(Items.itemName,\" \",OrdersItemsXRef.itemNumber,\" \",Items.itemImage) AS itemList, SUM(Items.itemPrice * OrdersItemsXRef.itemNumber) AS orderPrice " + 
			 		  "FROM (((Orders " + 
			 		  "INNER JOIN Users ON Users.userID = Orders.userID) " + 
					  "INNER JOIN OrdersItemsXRef ON OrdersItemsXRef.orderID = Orders.orderID) " +
			 		  "INNER JOIN Items ON Items.itemID = OrdersItemsXRef.itemID) " +
					  "WHERE Orders.status = 0 " + 
					  "GROUP BY Orders.orderID"
			db.query(sql, function(err, rows) {
				if (err) {
					console.log(err);
					res.json({message: "Something went wrong. Please try again"});
					return;
				}

				let response_json = {
					message: "valid token",
					orders: rows
				}

				let order_list = response_json.orders;
				for(var i = 0; i < order_list.length; i++) {
					let item_list = order_list[i].itemList.split(',');
					var itemList_json_list = [];
					for(var j = 0; j < item_list.length; j++) {
						let temp = item_list[j].split(' ');
						let itemList_json = {
							itemName: temp[0],
							itemNumber: temp[1],
							itemImage: temp[2]
						};
						itemList_json_list.push(itemList_json);
					}
					response_json.orders[i].itemList = itemList_json_list;
				}


				res.json(response_json);
			});
		}
		else {
			res.json({message: "invalid token"});
		}
	});
});

router.post('/appSelectOrders' ,function(req, res, next) {
	let token = req.body.token;
	console.log(token);
	if(!token) {
		res.json({message: "Not Login yet"});
		return;
	}
	var db = req.con;
	db.query('SELECT * FROM Drivers WHERE driverToken = ?', token, function(err, rows) {
		if (err) {
			console.log(err);
			res.json({message: "Something went wrong. Please try again"});
			return;
		}

		if(rows.length) {
			var order_id = req.body.orderID;
			let driver_id = req.body.driverID;
			console.log("order_id: "+order_id);
			console.log('driver_id: '+driver_id);
			if(!order_id || !driver_id) {
				res.json({message: "invalid format"});
				return;
			}
			order_id = order_id.split(',').map(Number);

			let sql_params = [];

			sql_params.push(driver_id);
			sql_params.push(1);
			sql_params.push(order_id);

			db.query('UPDATE Orders SET driverID = ?, status = ? WHERE orderID IN (?)',sql_params, function(err, rows) {
				if (err) {
					console.log(err);
					res.json({message: "Something went wrong. Please try again"});
					return;
				}

				let msg = JSON.stringify({
					type: "state_update",
					content: {
						status: 1,
						orderID: order_id
					}
				});

				dan.push('FP_WS_msg_push',[msg]);

				res.json({
					message: "Success",
				});
			});
		}
		else {
			res.json({message: "invalid token"});
		}
	});
});

router.get('/appGetDriverOrders', function(req, res, next) {
	let token = req.query.token;
	var driver_id = req.query.driverID;
	if(!token || !driver_id) {
		res.json({message: "Not Login yet"});
		return;
	}
	var db = req.con;
	db.query('SELECT * FROM Drivers WHERE driverToken = ? AND driverID = ?', [token, driver_id], function(err, rows) {
		if (err) {
			console.log(err);
			res.json({message: "Something went wrong. Please try again"});
			return;
		}
		if (rows.length) {
			var sql = "SELECT Orders.orderID, Orders.address,GROUP_CONCAT(Items.itemName,\" \",OrdersItemsXRef.itemNumber,\" \",Items.itemImage) AS itemList, SUM(Items.itemPrice * OrdersItemsXRef.itemNumber) AS orderPrice " + 
			 		  "FROM (((Orders " + 
			 		  "INNER JOIN Users ON Users.userID = Orders.userID) " + 
					  "INNER JOIN OrdersItemsXRef ON OrdersItemsXRef.orderID = Orders.orderID) " +
			 		  "INNER JOIN Items ON Items.itemID = OrdersItemsXRef.itemID) " +
					  "WHERE Orders.status = 1 " +
					  "AND Orders.driverID = ? "+ 
					  "GROUP BY Orders.orderID"
			db.query(sql, [driver_id], function(err, rows) {
				if (err) {
					console.log(err);
					res.json({message: "Something went wrong. Please try again"});
					return;
				}

				let response_json = {
					message: "valid token",
					orders: rows
				}

				let order_list = response_json.orders;

				for(var i = 0; i < order_list.length; i++) {
					let item_list = order_list[i].itemList.split(',');
					var itemList_json_list = [];
					for(var j = 0; j < item_list.length; j++) {
						let temp = item_list[j].split(' ');
						let itemList_json = {
							itemName: temp[0],
							itemNumber: temp[1],
							itemImage: temp[2]
						};
						itemList_json_list.push(itemList_json);
					}
					response_json.orders[i].itemList = itemList_json_list;
				}


				res.json(response_json);
			});
		}
		else {
			res.json({message: "invalid token"});
		}
	});
});


router.post('/appCompleteOrders' ,function(req, res, next) {
	let token = req.body.token;
	console.log(token);
	if(!token) {
		res.json({message: "Not Login yet"});
		return;
	}
	var db = req.con;
	db.query('SELECT * FROM Drivers WHERE driverToken = ?', token, function(err, rows) {
		if (err) {
			console.log(err);
			res.json({message: "Something went wrong. Please try again"});
			return;
		}

		if(rows.length) {
			var order_id = req.body.orderID;
			let driver_id = req.body.driverID;
			console.log("order_id: "+order_id);
			console.log('driver_id: '+driver_id);
			if(!order_id || !driver_id) {
				res.json({message: "invalid format"});
				return;
			}
			order_id = order_id.split(',').map(Number);

			let sql_params = [];

			sql_params.push(driver_id);
			sql_params.push(-1);
			sql_params.push(order_id);

			db.query('UPDATE Orders SET driverID = ?, status = ? WHERE orderID IN (?)',sql_params, function(err, rows) {
				if (err) {
					console.log(err);
					res.json({message: "Something went wrong. Please try again"});
					return;
				}

				let msg = JSON.stringify({
					type: "state_update",
					content: {
						status: -1,
						orderID: order_id
					}
				});

				dan.push('FP_WS_msg_push',[msg]);

				res.json({
					message: "Success",
				});
			});
		}
		else {
			res.json({message: "invalid token"});
		}
	});
});


module.exports = router;
