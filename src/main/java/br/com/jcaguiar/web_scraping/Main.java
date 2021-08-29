package br.com.jcaguiar.web_scraping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {
	//CONFIGURATION ATTRIBUTES
	static List<HtmlElement> targetPages = new ArrayList<HtmlElement>();
	static List<String[]> diretrix = new ArrayList<String[]>();
	static List<String> urlsToGo = new ArrayList<String>();
	static List<String> categorias = new ArrayList<String>();
	static List<String> nomes = new ArrayList<String>();
	static List<String> descricoes = new ArrayList<String>();
	static List<String> marcas = new ArrayList<String>();
	static List<String> materiais = new ArrayList<String>();
	static List<String> valores = new ArrayList<String>();
	static List<List<String>> medidas = new ArrayList<List<String>>();
	static List<List<String>> imagens = new ArrayList<List<String>>();
	static Scraper scraper;
	static int count = 0;
	
	private static void setMapping() {
		diretrix.add(
			new String[] {"Jaquetas", "https://www.lojasrenner.com.br/c/masculino/casacos-e-jaquetas/-/N-1bpn6of"}
			//map.put("Camisas", "https://www.lojasrenner.com.br/c/masculino/camisas/-/N-1qa4gj6/p1");
			//map.put("Camisetas", "https://www.lojasrenner.com.br/c/masculino/camisetas/-/N-1gu70uy/p1");
			//map.put("Calças", "https://www.lojasrenner.com.br/c/masculino/calcas/-/N-kyxeyg/p1");
			//map.put("Blazers", "https://www.lojasrenner.com.br/c/masculino/casacos-e-jaquetas/blazers/-/N-bzezhf/p1");
		);
		//converter(map);
	}
	
	private static void converter(Map<String, String> map) {
		String[] campos = (String[]) map.keySet().toArray();
		String[] valores = (String[]) map.values().toArray();
		for(int i = 0; i < campos.length; i++) {
			diretrix.add(new String[] {campos[i], valores[i]});
		}
	}

	
	public static void main(String[] args) {
		//MAIN URL PATH
		setMapping();
		String searchQuery = "" ;
		String tagRennerPages = "//a[@class='ProductBox_productBox__1QP1S']";
		
		//WEB SCRAPER ITERATOR
		for(String[] index : diretrix) {
			scraper = new Scraper(index[1], searchQuery);
			targetPages = scraper.getAllElementsByXPath(tagRennerPages);
			urlsToGo.clear();
			try {
				urlsToGo.addAll( scraper.getAttributeFromTag(targetPages, "href") );
				
				//LOOPING PELAS HREF ENCONTRADAS
				for(String url : urlsToGo) {
					System.out.printf("*****%s*****\n", index[0]);
					String baseUrl = "https://www.lojasrenner.com.br";
					scraper.pageFormatAndGo(baseUrl + url, "");
					coletarProdutosRenner();
					categorias.add(index[0]);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		//FILLING LAST COLUMNS
		//Modelo
		List<String> modelos = nomes;
		modelos.forEach(modelo->{
			modelo = "";
		});
		//Estoque
		List<Integer> estoques = new ArrayList<Integer>();
		nomes.forEach(nome->{
			estoques.add((int) (Math.random() * 100 + 25));
		});
		//Codigo Ean
		List<String> codigos = nomes;
		codigos.forEach(cod->{
			cod = "789" + 
					String.valueOf(Math.random()*10) + 
					String.valueOf(Math.random()*10) + 
					String.valueOf(Math.random()*10) +
					String.valueOf(Math.random()*10) +
					String.valueOf(Math.random()*10) +
					String.valueOf(Math.random()*10) +
					String.valueOf(Math.random()*10) +
					String.valueOf(Math.random()*10) +
					String.valueOf(Math.random()*10) +
					"0";
		});
		
		List<String[]> csv = new ArrayList<String[]>();
		/** CSV PRODUTC COLUMNS:
		 * "CATEGORIA",			A (00)
		 * "NOME",				B (01)
		 * "DESCRIÇÃO",			C (02)
		 * "MARCA",				D (03)
		 * "MODELO",			E (04)
		 * "PREÇO",				F (05)
		 * "ESTOQUE",			G (06)
		 * "MATERIAIS",			H (07)
		 * "MEDIDAS",			I (08)
		 * "TAMANHO",			J (09)
		 * "CODIGO EAN"			K (10)
		 */
		for(int i = 0; i < nomes.size(); i++) {
			//LOOPING FOR EACH PRODUCT SIZE
			for(int ii = 0; ii < 5; ii++) {
				String tamanho = "P";
				tamanho = ii == 1 ? "M": tamanho;
				tamanho = ii == 2 ? "G": tamanho;
				tamanho = ii == 3 ? "GG": tamanho;
				tamanho = ii == 4 ? "EG": tamanho;
				csv.add(new String[] {
						categorias.get(i),
						nomes.get(i),
						descricoes.get(i),
						marcas.get(i),
						modelos.get(i),
						valores.get(i),
						String.valueOf(estoques.get(i)),
						materiais.get(i),
						String.join(", ", medidas.get(i)),
						tamanho,
						codigos.get(i)
				});
			}
		}
		
		//FILLING CSV SPREADSHEET
		CsvManager csvManager = new CsvManager();
		csvManager.escreverCsv("tabela produtos teste", csv);
	}
	
	//GET PRODUCTS FROM RENNER.COM
	public static void coletarProdutosRenner() {
		//POINTER VARIABLE
		String targetTag;
		
		//SAVE LOCAL HTML FILE
		String webSiteXml = scraper.getPage().asXml();
		String path = String.format("C://Users/JM Costal Aguiar/web-scraper/renner%s.html", ++count);
		File arquivo = new File(path);
	    try (PrintWriter printWriter = new PrintWriter(arquivo)) {
            printWriter.print(webSiteXml);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//GETING PRODUCT DATA
		try {
			//PRODUCT NAME
			targetTag = "//h1[@class='product_name']/span";
			HtmlElement tagProductName = scraper.getOneElementByXPath(targetTag);
			nomes.add(tagProductName.getTextContent());
			
			//PRODUCT PRICE
			targetTag = "//span[@class='best_price']";
			HtmlElement tagProductPrice = scraper.getOneElementByXPath(targetTag);
			valores.add(tagProductPrice.getTextContent());
			
			//PRODUCT DESCRIPTION, MATERIALS AND BRAND
			targetTag = "//div[@class='desc selected']/ul/li";
			List<HtmlElement> tagProductInfo = scraper.getAllElementsByXPath(targetTag);
			boolean materialArea = false;
			for(HtmlElement info : tagProductInfo) {
				if(materialArea) {
					materiais.add(info.getTextContent());
					System.out.printf("\t [Material]: %s \n", info.getTextContent());
				}
				else {
					String thisText = info.getTextContent().toLowerCase();	
					if(thisText.startsWith("marca")) {
						marcas.add(info.getTextContent());
						System.out.printf("\t [Marca]: %s \n", info.getTextContent());
						materialArea = true;
					}
					else {
						descricoes.add(info.getTextContent());
						System.out.printf("\t [Descrição]: %s \n", info.getTextContent());
					}
				}
			}
			
			//PRODUCT LENGTH OF PARTIES
			List<String> tamanhos = new ArrayList<String>();
			int random = (int) Math.random() * 9;
			int pmggg = 8;
			//0 = P;   1 = M;   2 = G;   3 = GG;   4 = EG	 
			for(int size = 0; size < 5; size++) {
				//Tomrax
				int tomrax = 88 + (size * pmggg) + random;
				tamanhos.add(Integer.toString(tomrax));
				
				//Cintura
				int cintura = 74 + (size * pmggg) + random;
				tamanhos.add(Integer.toString(cintura));
				
				//Quadril
				int quadril = 92 + (size * pmggg) + random;
				tamanhos.add(Integer.toString(quadril));
				
				//Pescoço
				int pescoco = 37 + size;
				tamanhos.add(Integer.toString(pescoco));
			}
			medidas.add(tamanhos);
			
			//PRODUCT IMAGES
			targetTag = "//div[@class='item open-gallery ']/img";
			List<HtmlElement> tagsProductImg = scraper.getAllElementsByXPath(targetTag);
			List<String> imgLink = new ArrayList<String>();
			tagsProductImg.forEach(img->{
				String stringImg = img.getAttribute("data-zoom-image");
				imgLink.add(stringImg);
				//System.out.printf("\t > %s \n", stringImg);
			});
			System.out.printf("LOG: Total de %s imagens para para esse produto. \n", imgLink.size());
			imagens.add(imgLink);
			
		}
		catch (Exception e) {
			System.out.println("LOG: unexpected error trying get some data from the source.");
			e.printStackTrace();
			
		}
	}
	
	public static void coletarProdutosZara() {
		//POINTER VARIABLE
		String targetTag;
		
		//Grava o arquivo no disco
		String webSiteXml = scraper.getPage().asXml();
		String path = "C://Users/JM Costal Aguiar/zaraXml.html";
		File arquivo = new File(path);
	    try (PrintWriter printWriter = new PrintWriter(arquivo)) {
            printWriter.print(webSiteXml);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//PRODUCT NAME
		targetTag = "//h1[@class='product-detail-info__name']";
		HtmlElement tagProductName = scraper.getOneElementByXPath(targetTag);
		nomes.add(tagProductName.getTextContent());
		
		//PRODUCT DESCRIPTION (BASIC)
		targetTag = "//div[@class='expandable-text__inner-content']/p";
		HtmlElement tagProductDescription = scraper.getOneElementByXPath(targetTag);
		descricoes.add(tagProductDescription.getTextContent());
		
		//PRODUCT PRICE
		targetTag = "//span[@class='price__amount-current']";
		HtmlElement tagProductPrice = scraper.getOneElementByXPath(targetTag);
		valores.add(tagProductPrice.getTextContent());
		
		//PRODUCT MATERIALS (AREA)
//		targetTag = "//div[@data-observer-key='materials']" +
//				"/div[@class='structured-component-text-block-subtitle']/span/span";
//		List<HtmlElement> tagProductMaterialType = scraper.getAllElementsByXPath(targetTag);
		
		
		//PRODUCT MATERIALS (TECNICAL NAME)
//		targetTag = "//div[@data-observer-key='materials']" +
//				"/div[@class='structured-component-text-block-paragraph']/span/span";
//		List<HtmlElement> tagProductMaterialName = scraper.getAllElementsByXPath(targetTag);
		
		
		//Armazenar todo o texto dentro da tag <div> com atributo data-observer-key='materials'
		//Tratar texto depois
		//Armazenar na listagem oficial
		targetTag = "//div[@data-observer-key='materials']";
		HtmlElement tagProductMaterial = scraper.getOneElementByXPath(targetTag);
		String textProductMaterial = tagProductMaterial.asText();
		materiais.add(textProductMaterial);
		
		//PRODUCT LENGTH OF PARTIES
		//targetTag = "//span[@class='product-detail-interactive-size-guide-image__info-box']";}
		
		//PRODUCT IMAGES
		targetTag = "//source[@sizes='40vw']";
		List<HtmlElement> tagsProductImg = scraper.getAllElementsByXPath(targetTag);
		List<String> imgLink = new ArrayList<String>();
		tagsProductImg.forEach(img->{
			String stringImg = img.getAttribute("srcset");
			int stringEnd = stringImg.indexOf(" ");
			stringImg = stringImg.substring(0, stringEnd);
			imgLink.add(stringImg);
			//System.out.printf("\t > %s \n", stringImg);
		});
		imagens.add(imgLink);
		
		
		//LOG
		System.out.println("Coletando urlsToGo das imagens. Tag 'srcset'.");
	}

}
