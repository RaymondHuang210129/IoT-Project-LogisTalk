var csmapi = require('./csmapi.js');
var dai = require('./dai.js');

var odf_data = [];

const ida = (function(){
        csmapi.set_endpoint ('https://demo.iottalk.tw');
   //      var profile = {
		 //    'dm_name': '0516228_Bulb',          
			// 'idf_list':[Dummy_Sensor],
			// 'odf_list':[Dummy_Control],
		 //        'd_name': undefined,
   //      };

        function FP_WS_trace_pull (data) {
            // odf_data = data[0];

            console.log(data[0]);
            let temp_data = JSON.parse(data[0]);
            let temp_flag = 1;
            for(var i = 0; i < odf_data.length; i++) {
                if(odf_data[i].driverId == temp_data.driverId) {
                    temp_flag = 0;
                    odf_data[i].lat = temp_data.lat;
                    odf_data[i].long = temp_data.long;
                    break;
                }
            }

            if(temp_flag) {
                odf_data.push(temp_data);
            }

            module.exports.odf_data = odf_data;
        }

        function FP_WS_msg_push (data) {
            // console.log('push data');
            return data;
        }

        var profile = {
            'dm_name': 'FP_WebServer',          
            'idf_list':[FP_WS_msg_push],
            // 'odf_list':[Luminance, Color_O],
            'odf_list':[FP_WS_trace_pull],
            'd_name': "FP_WebServer",
        };
		
        var r = 255 ;
        var g = 255;
        var b = 0;
        var lum = 100;


        // function draw () {
        //     var rr = Math.floor((r * lum) / 100);
        //     var gg = Math.floor((g * lum) / 100);
        //     var bb = Math.floor((b * lum) / 100);
        //     $('.bulb-top, .bulb-middle-1, .bulb-middle-2, .bulb-middle-3, .bulb-bottom, .night').css(
        //         {'background': 'rgb('+ rr +', '+ gg +', '+ bb +')'}
        //     );
        // }

        // function Luminance(data) {
        //     lum = data[0];
        //     draw();
        // }

        // function Color_O(data) {
        //     r = data[0];
        //     g = data[1];
        //     b = data[2];
        //     draw();
        // }

        // function Dummy_Sensor(){
        //     return Math.random();
        // }

        // function Dummy_Control(data){
        //    $('.ODF_value')[0].innerText=data[0];
        // }
      
/*******************************************************************/                
        function ida_init(){
	    console.log(profile.d_name);
        // $('font')[0].innerText = profile.d_name;
	}
        var ida = {
            'ida_init': ida_init,
        }; 
        dai(profile,ida);     
});

module.exports = {
    ida: ida,
    odf_data: odf_data
}
