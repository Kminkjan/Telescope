package crawler;

/**
 * 
 * @author Roel
 *
 */
public class JSONMaker {
	public static String generateErrorMessage(String code, String message) {
		String output = "{ \"error_code\": \"" + code + "\", \"timestamp\": " + CrawlerCommand.getCurrentTimestamp() +", \"message\": \"" + message + "\" }";
		return output;
	}
	String output = "";
	
	public JSONMaker() {
		output = "";
		openObject();
	}
	
	public void addObject(String key, String value) {
		output += "\"" + key + "\": \"" + value + "\",";	 
	}
	
	public void addObject(String key, int value) {
		output += "\"" + key + "\": " + value + ",";	 
	}
	
	public void addObject(String key, double value) {
		output += "\"" + key + "\": " + value + ",";	 
	}
	
	public void addObject(String key, boolean value) {
		output += "\"" + key + "\": " + value + ",";	 
	}
	
	public void openArray(String name){
		output += 	"\"" + name + "\": [";
	}
	
	public void trimComma() {
		output = output.substring(0, output.length() - 1);
	}
	
	public void closeArray() {
		output += "],";
	}
	
	public void openObject() {
		output += "{ ";
	}
	
	public void closeObject() {
		output += "},";
	}
	
	public String getJSON() {
		closeObject();
		trimComma();
		return output;
	}
}
