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
		//for(int i=0; i<message.length; i++) System.out.println("Byte "+i+ " is: "+(char)message[i]);
		
		int typePosition = afterAnswerName(message);
		//Probably should have made an int to short function
		short typeAns = (short) ((message[typePosition++]<<8)+ message[typePosition++]);
		short classData = (short) ((message[typePosition++]<<8)+ message[typePosition++]);
		long TTL = (long) ((message[typePosition++]<<24)+(message[typePosition++]<<16)
				+(message[typePosition++]<<8)+(message[typePosition++])); //int is only 31 bits positive
		
		int rLength = (int) ((message[typePosition++]<<8)+ message[typePosition++]);
		byte[] rData = new byte[rLength];
		
		int rDataPointer = 0;
		for(int i = typePosition; i<(typePosition+rLength); i++){
			rData[rDataPointer++] = message[i];
		}
		
		boolean authoratative = (message[2]&0b00000100) == 0b00000100;

		
		switch(typeAns){
		case Datagram.A:
			ARecordResults(rData, TTL, authoratative);
			break;
		case Datagram.MX:
			MXRecordResults(rData, TTL, authoratative, message);
			break;
		case Datagram.NS:
			NSRecordResults(rData, TTL, authoratative, message);
			break;
		case 0x0005: //Cname
			CNAMEResults(rData, TTL, authoratative);
			break;
		default:
			System.out.println("ERROR\tCould not process record type: "+typeAns);
			break;	
		}
		
	}
	private static void ARecordResults(byte[] message, long cacheTime, boolean auth) {
		
		//message is the IP addr
		System.out.print(String.format("IP\t%d.%d.%d.%d\t%d\t",
				message[0]&0xFF,message[1]&0xFF,message[2]&0xFF,message[3]&0xFF, cacheTime));
		if(auth)
			System.out.println("auth");
		else
			System.out.println("nonauth");		
	}
	private static void MXRecordResults(byte[] message, long cacheTime, boolean auth, byte[]rawResponse) {
		//for(byte i: message) System.out.println(Integer.toBinaryString(i&0xFF));
		
		//Pref | 16 | want low
		//Exch | n  | name
		int pref = (int)((message[0]<<8)+(message[1]));
		
		String qname = qname(2, message, rawResponse);
		/*
		String alias = "";
		int labelLen = message[2];
		for(int i=3; i<message.length; i++){
			if(labelLen!=0){
				alias+=(char)message[i];
				labelLen--;
			} else{
				labelLen = message[i];
			}
		}*/
		
		System.out.print(String.format("MX\t%s\t%d\t%d\t", qname, pref, cacheTime));
		
		
		if(auth)
			System.out.println("auth");
		else
			System.out.println("nonauth");	
	}
	private static void NSRecordResults(byte[] message, long cacheTime, boolean auth, byte[]rawResponse) {
		String alias = qname(0, message, rawResponse);
		
		/*
		int labelLen = message[0];
		for(int i=1; i<message.length; i++){
			if(labelLen!=0){
				System.out.println(Integer.toBinaryString(message[i]&0xFF));
				alias+=(char)message[i];
				labelLen--;
			} else{
				labelLen = message[i];
			}
		}
		*/
		
		System.out.print(String.format(
				"NS\t%s\t%d\t", alias, cacheTime));
		if(auth)
			System.out.println("auth");
		else
			System.out.println("nonauth");
	}
	private static String qname(int start, byte[] message, byte[] rawResponse) {
		int absPosition = start;
		
		String fqdn = "";
		
		//Processes all labels till done
		while(message[absPosition] != 0){
			//Processes a single label
			int labelLength = message[absPosition];
			if((labelLength&0xc0) == 0xc0){
				short pointerTo = (short) ((labelLength<<8)&0x3FFF);
				pointerTo= (short) (pointerTo + message[++absPosition]&0xFF);
				
				fqdn+=qNameByPointer(pointerTo, rawResponse);
				absPosition++;
				break; //Not sure if good idea
			}else{
				fqdn+=qNameByLabels(absPosition, message);
				absPosition+=labelLength+1;
			}
			fqdn+=".";
		}
		return fqdn.substring(0, fqdn.length()-1);
	}
	private static String qNameByLabels(int position, byte[] message){
		int labelLen = message[position++]+position;
		String response = "";
		for(int i = position; i<labelLen; i++){
			//System.out.println(String.format("i:%d\tlabelLen:%d\t", i, labelLen));
			response+=(char) message[i];
			//System.out.println(response);
		}
		return response;
	}
	private static String qNameByPointer(short pointer, byte[] message){
		String response = "";
		
		int labelLen = message[pointer]+pointer;
		pointer++;
		while(labelLen!=0){
			for(int i=pointer; i<labelLen+1; i++){
				response+=(char) message[i];
				pointer++;
			}
			labelLen = message[pointer++];
			response+=".";
		}
		
		return response;
	}
	
	private static void CNAMEResults(byte[] message, long cacheTime, boolean auth){
		String alias = "";
		
		int labelLen = message[0];
		for(int i=0; i<message.length; i++){
			if(labelLen!=0){
				alias+=(char)message[i];
				labelLen--;
			} else{
				labelLen = message[i];
			}
		}
		
		
		System.out.print(String.format(
				"CNAME\t%s\t%d\t", alias, cacheTime));
		if(auth)
			System.out.println("auth");
		else
			System.out.println("nonauth");
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
		
		if(dnsMessage[startOfAnswer] >= (byte)0xc0){ //If we start with 0b11 then it's a pointer
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
				"This client was unable to receive a response from %d.%d.%d.%d after %d attempts\n"
				+ "Consider adjusting the timeout [-t] to some value greater than %d"
						, ip[0], ip[1], ip[2], ip[3]
						, retry, timeout));
	}
}
