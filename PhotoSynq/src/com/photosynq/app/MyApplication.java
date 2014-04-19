package com.photosynq.app;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.photosynq.app.model.ResearchProject;


public class MyApplication extends Application{
	String token;
	ResearchProject researchProject;
	BluetoothAdapter btAdapter;
	BluetoothDevice btDevice;
	//BTDeviceItem btDeviceItem;
	boolean isQuickMeasurement;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public ResearchProject getResearchProject() {
		return researchProject;
	}
	public void setResearchProject(ResearchProject researchProject) {
		this.researchProject = researchProject;
	}
	public BluetoothAdapter getBtAdapter() {
		return btAdapter;
	}
	public void setBtAdapter(BluetoothAdapter btAdapter) {
		this.btAdapter = btAdapter;
	}
	public boolean isQuickMeasurement() {
		return isQuickMeasurement;
	}
	public void setQuickMeasurement(boolean isQuickMeasurement) {
		this.isQuickMeasurement = isQuickMeasurement;
	}
	public BluetoothDevice getBtDevice() {
		return btDevice;
	}
	public void setBtDevice(BluetoothDevice btDevice) {
		this.btDevice = btDevice;
	}
}
