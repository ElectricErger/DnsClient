package client;

import java.util.HashMap;
import java.util.Map;

public class ParameterScanner {
	public static final String HELP = "help";
	public static final String TIMEOUT = "timeout";
	public static final String RETRY = "retry";
	public static final String PORT = "port";
	public static final String MX = "MX";
	public static final String NS = "NS";
	public static final String SERVER = "server";
	public static final String REQUEST = "request";

	public static final int DEFAULT_TIMEOUT = 5;
	public static final int DEFAULT_RETRY = 3;
	public static final int DEFAULT_PORT = 53;
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Comparable> parseParams(String[] raw) throws IllegalParameter{
		//Create a hashmap and populate it with defaults
		Map<String, Comparable> parsed = new HashMap<String, Comparable>();
		parsed.put(TIMEOUT, DEFAULT_TIMEOUT);
		parsed.put(RETRY, DEFAULT_RETRY);
		parsed.put(PORT, DEFAULT_PORT);
		parsed.put(MX, false);
		parsed.put(NS, false);
		
		
		for(int i=0; i<raw.length; i++){
			try{
				//Flag or Parameter
				if(raw[i].charAt(0)=='-'){				
					if(raw[i] == "-h"){	parsed.put(HELP, true); }
					else if(raw[i].equals("-t")){ parsed.put(TIMEOUT, Integer.parseInt(raw[++i]));} //Will overwrite the default
					else if(raw[i].equals("-r")){ parsed.put(RETRY, Integer.parseInt(raw[++i])); }
					else if(raw[i].equals("-p")){ parsed.put(PORT, Integer.parseInt(raw[++i]));}
					else if(raw[i].equals("-mx")){ parsed.put(MX, true); }
					else if(raw[i].equals("-ns")){ parsed.put(NS, true); }
					else { throw new IllegalParameter(raw[i]); } //Um...
				} else {
					parsed.put(SERVER, raw[i++]);
					parsed.put(REQUEST, raw[i++]);
				}
			}catch(Exception e){
				System.out.println("Your input is invalid for "+ raw[i]);
				System.exit(1);
			}

		}
		return parsed;
	}
	
	@SuppressWarnings("rawtypes")
	public static void verifyParams(Map<String, Comparable> params) throws IllegalType{
		//Just go through each and verify if they are the right type		
		try{
			isInt(params.get(TIMEOUT));
			isInt(params.get(RETRY));
			isInt(params.get(PORT));
			isBool(params.get(MX));
			isBool(params.get(NS));
			verifyServer((String) params.get(SERVER));
			verifyName((String) params.get(REQUEST));
			
		}catch(IllegalType e){
			throw e; //Just don't get caught by the silly Exception I put in.
		}catch(Exception e){
			System.out.println("Illegal IP address: "+ e.getMessage());
		}
	}
	@SuppressWarnings("rawtypes")
	private static void isBool(Comparable b) throws IllegalType{
		if(b.getClass() != Boolean.class){
			throw new IllegalType(b + " is not a boolean");
		}
		
	}
	@SuppressWarnings("rawtypes")
	private static void isInt(Comparable i) throws IllegalType {
		if(i.getClass() != Integer.class){
			throw new IllegalType(i + " is not an integer");
		}
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
	private static int ipAddressToBytes(String server) throws Exception {
		//This function is just to clean up method calls.
		return DnsClient.ipAddressToBytes(server);
	}
	
	//Public helper functions
	public static short getQueryType(@SuppressWarnings("rawtypes") Map<String, Comparable> params){
		short queryType = Datagram.A;
		if((boolean) params.get(MX))
			queryType = Datagram.MX;
		else if((boolean) params.get(NS))
			queryType = Datagram.NS;
		else queryType = Datagram.A;
		
		return queryType;
	}
	
	
}

@SuppressWarnings("serial")
class IllegalParameter extends Exception{
	IllegalParameter(String s){ super(s); }
	IllegalParameter(){ super(); }
}

class IllegalType extends Exception{
	IllegalType(String s){ super(s); }
	IllegalType(){ super(); }
}
