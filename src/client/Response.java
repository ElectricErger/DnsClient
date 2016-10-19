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
		short type = Datagram.getQT(message);
		int typePosition = afterAnswerName(message);
		//Probably should have made an int to short function
		short typeAns = (short) ((message[typePosition++]<<8)+ message[typePosition++]);
		short classData = (short) ((message[typePosition++]<<8)+ message[typePosition++]);
		typePosition+=4; //Skip TTL
		int rLength = (int) ((message[typePosition++]<<8)+ message[typePosition++]);
		byte[] rData = new byte[rLength];
		
		int rDataPointer = 0;
		for(int i = typePosition; i<(typePosition+rLength); i++){
			rData[rDataPointer++] = message[i];
		}
		
		switch(type){
		case Datagram.A:
			ARecordResults(rData);
			break;
		case Datagram.MX:
			MXRecordResults(rData);
			break;
		case Datagram.NS:
			NSRecordResults(rData);
			break;
		}
		
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
	
	//Processing received
	private static int getHeaderEnd(){
		return Datagram.headerLength();
	}
	private static int getQuestionEnd(byte[] dnsMessage){
		return Datagram.questionLength(dnsMessage);
	}
	private static int getAnswerEnd(byte[] dnsMessage){
		/* Name | n
		 * Type | 16b
		 * Class| 16b
		 * TTL  | 32b
		 * RDLen| 16b
		 * RDAns| m
		 */
		
		int startingPoint = getQuestionEnd(dnsMessage);
		
		
		return Datagram.questionLength(dnsMessage);
	}
	private static int afterAnswerName(byte[] dnsMessage){
		int startOfAnswer = getHeaderEnd() + getQuestionEnd(dnsMessage) +1;
		
		if(dnsMessage[startOfAnswer] >= 0xc0){ //If we start with 0b11 then it's a pointer
			return startOfAnswer+2;
		}else{
			return startOfAnswer+Datagram.questionLength(dnsMessage);
		}
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
