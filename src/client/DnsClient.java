package client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;


public class DnsClient {
	
	public static void main(String[] args){
		if(args.length<2){
			printIllegalParams(args.length);
			System.exit(1);
		}
		
		//Parse and verify params
		@SuppressWarnings("rawtypes")
		Map<String, Comparable> parameters = null;
		try{
			parameters = ParameterScanner.parseParams(args); //Handles help and populates the params array
			if(parameters.get(ParameterScanner.HELP) != null){
				printHelp();
				System.exit(0);
			}
			ParameterScanner.verifyParams(parameters);
		}catch(IllegalParameter e){ //One of parameters are not defined
			printBadParam(e);
			System.exit(1);
		}catch(IllegalType e1){//The parameters are illegal types
			printBadType(e1);
			System.exit(1);
		}
		
		//Locked and loaded, ready to send
		launchQuery(parameters);
	}

	
	private static void printIllegalParams(int len){
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
	private static void printBadParam(IllegalParameter e){
		System.out.println(
				String.format("One of your parameters %s, was not identified. \n"
						+ "Review your request again. If the system fails again."
						+ "Talk to the creators", e.getMessage()));
	}
	private static void printBadType(IllegalType e){
		//TODO Illegal type error
		System.out.println("");
	}
	
	
	public static int ipAddressToBytes(String ipAddr) throws Exception {
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
		short queryType = ParameterScanner.getQueryType(params);
		//Make datagram
		Datagram d = new Datagram((String) params.get("request"), queryType);
		int ipInBits = (int) params.get(ParameterScanner.SERVER);
		int port = (int) params.get(ParameterScanner.PORT);
		InetAddress addr;
		
		//Open port
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			addr = InetAddress.getByAddress(intToByteArray(ipInBits));
			
			for(int retries = (int) params.get(ParameterScanner.RETRY); retries>0; retries--){
				socket.send(d.compileDatagram(addr, port)); //DNS header doesn't matter. We want any response we sent
				socket.setSoTimeout((int) params.get(ParameterScanner.TIMEOUT)); //Timeout functionality
				//TODO while !TimeoutError scan for incomming package
			}
		} catch (SocketException e) {
			System.out.println("No port was available to send out traffic. Try running the program as root.");
			System.exit(1);
		}catch (UnknownHostException e) {
			System.out.println("The IP address is somehow still illegal....");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("We cannot connect to the internet at this time");
			e.printStackTrace();
		}
		
		
		//Wait -t seconds
		//retry -r times if failed
		
		
		printResults();
	}
	private static byte[] intToByteArray(int i){
		return new byte[]{
				(byte) (i >> 24),
				(byte) (i >> 16),
				(byte) (i >> 8),
				(byte) (i)
		};
	}
	
	private static void printResults(){
		
	}
}
