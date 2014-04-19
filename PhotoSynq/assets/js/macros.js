/*
Predefined macros
Requires functions.js


// Macro Template
//------------------------------------------------------------------------------------------------------------------
function MacroTemplate(json){
	var output = [];
	var values = json[j].data_raw

	if(values){
		
		## Here comes your data evaluation
		##..
		
		GraphType (required) --> line, point, pointline (Sets graphing for raw data view)
		Macro (required) --> (String) Macroname
		HTML (required) --> (String) String that gives explanation for calculated value. Can be empty.
		Further values (optional) --> (float, int) won't be displayed in raw data view but will be available for data comparison view
		
		## Math functions available
		MathSUM(values); --> calculates sum from array (e.g. [1,2,3.5])
		MathMEAN(values); --> calculates mean from array (e.g. [1,2,3.5])
		MathROUND(value, digets) --> rounds float to a certain number of digets (e.g 1.26377827 -> 1.264)
		MathSTDEV(values);  --> calculates standard deviation of population from array ([1,2,3.5])
		MathSTDEVS(values);  --> calculates standard deviation of sample from array ([1,2,3.5])
		MathSTDERR(values);  --> calculates standard error from array ([1,2,3.5])
		MathLINREG(x,y); --> calculates linear regression from array (x = [1,2,3.5], y= [1,2,3.5])		
		
		
		// Example output
		output = {"Calculation": val, "Macro": 'Macroname', "HTML": "value "+val+"", "GraphType": 'line'};
	}
	
	return output;
}
*/

// Calculation of PhiII and LEF from fluorescence data (script 002)
//------------------------------------------------------------------------------------------------------------------
function Phi2LEF(json){
	var output = false;
	var values = json.data_raw
	var phi2 = false;
	var lef = false;
	var lefhtml = 'no light intensity available for calculation';
	var Fs = values.slice(38,48); // Mean for Fs -> 39-49
	var FmP = values.slice(88,98); // Mean for FmP -> 89-99
	var light = parseFloat(json.light_intensity);
	
	if(values){
		if(Fs instanceof Array)
			Fs = MathMEAN(Fs);

		if(FmP instanceof Array)
			FmP = MathMEAN(FmP);		

		// Calculate PhiII => Fm'-Fs / Fm'
		phi2 = (FmP-Fs) / FmP;
		
		// Calculate LEF => PhiII * 0.4 * lightintensity
		if(light)
			lef = (phi2 * light * 0.4);
	
		output = {"Macro": 'Phi2,LEF', "HTML": "<b>&Phi;II:</b> <i>"+ MathROUND(phi2,3)+"</i> | <b>LEF:</b> <i>"+MathROUND(lef,3)+"</i>", "GraphType": 'line'};
	//	if(!isNaN(phi2))
			output['Phi2'] = phi2;
	//	if(!isNaN(lef))
			output['LEF'] = lef;
	//	if(!isNaN(Fs))
			output['Fs'] = Fs;
	//	if(!isNaN(FmP))
			output['FmP'] = FmP;
	}
	return output;
}

// Calculation of SPAD value
//------------------------------------------------------------------------------------------------------------------
function SPAD(json){
	
	var output = false;
	var values = json.data_raw;
	values = values.slice(50,450)
	var spad = false;
	var calibration940 = 0;
	var calibration650 = 0;
	if(json.chlorophyll_spad_calibration != undefined){
		calibration940 = json.chlorophyll_spad_calibration[0];
		calibration650 = json.chlorophyll_spad_calibration[1];
	}
	var values940 = [];
	var values650 = [];
	
	if(values){
		for ( var i=0; i< (values.length); i+=2 ){
			values940.push(values[i]);
			values650.push(values[i+1]);
		}
		
		if(values940 instanceof Array)
			values940 = MathMEAN(values940);

		if(values650 instanceof Array)
			values650 = MathMEAN(values650);		

		//console.log(values940+' '+	calibration940+' '+	values650+' '+	calibration650);
			
		// Calculate SPAD
		//spad = Math.log((values940 / calibration940) / (values650 / calibration650)) * 100;
		spad = MathLOG((values940 / calibration940) / (values650 / calibration650)) * 100;
		
		output = {"Spad": spad, "Macro": 'SPAD',  "HTML": "<b>Spad value:</b> <i>"+ MathROUND(spad) +"</i>", "GraphType": 'points'};
	}
		
	return output;
}


function chlorophyll_spad_ndvi(json){
	var output = false;
	var values = json.data_raw;
	values = values.slice(40,340)
	var spad = false;
	var ndvi = false;
	var calibration940 = json.chlorophyll_spad_calibration[0];
	var calibration650 = json.chlorophyll_spad_calibration[1];

	var values_spad940 = [];
	var values_spad650 = [];
	var values_ndvi940 = [];
	var values_ndvi650 = [];
	
	if(values){
		for ( var i=0; i< (values.length); i+=4 ){
			values_spad940.push(values[i]);
			values_spad650.push(values[i+1]);
			values_ndvi940.push(values[i+2]);
			values_ndvi650.push(values[i+3]);
		}
		
		if(values_spad940 instanceof Array)
			values_spad940 = MathMEAN(values_spad940);

		if(values_spad650 instanceof Array)
			values_spad650 = MathMEAN(values_spad650);
			
		if(values_ndvi940 instanceof Array)
			values_ndvi940 = MathMEAN(values_ndvi940);

		if(values_ndvi650 instanceof Array)
			values_ndvi650 = MathMEAN(values_ndvi650);	

		//console.log(values940+' '+	calibration940+' '+	values650+' '+	calibration650);
			
		// Calculate SPAD
		spad = MathLOG((values_spad940 / calibration940) / (values_spad650 / calibration650)) * 100;
		
		// Calculate NDVI
		ndvi = ((values_ndvi940/calibration940) - (values_ndvi650/calibration650)) / ((values_ndvi940/calibration940) + (values_ndvi650/calibration650));
		//ndvi = ((values_ndvi940) - (values_ndvi650)) / ((values_ndvi940) + (values_ndvi650));
		
		output = {"Spad": spad, "NDVI": ndvi, "Macro": 'SPAD_NDVI',  "HTML": "<b>Spad value:</b> <i>"+ MathROUND(spad) +"</i> <b>NDVI value:</b> <i>"+ MathROUND(ndvi) +"</i> ", "GraphType": 'points'};
	}
		
	return output;
}

// ECS 520nm Measurement
//------------------------------------------------------------------------------------------------------------------
function ECS520Dirk(json){
	var output = false;
	var values = json.data_raw;

	if(values){
		var vHplus, ecst, gHplus, tau = 0;
		
		// Calculate log (I0/I)
		var valuesNew = [];
		for ( var i=0, len = values.length; i<len; i++){
			valuesNew[i] = MathLOG((values[0]/values[i]));
		}
		
		// Subtract Line and prepare data for geting ECS values
		var start = 80
		var end = 98
		var regy = valuesNew.slice(start,end); 
		var regx = [];
		while ( start < end ) {
			regx.push(start);
			start += 1;
		}
		var reg = MathLINREG(regx,regy);
		values = [];
		for(var i=0, len = valuesNew.length; i<len; i++){
			values.push(valuesNew[i] - (reg.m * i + reg.b))
		}
		
		// Normalize beginning of kinetic to determine tau
		
		var start = 97
		var end = 150
		var y = values.slice(start,end);
		
		var max = MathMAX(y);
		var min = MathMIN(y);
		
		var ynorm = [];
		for(var i=0, len = y.length; i<len; i++){
			ynorm.push((y[i]-min)/(max-min));
		}			
		
		var x = [];
		while ( start < end ) {
			x.push(start);
			start += 1;
		}
		
		var high;
		var low;

		for(var i=0,len = ynorm.length; i< len;i++){
			if(ynorm[i] >= (1/Math.E)){
				high = i;
				if(ynorm[i+1])
					low = i+1;
				else
					low = i;
			}
		}
		
		// vH+ => Initial Slope based on regression line
		var reg = MathLINREG([0,1,2,3],values.slice(97,101));
		var vHplus = Math.abs(reg.m)/0.001;			
		
		// Calculate tau
		var reg = MathLINREG([low,high],[ynorm[low],ynorm[high]]);
		var tau =  ((1/Math.E) - reg.b/reg.m)*0.001;
		
		// gH+ => 1/tau
		var gHplus = 1/tau;
		
		// Calculate ECSt
		var ecst = Math.abs( MathMEAN(values.slice(170,190)));
	
		values = false;
		output = {"Macro": 'ECS', "HTML": "<b>vH+:</b> <i>"+MathROUND(vHplus,4)+"</i> | <b>gH+:</b> <i>"+MathROUND(gHplus,4)+"</i> | <b>ECSt:</b> <i>"+MathROUND(ecst,4)+"</i> | <b>&tau;:</b> <i>"+MathROUND(tau,4)+"</i>", "GraphType": 'line'};
		
		//if(!isNaN(vHplus))
			output['vHplus'] = vHplus;
		//if(!isNaN(gHplus))
			output['gHplus'] = gHplus;
		//if(!isNaN(tau))
			output['tau'] = tau;
		//if(!isNaN(ecst))
			output['ecst'] = ecst;			
	}
	return output;
}

// Calculation of DIRK at 810
//------------------------------------------------------------------------------------------------------------------
function DIRK810(json){
	var output = false;
	var values = json.data_raw
	var dirk810 = false;
	
	if(values){
		dirk810 = MathMEAN(values.slice(130,180)) - MathMEAN(values.slice(20,90)) + 50;
		output = {"dirk810": dirk810, "Macro": 'DIRK810', "HTML": "<b>DIRK 810:</b> <i>"+ MathROUND(dirk810,3)+"</i>", "GraphType": 'line'};
	}
	return output;
}


// Calculation of DIRK at 940
//------------------------------------------------------------------------------------------------------------------
function DIRK940(json){
	var output = false;
	var values = json.data_raw
	var dirk940 = false;
	
	if(values){
		dirk940 = MathMEAN(values.slice(130,180)) - MathMEAN(values.slice(20,90)) + 50;
		output = {"dirk940": dirk940, "Macro": 'DIRK940', "HTML": "<b>DIRK 940:</b> <i>"+ MathROUND(dirk940,3)+"</i>", "GraphType": 'line'};
	}
	return output;
}

// Calculation of baseline_sample value
//------------------------------------------------------------------------------------------------------------------
function baseline_sample(json){
	var output = false;
	var values = json.data_raw;
	if(values){
		var avg = MathMEAN(values.slice(25,375))
		output = {"Baseline": avg, "Macro": 'baseline_sample',  "HTML": "<b>Baseline avg. Intensity:</b> <i>"+ MathROUND(avg) +"</i>", "GraphType": 'line'};
	}
	return output;
}


function chlorophyll_spad_ndvi(json){
	var output = false;
	var values = json.data_raw;
	values = values.slice(40,340)
	var spad = false;
	var ndvi = false;
	var calibration940 = json.chlorophyll_spad_calibration[0];
	var calibration650 = json.chlorophyll_spad_calibration[1];

	var values_spad940 = [];
	var values_spad650 = [];
	var values_ndvi940 = [];
	var values_ndvi650 = [];
	
	if(values){
		for ( var i=0; i< (values.length); i+=4 ){
			values_spad940.push(values[i]);
			values_spad650.push(values[i+1]);
			values_ndvi940.push(values[i+2]);
			values_ndvi650.push(values[i+3]);
		}
		
		if(values_spad940 instanceof Array)
			values_spad940 = MathMEAN(values_spad940);

		if(values_spad650 instanceof Array)
			values_spad650 = MathMEAN(values_spad650);
			
		if(values_ndvi940 instanceof Array)
			values_ndvi940 = MathMEAN(values_ndvi940);

		if(values_ndvi650 instanceof Array)
			values_ndvi650 = MathMEAN(values_ndvi650);	

		//console.log(values940+' '+	calibration940+' '+	values650+' '+	calibration650);
			
		// Calculate SPAD
		spad = MathLOG((values_spad940 / calibration940) / (values_spad650 / calibration650)) * 100;
		
		// Calculate NDVI
		ndvi = ((values_ndvi940/calibration940) - (values_ndvi650/calibration650)) / ((values_ndvi940/calibration940) + (values_ndvi650/calibration650));
		//ndvi = ((values_ndvi940) - (values_ndvi650)) / ((values_ndvi940) + (values_ndvi650));
		
		output = {"Spad": spad, "NDVI": ndvi, "Macro": 'SPAD_NDVI',  "HTML": "<b>Spad value:</b> <i>"+ MathROUND(spad) +"</i> <b>NDVI value:</b> <i>"+ MathROUND(ndvi) +"</i> ", "GraphType": 'points'};
	}
		
	return output;
}

// Extract environmental variables
//------------------------------------------------------------------------------------------------------------------
function Environment(json){
	var output = false;
	var values = json;
		
	if(values){
		var parameters = [];
		$.each(values, function(key, value){
			//if(key != "data_raw" && key != "HTML" && key != "Macro" && key != "Graphtype" && key != "end")
			if(key == "temperature" || key == "relative_humidity" || key == "co2_content" || key == "light_intensity" || key == "time")
				parameters[key] = value;
		});
		output = parameters;
	}
		
	return output;
}

// Extract environmental variables
//------------------------------------------------------------------------------------------------------------------
function AddLinearRegressionLines(chart){

	//Calculations
	var min = chart.xAxis[0].min
	var max = chart.xAxis[0].max;
	//chart.yAxis[0].min;
	//chart.yAxis[0].max;
	//chart.series[seriesID].name
	//chart.series[seriesID].color

	if(chart != undefined){
		for(seriesID in chart.series){

			var reg = MathLINREG(chart.series[seriesID].processedXData,chart.series[seriesID].processedYData);
	
			var series = {
				type: 'line',
				name: 'r2 = '+ MathROUND(reg['r2']),
				data: [[min, (min*reg['m'] + reg['b'])], [max, (max*reg['m'] + reg['b'])]],
				color: chart.series[seriesID].color,
				dashStyle: 'shortdash',
				marker: {
					enabled: false
				},
				states: {
					hover: {
						lineWidth: 0
					}
				},
				enableMouseTracking: false,
				animation: false
			};
			chart.addSeries(series, false);
		}
	
		chart.redraw();
	}
}

// Custom Macro setup
//------------------------------------------------------------------------------------------------------------------
function ArtefactFilter(series, protocol){
	if(protocol == "fluorescence"){
		series[50] = MathMEAN([series[49],series[51]]);
		series[100] = MathMEAN([series[99],series[101]]);
	}
	
	if(protocol == "810_dirk" || protocol == "940_dirk" || protocol == "dirk"){
		series[0] = series[1];
		series[100] = MathMEAN([series[99],series[101]]);
		series[200] = MathMEAN([series[199],series[201]]);
		series[297] = series[296];
	}			

	if(protocol == "chlorophyll_spad" || protocol == "940_dirk" || protocol == "dirk"){
		series[0] = series[1];
		series[100] = MathMEAN([series[99],series[101]]);
		series[200] = MathMEAN([series[199],series[201]]);
		series[297] = series[296];
	}
	return series;
}

// Custom Macro setup
//------------------------------------------------------------------------------------------------------------------
function CustomMacro(code,json){
	eval(code);
	return output;
}

// Custom Macro setup
//------------------------------------------------------------------------------------------------------------------
function Array_Unique(array){
	if(typeof someVar === 'string' )
		return array;

	var u = {}, a = [];
	for(var i = 0, l = array.length; i < l; ++i){
		if(u.hasOwnProperty(array[i])) {
			continue;
		}
		a.push(array[i]);
		u[array[i]] = 1;
	}
   return a;
}