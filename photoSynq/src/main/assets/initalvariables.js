/*
Define initial variables and files holding settings
*/

//-----------------------------------------------------------------------------------------------------------------------------------
// Array for tooltips
//-----------------------------------------------------------------------------------------------------------------------------------
var tooltips = {
'LEF':'(L)inear (e)lectron (t)ransfer. Defines how fast electrons flow through the photosynthetic machinery. Depends on the light intensity and Photosystem II activity (ΦII)',
'Phi2':'ΦII (phi - 2) reflects the activity of Photosystem II. Value is between 0 for no activity and about 0.85 for maximum activity.',
'Spad':'This value reflects the amount of chlorophyll in the sample. Higher value means higher chlorophyll content.',
'FmP':'Maximal variable fluorecence',
'Fs':'Minimal variable fluorescence',
'ecst':'Total change of the ECS signal upon rapidly switching of the light.',
'vHplus':'Proton flux - inital rate of the decay of the ECS signal',
'gHplus':'Proportional to the aggregat conductivity of the thylakoid membrane - inverse lifetime of the rapid decay of ECS',
'tau':'τ - half life of the rapid decay of the ECS signal',
'light_intensity':'Light intensity in lumen.',
'co2_content':'CO2 concentration in ppm (parts per millions)',
'relative_humidity':'Relative humidity in percent [%]',
'temperature':'Temperature in degrees celcius [°C]'
}

//-----------------------------------------------------------------------------------------------------------------------------------
// Array for json variable replacements
//-----------------------------------------------------------------------------------------------------------------------------------
var replacements = {
'light_intensity':'Light intensity [lumen]',
'co2_content':'CO2 content [ppm]',
'relative_humidity':'Relative humidity [%]',
'temperature':'Temperature [°C]',
'fluorescence':'<i class="icon-fluorescence" title="Fluorescence"></i> Chlorophyll fluorescence',
'810_dirk':'<i class="icon-absorbance" title="Absorbance"></i> 810 nm DIRK',
'chlorophyll_spad':'<i class="icon-reflectance" title="Reflectance"></i> SPAD',
'940_dirk':'<i class="icon-absorbance" title="Absorbance"></i> 940 nm DIRK',
'dirk':'<i class="icon-absorbance" title="Absorbance"></i> 520 nm DIRK',
'tau':'τ',
'Phi2':'ΦII',
'FmP':'Fm\'',
'gHplus':'gH+',
'vHplus':'vH+',
'ecst':'ECSt'
}

//-----------------------------------------------------------------------------------------------------------------------------------
// Array with variables to hide from user in cellphone app
//-----------------------------------------------------------------------------------------------------------------------------------
var variablehidephone = [
	'userinput_1_question',
	'userinput_1_answer',
	'userinput_2_question',
	'userinput_2_answer',
	'device_id',
	'firmware_version',
	'time',
	'protocol_id',
	'protocol_number',
	'protocol_name',
	'baseline_sample',
	'baseline_values',
	'chlorophyll_spad_calibration',
	'red',
	'green',
	'blue',
	'board_temperature',
	'averages',
	'repeats',
	'data_raw',
	'end'
];