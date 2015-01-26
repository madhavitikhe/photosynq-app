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

function macro_3(json){
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
	
	output = {"Spad": spad, "Macro": 'SPAD_NDVI',  "HTML": "<b>Spad value:</b> <i>"+ MathROUND(spad) +"</i> | <b>NDVI value:</b> <i>"+ MathROUND(ndvi) +"</i> ", "GraphType": 'points'};
}
	
return output;
 }

function macro_6(json){
var output = false;
var values = json.data_raw

if(values){

	// Pick values from trace
	var f0 = MathMEAN(values.slice(38,48));
	var fm = MathMEAN(values.slice(38,48));
	var fs = MathMEAN(values.slice(38,48));
	var fmp = MathMEAN(values.slice(38,48));
	var fsp = MathMEAN(values.slice(38,48));
	var fmpp = MathMEAN(values.slice(38,48));
	var light = parseFloat(json.light_intensity);

	// Parameter calculations
	var phi2 = (FmP - Fs) / FmP;
	var lef = (phi2 * light * 0.4);
	var npq = (fm - fmp) / fmp;
	var qE = (fmpp - fmp) / fmpp;
	var qEsv = (fm / fmp) - (fm / fmpp);
	var fvfm = (fm - fv) / fm;
	var qI = (fm - fmpp) / fmpp;
	var qP = (fmp - fs) / (fmp - fsp);
	var qL = fsp / (fs * qP);

	output = {"Macro": 'Fluorescence Trace', "HTML": "", "GraphType": 'line'};

	// Add values from trace
	output['F0'] = f0;
	output['Fm'] = fm;
	output['Fs'] = fs;
	output['FmP'] = fmp;
	output['FsP'] = fsp;
	output['FmPP'] = fmpp;
	
	// Add calculated values
	output['Phi2'] = phi2;
	output['LEF'] = lef;
	output['NPQ'] = npq;
	output['qE'] = qE;
	output['qEsv'] = qEsv;
	output['Fv/Fm'] = fvfm;
	output['qI'] = qI;
	output['qP'] = qP;
	output['qL'] = qL;

}
return output;
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
var Fsnn = MathMEAN(data.slice(22,38))+json.slope_34*10+json.yintercept_34;
var FmPnn = MathMEAN(data.slice(42,88))+json.slope_34*10+json.yintercept_34;
var fvfmnn = (FmPnn-Fsnn)/FmPnn;
var Fsn = MathMEAN(data.slice(22,38));
var FmPn = MathMEAN(data.slice(42,88));
var fvfmn = (FmPn-Fsn)/FmPn;
var Fs = MathMEAN(data.slice(22,38)) - baseline;
var FmP = MathMEAN(data.slice(42,88)) - baseline;
var fvfm = (FmP-Fs)/FmP;
output["Fs"] = MathROUND(Fs,1);
output["Fm'"] = MathROUND(FmP,1);
output["Fv/Fm no baseline no offset"] = MathROUND(fvfmnn,3);
output["Fv/Fm no baseline"] = MathROUND(fvfmn,3);
output["Fv/Fm"] = MathROUND(fvfm,3);
output["shinyness"] = MathROUND(shinyness,2);
output["sample_cal"] = MathROUND(sample_cal,1);
output["baseline"] = MathROUND(baseline,1);
return output;
// example data from device to calculate
//{"device_id": 21.00,"firmware_version": "0.10","sample": [[{"protocol_id": "","slope_34":6.86,"yintercept_34":620.54,"slope_35":7.94,"yintercept_35":493.13,"get_ir_baseline": [[16,2073.11,255.68],[14,3778.02,578.79]],"data_raw":[2190,2204,2177,2207,2189,2214,2195,2185,2201,2180,2197,2154,2172,2193,2145,2191,2187,2204,2178,2180,2356,2340,2371,2373,2382,2366,2378,2399,2378,2389,2387,2390,2384,2401,2412,2386,2411,2401,2391,2421,2425,6438,7055,7341,7477,7538,7583,7606,7653,7669,7685,7728,7682,7688,7723,7733,7738,7758,7744,7754,7753,7755,7775,7768,7772,7744,7761,7788,7729,7768,7764,7739,7749,7733,7725,7793,7781,7761,7758,7769,7773,7748,7762,7768,7777,7753,7777,7768,7764,7753,7750,6849,6146,5657,5413,5218,5060,4949,4861,4821,4720,4693,4635,4580,4555,4551,4536,4510,4470,4480]}

 }

function macro_7(json){
// example protocol JSON to be used with this macro
// [{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[15,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,20,0],"act_intensities":[0,0,1140,0],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,200,200,200],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[15],[15],[15]]},{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[16,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,20,0],"act_intensities":[0,0,1140,0],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,200,200,200],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[16],[16],[16]]},{"protocols_delay":4,"get_offset":1,"get_ir_baseline":[20,14],"pulsesize":10,"pulsedistance":10000,"act1_lights":[0,0,15,0],"act_intensities":[700,700,700,700],"cal_intensities":[4095,0,0,0],"meas_intensities":[0,0,250,0],"pulses":[20,20,50,20],"detectors":[[34],[34],[34],[34]],"meas_lights":[[14],[20],[20],[20]]}]

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
var Fsnn = MathMEAN(data.slice(22,38))+json.slope_34*10+json.yintercept_34;
var FmPnn = MathMEAN(data.slice(42,88))+json.slope_34*10+json.yintercept_34;
var fvfmnn = (FmPnn-Fsnn)/FmPnn;
var Fsn = MathMEAN(data.slice(22,38));
var FmPn = MathMEAN(data.slice(42,88));
var fvfmn = (FmPn-Fsn)/FmPn;
var Fs = MathMEAN(data.slice(22,38)) - baseline;
var FmP = MathMEAN(data.slice(42,88)) - baseline;
var fvfm = (FmP-Fs)/FmP;
output["Fs"] = MathROUND(Fs,1);
output["Fm'"] = MathROUND(FmP,1);
output["Fv/Fm no baseline no offset"] = MathROUND(fvfmnn,3);
output["Fv/Fm no baseline"] = MathROUND(fvfmn,3);
output["Fv/Fm"] = MathROUND(fvfm,3);
output["shinyness"] = MathROUND(shinyness,2);
output["sample_cal"] = MathROUND(sample_cal,1);
output["baseline"] = MathROUND(baseline,1);
return output;
// example data from device to calculate
//{"device_id": 21.00,"firmware_version": "0.10","sample": [[{"protocol_id": "","slope_34":6.86,"yintercept_34":620.54,"slope_35":7.94,"yintercept_35":493.13,"get_ir_baseline": [[16,2073.11,255.68],[14,3778.02,578.79]],"data_raw":[2190,2204,2177,2207,2189,2214,2195,2185,2201,2180,2197,2154,2172,2193,2145,2191,2187,2204,2178,2180,2356,2340,2371,2373,2382,2366,2378,2399,2378,2389,2387,2390,2384,2401,2412,2386,2411,2401,2391,2421,2425,6438,7055,7341,7477,7538,7583,7606,7653,7669,7685,7728,7682,7688,7723,7733,7738,7758,7744,7754,7753,7755,7775,7768,7772,7744,7761,7788,7729,7768,7764,7739,7749,7733,7725,7793,7781,7761,7758,7769,7773,7748,7762,7768,7777,7753,7777,7768,7764,7753,7750,6849,6146,5657,5413,5218,5060,4949,4861,4821,4720,4693,4635,4580,4555,4551,4536,4510,4470,4480]}

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
var Fsnn = MathMEAN(data.slice(22,38))+json.slope_34*10+json.yintercept_34;
var FmPnn = MathMEAN(data.slice(42,88))+json.slope_34*10+json.yintercept_34;
var fvfmnn = (FmPnn-Fsnn)/FmPnn;
var Fsn = MathMEAN(data.slice(22,38));
var FmPn = MathMEAN(data.slice(42,88));
var fvfmn = (FmPn-Fsn)/FmPn;
var Fs = MathMEAN(data.slice(22,38)) - baseline;
var FmP = MathMEAN(data.slice(42,88)) - baseline;
var fvfm = (FmP-Fs)/FmP;
output["Fs"] = MathROUND(Fs,1);
output["Fm'"] = MathROUND(FmP,1);
output["Fv/Fm no baseline no offset"] = MathROUND(fvfmnn,3);
output["Fv/Fm no baseline"] = MathROUND(fvfmn,3);
output["Fv/Fm"] = MathROUND(fvfm,3);
output["shinyness"] = MathROUND(shinyness,2);
output["sample_cal"] = MathROUND(sample_cal,1);
output["baseline"] = MathROUND(baseline,1);
return output;
// example data from device to calculate
//{"device_id": 21.00,"firmware_version": "0.10","sample": [[{"protocol_id": "","slope_34":6.86,"yintercept_34":620.54,"slope_35":7.94,"yintercept_35":493.13,"get_ir_baseline": [[16,2073.11,255.68],[14,3778.02,578.79]],"data_raw":[2190,2204,2177,2207,2189,2214,2195,2185,2201,2180,2197,2154,2172,2193,2145,2191,2187,2204,2178,2180,2356,2340,2371,2373,2382,2366,2378,2399,2378,2389,2387,2390,2384,2401,2412,2386,2411,2401,2391,2421,2425,6438,7055,7341,7477,7538,7583,7606,7653,7669,7685,7728,7682,7688,7723,7733,7738,7758,7744,7754,7753,7755,7775,7768,7772,7744,7761,7788,7729,7768,7764,7739,7749,7733,7725,7793,7781,7761,7758,7769,7773,7748,7762,7768,7777,7753,7777,7768,7764,7753,7750,6849,6146,5657,5413,5218,5060,4949,4861,4821,4720,4693,4635,4580,4555,4551,4536,4510,4470,4480]}

 }

