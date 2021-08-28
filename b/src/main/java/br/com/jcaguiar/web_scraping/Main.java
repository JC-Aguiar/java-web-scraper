package br.com.jcaguiar.web_scraping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {

	
	public static void main(String[] args) {
		String searchQuery = "v1=1879113" ;
		String urlAlvo = "https://www.zara.com/br/pt/man-jackets-l640.html?";

		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);  
		client.getOptions().setJavaScriptEnabled(false);  
		try {  
		  String urlFinal = urlAlvo + URLEncoder.encode(searchQuery, "UTF-8");
		  HtmlPage page = client.getPage(urlFinal);
		  second(client, page);
		}
		catch(Exception e){
		  e.printStackTrace();
		}

	}
	
	public static void second(WebClient client, HtmlPage page) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String elementoAlvo = "//ul[@class='product-grid-block-dynamic__row']/li/a";
		List<HtmlElement> tagsHtml = (List<HtmlElement>) page.getByXPath(elementoAlvo);
		
		if(tagsHtml.isEmpty()) {
			System.out.println("Elemento nÃ£o detectado na pÃ¡gina: " + page.getUrl());
			return;
		}
		System.out.printf("TAGS ENCONTRADAS: %d \n", tagsHtml.size());
		
		final List<String> urls = new ArrayList<String>();
		List<String> imagens = new ArrayList<String>();
		List<String> nomes = new ArrayList<String>();
		List<String> valores = new ArrayList<String>();
		
		tagsHtml.forEach(tag->{
			urls.add( tag.getAttribute("href") );
		});
		
		System.out.printf("URLS SIZE: %d \n", urls.size());
		
		for(String url : urls) {
			String imgAlvo = "//source[@sizes='40vw']";
			String urlFinal = url + URLEncoder.encode("", "UTF-8");
			page = client.getPage(urlFinal);
			List<HtmlElement> elementos = (List<HtmlElement>) page.getByXPath(imgAlvo);
			final List<String> imgLink = new ArrayList<String>();
			elementos.forEach(element->{
				String link = element.getAttribute("srcset");
				int stringEnd = link.indexOf(" ");
				link = link.substring(0, stringEnd);
				imgLink.add(link);
				System.out.printf("\t IMAGS-PAGE: %s \n", link);
			});
			
			
			
			
			//String htmlImg = elemento.getAttribute("srcset");
			//System.out.printf("\t %s \n", htmlImg );
			
//			List<HtmlElement> itens = ((List<HtmlElement>) html.getByXPath(".//li/a[@class='product-link']"));
//			System.out.printf("ITENS TOTAIS: %s \n", itens.size());
//			for(HtmlElement item : itens) {
//				System.out.printf("ITEM: %s \n", item.asText());
//				String url = item.getAttribute("href");
//				urls.add(url);
//				System.out.printf("LINK ADD: %s\n", url);
//			}
			
			
//		    HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']"));
//		    HtmlElement itemTag = ((HtmlElement) htmlItem.getFirstByXPath(".//a[@class='result-title hdrlnk']"));
//		    String itemName = itemTag.getTextContent();
//		    String itemUrl = itemTag.getAttribute("href");
//
//		    // It is possible that an item doesn't have any price, we set the price to 0.0 in this case
//		    String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText() ;
//		    
//			System.out.printf("NOME: %s,  PREÃ‡O: %s,  URL: %s \n",
//					itemName, itemPrice, itemUrl
//			);
	  }
		
	}
	
	
	public static void backup() {
		String searchQuery = "Iphone 6s" ;
		String urlAlvo = "https://newyork.craigslist.org/search/sss?sort=rel&query=";

		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);  
		client.getOptions().setJavaScriptEnabled(false);  
		try {  
		  String urlFinal = urlAlvo + URLEncoder.encode(searchQuery, "UTF-8");
		  HtmlPage page = client.getPage(urlFinal);
		  second(page);
		}
		catch(Exception e){
		  e.printStackTrace();
		}

	}
	
	
	public static void backup2(HtmlPage page) {
		List<HtmlElement> items = (List<HtmlElement>) page.getByXPath("//li[@class='result-row']") ;
		if(items.isEmpty()) {
		  System.out.println("Elemento algo nÃ£o detectado.");
		}
		else {
		  for(HtmlElement htmlItem : items){
		    HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath(".//p[@class='result-info']/a"));
		    HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']"));
		    HtmlElement itemTag = ((HtmlElement) htmlItem.getFirstByXPath(".//a[@class='result-title hdrlnk']"));
		    String itemName = itemTag.getTextContent();
		    String itemUrl = itemTag.getAttribute("href");

		    // It is possible that an item doesn't have any price, we set the price to 0.0 in this case
		    String itemPrice = spanPrice == null ? "0.0" : spanPrice.asText() ;
		    
			System.out.printf("NOME: %s,  PREÃ‡O: %s,  URL: %s \n",
					itemName, itemPrice, itemUrl
			);
		  }
		}
	}

}
