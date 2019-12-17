$(document).ready(function() {
	
	var mapGrid = generateEmptyMap($('#mapSize').val());
	drawMap($('#mapSize').val(), mapGrid);
	updateCanvasText();
	/*
    $('#generateButton').click(function() {
    	
    	mapGrid = generateMapEmpty($('#mapSize').val());
    	drawMap($('#mapSize').val(), mapGrid);
    	$('#mapGrid').val(JSON.stringify(mapGrid));
    	//alertMap(mapGrid);
    	//echoMap(mapGrid);
    	
    });
    */
	
	$('#resetButton').on('click', function() {
    	
		//Debug:
		mapGrid = canvasToGrid();
		$('#mapGrid').val(JSON.stringify(mapGrid));
		
		mapGrid = generateEmptyMap($('#mapSize').val());
		drawMap($('#mapSize').val(), mapGrid);
		updateCanvasText();
		clickX = new Array();
		clickY = new Array();
		//clickDrag = new Array();
		clickSize = new Array();
		
		clickXMap = new Array();
		clickYMap = new Array();
		clickSizeMap = new Array();
    	
    });
    
	$('#mapSize').on('change', function() {
		mapGrid = generateEmptyMap($('#mapSize').val());
		drawMap($('#mapSize').val(), mapGrid);
		updateCanvasText();
		clickX = new Array();
		clickY = new Array();
		//clickDrag = new Array();
		clickSize = new Array();
		
		clickXMap = new Array();
		clickYMap = new Array();
		clickSizeMap = new Array();
		
		radius = ($('#mapCanvas').width()/$('#mapSize').val()) * $('#islandSize').val();
		})
		
	$('#islandSize').on('change', function() {		
		radius = ($('#mapCanvas').width()/$('#mapSize').val()) * $('#islandSize').val();
		updateCanvasText();
		})
	
    $('#mapForm').submit(function() {

    	
		mapGrid = canvasToGrid();
		$('#mapGrid').val(JSON.stringify(mapGrid));	

    	return true; // return false to cancel form action
    });  	
	
	context = document.getElementById('mapCanvas').getContext("2d");
	var paint;
	$('#mapCanvas').mousedown(function(e){
		  var mouseX = e.pageX - this.offsetLeft;
		  var mouseY = e.pageY - this.offsetTop;
				
		  paint = true;
		  addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop);
		  redraw();
		  paint = false; //Not draggable!
		});
	$('#mapCanvas').mousemove(function(e){
		  if(paint){
		    addClick(e.pageX - this.offsetLeft, e.pageY - this.offsetTop, true);
		    redraw();
		  }
		});
	$('#mapCanvas').mouseup(function(e){
		  paint = false;
		 // drawMap($('#mapSize').val(), mapGrid);
		 // var clickX = new Array();
		 // var clickY = new Array();
		});
	$('#mapCanvas').mouseleave(function(e){
		  paint = false; 
		});
	
});

var clickX = new Array();
var clickY = new Array();
var clickXMap = new Array();
var clickYMap = new Array();
//var clickDrag = new Array();
var clickSize = new Array();
var clickSizeMap = new Array();
var paint;
var context;
var radius = ($('#mapCanvas').width()/$('#mapSize').val()) * $('#islandSize').val();

function addClick(x, y, dragging)
{  
	  clickX.push(x);
	  clickY.push(y);
	  //clickDrag.push(dragging);
	  clickSize.push(radius);
	  
	  clickXMap.push(Math.floor((x / $('#mapCanvas').width()) * $('#mapSize').val()));
	  clickYMap.push(Math.floor((y / $('#mapCanvas').height()) * $('#mapSize').val()));
	  clickSizeMap.push($('#islandSize').val());
}

function redraw(){
	  context.strokeStyle = "#80C9E8";
	  context.rect(0, 0, context.canvas.width, context.canvas.height); // Clears the canvas
	  
	  context.strokeStyle = "#95E880";
	  context.lineJoin = "round";
	 //context.lineWidth = $('#islandSize').val();
				
	  for(var i=0; i < clickX.length; i++) {		
	    context.beginPath();
	    //if(clickDrag[i] && i){
	    //  context.moveTo(clickX[i-1], clickY[i-1]);
	    // }else{
	     context.moveTo(clickX[i]-1, clickY[i]);
	    // }
	     context.lineTo(clickX[i], clickY[i]);
	     context.closePath();
	     context.lineWidth = clickSize[i];
	     context.stroke();
	  }
	}

function canvasToGrid()
{
	var mapSize = $('#mapSize').val();
	var grid = generateEmptyMap(mapSize);
	var x;
	var y;
	var lim;
	
	//alert(JSON.stringify(grid));
	//alert(grid[15][15]);
		
	 for(var i=0; i < clickXMap.length; i++) {		
		    //context.beginPath();
		 lim = Math.floor(clickSizeMap[i]/2);
		 	for (var rY = -lim; rY < lim; rY++)
		 	{
		 		for (var rX = -lim; rX < lim; rX++)
			 	{
		 			x = clickXMap[i] + rX;
		 			y = clickYMap[i] + rY;
		 			
		 			if(x >= 0 && x < mapSize && y >= 0 && y < mapSize)
	 				{
		 				if(Math.sqrt(Math.pow(rX,2) + Math.pow(rY,2)) <= lim)
	 					{
			 				//alert(grid[x][y]);
			 				console.log("(" + x + ":" + y + ")");
			 				grid[y][x] = "L";
	 					}
	 				}
			 	}
		 	}
		  }	
	
	return grid;
}

function updateCanvasText()
{
	$('#canvasText').html('<b>Map Size: ' + $('#mapSize').val() + 'x' + $('#mapSize').val() + ' tiles</b>, <br/><em>Click to draw islands</em>, <br/>Island Size: ' + $('#islandSize').val() + ' tiles');
}

function drawMap(size, mG){		
	
	var maxCanvasSize = $('#mapCanvas').width();
	var drawMulti = 1;
	drawMulti = maxCanvasSize / size;
	if (drawMulti <= 0)
		drawMulti = 1;
	
	$.jCanvas.defaults.fromCenter = false;
	$('#mapCanvas').clearCanvas();
	$('#mapCanvas').drawRect({
		  fillStyle: '#80C9E8',
		  x: 0, y: 0,
		  width: maxCanvasSize,
		  height: maxCanvasSize
		});
	
		
	
	/*
	var drawMulti = 1;
	drawMulti = maxCanvasSize / size;
	if (drawMulti <= 0)
		drawMulti = 1;
	
	$('#mapCanvas').clearCanvas();
	$.jCanvas.defaults.fromCenter = false;
	
	for(var j = 0; j < size; j++)
	{
		for(var i = 0; i < size; i++)
		{
			if(mG[j][i] == "~")
			{
				$('#mapCanvas').drawRect({
					  fillStyle: '#80C9E8',
					  x: i*drawMulti, y: j*drawMulti,
					  width: drawMulti,
					  height: drawMulti
					});
			}
			else if(mG[j][i] == "L")
			{
				$('#mapCanvas').drawRect({
					  fillStyle: '#95E880',
					  x: i*drawMulti, y: j*drawMulti,
					  width: drawMulti,
					  height: drawMulti
					});
			}
			
		}
	}
	*/
}

function generateEmptyMap(mapSize) {
	
	var result = [];
	
	//Generate seamap:
	for (var j = 0; j < mapSize; j++) { 
		result.push([]);
		for (var i = 0; i < mapSize; i++) { 
			result[j].push("~");
		}
	}	
	
    return result;              // The function returns a generated array    
}

function getRandomC(size){
	var result = Math.floor((Math.random() * size));
	return result;
}

function alertMap(mG)
{
	var resultString = "";
	
	for(var j = 0; j < mG.length; j++)
	{
		for(var i = 0; i < mG[j].length; j++)
			{
				resultString += mG[i][j];
			}
		resultString += "\n";
	}	
	console.log(mG);
}

function echoMap(mG)
{
	$('#mapTable').empty()
			
	for(outer in mG)
	{		
		tRow = $('<tr>');
			for(inner in outer)
			{				
				tRow.append($('<td>').html(inner));				
			}
		$('#mapTable').append(tRow);
	}
}

function echoMap2(mG)
{
	$('#mapTable').empty()
			
	 $.each(mG, function(i) {
	      tRow = $('<tr>');
	               
	      tCell = $('<td>').html(data[i]);
	    
	      $('#mapTable').append(tRow.append(tCell));
	    });
}

function generateMapAuto(mapSize, islandSize, islands) {
	
	var result = [];
	
	//Generate seamap:
	for (var j = 0; j < mapSize; j++) { 
		result.push([]);
		for (var i = 0; i < mapSize; i++) { 
			result[j].push("~");
		}
	}
	
	//Generate islands:
	for (n = 0; n < islands; n++) { 
		
		var x = getRandomC(mapSize);
		var y = getRandomC(mapSize);		
		
		for(var j = y; j < (y + islandSize); j++)
		{
			for (var i = x; i < (x + islandSize); i++) { 
				if(i < mapSize && j < mapSize)
				{
					result[i][j]="L";
				}
			}
		}
	}		
    return result;              // The function returns a generated array    
}
