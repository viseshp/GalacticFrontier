package com.callumcarmicheal.old.solar.exceptions;

import com.callumcarmicheal.old.solar.objects.IPlanet;

public class PlanetException extends Exception {

	public IPlanet planet;
	public ExCause cause;
	public String reasonString;
	
	
	public PlanetException(IPlanet planet, ExCause Cause, String Reason) {
		this.planet = planet;
		this.cause = Cause;
		this.reasonString = Reason;
	}
	
	public PlanetException(ExCause Cause, String Reason) {
		this(null, Cause, Reason);
	}
	
	public void printERR() {
		System.err.println("PLANETEXCEPTION : Cause (" + cause + ") Reason (" + reasonString + ")");
	}
}
