function macro_1(json){
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

function macro_2(json){
var output = false;
var values = json;
	
if(values){
	var parameters = [];
	$.each(values, function(key, value){
		//if(key != "data_raw" && key != "HTML" && key != "Macro" && key != "Graphtype" && key != "end")
		if(key == "temperature" || key == "relative_humidity" || key == "co2_content" || key == "light_intensity")
			parameters[key] = value;
	});
	output = parameters;
}
		
return output;
 }

function macro_4(json){
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

function macro_5(json){
var output = false;
var values = json.data_raw;
if(values){
	var avg = MathMEAN(values.slice(25,375))
	output = {"Baseline": avg, "Macro": 'baseline_sample',  "HTML": "<b>Baseline avg. Intensity:</b> <i>"+ MathROUND(avg) +"</i>", "GraphType": 'line'};
}
return output;
 }

function macro_6(json){
var output = false;
var data = json.data_raw;
var sample_cal = MathMEAN(data.slice(2,18));

/********retrieve and calculate the calibration information from the data JSON and save (using measuring light 15, calibrating light 14, actinic/saturating light 20)******/
for (i in json.get_ir_baseline) {	
	if (json.get_ir_baseline[i][0] == 15) {
		var slope_light = json.get_ir_baseline[i][1];
		var yint_light = json.get_ir_baseline[i][2];
	}
	if (json.get_ir_baseline[i][0] == 14) {
		var slope_cal = json.get_ir_baseline[i][1];
		var yint_cal = json.get_ir_baseline[i][2];
	}
}
for (i in json.get_lights_cal) {	
	if (json.get_lights_cal[i][0] == 20) {
		var slope_act = json.get_lights_cal[i][1];
		var yint_act = json.get_lights_cal[i][2];
	}
}

// calculate the baseline
var shinyness = (sample_cal-yint_cal)/slope_cal; // where 0 is dull black electrical tape, and 1 is shiny aluminum
var baseline = slope_light*shinyness+yint_light;
/*************************************************/

if(data){
  
  // calculate Fv/Fm
  var f0 = MathMEAN(data.slice(21,24)) - baseline; 
  var fm_sort = data.slice(42,88).sort();
  var fm = MathMEAN(fm_sort.slice(43,46)) - baseline;
  var fvfm = (fm-f0)/fm;

  // calculate Fs/Fm'
  var fs = MathMEAN(data.slice(291,294)) - baseline; 
  var fmp_sort = data.slice(312,358).sort();
  var fmp = MathMEAN(fmp_sort.slice(43,46)) - baseline;
  var fsfmp = (fmp-fs)/fmp;

  // calculate Fs'/Fm''
  var fsp = MathMEAN(data.slice(21,24)) - baseline; 
  var fmpp_sort = data.slice(582,628).sort();
  var fmpp = MathMEAN(fmpp_sort.slice(43,46)) - baseline;
  var fspfmpp = (fmpp-fsp)/fmpp;

/***** calculate the intensity from actinic light at intensity setting 50 in micro einsteins *****/
  var actinic_intensity = 50*slope_act+yint_act;

  // Parameter calculations
  var phi2 = (fmp - fs) / fmp;
  var lef = (phi2 * actinic_intensity);
  var npq = (fm - fmp) / fmp;
  var qE = (fmpp - fmp) / fmpp;
  var qEsv = (fm / fmp) - (fm / fmpp);
  var fvfm = (fm - f0) / fm;
  var qI = (fm - fmpp) / fmpp;
  var qP = (fmp - fs) / (fmp - fsp);
  var qL = fsp / (fs * qP);

  output = {"Macro": 'Fluorescence Trace', "HTML": "", "GraphType": 'line'};

  // Add values from trace
  output['light'] = actinic_intensity;
  output['F0'] = MathROUND(f0,2);
  output['Fm'] = MathROUND(fm,2);
  output['Fs'] = MathROUND(fs,2);
  output['FmP'] = MathROUND(fmp,2);
  output['FsP'] = MathROUND(fsp,2);
  output['FmPP'] = MathROUND(fmpp,2);

  // Add calculated values
  output['Phi2'] = MathROUND(phi2,2);
  output['LEF'] = MathROUND(lef,2);
  output['NPQ'] = MathROUND(npq,2);
  output['qE'] = MathROUND(qE,2);
  output['qEsv'] = MathROUND(qEsv,2);
  output['Fv/Fm'] = MathROUND(fvfm,2);
  output['qI'] = MathROUND(qI,2);
  output['qP'] = MathROUND(qP,2);
  output['qL'] = MathROUND(qL,2);
  output['baseline'] = MathROUND(baseline,2);
}
return output;
 }

function macro_9(json){
// example protocol JSON to be used with this macro
// [{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[15,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,20,0],"act_intensities":[0,0,1140,0],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,200,200,200],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[15],[15],[15]]},{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[16,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,20,0],"act_intensities":[0,0,1140,0],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,200,200,200],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[16],[16],[16]]},{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[20,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,15,0],"act_intensities":[700,700,700,700],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,0,250,0],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[20],[20],[20]]}]

var data = json.data_raw;
var output = {};
var sample_cal = MathMEAN(data.slice(2,18));

// retrieve the baseline information from the data JSON and save (using measuring light 20, calibrating light 14)
for (i in json.get_ir_baseline) {	
	if (json.get_ir_baseline[i][0] == 20) {
		var slope_light = json.get_ir_baseline[i][1];
		var yint_light = json.get_ir_baseline[i][2];
	}
	if (json.get_ir_baseline[i][0] == 14) {
		var slope_cal = json.get_ir_baseline[i][1];
		var yint_cal = json.get_ir_baseline[i][2];
	}
}

// calculate the baseline
var shinyness = (sample_cal-yint_cal)/slope_cal; // where 0 is dull black electrical tape, and 1 is shiny aluminum
var baseline = slope_light*shinyness+yint_light;

var Fss = MathMEAN(data.slice(22,38)) - baseline;
var FmPs = MathMEAN(data.slice(42,88)) - baseline; // take the 4 largest values and average them
var fvfms = (FmPs-Fss)/FmPs;

var Fs = MathMEAN(data.slice(21,24)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals = data.slice(42,88).sort();  // sort the saturating light values from low to high
var FmP = MathMEAN(sat_vals.slice(43,46)) - baseline; // take the 4 largest values and average them
var fvfm = (FmP-Fs)/FmP;

var Fsnn = Fs+json.slope_34*10+json.yintercept_34;
var FmPnn = FmP+json.slope_34*10+json.yintercept_34;
var fvfmnn = (FmPnn-Fsnn)/FmPnn;

var Fsn = Fs + baseline;
var FmPn = FmP + baseline;
var fvfmn = (FmPn-Fsn)/FmPn;

//output["sorted"] = sat_vals;
output["Fs"] = MathROUND(Fs,1);
//output["Fss"] = MathROUND(Fss,1);
output["FmPrime"] = MathROUND(FmP,1);
//output["FmPrimes"] = MathROUND(FmPs,1);
//output["Fv/Fm nb no"] = MathROUND(fvfmnn,3);
//output["Fv/Fm nb"] = MathROUND(fvfmn,3);
output["Phi2"] = MathROUND(fvfm,3);
output["LEF"] = MathROUND((fvfm  * 0.45 * json.light_intensity),3);
//output["Fv/Fms"] = MathROUND(fvfms,3);
//output["shinyness"] = MathROUND(shinyness,2);
//output["sample_cal"] = MathROUND(sample_cal,1);
output["baseline"] = MathROUND(baseline,1);
return output;
// example data from device to calculate
//{"device_id": 21.00,"firmware_version": "0.10","sample": [[{"protocol_id": "","slope_34":6.86,"yintercept_34":620.54,"slope_35":7.94,"yintercept_35":493.13,"get_ir_baseline": [[16,2073.11,255.68],[14,3778.02,578.79]],"data_raw":[2190,2204,2177,2207,2189,2214,2195,2185,2201,2180,2197,2154,2172,2193,2145,2191,2187,2204,2178,2180,2356,2340,2371,2373,2382,2366,2378,2399,2378,2389,2387,2390,2384,2401,2412,2386,2411,2401,2391,2421,2425,6438,7055,7341,7477,7538,7583,7606,7653,7669,7685,7728,7682,7688,7723,7733,7738,7758,7744,7754,7753,7755,7775,7768,7772,7744,7761,7788,7729,7768,7764,7739,7749,7733,7725,7793,7781,7761,7758,7769,7773,7748,7762,7768,7777,7753,7777,7768,7764,7753,7750,6849,6146,5657,5413,5218,5060,4949,4861,4821,4720,4693,4635,4580,4555,4551,4536,4510,4470,4480]}

 }

function macro_3(json){
var output = false;
var values = json.data_raw;
values = values.slice(40,340)
var spad = false;
var ndvi = false;
var calibration940 = 1;//json.chlorophyll_spad_calibration[0];
var calibration650 = 1;//json.chlorophyll_spad_calibration[1];

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
	
//	output = {"Spad": spad, "Macro": 'SPAD_NDVI',  "HTML": "<b>Spad value:</b> <i>"+ MathROUND(spad) +"</i> | <b>NDVI value:</b> <i>"+ MathROUND(ndvi) +"</i> ", "GraphType": 'points'};
}

output = {"Spad":1};
	
return output;
 }

function macro_11(json){
var data = json.data_raw;
var output = {"toDevice":"1015+"};// output to device, start 1015 to call spad calibration
var lights = [15,16,11,12,20,2,14,10];// define the lights to be calibrated
var pulses = 100;// number of pulses in a cycle
for (var i = 0;i<lights.length;i++) { // loop through and save one averaged 'point' for each of the cycles
	output ["toDevice"] += lights[i]; 
	output ["toDevice"] += "+";  
    output ["toDevice"] += MathMEAN(json.data_raw.slice((i*pulses+10),(i*pulses+90))); 
	output ["toDevice"] += "+";    
	output ["toDevice"] += "0"; 
	output ["toDevice"] += "+";
}

output["toDevice"] += "-1+";

return output;	
 }

function macro_14(json){

 }

function macro_8(json){
// example protocol JSON to be used with this macro
// [{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[15,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,20,0],"act_intensities":[0,0,1140,0],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,200,200,200],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[15],[15],[15]]},{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[16,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,20,0],"act_intensities":[0,0,1140,0],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,200,200,200],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[16],[16],[16]]},{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[20,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,15,0],"act_intensities":[700,700,700,700],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,0,250,0],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[20],[20],[20]]}]

var data = json.data_raw;
var output = {};
var sample_cal = MathMEAN(data.slice(2,18));

// retrieve the baseline information from the data JSON and save (using measuring light 16, calibrating light 14)
for (i in json.get_ir_baseline) {	
	if (json.get_ir_baseline[i][0] == 16) {
		var slope_light = json.get_ir_baseline[i][1];
		var yint_light = json.get_ir_baseline[i][2];
	}
	if (json.get_ir_baseline[i][0] == 14) {
		var slope_cal = json.get_ir_baseline[i][1];
		var yint_cal = json.get_ir_baseline[i][2];
	}
}


// calculate the baseline
var shinyness = (sample_cal-yint_cal)/slope_cal; // where 0 is dull black electrical tape, and 1 is shiny aluminum
var baseline = slope_light*shinyness+yint_light;

var Fss = MathMEAN(data.slice(22,38)) - baseline;
var FmPs = MathMEAN(data.slice(42,88)) - baseline; // take the 4 largest values and average them
var fvfms = (FmPs-Fss)/FmPs;

var Fs = MathMEAN(data.slice(21,24)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals = data.slice(42,88).sort();  // sort the saturating light values from low to high
var FmP = MathMEAN(sat_vals.slice(43,46)) - baseline; // take the 4 largest values and average them
var fvfm = (FmP-Fs)/FmP;

var Fsnn = Fs+json.slope_34*10+json.yintercept_34;
var FmPnn = FmP+json.slope_34*10+json.yintercept_34;
var fvfmnn = (FmPnn-Fsnn)/FmPnn;

var Fsn = Fs + baseline;
var FmPn = FmP + baseline;
var fvfmn = (FmPn-Fsn)/FmPn;

//output["sorted"] = sat_vals;
output["Fs"] = MathROUND(Fs,1);
//output["Fss"] = MathROUND(Fss,1);
output["FmPrime"] = MathROUND(FmP,1);
//output["FmPrimes"] = MathROUND(FmPs,1);
//output["Fv/Fm nb no"] = MathROUND(fvfmnn,3);
//output["Fv/Fm nb"] = MathROUND(fvfmn,3);
output["Phi2"] = MathROUND(fvfm,3);
output["LEF"] = MathROUND((fvfm  * 0.45 * json.light_intensity),3);
//output["Fv/Fms"] = MathROUND(fvfms,3);
//output["shinyness"] = MathROUND(shinyness,2);
//output["sample_cal"] = MathROUND(sample_cal,1);
output["baseline"] = MathROUND(baseline,1);
return output;
// example data from device to calculate
//{"device_id": 21.00,"firmware_version": "0.10","sample": [[{"protocol_id": "","slope_34":6.86,"yintercept_34":620.54,"slope_35":7.94,"yintercept_35":493.13,"get_ir_baseline": [[16,2073.11,255.68],[14,3778.02,578.79]],"data_raw":[2190,2204,2177,2207,2189,2214,2195,2185,2201,2180,2197,2154,2172,2193,2145,2191,2187,2204,2178,2180,2356,2340,2371,2373,2382,2366,2378,2399,2378,2389,2387,2390,2384,2401,2412,2386,2411,2401,2391,2421,2425,6438,7055,7341,7477,7538,7583,7606,7653,7669,7685,7728,7682,7688,7723,7733,7738,7758,7744,7754,7753,7755,7775,7768,7772,7744,7761,7788,7729,7768,7764,7739,7749,7733,7725,7793,7781,7761,7758,7769,7773,7748,7762,7768,7777,7753,7777,7768,7764,7753,7750,6849,6146,5657,5413,5218,5060,4949,4861,4821,4720,4693,4635,4580,4555,4551,4536,4510,4470,4480]}

 }

function macro_10(json){
//Define your variables here...
var data = json.data_raw;
var output = {"toDevice":"1014+"};
var lights = [15,16,20,14,15,16,20,14];
var pulses = 100;
var count = [];

//go get the values from the data json (4 lights repeated twice)
for (i = 0;i<lights.length/2;i++) {
	var low = [MathMEAN(data.slice((i*pulses+10),(i*pulses+90)))];
	var high = [MathMEAN(data.slice(((i+lights.length/2)*pulses+10),((i+lights.length/2)*pulses+90)))];
	var slope = high - low;	
	var yint = high;	

	output[lights[i]+"_low"] = low;
	output[lights[i]+"_high"] = high;
	output[lights[i]+"_difference"] = high-low;  
  
    output["toDevice"] += lights[i] + "+"; 
    output["toDevice"] += MathROUND(slope,2) + "+"; 
//    output["toDevice"] += MathROUND(low,2) + "+";
    output["toDevice"] += MathROUND(yint,2) + "+";
}

output["toDevice"] += "-1+";

//Return data
return output;

 }

function macro_12(json){
var output = {};
var spad;

// get calibration data
for (var i = 0;i<json.get_blank_cal.length;i++) {	
  if (json.get_blank_cal[i][0] == 20) {	// if the first element of the array is 20, then...
    calibration_650 = json.get_blank_cal[i][1];	// then the second element is the 650 calibration
  }
  if (json.get_blank_cal[i][0] == 12) {	// if the first element of the array is 12, then...
    calibration_940 = json.get_blank_cal[i][1];	// then the second element is the 940 calibration
  }
}

// now get the sample 650 and 940 values averaged
var sample_940 = MathMEAN(json.data_raw.slice(5,95));
var sample_650 = MathMEAN(json.data_raw.slice(105,195));
spad = MathLOG((sample_940 / calibration_940) / (sample_650 / calibration_650))*100;

//output ["get"] = MathROUND(spad,2);
//output ["spad"] = MathROUND(spad,2);
output["SPAD"] = MathROUND(spad/2.73,2);
//output ["sample 940"] = MathROUND(sample_940,2);
//output ["cal 940"] = MathROUND(calibration_940,2);
//output ["sample 650"] = MathROUND(sample_650,2);
//output ["cal 650"] = MathROUND(calibration_650,2);

return output;
 }

function macro_15(json){
var output = {};
var spad;

// get calibration data
for (var i = 0;i<json.get_blank_cal.length;i++) {	
  if (json.get_blank_cal[i][0] == 20) {	// if the first element of the array is 20, then...
    calibration_650 = json.get_blank_cal[i][1];	// then the second element is the 650 calibration
  }
  if (json.get_blank_cal[i][0] == 12) {	// if the first element of the array is 12, then...
    calibration_940 = json.get_blank_cal[i][1];	// then the second element is the 940 calibration
  }
}

// now get the sample 650 and 940 values averaged
var sample_940 = MathMEAN(json.data_raw.slice(5,95));
var sample_650 = MathMEAN(json.data_raw.slice(105,195));
spad = MathLOG((sample_940 / calibration_940) / (sample_650 / calibration_650))*100;

//output ["get"] = MathROUND(spad,2);

//output ["spad"] = MathROUND(spad,2);

if (json.get_userdef0[0] == 0) {

//output["Minolta spad"] = MathROUND(spad/2.75,2);

//output["I did it null"] = 1;
}
else {

//output["Minolta spad"] = MathROUND(spad/json.get_userdef0[0],2);

//output["I did it"] = 1;
}

output ["sample 940"] = MathROUND(sample_940,2);
output ["cal 940"] = MathROUND(calibration_940,2);
output ["sample 650"] = MathROUND(sample_650,2);
output ["cal 650"] = MathROUND(calibration_650,2);

return output;
 }

function macro_18(json){
var output = {};

// Calculate the final conductance value in uS/m
var blank = MathMEAN(json.data_raw.slice(5,495));
var sample = MathMEAN(json.data_raw.slice(505,995));
var raw_value = blank - sample;
var resistance_value = raw_value*(2.5/65535); // 2.5V in 2^16 counts
var conductance_value = 1/resistance_value;
var conductance_value_per_meter = conductance_value*(1/.0135) // distance between electrodes in DF Robot moisture sensor V2 is 13.5mm or .0135m

output ["conductance (uS per m)"] = MathROUND(conductance_value_per_meter,2);
output ["resistance"] = MathROUND(resistance_value,4);
output ["sample"] = MathROUND(sample,1);
output ["blank"] = MathROUND(blank,1);

return output;
 }

function macro_16(json){
//Define your variables here...
var output = false;

//Show value and name in output
var avg = MathROUND( MathMEAN(json.data_raw) , 2)
var std = MathROUND( MathSTDEV(json.data_raw) , 2)
var err = MathROUND( (std / avg) * 100 , 2)

output = {'Average': avg, 'Std': std, 'Error': err };

//Return data
return output;
 }

function macro_17(json){
// a linear regression is made from the raw values subtracted from the blank.  In addition, the log relationship is also calculated and outputted.

var output = {};

var I0 = json.get_userdef2[0]; // get blank

var Step11 = json.data_raw.slice(40,460);
var Step12 = json.data_raw.slice(540,960);
var Step13 = json.data_raw.slice(1040,1460);

var Step21 = json.data_raw.slice(1540,1960);
var Step22 = json.data_raw.slice(2040,2460);
var Step23 = json.data_raw.slice(2540,2960);

var Step31 = json.data_raw.slice(3040,3560);
var Step32 = json.data_raw.slice(3540,4060);
var Step33 = json.data_raw.slice(4040,4560);

var I1 = (MathMEAN(Step11)+MathMEAN(Step12)+MathMEAN(Step13))/3;
var I2 = (MathMEAN(Step21)+MathMEAN(Step22)+MathMEAN(Step23))/3;
var I3 = (MathMEAN(Step31)+MathMEAN(Step32)+MathMEAN(Step33))/3;

var A1 = MathLOG( ( I1/I0 ) ) * -1;
var A2 = MathLOG( ( I2/I0 ) ) * -1;
var A3 = MathLOG( ( I3/I0 ) ) * -1;

var reg = MathLINREG([A1,A2,A3], [5,10,20] );   // measured in micromolar

// curve for absolute value, subtracted from blank

var _A1 = I0 - I1;
var _A2 = I0 - I2;
var _A3 = I0 - I3;

var _reg = MathLINREG([_A1,_A2,_A3], [5,10,20] );   // measured in micromolar

//var sample = (As - reg.b)/reg.m

//var activeCarbon = (0.02 - (reg.b +(reg.m * As))) * 9000 * (0.02 / 0.00248)

//Active Carbon (mg kg -1) = [0.02 mol/ L – (a + b × Abs)] × (9000 mg C/ mol) × (0.02 L solution/ Wt)
//[0.02 M – (-0.00004 + (0.0502 × 0.3087)] × (9000 mg C/ mol) × (0.02 L solution/ 0.00248 kg) = 329.75 mg POXC kg -1 soil

output["f(x)log"] = ["y = " + MathROUND(reg.m,6)+" x +" + MathROUND(reg.b,6)," r2: " + MathROUND(_reg.r2,2)];

output["f(x)linear"] = ["y = " + MathROUND(_reg.m,6)+" x +" + MathROUND(_reg.b,6)," r2: " + MathROUND(_reg.r2,2)];

output["toDevice"] = ["1020+"+MathROUND(reg.m,6)+"+"+MathROUND(reg.b,6)+"+-1+"];
//output["reg.m"] = _reg.m;
//output["reg.b"] = _reg.b;
//output["reg.r2"] = _reg.r2;
output["_A1"] = _A1;
output["_A2"] = _A2;
output["_A3"] = _A3;
output["A1"] = A1;
output["A2"] = A2;
output["A3"] = A3;
output["blank"] = I0;

return output;
 }

function macro_7(json){
var data = json.data_raw;
var output = {};
var sample_cal = MathMEAN(data.slice(2,18));

// retrieve the baseline information from the data JSON and save (using measuring light 15, calibrating light 14)
for (i in json.get_ir_baseline) {	
	if (json.get_ir_baseline[i][0] == 15) {
		var slope_light = json.get_ir_baseline[i][1];
		var yint_light = json.get_ir_baseline[i][2];
	}
	if (json.get_ir_baseline[i][0] == 14) {
		var slope_cal = json.get_ir_baseline[i][1];
		var yint_cal = json.get_ir_baseline[i][2];
	}
}


// calculate the baseline
var shinyness = (sample_cal-yint_cal)/slope_cal; // where 0 is dull black electrical tape, and 1 is shiny aluminum
var baseline = slope_light*shinyness+yint_light;

var Fss = MathMEAN(data.slice(22,38)) - baseline;
var FmPs = MathMEAN(data.slice(42,88)) - baseline; // take the 4 largest values and average them
var fvfms = (FmPs-Fss)/FmPs;

var Fs = MathMEAN(data.slice(21,24)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals = data.slice(42,88).sort();  // sort the saturating light values from low to high
var FmP = MathMEAN(sat_vals.slice(43,46)) - baseline; // take the 4 largest values and average them
var fvfm = (FmP-Fs)/FmP;

var Fsnn = Fs+json.slope_34*10+json.yintercept_34;
var FmPnn = FmP+json.slope_34*10+json.yintercept_34;
var fvfmnn = (FmPnn-Fsnn)/FmPnn;

var Fsn = Fs + baseline;
var FmPn = FmP + baseline;
var fvfmn = (FmPn-Fsn)/FmPn;

var Fs_stdev = MathSTDEV(data.slice(22,38));
var Fm_stdev = MathSTDEV(data.slice(42,88));
var Fs_relative_stdev = (MathMEAN(data.slice(42,88))-MathMEAN(data.slice(22,38))) / MathSTDEV(data.slice(22,38));

//output["sorted"] = sat_vals;
output["Phi2"] = MathROUND(fvfm,3);
output["LEF"] = MathROUND((fvfm  * 0.45 * json.light_intensity),3);
output["Fs"] = MathROUND(Fs,1);
output["FmPrime"] = MathROUND(FmP,1);
//output["Fss"] = MathROUND(Fss,1);
//output["FmPrimes"] = MathROUND(FmPs,1);
//output["Fv/Fm nb no"] = MathROUND(fvfmnn,3);
//output["Fv/Fm nb"] = MathROUND(fvfmn,3);
//output["Fv/Fms"] = MathROUND(fvfms,3);
//output["shinyness"] = MathROUND(shinyness,2);
//output["sample_cal"] = MathROUND(sample_cal,1);
output["baseline"] = MathROUND(baseline,1);
// output signal quality
output["Fs_relative_stdev"] = MathROUND(Fs_relative_stdev,4); 
output["Fs_stdev"] = MathROUND(Fs_stdev,4);
output["Fm_stdev"] = MathROUND(Fm_stdev,4);

return output;
 }

function macro_20(json){
var output = {};

var blank1 = MathMEAN(json.data_raw.slice(40,460));
var blank2 = MathMEAN(json.data_raw.slice(540,960));
var blank3 = MathMEAN(json.data_raw.slice(1040,1460));
var blank4 = MathMEAN(json.data_raw.slice(1540,1960));
var blank5 = MathMEAN(json.data_raw.slice(2040,2460));
var blank = (blank1+blank2+blank3+blank4+blank5)/5;

output["blank"] = blank;
output["toDevice"] = ["1021+"+blank+"+0+-1+"];

return output;
 }

function macro_19(json){
var output = {};

// perform 5 technical replicates
var sample1 = MathMEAN(json.data_raw.slice(40,460));
var sample2 = MathMEAN(json.data_raw.slice(540,960));
var sample3 = MathMEAN(json.data_raw.slice(1040,1460));
var sample4 = MathMEAN(json.data_raw.slice(1540,1960));
var sample5 = MathMEAN(json.data_raw.slice(2040,2460));
var sample = (sample1+sample2+sample3+sample4+sample5)/5;

// NOTE! based on uM values for potassium permangenate concentrations
var slope = json.get_userdef1[0]; 
var yint = json.get_userdef1[1];
var blank = json.get_userdef2[0];

//var sample = MathMEAN(_sample);

var log_sample = MathLOG( ( sample/blank ) ) * -1;

var sample_concentration = (log_sample - yint)/slope;

var activeCarbon = (0.02 - (yint/1000 +(slope/1000 * log_sample))) * 9000 * (0.02 / 0.00248); // divide by 1000 to convert yint and slope to mM from uM

//Active Carbon (mg kg -1) = [0.02 mol/ L – (a + b × Abs)] × (9000 mg C/ mol) × (0.02 L solution/ Wt)
//[0.02 M – (-0.00004 + (0.0502 × 0.3087)] × (9000 mg C/ mol) × (0.02 L solution/ 0.00248 kg) = 329.75 mg POXC kg -1 soil

output["active carbon"] = MathROUND(activeCarbon,1);
output["sample concentration"] = MathROUND(sample_concentration,4);
output["blank signal"] = MathROUND((blank - sample),1);
output["sample"] = MathROUND(sample,1);
output["log sample"] = MathROUND(log_sample,3);
output["blank"] = MathROUND(blank,1);
output["slope"] = MathROUND(slope,6);
output["yint"] = MathROUND(yint,6);

return output;
 }

function macro_21(json){
var output = {};

var resistance_value = json.analog_read*(2.5/65535); // 2.5V in 2^16 counts
var conductance_value = 1/resistance_value;
var conductance_value_per_meter = conductance_value*.0135*1000 // distance between electrodes in DF Robot moisture sensor V2 is 13.5mm or .0135m

output ["conductance (mS/m)"] = MathROUND(conductance_value_per_meter,2);
output ["resistance"] = MathROUND(resistance_value,4);
output ["raw counts"] = json.analog_read;

return output;
 }

function macro_22(json){
var output = {};

var sample = MathMEAN(json.data_raw.slice(40,460));

// NOTE! based on uM values for potassium permangenate concentrations
var slope = json.get_userdef1[0]; 
var yint = json.get_userdef1[1];
var blank = json.get_userdef2[0];

//var sample = MathMEAN(_sample);

var log_sample = MathLOG( ( sample/blank ) ) * -1;

var sample_concentration = (log_sample - yint)/slope;

var activeCarbon = (0.02 - (yint/1000 +(slope/1000 * log_sample))) * 9000 * (0.02 / 0.00248); // divide by 1000 to convert yint and slope to mM from uM

//Active Carbon (mg kg -1) = [0.02 mol/ L – (a + b × Abs)] × (9000 mg C/ mol) × (0.02 L solution/ Wt)
//[0.02 M – (-0.00004 + (0.0502 × 0.3087)] × (9000 mg C/ mol) × (0.02 L solution/ 0.00248 kg) = 329.75 mg POXC kg -1 soil

output["active carbon"] = MathROUND(activeCarbon,1);
output["sample concentration"] = MathROUND(sample_concentration,4);
output["blank signal"] = MathROUND((blank - sample),1);
output["sample"] = MathROUND(sample,1);
output["log sample"] = MathROUND(log_sample,3);
output["blank"] = MathROUND(blank,1);
output["slope"] = MathROUND(slope,6);
output["yint"] = MathROUND(yint,6);

return output;
 }

function macro_24(json){
var output = {};

var ave = MathMEAN(json.data_raw.slice(10,90));

output ["electronic baseline"] = MathROUND(ave,2);
output["pulse length us"] = 10;

return output;
 }

function macro_23(json){
var data = json.data_raw;
var output = {"toDevice":"1015+"};// output to device, start 1015 to call spad calibration
var lights = [15,16,11,12,20,2,14,10];// define the lights to be calibrated
var pulses = 200;// number of pulses in a cycle
for (var i = 0;i<lights.length;i++) { // loop through and save one averaged 'point' for each of the cycles
    output [lights[i]]  = MathROUND(MathMEAN(json.data_raw.slice((i*pulses+40),(i*pulses+160))),2);
	output ["toDevice"] += lights[i]; 
	output ["toDevice"] += "+";  
    output ["toDevice"] += MathMEAN(json.data_raw.slice((i*pulses+40),(i*pulses+160))); 
	output ["toDevice"] += "+";    
	output ["toDevice"] += "0"; 
	output ["toDevice"] += "+";
}

output["toDevice"] += "-1+";

return output;	

 }

function macro_25(json){

var output = {};
var spad;

var min;
var max;
var dif;
var slope;

min = MathMEAN(json.data_raw.slice(10,90));
max = MathMAX(json.data_raw.slice(105,195));
dif = max - min;
slope = MathMEAN(json.data_raw.slice(101,111)) - json.data_raw[100];

output ["min"] = MathROUND(min,2);
output ["max"] = MathROUND(max,2);
output ["dif"] = MathROUND(dif,2);
output ["slope"] = MathROUND(slope,2);


return output;
 }

function macro_26(json){
var data = json.data_raw;
var output = {};
var sample_cal = MathMEAN(data.slice(2,18));

// retrieve the baseline information from the data JSON and save (using measuring light 15, calibrating light 14)
for (i in json.get_ir_baseline) {	
	if (json.get_ir_baseline[i][0] == 15) {
		var slope_light = json.get_ir_baseline[i][1];
		var yint_light = json.get_ir_baseline[i][2];
	}
	if (json.get_ir_baseline[i][0] == 14) {
		var slope_cal = json.get_ir_baseline[i][1];
		var yint_cal = json.get_ir_baseline[i][2];
	}
}


// calculate the baseline
var shinyness = (sample_cal-yint_cal)/slope_cal; // where 0 is dull black electrical tape, and 1 is shiny aluminum
var baseline = slope_light*shinyness+yint_light;

var Fss = MathMEAN(data.slice(22,38)) - baseline;
var FmPs = MathMEAN(data.slice(42,88)) - baseline; // take the 4 largest values and average them
var fvfms = (FmPs-Fss)/FmPs;

var Fs = MathMEAN(data.slice(21,24)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals = data.slice(42,88).sort();  // sort the saturating light values from low to high
var FmP = MathMEAN(sat_vals.slice(43,46)) - baseline; // take the 4 largest values and average them
var fvfm = (FmP-Fs)/FmP;

var Fsnn = Fs+json.slope_34*10+json.yintercept_34;
var FmPnn = FmP+json.slope_34*10+json.yintercept_34;
var fvfmnn = (FmPnn-Fsnn)/FmPnn;

var Fsn = Fs + baseline;
var FmPn = FmP + baseline;
var fvfmn = (FmPn-Fsn)/FmPn;

var Fs_stdev = MathSTDEV(data.slice(22,38));
var Fm_stdev = MathSTDEV(data.slice(42,88));
var Fs_relative_stdev = (MathMEAN(data.slice(42,88))-MathMEAN(data.slice(22,38))) / MathSTDEV(data.slice(22,38));

//NPQt parameters
var FoPrime = MathMEAN(data.slice(1250,1300)) - baseline;
var NPQt = 4.88/((Fs/FoPrime)*(1/(1-fvfm))-1)-1

//output["sorted"] = sat_vals;
output["Phi2"] = MathROUND(fvfm,3);
output["LEF"] = MathROUND((fvfm  * 0.45 * json.light_intensity),3);
output["NPQt"] = MathROUND(NPQt,3);
output["FoPrime"] = MathROUND(FoPrime,3);
output["Fs"] = MathROUND(Fs,1);
output["FmPrime"] = MathROUND(FmP,1);
output["baseline"] = MathROUND(baseline,1);

// output signal quality
output["Fs_relative_stdev"] = MathROUND(Fs_relative_stdev,4); 
output["Fs_stdev"] = MathROUND(Fs_stdev,4);
output["Fm_stdev"] = MathROUND(Fm_stdev,4);

return output;
 }

function macro_29(json){
var output = {};

var ave = MathMEAN(json.data_raw.slice(10,90));

output ["electronic baseline"] = MathROUND(ave,2);
output["pulse length us"] = 50;

return output;
 }

function macro_30(json){
var output = {};

var ave = MathMEAN(json.data_raw.slice(10,90));

output ["electronic baseline"] = MathROUND(ave,2);
output["pulse length us"] = 100;

return output;
 }

function macro_31(json){
var output = {};

var ave = MathMEAN(json.data_raw.slice(10,90));

output ["electronic baseline"] = MathROUND(ave,2);
output["pulse length us"] = 10;

return output;
 }

function macro_32(json){
var output = {};

var ave = MathMEAN(json.data_raw.slice(10,90));

output ["electronic baseline"] = MathROUND(ave,2);
output["pulse length us"] = 20;

return output;
 }

function macro_27(json){
var data = json.data_raw;
var output = {};
var sample_cal = MathMEAN(data.slice(2,18));

// retrieve the baseline information from the data JSON and save (using measuring light 15, calibrating light 14)
for (i in json.get_ir_baseline) {	
	if (json.get_ir_baseline[i][0] == 15) {
		var slope_light = json.get_ir_baseline[i][1];
		var yint_light = json.get_ir_baseline[i][2];
	}
	if (json.get_ir_baseline[i][0] == 14) {
		var slope_cal = json.get_ir_baseline[i][1];
		var yint_cal = json.get_ir_baseline[i][2];
	}
}

// calculate the baseline
var shinyness = (sample_cal-yint_cal)/slope_cal; // where 0 is dull black electrical tape, and 1 is shiny aluminum
var baseline = slope_light*shinyness+yint_light;

var Fss = MathMEAN(data.slice(22,38)) - baseline;
var FmPs = MathMEAN(data.slice(42,88)) - baseline; // take the 4 largest values and average them
var fvfms = (FmPs-Fss)/FmPs;

var Fs = MathMEAN(data.slice(21,24)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals = data.slice(42,88).sort();  // sort the saturating light values from low to high
var FmP = MathMEAN(sat_vals.slice(43,46)) - baseline; // take the 4 largest values and average them
var fvfm = (FmP-Fs)/FmP;

var Fsnn = Fs+json.slope_34*10+json.yintercept_34;
var FmPnn = FmP+json.slope_34*10+json.yintercept_34;
var fvfmnn = (FmPnn-Fsnn)/FmPnn;

var Fsn = Fs + baseline;
var FmPn = FmP + baseline;
var fvfmn = (FmPn-Fsn)/FmPn;

var Fs_stdev = MathSTDEV(data.slice(22,38));
var Fm_stdev = MathSTDEV(data.slice(42,88));
var Fs_relative_stdev = (MathMEAN(data.slice(42,88))-MathMEAN(data.slice(22,38))) / MathSTDEV(data.slice(22,38));

//NPQt parameters
var FoPrime = MathMEAN(data.slice(760,789)) - baseline;
var NPQt = 4.88/((Fs/FoPrime)*(1/(1-fvfm))-1)-1
//following loops, light
var Fs1 = MathMEAN(data.slice(811,814)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals1 = data.slice(831,880).sort();  // sort the saturating light values from low to high
var Fm1 = MathMEAN(sat_vals1.slice(41,45)) - baseline; // take the 4 largest values and average them
var fvfm1 = (Fm1-Fs1)/Fm1;
var FoPrime1 = MathMEAN(data.slice(1550,1579)) - baseline;
var NPQt1 = 4.88/((Fs1/FoPrime1)*(1/(1-fvfm1))-1)-1

var Fs2 = MathMEAN(data.slice(1601,1620)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals2 = data.slice(1621,1670).sort();  // sort the saturating light values from low to high
var Fm2 = MathMEAN(sat_vals2.slice(41,45)) - baseline; // take the 4 largest values and average them
var fvfm2 = (Fm2-Fs2)/Fm2;
var FoPrime2 = MathMEAN(data.slice(2340,2369)) - baseline;
var NPQt2 = 4.88/((Fs2/FoPrime2)*(1/(1-fvfm2))-1)-1

var Fs3 = MathMEAN(data.slice(2391,2410)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals3 = data.slice(2411,2460).sort();  // sort the saturating light values from low to high
var Fm3 = MathMEAN(sat_vals3.slice(41,45)) - baseline; // take the 4 largest values and average them
var fvfm3 = (Fm3-Fs3)/Fm3;
var FoPrime3 = MathMEAN(data.slice(3130,3159)) - baseline;
var NPQt3 = 4.88/((Fs3/FoPrime3)*(1/(1-fvfm3))-1)-1

var Fs4 = MathMEAN(data.slice(3181,3200)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals4 = data.slice(3201,3250).sort();  // sort the saturating light values from low to high
var Fm4 = MathMEAN(sat_vals4.slice(41,45)) - baseline; // take the 4 largest values and average them
var fvfm4 = (Fm4-Fs4)/Fm4;
var FoPrime4 = MathMEAN(data.slice(3920,3949)) - baseline;
var NPQt4 = 4.88/((Fs4/FoPrime4)*(1/(1-fvfm4))-1)-1

//following loops, dark
var Fs5 = MathMEAN(data.slice(3971,3990)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals5 = data.slice(3991,4040).sort();  // sort the saturating light values from low to high
var Fm5 = MathMEAN(sat_vals5.slice(41,45)) - baseline; // take the 4 largest values and average them
var fvfm5 = (Fm5-Fs5)/Fm5;
var FoPrime5 = MathMEAN(data.slice(4710,4739)) - baseline;
var NPQt5 = 4.88/((Fs5/FoPrime5)*(1/(1-fvfm5))-1)-1

var Fs6 = MathMEAN(data.slice(4761,4780)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals6 = data.slice(4781,4830).sort();  // sort the saturating light values from low to high
var Fm6 = MathMEAN(sat_vals6.slice(41,45)) - baseline; // take the 4 largest values and average them
var fvfm6 = (Fm6-Fs6)/Fm6;
var FoPrime6 = MathMEAN(data.slice(5500,5579)) - baseline;
var NPQt6 = 4.88/((Fs6/FoPrime6)*(1/(1-fvfm6))-1)-1

var Fs7 = MathMEAN(data.slice(5551,5570)) - baseline; // take only the first 4 values in the Fs range, excluding the very first
var sat_vals7 = data.slice(5571,5621).sort();  // sort the saturating light values from low to high
var Fm7 = MathMEAN(sat_vals7.slice(41,45)) - baseline; // take the 4 largest values and average them
var fvfm7 = (Fm7-Fs7)/Fm7;
var FoPrime7 = MathMEAN(data.slice(6290,6319)) - baseline;
var NPQt7 = 4.88/((Fs7/FoPrime7)*(1/(1-fvfm7))-1)-1

//NPQ

var NPQ1 = (FmP-Fm1)/Fm1
var NPQ2 = (FmP-Fm2)/Fm2
var NPQ3 = (FmP-Fm3)/Fm3
var NPQ4 = (FmP-Fm4)/Fm4
var NPQ5 = (FmP-Fm5)/Fm5
var NPQ6 = (FmP-Fm6)/Fm6
var NPQ7 = (FmP-Fm7)/Fm7

//qP
var qP=(FmP-Fs)/(FmP-FoPrime)
var qP1=(Fm1-Fs1)/(Fm1-FoPrime)
var qP2=(Fm2-Fs2)/(Fm2-FoPrime)
var qP3=(Fm3-Fs3)/(Fm3-FoPrime)
var qP4=(Fm4-Fs4)/(Fm4-FoPrime)
var qP5=(Fm5-Fs5)/(Fm5-FoPrime)
var qP6=(Fm6-Fs6)/(Fm6-FoPrime)
var qP7=(Fm7-Fs7)/(Fm7-FoPrime)

//qL
var qL=((FmP-Fs)/(FmP-FoPrime))*(FoPrime/Fs)
var qL1=((Fm1-Fs1)/(Fm1-FoPrime))*(FoPrime/Fs1)
var qL2=((Fm2-Fs2)/(Fm2-FoPrime))*(FoPrime/Fs2)
var qL3=((Fm3-Fs3)/(Fm3-FoPrime))*(FoPrime/Fs3)
var qL4=((Fm4-Fs4)/(Fm4-FoPrime))*(FoPrime/Fs4)
var qL5=((Fm5-Fs5)/(Fm5-FoPrime))*(FoPrime/Fs5)
var qL6=((Fm6-Fs6)/(Fm6-FoPrime))*(FoPrime/Fs6)
var qL7=((Fm7-Fs7)/(Fm7-FoPrime))*(FoPrime/Fs7)

//ETR depends on light intensity
var ETR=250*0.84*0.5*fvfm
var ETR1=250*0.84*0.5*fvfm1
var ETR2=250*0.84*0.5*fvfm2
var ETR3=250*0.84*0.5*fvfm3
var ETR4=250*0.84*0.5*fvfm4
var ETR5=250*0.84*0.5*fvfm5
var ETR6=250*0.84*0.5*fvfm6
var ETR7=250*0.84*0.5*fvfm7

//output["sorted"] = sat_vals;
output["Phi2"] = MathROUND(fvfm,3);
output["LEF"] = MathROUND((fvfm  * 0.45 * json.light_intensity),3);
output["FoPrime"] = MathROUND(FoPrime,3);

output["Fs"] = MathROUND(Fs,1);
output["FmPrime"] = MathROUND(FmP,1);
output["baseline"] = MathROUND(baseline,1);

output["qP"] = MathROUND(qP,3);
output["qP1"] = MathROUND(qP1,3);
output["qP2"] = MathROUND(qP2,3);
output["qP3"] = MathROUND(qP3,3);
output["qP4"] = MathROUND(qP4,3);
output["qP5"] = MathROUND(qP5,3);
output["qP6"] = MathROUND(qP6,3);
output["qP7"] = MathROUND(qP7,3);

output["qL"] = MathROUND(qL,3);
output["qL1"] = MathROUND(qL1,3);
output["qL2"] = MathROUND(qL2,3);
output["qL3"] = MathROUND(qL3,3);
output["qL4"] = MathROUND(qL4,3);
output["qL5"] = MathROUND(qL5,3);
output["qL6"] = MathROUND(qL6,3);
output["qL7"] = MathROUND(qL7,3);

output["NPQ1"] = MathROUND(NPQ1,3);
output["NPQ2"] = MathROUND(NPQ2,3);
output["NPQ3"] = MathROUND(NPQ3,3);
output["NPQ4"] = MathROUND(NPQ4,3);
output["NPQ5"] = MathROUND(NPQ5,3);
output["NPQ6"] = MathROUND(NPQ6,3);
output["NPQ7"] = MathROUND(NPQ7,3);

output["NPQt1"] = MathROUND(NPQt1,3);
output["NPQt2"] = MathROUND(NPQt2,3);
output["NPQt3"] = MathROUND(NPQt3,3);
output["NPQt4"] = MathROUND(NPQt4,3);
output["NPQt5"] = MathROUND(NPQt5,3);
output["NPQt6"] = MathROUND(NPQt6,3);
output["NPQt7"] = MathROUND(NPQt7,3);

output["fvfm"] = MathROUND(fvfm,3);
output["fvfm1"] = MathROUND(fvfm1,3);
output["fvfm2"] = MathROUND(fvfm2,3);
output["fvfm3"] = MathROUND(fvfm3,3);
output["fvfm4"] = MathROUND(fvfm4,3);
output["fvfm5"] = MathROUND(fvfm5,3);
output["fvfm6"] = MathROUND(fvfm6,3);
output["fvfm7"] = MathROUND(fvfm7,3);

output["ETR"] = MathROUND(ETR,1);

// output signal quality
output["Fs_relative_stdev"] = MathROUND(Fs_relative_stdev,4); 
output["Fs_stdev"] = MathROUND(Fs_stdev,4);
output["Fm_stdev"] = MathROUND(Fm_stdev,4);

return output;
 }

function macro_33(json){
var output = {};

var ave = MathMEAN(json.data_raw.slice(10,90));

output ["electronic baseline"] = MathROUND(ave,2);
output["pulse length us"] = 50;

return output;
 }

function macro_28(json){
var output = {};

var ave = MathMEAN(json.data_raw.slice(10,90));

output ["electronic baseline"] = MathROUND(ave,2);
output["pulse length us"] = 20;

return output;
 }

function macro_34(json){
var output = {};

var ave = MathMEAN(json.data_raw.slice(10,90));

output ["electronic baseline"] = MathROUND(ave,2);
output["pulse length us"] = 100;

return output;
 }

function macro_45(json){
//Define your variables here...
var output = {};

var data = json.data_raw

   	function unique(q){
        var arr = [];
        for (var i = 0; i < q.length; i++) {
            if (arr.indexOf(q[i]) == -1)
                arr.push(q[i]);
        }
        return arr;
    }

//Check if the value exists in json
if (json.time !== undefined){

  var leds = [];
  var par_data = [];
  var intensity = [];
  var message = json.message;
// Number of points in a single fitted calibration
  var points_per_fit = 4;
  
// Pull out the LEDs, intensities, and data for each of the calibration points
  for (var x=1;x<message.length;x++) {  
    var split_message = message[x][1].split(/[\s]+/);
    leds.push(Number(split_message[split_message.length-2]));
    intensity.push(Number(split_message[split_message.length-1]));
    par_data.push(Number(message[x][2]));
  }

  var leds_unique = unique(leds);
  var leds_unique_data = [];
  var leds_unique_intensity = [];

// make a list of the unique LEDs, and subarrays for the data and intensities for each LED
  for (var x=0;x<leds_unique.length;x++) {
    leds_unique_data[x] = [];
    leds_unique_intensity[x] = [];
    for (var y=0;y<points_per_fit;y++) {
      leds_unique_data[x].push(par_data[points_per_fit*x+y]);
      leds_unique_intensity[x].push(intensity[points_per_fit*x+y]);
    }
  }
  
  var detector = [];
  var points_per_fit = 4;
  
  for (var x=0;x<leds_unique.length;x++) {
    detector[x] = [];
    for (var y=0;y<points_per_fit;y++) {
	  response = data.slice(50*(points_per_fit*x+y),50*(points_per_fit*x+y)+50);
      detector[x].push(MathMEAN(response,1));
    }
  }
  
//  output the results
//  output ["unique LEDs"] = leds_unique;
//  output ["intensity"] = intensity;   
//  output ["all data"] = par_data; 
//  output ["all message"] = message; 
  if (message[0][2] == 1) {
    output ["toDevice"] = "1012+";  // factory setting
  }
  else {
    output ["toDevice"] = "1011+";  // user setting
  }    
  output['unique LEDs'] = "";	
  for (i in leds_unique) {
    output['unique LEDs'] += leds_unique[i];
    if (i < leds_unique.length-1) {
      output['unique LEDs'] += ",";
    }
  }
  // perform linear regression, calculate mx+b for each light
  for (var x=0;x<leds_unique_data.length;x++) {
    var reg = MathLINREG(leds_unique_intensity[x],detector[x]);
      if (reg.m) {
        output["slope " + leds_unique[x]] = MathROUND(reg.m,3);
        output["yint " + leds_unique[x]] = MathROUND(reg.b,1);
        output["r2 " + leds_unique[x]] = MathROUND(reg.r,3);
        output ["toDevice"] += leds_unique[x]; 
        output ["toDevice"] += "+";  
        output ["toDevice"] += Number(MathROUND(reg.m,3)); 
        output ["toDevice"] += "+";    
        output ["toDevice"] += Number(MathROUND(reg.b,1)); 
        output ["toDevice"] += "+";
      }
    }  
	output ["toDevice"] += "-1+";

}

//Return data
return output;

 }

function macro_44(json){
//function Macro_44(json) {
    //Define your variables here...
    var output = {};
    
   	function unique(q){
        var arr = [];
        for (var i = 0; i < q.length; i++) {
            if (arr.indexOf(q[i]) == -1)
                arr.push(q[i]);
        }
        return arr;
    }

    //Check if the value exists in json
    if (json.time !== undefined) {

        var leds = [];
        var par_data = [];
        var intensity = [];
        var message = json.message;
        // Number of points in a single fitted calibration
        var points_per_fit = 4;

        // Pull out the LEDs, intensities, and data for each of the calibration points
        for (var x = 1; x < message.length; x++) {
            var split_message = message[x][1].split(/[\s]+/);
          if (Number(split_message[split_message.length-2])) {
            leds.push(Number(split_message[split_message.length-2]));
            intensity.push(Number(split_message[split_message.length-1]));
            par_data.push(Number(message[x][2]));
          }
          }

        var leds_unique = unique(leds);
        var leds_unique_data = [];
        var leds_unique_intensity = [];

        // make a list of the unique LEDs, and subarrays for the data and intensities for each LED
        for (var x = 0; x < leds_unique.length; x++) {
            leds_unique_data[x] = [];
            leds_unique_intensity[x] = [];
            for (var y = 0; y < points_per_fit; y++) {
                leds_unique_data[x].push(par_data[points_per_fit * x + y]);
                leds_unique_intensity[x].push(intensity[points_per_fit * x + y]);
            }
        }

//      output the results
//      output ["all data"] = leds_unique_data; 
//      output ["all leds"] = leds; 
        output ["toDevice"] = "1016+";
        output['unique LEDs'] = "";	

      	for (i in leds_unique) {
         	output['unique LEDs'] += leds_unique[i];
         	if (i < leds_unique.length-1) {
	         	output['unique LEDs'] += ",";
        	}
	    }
//      perform linear regression, calculate mx+b for each light
        for (var x = 0; x < leds_unique_data.length; x++) {
            var reg = MathLINREG(leds_unique_intensity[x], leds_unique_data[x]);
          if (reg.m) {
            output["slope " + leds_unique[x]] = MathROUND(reg.m, 3);
            if (!(reg.b > 0 && (leds_unique[x] != 2 | leds_unique[x] != 20))) { // force y int to 0 if greater than zero on actinic lights which must be off when intensity = 0.
            	output["yint " + leds_unique[x]] = MathROUND(reg.b, 1);
            }
            else {	// if actinic light y intercept is > 0, then make it zero.
            	output["yint " + leds_unique[x]] = 0;
            }
            output["r2 " + leds_unique[x]] = MathROUND(reg.r, 3);
            output ["toDevice"] += leds_unique[x];
            output ["toDevice"] += "+";
            output ["toDevice"] += Number(MathROUND(reg.m, 3));
            output ["toDevice"] += "+";
            output ["toDevice"] += Number(MathROUND(reg.b, 1));
            output ["toDevice"] += "+";
          }
        }
        output ["toDevice"] += "-1+";
    }

    //Return data
    return output;
//}

 }

function macro_47(json){
var data = json.data_raw;
var output = {};
var sample_cal = MathMEAN(data.slice(2,18));

// retrieve the baseline information from the data JSON and save (using measuring light 15, calibrating light 14)
for (var i in json.get_ir_baseline) {	
	if (json.get_ir_baseline[i][0] == 15) {
		var slope_light = json.get_ir_baseline[i][1];
		var yint_light = json.get_ir_baseline[i][2];
	}
	if (json.get_ir_baseline[i][0] == 14) {
		var slope_cal = json.get_ir_baseline[i][1];
		var yint_cal = json.get_ir_baseline[i][2];
	}
}

var npq_string = [];
var phi1_string = [];

  for (var i=20;i<data.length;i++) {
    if (i%2 == 1) {
	    npq_string.push(data[i]);
//	    npq_string += ",";
    }
    else {
	    phi1_string.push(data[i]);
//	    phi1_string += ",";
    }
}

// PSII efficiency parameters

// calculate the baseline
var shinyness = (sample_cal-yint_cal)/slope_cal; // where 0 is dull black electrical tape, and 1 is shiny aluminum
var baseline = slope_light*shinyness+yint_light;

var sorted = npq_string.slice(0,5).sort();  // sort to eliminate high or low vals
var Fs = MathMEAN(sorted.slice(1,4)) - baseline;
sorted = npq_string.slice(12,33).sort();  
var FmP = MathMEAN(sorted.slice(17,20)) - baseline;
var fvfm = (FmP-Fs)/FmP;

//var Fs_stdev = MathSTDEV(npq_string.slice(22,38));
//var Fm_stdev = MathSTDEV(npq_string.slice(42,88));
//var Fs_relative_stdev = (MathMEAN(npq_string.slice(42,88))-MathMEAN(npq_string.slice(22,38))) / MathSTDEV(npq_string.slice(22,38));

//NPQt parameters

sorted = npq_string.slice(700,730).sort();  
var FoPrime = MathMEAN(sorted.slice(2,28)) - baseline;
var NPQt = 4.88/((Fs/FoPrime)*(1/(1-fvfm))-1)-1;


/*
1255 - 780
390 - 626 
170 - 270
85 - 135
1490 - 1540
1470 - 1520
738 - 757

1250 - 1000
1230 - 980
615 - 490

1490 - 1400
1470 - 1380
735 - 690

*/

//PSI efficiency parameters

sorted = phi1_string.slice(2,8).sort();  // sort to eliminate high or low vals
var phi1_s = MathMEAN(sorted.slice(2,6));
//var phi1_so = sorted.slice(2,6);

sorted = phi1_string.slice(25,40).sort();  // sort to eliminate high or low vals
var phi1_m = MathMEAN(sorted.slice(2,13));
//var phi1_mo = sorted.slice(2,13);

sorted = phi1_string.slice(85,135).sort();  // sort to eliminate high or low vals
var phi1_min = MathMEAN(sorted.slice(5,45));
//var phi1_mino = sorted.slice(5,45);

sorted = phi1_string.slice(490,615).sort();  // sort to eliminate high or low vals
var phi1_farred = MathMEAN(sorted.slice(10,115));
//var phi1_farredo = sorted.slice(10,115);

sorted = phi1_string.slice(690,735).sort();  // sort to eliminate high or low vals
var phi1_sprime = MathMEAN(sorted.slice(5,40));
//var phi1_sprimeo = sorted.slice(5,40);

sorted = phi1_string.slice(747,757).sort();  // sort to eliminate high or low vals
var phi1_max = MathMEAN(sorted.slice(2,7));
//var phi1_maxo = sorted.slice(2,7);


phi1_ambient = phi1_m - phi1_s;
phi1_max = phi1_max - phi1_sprime;

var phi1_total = phi1_min-phi1_max;
var phi1_active = phi1_min-phi1_farred;
var phi1 = phi1_active/phi1_total;
var phi1_dave = phi1_ambient/phi1_max;

// for troubleshooting
//output["1"] = npq_string.slice(700,730);
//output["2"] = npq_string.slice(12,33);
//output["3"] = sat_vals.slice(17,20);
//output["4"] = Number(MathROUND(fvfm,3));
//output["phi1 all"] = phi1_string;
//output["npq all"] = npq_string;

//output["sorted"] = sat_vals
//output["phi1_min"] = Number(MathROUND(phi1_min,1));
//output["phi1_farred"] = Number(MathROUND(phi1_farred,1));
//output["phi1_max"] = Number(MathROUND(phi1_max,1));
//output["phi1_s"] = phi1_so;
//output["phi1_m"] = phi1_mo;
//output["phi1_sprime"] = phi1_sprimeo;

output["phi1_ambient"] = Number(MathROUND(phi1_ambient));
output["phi1_max"] = Number(MathROUND(phi1_max));
output["phi1_dave"] = Number(MathROUND(phi1_dave));
output["phi2"] = Number(MathROUND(fvfm,3));
//output["phi1_active"] = Number(MathROUND(phi1_active,1));
output["phi1"] = Number(MathROUND(phi1,3));
output["LEF"] = Number(MathROUND((fvfm  * 0.45 * json.light_intensity),3));
output["ps1_LEF_dave"] = Number(MathROUND((phi1_dave * json.light_intensity),3));
output["ps1_LEF"] = Number(MathROUND((phi1 * json.light_intensity),3));
output["NPQt"] = Number(MathROUND(NPQt,3));
output["phi2 by phi1"] =  Number(MathROUND(fvfm/phi1,3));
output["phi2 by phi1 dave"] =  Number(MathROUND(fvfm/phi1_dave,6));
output["FoPrime"] = Number(MathROUND(FoPrime,3));
output["Fs"] = Number(MathROUND(Fs,1));
output["FmPrime"] = Number(MathROUND(FmP,1));
output["phi1_total"] = Number(MathROUND(phi1_total,1));
output["baseline"] = Number(MathROUND(baseline,1));

// output signal quality
//output["Fs_relative_stdev"] = Number(MathROUND(Fs_relative_stdev,4)); 
//output["Fs_stdev"] = Number(MathROUND(Fs_stdev,4));
//output["Fm_stdev"] = Number(MathROUND(Fm_stdev,4));

return output;
 }

function macro_46(json){
//Define your variables here...
var output = {};

var data = json.data_raw

output ["PAR from known source"] = Number(json.message[0][2]);


//Return data
return output;

 }

function macro_48(json){
//Define your variables here...
var output = false;

//Check if the value exists in json
if (json.time !== undefined){
  
var volume = 2781.156786;
var area = 182.4906028;
var pressure = 101.325;
var mole_wt = 44.01;
var mole_conversion = 0.000001;
var unit_conversion = 10;
var R_constant = 8.31441;
var unit = 1000;
var co2produced = json.co2
var temperature_kelvin = json.temperature + 273;
				


	//Show value and name in output
	output = {'time':json.time};
}

//Return data
return output;
 }

