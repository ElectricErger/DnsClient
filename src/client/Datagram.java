package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Random;

public class Datagram {
	//QT | 16 | A 0x0001, NS 0x0002, MX 0x000f
	public static final short A  = 0x0001;
	public static final short NS = 0x0002;
	public static final short MX = 0x000f;
	
	byte[] data;
	short queryType;
	
	public Datagram(String query, short queryType){
		data = guessDatagramSize(query);
		this.queryType = queryType;
		
		makeHeader();
		makeQuestion(query);
		//This is all we need for a query
	}
	
	//Setup functions//
	private byte[] guessDatagramSize(String query) {
		//Header = 16*6 bits
		//Question = querySize+2 bytes + 16*2 bits
		//Total = (16*8/8)+(qS.length+2)
		return new byte[18+query.length()];
	}
	//First 12 bytes go here
	private void makeHeader(){
		Random ran = new Random();
		//Name | size (bits) | value 
		//ID | 16 | Random
		//QR | 1 | 0
		//OP | 4 | 0
		//AA | 1 | ?
		//TC | 1 | ?
		//RD | 1 | 1
		//RA | 1 | ?
		//Z  | 3 | 0
		//RC | 4 | 0
		//QD | 16 | 1
		//AN | 16 | 0
		//NS | 16 | ?
		//AR | 16 | ?

		//ID(in 2 parts)
		data[0] = (byte) ran.nextInt((int) Math.pow(2, 8));
		data[1] = (byte) ran.nextInt((int) Math.pow(2, 8));
		//QR(1)OP(4)AA(1)TC(1)RD(1)
		data[2] = 0b00000001;
		//RA(1)Z(3)RCode(4)
		data[3] = 0b00000000;
		//QD(8)(8) -> (0)(1)
		data[4] = 0x00;
		data[5] = 0x01;
		//AN(8)(8) -> (0)(0)
		data[6] = 0x00;
		data[7] = 0x00;
		//NS(8)(8) -> (0)(0)
		data[8] = 0x00;
		data[9] = 0x00;
		//AR(8)(8) -> (0)(0)
		data[10] = 0x00;
		data[11] = 0x00;
		
	}
	//12-n go here
	private void makeQuestion(String query){
		final int START_POSITION = 12;
		int position = START_POSITION; //Always start at byte 12

		//Label to byte array
		String[] label = query.split("\\.");
		for(String l : label){
			data[position] = (byte) l.length();
			position++;
			for(int j=0; j<l.length(); j++){ //Relative position (label)
				data[position] = (byte) l.charAt(j);
				position++;
			}
		}
		data[position] = 0x00; //postpend a 0
		position++;
		
		//QueryType (8)(8)
		short tempQT = queryType;
		data[position] = (byte) ((tempQT >> 8) & 0xff); //Take the left 8 bits
		position++;
		data[position] = (byte) (queryType & 0xff); //Take the right 8 bits
		position++;
		
		//QC | 16 | 0x0001
		data[position] = 0x00;
		position++;
		data[position] = 0x01;
		position++;
	}

	//Accessors	
	public DatagramPacket compileDatagram(InetAddress a, int port){
		return new DatagramPacket(data, data.length, a, port);
	}
	public byte[] getRawData(){ return data; }
	public short getQueryType(){ return queryType; }
	public void setQueryType(short s){ queryType = s; }

	//Length in bytes
	public static int headerLength(){ return 12; }
	public static int questionLength(byte[] dnsMessage){
		//QName ends when you get a 0 at the start of a label
		
		int location = headerLength();
		//add prelabel int, until 0
		while(dnsMessage[location]!=0){
			location=location + dnsMessage[location] + 1;
		}
		location = location + 4; //4 bytes for QT and QC
		
		return location-headerLength(); //All the way to the last bit
	}
	public static short getQT(byte[] dnsMessage){
		int location = headerLength()+questionLength(dnsMessage);
		short qt = (short) ((dnsMessage[location-3]<<4) + dnsMessage[location-2]);
		
		return qt;
	}
}
