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
