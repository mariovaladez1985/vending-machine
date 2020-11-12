
function addDenomination()
{
	$.ajax({
		url : "/add-to-balance",
		data: {"denomination" : $("#denomination").val()},
		dataType: "json"
	}) .done(function( data ) {
	    var response =data;
	    if(response.status == "success")
    	{
	    	$("#cashBalance").html(response.cashBalance);
	    	$("#alertContainer").css("display","none");
    	}
	    else{
	    	$("#alertContainer").css("display","");
	    	$("#alertText").html(response.error);
	    	$("#alertText").attr("class", "alert alert-danger");
	    }
	}).fail(function(data) {
	    alert( "Unexpected error: " + JSON.parse(data) );
	});
}

function selectProduct()
{
	$.ajax({
		url : "/select-product",
		data: {"productCode" : $("#productCode").val()},
		dataType: "json"
	}) .done(function( data ) {
	    var response =data;
	    if(response.status == "success")
    	{
	    	$("#cashBalance").html(response.cashBalance);
	    	
	    	$("#alertContainer").css("display","");
	    	$("#alertText").html(response.message);
	    	$("#alertText").attr("class", "alert alert-success");
	    	
	    	if(response.cashBalance > 0 )
    		{
	    		$("#modalKeepBuyingBody").html(response.message + ", would like to purchase anything else?");
	    		$('#modalKeepBuying').modal('show');
    		}
	    	
    	}
	    else{
	    	$("#alertContainer").css("display","");
	    	$("#alertText").html(response.error);
	    	$("#alertText").attr("class", "alert alert-danger");
	    }
	}).fail(function(data) {
	    alert( "Unexpected error: " + JSON.parse(data) );
	});
}

function endTransaction()
{
	$.ajax({
		url : "/endTransaction",
		dataType: "json"
	}) .done(function( data ) {
	    var response =data;
	    if(response.status == "success")
    	{
	    	$("#cashBalance").html(response.cashBalance);
	    	
	    	$("#alertContainer").css("display","");
	    	$("#alertText").html(response.message);
	    	$("#alertText").attr("class", "alert alert-success");
    	}
	    else{
	    	$("#alertContainer").css("display","");
	    	$("#alertText").html(response.error);
	    	$("#alertText").attr("class", "alert alert-danger");
	    }
	}).fail(function(data) {
	    alert( "Unexpected error: " + JSON.parse(data) );
	});
}
