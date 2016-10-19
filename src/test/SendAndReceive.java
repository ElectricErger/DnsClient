package test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import client.Datagram;
import client.DnsClient;
import client.ParameterScanner;

public class SendAndReceive {

	@Test
	public void sendMechanism() {
		Map<String, Comparable> params = new HashMap<String, Comparable>();
		params.put(ParameterScanner.PORT, ParameterScanner.DEFAULT_PORT);
		params.put(ParameterScanner.RETRY, ParameterScanner.DEFAULT_RETRY);
		params.put(ParameterScanner.TIMEOUT, 100);
		params.put(ParameterScanner.SERVER, "@8.8.8.8");
		params.put(ParameterScanner.REQUEST, "www.mcgill.ca");
		
		DnsClient.launchQuery(params);
	}
	@Test
	public void seeOutput() {
		fail("Not yet implemented");
	}
	@Test
	public void shortTimeout() {
		Map<String, Comparable> params = new HashMap<String, Comparable>();
		params.put(ParameterScanner.PORT, ParameterScanner.DEFAULT_PORT);
		params.put(ParameterScanner.RETRY, ParameterScanner.DEFAULT_RETRY);
		params.put(ParameterScanner.TIMEOUT, 1);
		params.put(ParameterScanner.SERVER, "@8.8.8.8");
		params.put(ParameterScanner.REQUEST, "www.mcgill.ca");
		
		DnsClient.launchQuery(params);
	}
	@Test
	public void MXTest() {
		Map<String, Comparable> params = new HashMap<String, Comparable>();
		params.put(ParameterScanner.PORT, ParameterScanner.DEFAULT_PORT);
		params.put(ParameterScanner.RETRY, ParameterScanner.DEFAULT_RETRY);
		params.put(ParameterScanner.TIMEOUT, ParameterScanner.DEFAULT_TIMEOUT);
		params.put(ParameterScanner.MX, true);
		params.put(ParameterScanner.SERVER, "@8.8.8.8");
		params.put(ParameterScanner.REQUEST, "www.mcgill.ca");
		
		DnsClient.launchQuery(params);
	}
	@Test
	public void NSTest() {
		Map<String, Comparable> params = new HashMap<String, Comparable>();
		params.put(ParameterScanner.PORT, ParameterScanner.DEFAULT_PORT);
		params.put(ParameterScanner.RETRY, ParameterScanner.DEFAULT_RETRY);
		params.put(ParameterScanner.TIMEOUT, 100);
		params.put(ParameterScanner.NS, true);
		params.put(ParameterScanner.SERVER, "@8.8.8.8");
		params.put(ParameterScanner.REQUEST, "www.mcgill.ca");
		
		DnsClient.launchQuery(params);
	}
	@Test
	public void ATest() {
		Map<String, Comparable> params = new HashMap<String, Comparable>();
		params.put(ParameterScanner.PORT, ParameterScanner.DEFAULT_PORT);
		params.put(ParameterScanner.RETRY, ParameterScanner.DEFAULT_RETRY);
		params.put(ParameterScanner.TIMEOUT, 100);
		params.put(ParameterScanner.SERVER, "@8.8.8.8");
		params.put(ParameterScanner.REQUEST, "www.mcgill.ca");
		
		DnsClient.launchQuery(params);

	}

}
