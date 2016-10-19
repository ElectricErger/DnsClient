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
		for(int i=0; i<message.length; i++) System.out.println("Byte "+i+ " is: "+(char)message[i]);
		
		short type = Datagram.getQT(message);
		int typePosition = afterAnswerName(message);
		//Probably should have made an int to short function
		short typeAns = (short) ((message[typePosition++]<<8)+ message[typePosition++]);
		short classData = (short) ((message[typePosition++]<<8)+ message[typePosition++]);
		long TTL = (long) ((message[typePosition++]<<24)+(message[typePosition++]<<16)
				+(message[typePosition++]<<8)+(message[typePosition])); //int is only 31 bits positive
		int rLength = (int) ((message[typePosition++]<<8)+ message[typePosition++]);
		byte[] rData = new byte[rLength];
		int rDataPointer = 0;
		for(int i = typePosition; i<(typePosition+rLength); i++){
			rData[rDataPointer++] = message[i];
		}
		
		boolean authoratative = (message[2]&0b00000100) == 0b00000100;
		
		switch(type){
		case Datagram.A:
			ARecordResults(rData, TTL, authoratative);
			break;
		case Datagram.MX:
			MXRecordResults(rData, TTL, authoratative);
			break;
		case Datagram.NS:
			NSRecordResults(rData, TTL, authoratative);
			break;
		case 0x0005: //Cname
			CNAMEResults(rData, TTL, authoratative);
			break;
		}
		
	}
	private static void ARecordResults(byte[] message, long cacheTime, boolean auth) {
		
		//message is the IP addr
		System.out.print(String.format("IP\t%i.%i.%i.%i\t%i\t",
				message[0],message[1],message[2],message[3], cacheTime));
		if(auth)
			System.out.println("auth");
		else
			System.out.println("nonauth");		
	}
	private static void MXRecordResults(byte[] message, long cacheTime, boolean auth) {
		// TODO Auto-generated method stub
		//Pref | 16 | want low
		//Exch | n  | name
		int pref = (int)((message[0]<<8)+(message[1]));
		String alias = "";
		
		int labelLen = message[2];
		for(int i=2; i<message.length; i++){
			if(labelLen!=0){
				alias+=(char)message[i];
				labelLen--;
			} else{
				labelLen = message[i];
			}
		}
		
		System.out.print(String.format("MX\t%s\t%i\t%i\t", alias, pref, cacheTime));
		
		
		if(auth)
			System.out.println("auth");
		else
			System.out.println("nonauth");	
	}
	private static void NSRecordResults(byte[] message, long cacheTime, boolean auth) {
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
				"NS\t%s\t%i\t", alias, cacheTime));
		if(auth)
			System.out.println("auth");
		else
			System.out.println("nonauth");
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
				"CNAME\t%s\t%i\t", alias, cacheTime));
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
				"This client was unable to receive a response from %d.%d.%d.%d after %d attempts\n"
				+ "Consider adjusting the timeout [-t] to some value greater than %d"
						, ip[0], ip[1], ip[2], ip[3]
						, retry, timeout));
	}
}
