package client;

import java.net.DatagramPacket;

public class Response {
	public static void printResults(DatagramPacket toServer, DatagramPacket fromServer){
		byte[] message = toServer.getData();
		byte[] response = fromServer.getData();
		
		if(!((message[0] == response[0])&&(message[1] == response[1]))){
			System.out.println("Error\tResponse didn't have the same IDs");
			System.exit(1);
		}else{
			//STANDARD OUTPUT
			System.out.println("***Answer Section (1 record[s])***");
			analyseResponses(response);
		}
		
	}
	private static void analyseResponses(byte[] message) {
		// TODO Read through data and find the response data
		short type = getRecordType(message);
		
		switch(type){
		case Datagram.A:
			ARecordResults(message);
			break;
		case Datagram.MX:
			MXRecordResults(message);
			break;
		case Datagram.NS:
			NSRecordResults(message);
			break;
		}
		
	}
	private static short getRecordType(byte[] message) {
		int location = Datagram.headerLength()-1; //assumes base 1 array
		location=location + Datagram.questionLength(message);
		
		location = location - 4; //-4 to bring us right before QT
		
		return (short) (message[location+1]<<4 +  message[location+2]);
	}	
	private static void ARecordResults(byte[] message) {
		// TODO Auto-generated method stub
		
	}
	private static void MXRecordResults(byte[] message) {
		// TODO Auto-generated method stub
	}
	private static void NSRecordResults(byte[] message) {
		// TODO Auto-generated method stub
	}
	//STANDARD OUTPUT
	private static void printNoResults(){
		System.out.println("NOTFOUND");
	}
	
	public static void noResponseReceived(byte[] ip, int retry, int timeout){
		System.out.println(String.format(
				"This client was unable to receive a response from %i.%i.%i.%i after %i attempts\n"
				+ "Consider adjusting the timeout [-t] to some value greater than %i"
						, ip[0], ip[1], ip[2], ip[3]
						, retry, timeout));
	}
}
