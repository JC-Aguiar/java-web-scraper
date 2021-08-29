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
	/**CONSTRUCTOR DEFAULT
	 * 
	 */
	public Scraper() {
		defaultStart();
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**CONSTRUCTOR II
	 * 
	 * @param urlOrigin
	 */
	public Scraper(String urlOrigin) {
		this.urlOrigin = urlOrigin;
		this.urlQuery = "";
		defaultStart();
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**CONSTRUCTOR III
	 * 
	 * @param urlOrigin
	 * @param urlQuery
	 */
	public Scraper(String urlOrigin, String urlQuery) {
		this.urlOrigin = urlOrigin;
		this.urlQuery = urlQuery;
		defaultStart();
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**AFTER CONSTRUCTED
	 * 
	 */
	private void defaultStart() {
		htmlOnly();
		client.addRequestHeader("Content-Type", "application/xhtml+xml");
		pageFormatAndGo(urlOrigin, urlQuery);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**ENCODE PAGE
	 * 
	 */
	public void pageFormatAndGo(String urlBase, String query) {
		try {
			String urlFinal = urlBase + URLEncoder.encode(query, urlEncode);
			pageGo(urlFinal);
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
	public void pageGo(String url) {
		try {
			this.page = client.getPage(url);
			urlStack.add(url);
			System.out.printf("LOG: Successful access to: %s \n", url);
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
	public String getUrlHistory(int index) {
		return urlStack.get(index);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//WEBPAGE CONFIGURATIONS (BEFORE ACCESS)
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**JAVASCRIPT OFF
	 * 
	 */
	public void noJs() {
		client.getOptions().setJavaScriptEnabled(false);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**JAVASCRIPT ON
	 * 
	 */
	public void enableJs() {
		client.getOptions().setJavaScriptEnabled(true);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**CSS OFF
	 * 
	 */
	public void noCss() {
		client.getOptions().setCssEnabled(false);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**CSS ON
	 * 
	 */
	public void enableCsss() {
		client.getOptions().setCssEnabled(true);
	}
	
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**DESABLE NON-HTML ELEMENTS
	 * 
	 */
	public void htmlOnly() {
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
			if(elements.size() == 0) {
				throw new NullPointerException();
			}
			System.out.printf("LOG: Found %s total elements. \n",
					elements.size());
			for(HtmlElement item : elements) {
				System.out.printf("\t > %s \n", item);
			}
		}
		catch (NullPointerException e) {
			System.out.printf("LOG: No HTML %s element found. \n", elementPath);
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
			System.out.printf("LOG: Founded element: %s \n",
					element.asText());
		}
		catch (NullPointerException e) {
			System.out.printf("LOG: No HTML %s element found. \n", elementPath);
		}
		return element;
	}


	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	//UTILITY
	//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**GET ATTRIBUTE FROM HTML TAG LIST
	 * 
	 * @param targetPages
	 * @param attr
	 * @return
	 */
	public List<String> getAttributeFromTag(List<HtmlElement> tagsHtml, String attr) {
		List<String> values = new ArrayList<String>();
		System.out.printf("LOG: Geting attribute '%s' from HTML. \n", attr);
		for(HtmlElement tag : tagsHtml) {
			String item = tag.getAttribute(attr); 
			if(item != null && item != "" ) {
				values.add(item);
				System.out.printf("\t > %s \n", item);
			}
		}
		if(values.size() == 0 || values.isEmpty()) {
			System.out.println("LOG: No values founded. \n");
			throw new NullPointerException();
		}
		return values;
	}

}
