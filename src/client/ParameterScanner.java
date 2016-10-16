package client;

import java.util.HashMap;
import java.util.Map;

public class ParameterScanner {
	
	String[][] params = {
			{"-h", ""},
			{"-t","5"},
			{"-r","3"},
			{"-p","53"},
			{"queryType","T"}, //A, mx, ns
			{"@server",""},
			{"name",""}
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Comparable> parseParams(String[] raw){
		Map parsed = new HashMap<String, Comparable>();
		
		for(int i=0; i<raw.length; i++){
			
			//Flag or Parameter
			if(raw[i].charAt(0)=='-'){
				if(raw[i] == "-h"){	parsed.put("help", true); }
				else if(raw[i] == "-t"){ parsed.put("timeout", raw[++i]); }
				else if(raw[i] == "-r"){ parsed.put("retry", raw[++i]); }
				else if(raw[i] == "-p"){ parsed.put("port" , raw[++i]);}
				else if(raw[i] == "-mx"){ parsed.put("MX", true); }
				else if(raw[i] == "-nx"){ parsed.put("NX", true); }
				else { throw new Exception("Parameter not recognized"); }
			} else {
				parsed.put("server", raw[i++]);
				parsed.put("request", raw[i++]);
			}
		}
		
		if(raw.length>2)
			printError(raw.length);
		if(raw.length==0 || params[0][1].equals("T"))
			printHelp();
		
		return parsed;
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
}
