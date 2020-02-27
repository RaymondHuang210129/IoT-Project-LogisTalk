const request = require('request');

var csmapi = (function () {
    var ENDPOINT = null;

    function set_endpoint (endpoint) {
        ENDPOINT = endpoint;
    }

    function get_endpoint () {
        return ENDPOINT;
    }

    function register (mac_addr, profile, callback) {

        var options = {
            uri: ENDPOINT +'/'+ mac_addr,
            method:'POST',
            json: {'profile': profile},
            headers:{
                'Content-Type': 'application/json; charset=utf-8',
            }
        };

        request(options, function(err, res, body){
            if(callback){
                if (res.statusCode == 200) {
                    if(callback)
                        callback(true);
                }
                else{
                    if(callback){
                        callback(false);
                    }
                    console.log(body);
                }
            }
        });

        // let options = {
        //     uri: ENDPOINT +'/'+ mac_addr,
        //     json: true,     
        //     body: JSON.stringify({'profile': profile})
        // };

        // request.post(options, function(error, response, body) {
        //     if(error) {
        //         console.log("register error:");
        //         console.log(error);
        //         if (callback) {
        //             callback(false);
        //         }
        //     }
        //     else {
        //         if (callback) {
        //             callback(true, body.d_name, body.password);
        //         }
        //     }
        // });


        // $.ajax({
        //     type: 'POST',
        //     url: ENDPOINT +'/'+ mac_addr,
        //     data: JSON.stringify({'profile': profile}),
        //     contentType:"application/json; charset=utf-8",
        // }).done(function (result) {
        //     if (callback) {
        //         callback(true, result.d_name, result.password);
        //     }
        // }).fail(function () {
        //     if (callback) {
        //         callback(false);
        //     }
        // });
    }

    function deregister (mac_addr, callback) {
        var options = {
            url: ENDPOINT +'/'+ mac_addr,
            method:'DELETE',
            headers:{
                'Content-Type': 'application/json; charset=utf-8',
            }
        };
        request(options, function(err, res, body){
            if(callback){
                if (res.statusCode == 200) {
                    if(callback)
                        callback(true);
                }
                else{
                    if(callback)
                        callback(false);
                    console.log(body);
                }
            }
        });

        // let options = {
        //     uri: ENDPOINT +'/'+ mac_addr,
        //     json: true
        // };

        // request.del(options, function(error, response, body) {
        //     if(error) {
        //         console.log("deregister error:");
        //         console.log(error);
        //         if (callback) {
        //             callback(false);
        //         }
        //     }
        //     else {
        //         if (callback) {
        //             callback(true);
        //         }
        //     }
        // });

        // $.ajax({
        //     type: 'DELETE',
        //     url: ENDPOINT +'/'+ mac_addr,
        //     contentType:"application/json; charset=utf-8",
        // }).done(function () {
        //     if (callback) {
        //         callback(true);
        //     }
        // }).fail(function () {
        //     if (callback) {
        //         callback(false);
        //     }
        // });
    }

    function pull (mac_addr, password, odf_name, callback) {

        // let options = {
        //     uri: ENDPOINT +'/'+ mac_addr,
        //     json: true
        // };

        // request.del(options, function(error, response, body) {
        //     if(error) {
        //         console.log("deregister error:");
        //         console.log(error);
        //         if (callback) {
        //             callback(false);
        //         }
        //     }
        //     else {
        //         if (callback) {
        //             callback(true);
        //         }
        //     }
        // });

        // process.exit()


        var options = {
            url: ENDPOINT +'/'+ mac_addr +'/'+ odf_name,
            method:'GET',
            headers:{
                'Content-Type': 'application/json; charset=utf-8',
                'password-key': password,
            }
        };
        request(options, function(err, res, body){
            if(callback){
                if (res.statusCode == 200) {
                    if(callback){
                        body = JSON.parse(body);
                        callback(body['samples']);
                    }
                }
                else{
                    console.log(body);
                }
            }
        });

        // let options = {
        //     uri: ENDPOINT +'/'+ mac_addr +'/'+ odf_name,
        //     json: true,     
        //     headers: {'password-key': password}
        // };

        // request.get(options, function(error, response, body) {
        //     if(error) {
        //         console.log("pull error:");
        //         console.log(error);
        //         if (callback) {
        //             callback([], error);
        //         }
        //     }
        //     else {
        //         console.log("pull");
        //         console.log(body);
        //         console.log("pull");

        //         if (typeof body === 'string' && body.startsWith("mac_addr not found:")) {
        //             console.log('good');
        //             callback([], body);
        //         }
        //         else if (typeof body === 'string') {
        //             body = JSON.parse(body);
        //         }

        //         if (callback) {
        //             callback(body['samples']);
        //         }
        //     }
        // });

        // $.ajax({
        //     type: 'GET',
        //     url: ENDPOINT +'/'+ mac_addr +'/'+ odf_name,
        //     contentType:"application/json; charset=utf-8",
        //     headers: {'password-key': password},
        // }).done(function (obj) {
        //     if (typeof obj === 'string') {
        //         obj = JSON.parse(obj);
        //     }

        //     if (callback) {
        //         callback(obj['samples']);
        //     }
        // }).fail(function (error) {
        //     if (callback) {
        //         callback([], error);
        //     }
        // });
    }

    function push (mac_addr, password, idf_name, data, callback) {

        console.log('csmapi idf_name: ' + idf_name + ' data: ' + data);
        var options = {
            url: ENDPOINT +'/'+ mac_addr +'/' + idf_name,
            method:'PUT',
            json:{'data': data},
            headers:{
                'Content-Type': 'application/json; charset=utf-8',
                'password-key': password,
            }
        };
        request(options, function(err, res, body){
            if(callback){
                if (res.statusCode == 200) {
                    callback(true);
                }
                else{
                    callback(false);
                    console.log(body);
                }
            }
        });

        // let options = {
        //     uri: ENDPOINT +'/'+ mac_addr +'/' + idf_name,
        //     json: true,     
        //     body: JSON.stringify({'data': data}),
        //     headers: {'password-key': password}
        // };

        // request.put(options, function(error, response, body) {
        //     if(error) {
        //         console.log("push error:");
        //         console.log(error);
        //         if (callback) {
        //             callback(false);
        //         }
        //     }
        //     else {
        //         if (callback) {
        //             callback(true);
        //         }
        //     }
        // });

        // $.ajax({
        //     type: 'PUT',
        //     url: ENDPOINT +'/'+ mac_addr +'/'+ idf_name,
        //     data: JSON.stringify({'data': data}),
        //     contentType:"application/json; charset=utf-8",
        //     headers: {'password-key': password},
        // }).done(function () {
        //     if (callback) {
        //         callback(true);
        //     }
        // }).fail(function () {
        //     if (callback) {
        //         callback(false);
        //     }
        // });
    }

    return {
        'set_endpoint': set_endpoint,
        'get_endpoint': get_endpoint,
        'register': register,
        'deregister': deregister,
        'pull': pull,
        'push': push,
    };
})();

module.exports = csmapi;
