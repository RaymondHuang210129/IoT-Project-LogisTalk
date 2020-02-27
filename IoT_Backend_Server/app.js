var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var flash = require('connect-flash');
var bcrypt = require('bcrypt');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');

var app = express();

var dai = require('./dai.js');
var ida = require('./ida.js');
var dan = require('./dan.js');

ida.ida();

var db = require('./db.js');


var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;
var session = require('express-session');



passport.serializeUser(function (user, done) {
	done(null, user.driverID);
});

passport.deserializeUser(function(id, done) {
	// query database
	db.query("SELECT * FROM `Drivers` WHERE `driverID` = ?", id, function(err, rows) {
		done(err, rows[0]);
	});
});

passport.use('local-login', new LocalStrategy({
	// by default, local strategy uses username and password, and they can be overrode
	usernameField : 'account',
	passwordField : 'password',
	passReqToCallback : true // allows us to pass back the entire request to the callback
	},
	function(req, account, password, done) { // callback with account and password from our form
		db.query("SELECT * FROM `Drivers` WHERE `account` = ?", account, function(err, rows) {
			if(err) {
				console.log('app:錯誤');
				return done(err);
			}

			if(!rows.length) {
				console.log('app:帳號不存在');
				return done(null, false, req.flash('loginMessage', '帳號不存在')); // req.flash is the way to set flashdata using connect-flash
			}

			bcrypt.compare(password, rows[0].password).then(function(res) {
				if(res) {
					console.log("login success");
					return done(null, rows[0]); 
				}
				else {
					console.log("login fail");
					return done(null, false, req.flash('loginMessage', '密碼錯誤')); // create the loginMessage and save it to session as flashdata
				}
			});

		});

	}
));


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use(session({
	secret: 'secret_temp', //not sure what this is
	resave: false,
	saveUninitialized: false
}));

app.use(passport.initialize());
app.use(passport.session());

app.use(flash());

app.use(function(req, res, next) {
	req.con = db;
	next();
});

app.use(express.static('client/build'));
app.use('/', indexRouter);
app.use('/users', usersRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});


module.exports = app;
