$(document).ready(function(){	
    var options = {xaxis: { mode: "time", minTickSize: [1, "minute"]}};
	$.post('/get-logs', function(data){
	    $.plot($("#hits-by-time"), [data], options);
	    });		
});