package de.gaska;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.gaska.api.Part;
import de.gaska.impl.SearchServiceImpl;

public class Main {

	static String[] inputFiles = new String[]{"input_claas.csv", "input_andere.csv"};


	public static void main(String[] args) throws Exception {
		Logger logger = LoggerFactory.getLogger(Main.class);

		for (String inputFileName : inputFiles){
			File inputFile = new File(inputFileName);
			if(false == inputFile.exists()){
				System.err.println(String.format("Datei %s existiert nicht.", inputFile.getAbsolutePath()));
				return;
			}


			File outputFile = new File(inputFileName.replace("input_", "output_"));
			if(true == outputFile.exists()){
				System.err.println(String.format("Datei %s existiert bereits.", outputFile.getAbsolutePath()));
				//return;
			}

			List<Part> parts = new ArrayList<Part>();
			try(BufferedReader reader = new BufferedReader(new FileReader(inputFile))){
				String partNr = null;
				int id = 1;
				while((partNr = reader.readLine())  != null){
					if(null ==  partNr || partNr.trim().length() == 0 || partNr.trim().equals("-")){
						partNr = "?";
					}else if (inputFileName.contains("claas")){
						partNr = partNr.substring(0, partNr.length() -1);
					}
					parts.add(new Part(""+id++, partNr.trim()));
				}


			}catch (Exception e) {
				logger.error("Fehler :( ", e);
			}

			try{
				SearchServiceImpl service = new SearchServiceImpl("IMPEXTRADING", "Sergey Schwitin", "STOP29");
				service.authenticate();
				service.initParts(parts);
				service.printParts( new FileOutputStream(outputFile), parts);
			}catch(Exception e){
				logger.error("Fehler :( ", e);
			}
		}


	}
}
