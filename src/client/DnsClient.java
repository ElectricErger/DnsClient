package client;


public class DnsClient {
	
	public static void main(String[] args) throws Exception{
		String[][] params = {
				{"-h", ""},
				{"-t","5"},
				{"-r","3"},
				{"-p","53"},
				{"-mx/-ns","T"}, //It's either mx (T) or ns (F)
				{"@server",""},
				{"name",""}
		};
		parseParams(args, params);
		verifyServer(params[5][1]);
		verifyName(params[6][1]);
		//Now params are in array, launch query
		launchQuery(params);
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
		//Should have 2+ domains: 63 octals max each, 
	}

	private static void parseParams(String[] raw, String[][] params){
		//Identify flags
		for(int i=0; i<raw.length; i++){
			//Flag or Parameter
			if(raw[i].charAt(0)=='-'){
				switch(raw[i].charAt(1)){
					case 'h':
						params[0][1] = "T";
						break;
					case 't':
						params[1][1] = raw[++i];
						break;
					case 'r':
						params[2][1] = raw[++i];
						break;
					case 'p':
						params[3][1] = raw[++i];
						break;
					case 'm':
						params[4][1] = "T";
						break;
					case 'n':
						params[4][1] = "F";
						break;
				}
			} else {
				params[5][1] = raw[i++];
				params[6][1] = raw[i++];
			}
		}
		
		
		//Print help
		if(raw.length==0 || params[0][1].equals("T")){
			
			System.exit(0);
		}
		//Print error
		if(raw.length < 2){
			System.out.println("Insufficient parameters. "
					+ "Expected: 2 parameters, @server and name. Received: "+params.length
					+ "\n Use -h or --help for more information");
			System.exit(1);
		}
	}
	
	/**
	 * This function receives a valid server and DNS request.
	 * -It will open a port,
	 * -Processes the params into the correct type of payload
	 * -Send the request out
	 * -Receive a response
	 * -Output the final results
	 * @param params
	 */
	public static void launchQuery(String[][] params){
		
	}
}
