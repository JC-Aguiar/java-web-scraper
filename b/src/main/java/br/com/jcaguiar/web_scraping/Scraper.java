package br.com.jcaguiar.web_scraping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
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
	
	public Scraper(String urlOrigin, String urlQuery) {
		this.urlOrigin = urlOrigin;
		this.urlQuery = urlQuery;
		htmlOnly();
		pageReady();
		pageGo(urlStack.get(0));
	}
	
	private void pageReady() {
		try {
			urlStack.add(urlOrigin + URLEncoder.encode(urlQuery, urlEncode));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
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
	
	private void noJs() {
		client.getOptions().setJavaScriptEnabled(false);
	}
	
	private void enableJs() {
		client.getOptions().setJavaScriptEnabled(true);
	}
	
	private void noCss() {
		client.getOptions().setCssEnabled(false);
	}
	
	private void enableCsss() {
		client.getOptions().setCssEnabled(true);
	}
	
	private void htmlOnly() {
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setCssEnabled(false);
	}

}
