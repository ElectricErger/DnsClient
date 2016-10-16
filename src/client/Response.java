package client;

import java.net.DatagramPacket;

public class Response {
	public static void printResults(DatagramPacket fromServer){
		byte[] message = fromServer.getData();
		
		//STANDARD OUTPUT
		System.out.println("***Answer Section (1 record[s])***");
		
	}
	
	public static void noResponseReceived(byte[] ip, int retry, int timeout){
		System.out.println(String.format(
				"This client was unable to receive a response from %i.%i.%i.%i after %i attempts\n"
				+ "Consider adjusting the timeout [-t] to some value greater than %i"
						, ip[0], ip[1], ip[2], ip[3]
						, retry, timeout));
	}
}
