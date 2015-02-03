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


// Calculate Histogram from data -> array(y);
//------------------------------------------------------------------------------------------------------------------
function CalculateHistogram(values,range){
	var histogram = [];
	if(values.length >0){
		histogram['ranges'] = []
		histogram['frequency'] = [0,0,0,0,0,0,0,0,0,0];
		histogram['range_labels'] = [];
	
		var min = MathMIN(range);
		var max = MathMAX(range);
		var step = (max-min)/10
		var rmin = min;
		var rmax = min + step
	
		for (var i=0; i< 10; i++){
			rmin = min + (step * i);
			rmax = rmin + step;
			histogram['ranges'].push([rmin,rmax]);
			histogram['range_labels'].push(MathROUND(rmin)+" to "+MathROUND(rmax));
		}

		for (var i=0,len = values.length; i< len; i++){
			for(range in histogram['ranges']){
				if(values[i] >= histogram['ranges'][range][0] && values[i] <= histogram['ranges'][range][1]){
					histogram['frequency'][range] += 1;
				}
			}
		}
	}
	return histogram;
}

// Calculate Lineweaver–Burk plot from data -> array(x,y);
//------------------------------------------------------------------------------------------------------------------
function LineweaverBurk(values){
	var LineweaverBurk = [];
	var reciprocal = [];
	var regrecival = {x:[],y:[]};
	if(values.length >0){
		// Calculating reciprocal values
		for (var i=0; i< values.length; i++){
			reciprocal.push([(1/values[i][0]),(1/values[i][1])]);
			regrecival.x.push(1/values[i][0]);
			regrecival.y.push(1/values[i][1]);
		}
		// calculating linear regression
		var reg = MathLINREG(regrecival.x, regrecival.y);
		var y0 = (reg['b']/reg['m']);
		LineweaverBurk = {Plot:reciprocal,Regression:[[y0,0],[MathMAX(regrecival.x),(reg['m']*MathMAX(regrecival.x)+reg['b'])]],RegressionValues:reg,ReceivedValues:'Vmax: '+MathROUND((1/reg['b']),3)+' Km: '+MathROUND((1/y0),3)}
	}
	return LineweaverBurk;
}

// Calculate Michaelis-Menten plot from data -> array(x,y);
//------------------------------------------------------------------------------------------------------------------
function MichaelisMenten(values){
	var MichaelisMenten = [];
	var data = [];
	if(values.length >0){
		var regression = regressionABS('logarithmic',values);
	
		var vmax = regression['equation'][0] + regression['equation'][1] * MathLOG(10000000000000);
		var km = Math.pow(Math.E,(((vmax/2)-regression['equation'][1])/regression['equation'][0]))   //regression['equation'][0] + regression['equation'][1] * (Math.log(10000000000000) / Math.LN10);
		MichaelisMenten = {Plot:values,Regression:regression['points'],RegressionValues:regression['string'],ReceivedValues:'Vmax: '+MathROUND(vmax,3)+' Km: '+MathROUND(regression['equation'][0],3)}
	}
	return MichaelisMenten;
}