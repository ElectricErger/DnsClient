package client;

import java.net.DatagramPacket;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class DnsClient {
	
	public static void main(String[] args) throws Exception{
		@SuppressWarnings("rawtypes")
		Map<String, Comparable> parameters;
		try{
			parameters = ParameterScanner.parseParams(args); //Handles help and populates the params array
			parameters = ParameterScanner.verifyParams(parameters);
		}catch(Exception e){ //Your # of parameters are wrong
			printError(e);
			System.exit(1);
		}catch(NumberFormatException e1){//The parameters are illegal
			
		}
		
		launchQuery(parameters);
	}

	
	private static void printError(Exception len){
		System.out.println("Insufficient parameters. "
			+ "Expected: 2 parameters, @server and name. Received: "+len
			+ "\n Use -h or --help for more information");
	}
	
	private static void printHelp(){
		System.out.println("Insufficient parameters. "
			+ "Expected: 2 parameters, @server and name. Received: "
			+ "\n Use -h or --help for more information");
		System.exit(1);
	}
	
	@SuppressWarnings("rawtypes")
	private static Map<String, Comparable> verifyParams(String[][] params) throws Exception{
		//Perhaps a better data type fore easier processing...
		Map<String, Comparable> output = new HashMap<String, Comparable>();
		output.put("-t", getInt(params[1][1]));
		output.put("-r", getInt(params[2][1]));
		output.put("-p", getInt(params[3][1]));
		output.put("queryType", getBoolean(params[4][1]));
		verifyServer(params[5][1]);
		verifyName(params[6][1]);
		output.put("@server", params[5][1]);
		output.put("name", params[6][1]);
		
		return output;
	}
	
	private static int getInt(String str){
		try{
			return Integer.parseInt(str);
		}
		catch(NumberFormatException e){
			if(str.equals(""))
				return -1;
			System.out.println("Error: Your input for a parameter is not an integer. Ensure you put a space between your previous flag, and your nextr one.");
			System.exit(1); 
		}
		return -1; //It's actually impossible to reach here, but the compiler complained
		
	}

	private static boolean getBoolean(String str){
		try{
			return Boolean.parseBoolean(str);
		}
		catch(NumberFormatException e){
			if(str.equals(""))
				return true;
		}
		return false;
	}
	
	private static void verifyServer(String server) throws Exception {
		
		server = server.substring(1, server.length());
		int ipBytes = ipAddressToBytes(server);
		
		//Not allowed: Reserved IP's, Broadcasts, Multicasts
		//Everything else is allowed, but will give a note.
		//Based off of: https://en.wikipedia.org/wiki/Reserved_IP_addresses
		if(ipBytes >= ipAddressToBytes("0.0.0.0") && ipBytes <= ipAddressToBytes("0.255.255.255") ){
			System.out.println("Error:No broadcasts are allowed.");
			System.exit(1);
		}if(ipBytes >= ipAddressToBytes("10.0.0.0") && ipBytes < ipAddressToBytes("10.255.255.255")){
			System.out.println("Note: You are querying within you local network.");
		}if(ipBytes >= ipAddressToBytes("100.64.0.0") && ipBytes < ipAddressToBytes("100.127.255.255")){
			System.out.println("Note: You are talking direccctly to your ISP.");
		}if(ipBytes >= ipAddressToBytes("127.0.0.0") && ipBytes < ipAddressToBytes("127.255.255.255")){
			System.out.println("Note: You are querying your local DNS.");
		}if(ipBytes >= ipAddressToBytes("169.254.0.0") && ipBytes < ipAddressToBytes("169.254.255.255")){
			System.out.println("Note: This IP is used for link-local addresses. Something may be wrong with your DHCP server.");
		}if(ipBytes >= ipAddressToBytes("172.16.0.0") && ipBytes < ipAddressToBytes("172.31.255.255")){
			System.out.println("Note: You are querying within your local network.");
		}if(ipBytes >= ipAddressToBytes("192.0.0.0") && ipBytes < ipAddressToBytes("192.0.0.255")){
			System.out.println("Note: You are querying within your local network (IANA special addresses).");
		}if(ipBytes >= ipAddressToBytes("192.0.2.0") && ipBytes < ipAddressToBytes("192.0.2.255")){
			System.out.println("Note: You are querying within TEST-NET, this IP should not be used publicly.");
		}if(ipBytes >= ipAddressToBytes("192.88.99.0") && ipBytes < ipAddressToBytes("192.88.99.255")){
			System.out.println("Note: This is an anycast relay between IPv6 and IPv4.");
		}if(ipBytes >= ipAddressToBytes("192.168.0.0") && ipBytes < ipAddressToBytes("192.168.255.255")){
			System.out.println("Note: You are querying within your local network.");
		}if(ipBytes >= ipAddressToBytes("198.18.0.0") && ipBytes < ipAddressToBytes("1928.19.255.255")){
			System.out.println("Note: This IP address is used for internetwork communication, you are querying another subnet.");
		}if(ipBytes >= ipAddressToBytes("198.51.100.0") && ipBytes < ipAddressToBytes("198.51.100.255")){
			System.out.println("Note: You are querying within TEST-NET2, this IP should not be used publically.");
		}if(ipBytes >= ipAddressToBytes("203.0.113.0") && ipBytes < ipAddressToBytes("203.0.113.255")){
			System.out.println("Note: You are querying within TEST-NET3, this IP should not be used publically.");
		}if(ipBytes >= ipAddressToBytes("224.0.0.0") && ipBytes < ipAddressToBytes("239.255.255.255")){
			System.out.println("Error: No multicasts are allowed.");
			System.exit(1);
		}if(ipBytes >= ipAddressToBytes("240.0.0.0") && ipBytes < ipAddressToBytes("255.255.255.254")){
			System.out.println("Error: These IP addresses are reserved, and not allowed for usage.");
		}if(ipBytes == ipAddressToBytes("255.255.255.255")){
			System.out.println("Error: Broadcasts are not allowed.");
		}
		
	}

	private static int ipAddressToBytes(String ipAddr) throws Exception {
		String[] ipSegments = ipAddr.split("\\.");
		if(ipSegments.length < 4){
			throw new Exception("IP address is not valid. Insufficient digits");
		}
		int ip1 = Integer.parseInt(ipSegments[0]);
		int ip2 = Integer.parseInt(ipSegments[1]);
		int ip3 = Integer.parseInt(ipSegments[2]);
		int ip4 = Integer.parseInt(ipSegments[3]);
		
		return ip1<<(8*3) + ip2<<(8*2) + ip3<<(8*1) + ip4<<(8*0);
	}

	private static void verifyName(String name) {
		//Should have 2+ labels: 63 bytes max each,
		String[] domains = name.split("\\.");
		for(String domain:domains){
			if(domain.length()>63){ //char=1 byte, so 63 chars is the max.
				System.out.println("Your domain is illegal according to RFC 1034.");
				System.exit(1);
			}
		}
		//If all labels are less than 63 bytes we are good to go.
	}

	
	
	// byte a = 'a';
	//byte b = 0x7f;
	//byte c = 127;
	/**
	 * This function receives a valid server and DNS request.
	 * -It will open a port,
	 * -Processes the params into the correct type of payload
	 * -Send the request out
	 * -Receive a response
	 * -Output the final results
	 * @param params
	 */
	@SuppressWarnings("rawtypes")
	public static void launchQuery(Map<String, Comparable> params){
		byte[] datagram = createDatagram(params);
		//Open port
		//Send datagram
		//Wait -t seconds
		//retry -r times if failed
		printResults();
	}
	
	private static byte[] createDatagram(Map<String, Comparable> params){
		makeHeader();
		makeQuestion((String) params.get("name"), (String) params.get("queryType"));
		makeAnswer();
		makeAuthority();
		makeAdditional();
		
		DatagramPacket packet; //Return this datagram
		return new byte[0];
	}
	private static void makeHeader(){
		//I will use short for 16 bit values.
		//Name | size (bits) | value 
		//ID | 16 | Random
		Random ran = new Random();
		short id = (short) ran.nextInt(65536);
		
		//QR | 1 | 0
		//OP | 4 | 0
		//AA | 1 | ?
		//TC | 1 | ?
		//RD | 1 | 1
		//RA | 1 | ?
		//Z  | 3 | 0
		//RC | 4 | 0
		
		//QR(1)OP(4)AA(1)TC(1)RD(1)RA(1)Z(3)RCode(4)
		short flags = 0b00001000;
		
		//QD | 16 | 1
		short QDCount = 0x0001;
		
		//AN | 16 | 0
		short ANCount = 0x0000;
		
		//NS | 16 | ?
		short NSCount = 0x0000;
		
		//AR | 16 | ?
		short ARCount = 0x0000;
		
	}
	private static void makeQuestion(String query, String queryType){
		//QName is dynamic. It is organized by:
		//Length, char1, char2, ..., Length, char1, char2, ....
		//It is terminated with a '0', and divided by '.'
		byte[] QName = new byte[query.length()+2];
		String[] label = query.split("\\.");
		int i = 0; //Overall position (labelData)
		for(String l : label){
			QName[i] = (byte) l.length();
			i++;
			for(int j=0; j<l.length(); j++){ //Relative position (label)
				QName[i] = (byte) l.charAt(j);
				i++;
			}
		}
		//Postpend a 0
		QName[i] = 0x0000;
		
		//QT | 16 | A 0x0001, NS 0x0002, MX 0x000f
		short QType = 0;
		if(queryType == "A")
			QType = 0x0001;
		else if(queryType.equals("NS"))
			QType = 0x0002;
		else
			QType = 0x000f;
		
		//QC | 16 | 0x0001
		short QClass = 0x0001;
	}
	private static void makeAnswer(){} //Unused for query
	private static void makeAuthority(){} //Unused for query
	private static void makeAdditional(){} //Unused for query
	
	
	private static void printResults(){
		
	}
}
