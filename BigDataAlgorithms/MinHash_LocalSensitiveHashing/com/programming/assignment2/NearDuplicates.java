package com.programming.assignment2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Class Name: NearDuplicates
 * <p >This class has the main method which will take input from uses for location of documents, no. of Permutations, no of bands,
 * <p> accuracy thresholds, and a document name.
 * <p> Also uses LSH and MinHash to detect near duplicates in a set of documents.
 * 
 * @author: Dipanjan Karmakar
 * @author: Gaurav Bhatt
 */

public class NearDuplicates {
	
	public static void main(String [] args){
		String folderName,doc;
		int noOfPermFromInput, bands;
		Double simThresh;
		
		Scanner sc= new Scanner(System.in);
		
		/* Ask for folder's location */
		do{
			System.out.println("Please enter the folder path : ");
			folderName=sc.nextLine();
		}while(folderName.isEmpty());
		
		/* Ask for no of permutations */
		do{
			System.out.println("Please enter the number of permutataions : ");
			noOfPermFromInput=sc.nextInt();
		}while(noOfPermFromInput<=0);
		
		/* Ask for no. of bands */
		do{
			System.out.println("Please enter No. of bands to be used");
			bands = sc.nextInt();
		}while(bands<=0);
		
		/* Ask for similarity threshold */
		do{
			System.out.println("Please enter the  similarity threshold");
			simThresh=sc.nextDouble();
		}while(simThresh>1);
		
		/* Ask for a document's name */
		do{
			System.out.println("Please enter the name of a document from the collection");
			doc = sc.nextLine();
		} while(doc.isEmpty());
		sc.close();
		
		try {
			MinHash minHash = new MinHash(folderName, noOfPermFromInput);
			int minmat[][] = minHash.minHashMatrix();	//get the minhash matrix
			String[] docs = minHash.allDocs();			// get the list of all documents

			/* call constructor of LSH */
			LSH lsh = new LSH(minmat, docs, bands);			// create object to get the new duplicates
			
			/* Get similar documents and store it in a array of string */
			String[] simDocs =  lsh.nearDuplicatesOf(doc);		// get the list of similar documents
			for(int i=0; i<simDocs.length;i++){	
				System.out.println(simDocs[i] + " >> " + i);	// print the similar documents
			}
			
			/* Check for false positive using approximateJaccard method 
			 * and remove it based on the similarity threshold. 
			 */
			ArrayList<String> finalSimilar= new ArrayList<String>();
			for(String checkDoc: simDocs)
			{
				double approx=minHash.approximateJaccard(checkDoc, doc);
				if(approx>simThresh)
					finalSimilar.add(checkDoc);
			}
			System.out.println("Final Similar Documents >>" + finalSimilar);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}