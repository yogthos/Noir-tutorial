$(document).ready(function(){	
	var context = $('#context').val();
    var url = '/get-logs';
    if (context) url = context + url;
    var options = {xaxis: { mode: "time", minTickSize: [1, "minute"]}};
	$.post(url, function(data){
	    $.plot($("#hits-by-time"), [data], options);
	    });		
});