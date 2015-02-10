/*------------------------------------------------------------------------------------------------------------------
// Predefined functions to be used in macros
//----------------------------------------------------------------------------------------------------------------*/

// Calculate SUM: Input-> Array with values (float or int)
//------------------------------------------------------------------------------------------------------------------
function MathSUM(values){
	var sum = false;
	if(values){
		for(var i=0, len=values.length; i<len; i++)
			sum += values[i];
	}
	return parseFloat(sum);
}

// Round Number: Input-> Value (float) and number of decimal spaces (2 are standard)
//------------------------------------------------------------------------------------------------------------------
function MathROUND(value, digets){
	digets = typeof digets !== 'undefined' ? digets : 2;
	var val = false;
	if(value && parseInt(digets) >= 0){
		val = Math.round(value*Math.pow(10,digets))/Math.pow(10,digets);
	}
	return parseFloat(val);
}

// Calculate mean: Input-> Array with values (float or int)
//------------------------------------------------------------------------------------------------------------------
function MathMEAN(values){
	var mean = false;
	if(values){
		var sum = MathSUM(values);
		mean = sum / values.length;
	}
	return parseFloat(mean);
}

// Calculate Standard Deviation: Input-> Array with values (float or int)
//------------------------------------------------------------------------------------------------------------------
function MathSTDEV(values){
	var stdev = 0;
	if(values){
		if(values.length > 2){
			var mean = MathMEAN(values, false);
			var tmp = [];
			for(var i=0, len=values.length; i<len; i++)
				tmp.push(Math.pow((values[i]-mean),2));
			stdev = Math.sqrt(MathSUM(tmp) / values.length);
		}
	}
	return parseFloat(stdev);
}

// Calculate Standard Deviation Sample: Input-> Array with values (float or int)
//------------------------------------------------------------------------------------------------------------------
function MathSTDEVS(values){
	var stdevs = false;
	if(values){
		if(values.length > 2){
			var mean = MathMEAN(values, false);
			var tmp = [];
			for(var i=0, len=values.length; i<len; i++)
				tmp.push(Math.pow((values[i]-mean),2));
			stdevs = Math.sqrt(MathSUM(tmp) / (values.length -1));
		}
	}
	return parseFloat(stdevs);
}


// Calculate Standard Error: Input-> Array with values (float or int)
//------------------------------------------------------------------------------------------------------------------
function MathSTDERR(values){
	var stderr = false;
	if(values){
		if(values.length > 2){
			stderr = MathSTDEV(values) / Math.sqrt(values.length);
		}
	}
	return parseFloat(stderr);
}

// Get Maximum: Input-> Array with values (float or int)
//------------------------------------------------------------------------------------------------------------------
function MathMAX(values){
	var max = false;
	if(values){
		for(i=0;i<values.length;i++){
			if(!max)
				max = values[i];
			else if(values[i] > max)
				max = values[i];		
		}
	}
	return parseFloat(max);
}

// Get Minimum: Input-> Array with values (float or int)
//------------------------------------------------------------------------------------------------------------------
function MathMIN(values){
	var min = false;
	if(values){
		for(i=0;i<values.length;i++){
			if(!min)
				min = values[i];	
			else if(values[i] < min)
				min = values[i];		
		}
	}
	return parseFloat(min);
}

// LogBase10 Input-> Value (float)
//------------------------------------------------------------------------------------------------------------------
function MathLOG(value){
	var val = false;
	if(value){
		val = Math.log(value) / Math.LN10;
	}
	return parseFloat(val);
}

// Natural log Input-> Value (float)
//------------------------------------------------------------------------------------------------------------------
function MathLN(value){
	var val = false;
	if(value){
		val = Math.log(value);
	}
	return parseFloat(val);
}

// Median Input-> array (float)
//------------------------------------------------------------------------------------------------------------------
function MathMEDIAN(values){
	var val = false;
	if(values){
		// Sort values
		values.sort(function(a, b) {
			if (a < b) //sort string ascending
			return -1;
			if (a > b) return 1;
			return 0; //default return value (no sorting)
		});
		
		var n = values.length;
		// Even
		if((n % 2 == 0)){
			val = (values[(n/2)-1] + values[(n/2)])/2;
		}
		// Odd
		if((Math.abs(n) % 2 == 1)){
			val = values[Math.floor((n/2))];
		}
	}
	return parseFloat(val);
}

// Calculate Standard Error: Input-> Array with values (float or int)
//------------------------------------------------------------------------------------------------------------------
function MathLINREG(x, y) {
	var regression = false;
	
	// calculate number of points
	var xn = x.length;
	var yn = y.length;

	if(xn == yn){
		// Calculate Sums
		var xSum = MathSUM(x);
		var ySum = MathSUM(y);
		
		var xxSum = 0;
		var xySum = 0;
		var yySum = 0;
		
		for(var i = 0; i < xn; i++) {
			xySum += (x[i]*y[i]);
			xxSum += (x[i]*x[i]);
			yySum += (y[i]*y[i]);
		}
	
		// calculate slope
		var m = ((xn * xySum) - (xSum * ySum)) / ((xn * xxSum) - (xSum * xSum));

		// calculate intercept
		var b = (ySum - (m * xSum)) / xn;

		// calculate r
		var r = (xySum - ((1/xn)*xSum*ySum))/ Math.sqrt(((xxSum)-((1/xn)*(Math.pow(xSum,2))))*((yySum)-((1/xn)*(Math.pow(ySum,2)))));
	
		regression = {'m': m, 'b': b, 'r': r, 'r2': (r*r)}
	}
	return regression;
}