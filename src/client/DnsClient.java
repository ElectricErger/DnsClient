package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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
	public static byte[] intTo4ByteArray(int i){
		return new byte[]{
				(byte) (i >> 24),
				(byte) (i >> 16),
				(byte) (i >> 8),
				(byte) (i)
		};
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
	
	//This method is really big. Perhaps making a FSM would be better.
	@SuppressWarnings("rawtypes")
	public static void launchQuery(Map<String, Comparable> params){
		//Get raw data in easy forms
		short queryType = ParameterScanner.getQueryType(params);
		int ipInBits = (int) params.get(ParameterScanner.SERVER);
		int port = (int) params.get(ParameterScanner.PORT);
		
		//Get network objects
		DatagramSocket socket = null;
		InetAddress addr;
		DatagramPacket toServer = null;
		//For the return object
		byte[] response = new byte[100];
		DatagramPacket fromServer = new DatagramPacket(response, response.length);
		//Make datagram
		Datagram d = new Datagram((String) params.get(ParameterScanner.REQUEST), queryType);

		//Prep for transition states
		boolean responseReceived = false;
		long startTime = System.currentTimeMillis();
		long stopTime = System.currentTimeMillis();
		
		//Open port
		try {
			socket = new DatagramSocket();
			byte[] ipAddr = intTo4ByteArray(ipInBits);
			addr = InetAddress.getByAddress(ipAddr);
			toServer = d.compileDatagram(addr, port);
			
			
			//STANDARD OUTPUT
			String rT = "A";
			switch(d.getQueryType()){
			case d.MX:
				rT = "MX";
				break;
			case d.NS:
				rT = "NS";
				break;
			default:
				rT = "A";
			}
			
			System.out.println("DnsClient sending request for "+ params.get(ParameterScanner.REQUEST));
			System.out.println(String.format("Server: %i.%i.%i.%i",ipAddr[0],ipAddr[1],ipAddr[2],ipAddr[3]));
			System.out.println("Request type: " + rT);
			
			
			startTime = System.currentTimeMillis();
			for(int retries = 0; retries<(int) params.get(ParameterScanner.RETRY); retries++){ //Retry
				socket.send(toServer); //DNS header doesn't matter. We want any response we sent
				socket.setSoTimeout((int) params.get(ParameterScanner.TIMEOUT)); //Timeout functionality
				
				
				//Wait till timeout or received
				while(true){
					try{
						socket.receive(fromServer);
						
						//STANDARD OUTPUT
						System.out.println(String.format("Response received after %i seconds (%i retries)"
								,(System.currentTimeMillis()-startTime)/1000, retries));
						
						retries = (int) params.get(ParameterScanner.RETRY);
						responseReceived = true;
						break;
					}catch(SocketTimeoutException e){
						System.out.println("DNS request has timed out."); //Shouldn't produce an STDOUT?
						break;
					}
				}				
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
		
		
		if(responseReceived)
			Response.printResults(toServer, fromServer);
		else
			Response.noResponseReceived(
					intTo4ByteArray(ipInBits),
					(int) params.get(ParameterScanner.RETRY),
					(int) params.get(ParameterScanner.TIMEOUT));
	}

}
