package test;

import static org.junit.Assert.*;

import org.junit.Test;

import client.Datagram;

public class DatagramAnalyser {

	
	@Test
	public void getQT(){
		Datagram d = new Datagram("www.google.ca", Datagram.A);
		assertEquals(d.getQueryType(), Datagram.A);
	}
	
	@Test
	public void header() {
		Datagram d = new Datagram("www.google.ca", Datagram.A);
		byte[] data = d.getRawData();
		
		int questionEnd = d.headerLength()-1 + d.questionLength(data);
		short qt = (short) (data[questionEnd-4]<<4 + data[questionEnd-3]);
		
		assertEquals(qt, Datagram.A);
		assertEquals(data[d.headerLength()], 3);
	}
	
	@Test
	public void question(){
		
		fail("Not yet implemented");
	}

}
