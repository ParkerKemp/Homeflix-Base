/*Homeflix-Base: BaseHamster
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * 
 */

package com.thundercats.homeflix_base;

import java.io.OutputStream;
import java.io.PrintStream;

public class BaseHamster extends PrintStream {
	
	
	public BaseHamster(OutputStream main){
		super(main);
	}
	
	@Override
	public final void println(String b){
		
	}
}
