package elasticsearchlibrary;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AppbaseBasicTest {

	Appbase appbase;
	static final String user = "vspynv5Dg",
			pass = "f54091f5-ff77-4c71-a14c-1c29ab93fd15",
			URL = "http://scalr.api.appbase.io", appName = "Trial1796";
	String type = "product", id = "1";

	@Before
	public void setup() {
		appbase = new Appbase(URL,appName,user,pass);
	}

	@Test
	public void getURLTest() {
		String result=appbase.getURL(type);
		assertNotNull(result);
		assertEquals("http://scalr.api.appbase.io/Trial1796/product/", result);
		
		result=appbase.getURL(type, id);
		assertNotNull(result);
		assertEquals("http://scalr.api.appbase.io/Trial1796/product/1", result);
	}
	
	@Test
	public void getSearchURLTest(){
		String result=appbase.getSearchUrl("abc");
		assertNotNull(result);
		assertEquals("http://scalr.api.appbase.io/Trial1796/_search?q=abc", result);
		
		result=appbase.getSearchUrl("product", "abc");
		assertNotNull(result);
		assertEquals("http://scalr.api.appbase.io/Trial1796/product/_search?q=abc", result);
	}
	
	@Test
	public void getAuthTest(){
		String result=appbase.getAuth();
		assertNotNull(result);
		assertEquals("dnNweW52NURnOmY1NDA5MWY1LWZmNzctNGM3MS1hMTRjLTFjMjlhYjkzZmQxNQ==", result);
	}
	
	
	

}