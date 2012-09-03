$(document).ready(function(){	
	var context = $('#context').val();
    var url = context ? context + '/get-logs' : '/get-logs';
    var options = {xaxis: { mode: "time", minTickSize: [1, "minute"]}};
	$.post(url, function(data){
	    $.plot($("#hits-by-time"), [data], options);
	    });		
});