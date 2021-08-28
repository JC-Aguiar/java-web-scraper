package br.com.jcaguiar.web_scraping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.Getter;
import lombok.Setter;

public class Scraper {
	
	@Getter @Setter private String urlOrigin;
	@Getter @Setter private String urlQuery;
	@Getter @Setter private String urlEncode = "UTF-8";
	@Getter @Setter private List<String> urlStack = new ArrayList<String>();
	@Getter private HtmlPage page;
	final private WebClient client = new WebClient();
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**CONSTRUCTOR 1
 * 
 * @param urlOrigin
 */
	public Scraper(String urlOrigin) {
		this.urlOrigin = urlOrigin;
		this.urlQuery = "";
		defaultSetting();
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**CONSTRUCTOR 2
	 * 
	 * @param urlOrigin
	 * @param urlQuery
	 */
	public Scraper(String urlOrigin, String urlQuery) {
		this.urlOrigin = urlOrigin;
		this.urlQuery = urlQuery;
		defaultSetting();
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**AFTER CONSTRUCTED
	 * 
	 */
	private void defaultSetting() {
		htmlOnly();
		pageReady();
		pageGo(urlStack.get(0));
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**ENCODE PAGE
	 * 
	 */
	private void pageReady() {
		try {
			urlStack.add(urlOrigin + URLEncoder.encode(urlQuery, urlEncode));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**BROWSE
	 * 
	 * @param url: String URL to try a web access 
	 */
	private void pageGo(String url) {
		try {
			this.page = client.getPage(url);
		}
		catch (FailingHttpStatusCodeException e) {
			//System.out.println("Error: server failing");
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}	
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//WEBPAGE CONFIGURATIONS (BEFORE ACCESS)
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**JAVASCRIPT OFF
	 * 
	 */
	private void noJs() {
		client.getOptions().setJavaScriptEnabled(false);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**JAVASCRIPT ON
	 * 
	 */
	private void enableJs() {
		client.getOptions().setJavaScriptEnabled(true);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**CSS OFF
	 * 
	 */
	private void noCss() {
		client.getOptions().setCssEnabled(false);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**CSS ON
	 * 
	 */
	private void enableCsss() {
		client.getOptions().setCssEnabled(true);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**DESABLE NON-HTML ELEMENTS
	 * 
	 */
	private void htmlOnly() {
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setCssEnabled(false);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//WEBPAGE MANIPULATION
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**GET ALL HTML TAG
	 * 
	 * @param elementPath
	 * @return
	 */
	public List<HtmlElement> getAllElementsByXPath(String elementPath) {
		List<HtmlElement> elements = (List<HtmlElement>) page.getByXPath(elementPath);
		try {
			System.out.printf("LOG: Found %s total elements. \n",
					elements.size());
		}
		catch (NullPointerException e) {
			System.out.println("LOG: No HTML element found.");
		}
		return elements;
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**GET FIRST HTML TAG
	 * 
	 * @param elementPath
	 * @return
	 */
	public HtmlElement getOneElementByXPath(String elementPath) {
		HtmlElement element = (HtmlElement) page.getFirstByXPath(elementPath);
		try {
			System.out.printf("LOG: Founded element: \n",
					element.asText());
		}
		catch (NullPointerException e) {
			System.out.println("LOG: No HTML element found.");
		}
		return element;
	}

}
