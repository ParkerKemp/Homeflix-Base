package com.thundercats.homeflix_base;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.registry.RegistrationException;
import org.teleal.cling.registry.RegistryListener;

public class HomeflixBase {
	public static JTextArea textArea;
	public static void main(String[] args){
		
		/*
		RegistryListener listener = new DeviceListener();
		
		UpnpService upnpService = new UpnpServiceImpl(listener);
		
		try {
			upnpService.getRegistry().addDevice(createDevice());
		} catch (RegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		JFrame frame = new JFrame();
		textArea = new JTextArea();
		
		textArea.setEditable(false);
		
		frame.add(textArea);
		
		frame.setSize(400,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		new Thread(null, new ServerThread(6000), "Server-Thread").start();
	}
	
	public static void echo(String msg){
		System.out.println(msg);
		textArea.append(msg + "\n");
	}
	/*
	public static LocalDevice createDevice() throws ValidationException{
		DeviceIdentity identity =
	            new DeviceIdentity(
	                    UDN.uniqueSystemIdentifier("Demo Binary Light")
	            );

	    DeviceType type =
	            new UDADeviceType("BinaryLight", 1);

	    DeviceDetails details = null;
		try {
			details = new DeviceDetails(
					new URL("http", getLocalAddress().getHostAddress(), 6000, ""),
			        "Friendly Binary Light",
			        new ManufacturerDetails("ACME"),
			        new ModelDetails(
			                "BinLight2000",
			                "A demo light with on/off switch.",
			                "v1"
			        ),
			        "12345",
			        "1234",
			        null
			);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(details.getBaseURL().toString());

	    //Icon icon =
	    //        new Icon(
	    //                "image/png", 48, 48, 8,
	    //                getClass().getResource("icon.png")
	    //        );

	    LocalService<SwitchPower> switchPowerService =
	            new AnnotationLocalServiceBinder().read(SwitchPower.class);

	    switchPowerService.setManager(
	            new DefaultServiceManager(switchPowerService, SwitchPower.class)
	    );

	    return new LocalDevice(identity, type, details, switchPowerService);

	}
	
	public static InetAddress getLocalAddress(){
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()){
			    NetworkInterface current = interfaces.nextElement();
			    //System.out.println(current);
			    if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
			    Enumeration<InetAddress> addresses = current.getInetAddresses();
			    while (addresses.hasMoreElements()){
			        InetAddress current_addr = addresses.nextElement();
			        if(current_addr.isLoopbackAddress())
			        	continue;
			        if(current_addr instanceof Inet4Address)
			        	return current_addr;
			    }
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}*/
	
}
