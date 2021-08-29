package br.com.jcaguiar.web_scraping;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import lombok.Getter;
import lombok.ToString;

@ToString
final public class CsvManager {
	
	@Getter
	private List<String[]> arquivo = new ArrayList<String[]>();
	private final String DIRETORIO = "C:\\Users\\JM Costal Aguiar\\eclipse-workspace\\ecommerce\\src\\main\\resources\\cadastros\\";
	
	//BUILDING CSV
	public void escreverCsv(String name, List<String[]> csv) {
		name = DIRETORIO + name + ".csv";
		
		try (Writer writer = Files.newBufferedWriter(Paths.get(name))) {
			CSVWriter csvWriter = new CSVWriter(writer);
			
			//CRIANDO CABEÇALHO-------------COLUNAS
			String[] header = {				
					"CATEGORIA",			//A (00)
					"NOME",					//B (01)
					"DESCRIÇÃO",			//C (02)
					"MARCA",				//D (03)
					"MODELO",				//E (04)
					"PREÇO",				//F (05)
					"ESTOQUE",				//G (06)
					"MATERIAIS",			//H (07)
					"MEDIDAS",				//I (08)
					"TAMANHO",				//J (09)
					"CODIGO EAN"			//K (10)
			};
			
			//CSV HEADER (FIRST LINE)
			csvWriter.writeNext(header);
			
			//CSV BODY (REMAIN LINES)
	        csvWriter.writeAll(csv);
	        
	        //CLOSING CSV
	        csvWriter.flush();
	        writer.close();
	        
		}
		catch (IOException e) {
			e.printStackTrace();
			
		}
	}
	
	//READING CSV
	public void leitorCsv(String nome) {
		nome = DIRETORIO + nome;
		try( CSVReader csvReader = new CSVReader(new FileReader(nome)); ) {
			csvReader.skip(1);
		    String[] line = null;
		    while ( (line = csvReader.readNext()) != null ) {
		        arquivo.add( line[0].split(";") );
		        System.out.println( Arrays.toString( line[0].split(";") ) );
		    }
		} catch (FileNotFoundException e) {
			System.out.printf("Erro: Arquivo não encontrado\n");
			e.printStackTrace();
			
		} catch (IOException e) {
			System.out.printf("Erro: Falha na leitura/escrita\n");
			e.printStackTrace();
			
		} catch (CsvException e) {
			System.out.printf("Erro: Problema no manuseio do csv\n");
			e.printStackTrace();
		}
	}
	

}
