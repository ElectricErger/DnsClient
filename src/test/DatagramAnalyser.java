package test;

import static org.junit.Assert.*;

import org.junit.Test;

import client.Datagram;

public class DatagramAnalyser {

	
	@Test
	public void getQT(){
		Datagram d = new Datagram("www.google.ca", Datagram.A);
		byte[] data = d.getRawData();
	
		int questionEnd = data.length-1;
		short qt = (short) ((data[questionEnd-3]<<4) + data[questionEnd-2]);

		
		assertEquals(d.getQueryType(), Datagram.A);
		assertEquals(qt, Datagram.A);
		assertEquals(Datagram.getQT(data), Datagram.A);
		
	}
	
	@Test
	public void labelCheck() {
		Datagram d = new Datagram("www.google.ca", Datagram.A);
		byte[] data = d.getRawData();
		
		assertEquals(data[Datagram.headerLength()], 3); //www = 3 chars
		assertEquals(data[Datagram.headerLength()+4], 6); //google = 6 chars
		assertEquals(data[Datagram.headerLength()+4+7], 2); //ca = 2 chars
		
	}
	
	@Test
	public void question(){		
		Datagram d = new Datagram("www.google.ca", Datagram.A);
		byte[] data = d.getRawData();
		
		
		int startOfBody = Datagram.headerLength();
		int questionLength = Datagram.questionLength(data);
		
		System.out.println("Length:"+data.length+"\n"
				+ "Header:"+startOfBody+"\n"
				+ "Question:"+questionLength);
		assertEquals(startOfBody+questionLength, data.length-1);
	}

}
