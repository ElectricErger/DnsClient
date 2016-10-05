package test;

public class BitwiseIP {
	public static void main(String[] args){

		String[] ipSegments = args[0].split("\\.");
		int ip1 = Integer.parseInt(ipSegments[0].substring(1, ipSegments[0].length())) << (8*3);
		int ip2 = Integer.parseInt(ipSegments[1]) << (8*2);
		int ip3 = Integer.parseInt(ipSegments[2]) << (8*1); //Truncates 0's
		int ip4 = Integer.parseInt(ipSegments[3]) << (8*0);
		
		int ipByte = ip1 + ip2 + ip3 + ip4; //Concatinate not add?
		
		System.out.println(args[0]);
		System.out.println(Integer.toBinaryString(ipByte));
		System.out.println(Integer.toBinaryString(ip1));
		System.out.println(Integer.toBinaryString(ip2));
		System.out.println(Integer.toBinaryString(ip3));
		System.out.println(Integer.toBinaryString(ip4));
	}
}
