package client;


public class DnsClient {
	
	public static void main(String[] args){
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
	
	
	private static void verifyServer(String server) {
		//Should have an IP address: 3 decimals, 0-255, broadcasts/multicasts
		//We will allow for local network DNS and loopback
		String[] ipSegments = server.split(".");
		int ip1 = Integer.parseInt(ipSegments[0].substring(1, ipSegments[0].length())) << (8*3);
		int ip2 = Integer.parseInt(ipSegments[1]) << (8*2);
		int ip3 = Integer.parseInt(ipSegments[2]) << (8*1);
		int ip4 = Integer.parseInt(ipSegments[3]) << (8*0);
		
		int ipByte = ip1 + ip2 + ip3 + ip4;
		
		
		if(ip1 == 0 ){
			System.out.println("No broadcasts are allowed.");
			System.exit(1);
		}if(ip1 == 10 ){
			System.out.println("Note: You are querying within you local network.");
		}if(ip1 == 100 && ip2 >= 64 && ip2 < 128 ){
			System.out.println("Note: You are talking direccctly to your ISP.");
		}if(ip1 == 127 ){
			System.out.println("Note: You are querying your local DNS.");
		}if(ip1 == 169 && ip2 ==  ){
			System.out.println("Note: You are querying your local DNS.");
		}if(ip1 == 127 ){
			System.out.println("Note: You are querying your local DNS.");
		}if(ip1 == 127 ){
			System.out.println("Note: You are querying your local DNS.");
		}if(ip1 == 127 ){
			System.out.println("Note: You are querying your local DNS.");
		}if(ip1 == 127 ){
			System.out.println("Note: You are querying your local DNS.");
		}if(ip1 == 127 ){
			System.out.println("Note: You are querying your local DNS.");
		}if(ip1 == 0 ){
			System.out.println("IP Addresses can't start with 0 in the first octet.");
			System.exit(1);
		}
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
