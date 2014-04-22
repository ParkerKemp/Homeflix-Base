/*Homeflix-Base: DeviceListener
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * 
 */

package com.thundercats.homeflix_base;

import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;


public class DeviceListener implements RegistryListener {

	@Override
	public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device){
		System.out.println("Remote device discovery started");
	}
	
	@Override
	public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex){
		System.out.println("Remote device discovery failed");
	}
	
	@Override
	public void remoteDeviceAdded(Registry registry, RemoteDevice device){
		System.out.println("Remote device added");
		if(device == null)
			System.out.println("Device is null");
		else if(device.getDetails() == null)
			System.out.println("Details are null");
		else if(device.getDetails().getBaseURL() == null)
			System.out.println("Base URL is null");
		else
			System.out.println(device.getDetails().getBaseURL().toString());
	}
	
	@Override
	public void remoteDeviceUpdated(Registry registry, RemoteDevice device){
		System.out.println("Remote device updated");
		//System.out.println(device.getDetails().getBaseURL().toString());
	}
	
	@Override
	public void remoteDeviceRemoved(Registry registry, RemoteDevice device){
		System.out.println("Remote device removed");
	}
	
	@Override
	public void localDeviceAdded(Registry registry, LocalDevice device){
		System.out.println("Local device added");
		if(device == null)
			System.out.println("Device is null");
		else if(device.getDetails() == null)
			System.out.println("Details are null");
		else if(device.getDetails().getBaseURL() == null)
			System.out.println("Base URL is null");
		else
			System.out.println(device.getDetails().getBaseURL().toString());
	}
	
	@Override
	public void localDeviceRemoved(Registry registry, LocalDevice device){
		System.out.println("Local device removed");
	}
	
	@Override
	public void beforeShutdown(Registry registry){
		
	}
	
	@Override
	public void afterShutdown(){
		
	}
}
