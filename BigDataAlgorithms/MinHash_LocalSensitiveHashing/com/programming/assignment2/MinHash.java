package com.programming.assignment2;

/*
 *  MinHash class will create the MinHash for the documents and use that to calculate the Approx Jaccard Similarity. It shall also calculate the exact Jaccard similaity also
 * <p> We are using hash random permuatation to create hash function 
 * <p> *** We have used various optimizations in this program so that the efficiency is increased keeping the logic same *** 
 * @author Dipanjan Karmakar
 * @author Gaurav Bhatt
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;

public class MinHash {
	static protected String foldername;		// folder name from where to check the files
	static protected int noOfPermFromInput=200;	//no of perm varibale used for testing purpose
	static protected int noOfPerm;				// number of permutations to be used in creating the MinHash matrix
	static protected ArrayList<String> listOfTerms;	// list of all the terms in the doc
	static protected HashMap<String,Integer> docListMapToInt;	//map of file and index of the file-> to increase efficiency
	static protected HashMap<String,ArrayList<String>> docTermMapping;	// map of filename and terms contained in to
	static protected ArrayList<String> fileList;		// the list of documents
	static protected int reqPrime=0;				// prime number for using in hash function
	static protected int a[],b[];					// integers to be used in hash function (ax+b)%p format
	static protected int minHashMatrix[][];			// the actual minHashMatrix
	static protected HashMap<String,TreeSet<Integer>> binMatrix;	// the binary frequency matrix 
	
	/*
	 * MinHash Constructor
	 * <p>This function initializes the various variables and creates the object
	 * @param folder			The folder in which to look for the files
	 * @param numPermutations	the number of permutations
	 * @return 					an object of this class
	 */
	public MinHash(String folder, int numPermutations) throws FileNotFoundException
	{
		foldername=folder;
		noOfPerm=numPermutations;
		docListMapToInt= new HashMap<String,Integer>();
		listOfTerms= new ArrayList<String>();
		docTermMapping= new HashMap<String,ArrayList<String>>();
		preprocessData();
	}
	
	/*
	 *  This function pre-processes the data using the following logic:
	 *  <p> Read the files -> extract the terms -> remove stop words -> store them in various mapping variables so that the processing would be faster 
	 */
	protected void preprocessData() throws FileNotFoundException 
	{
		System.out.println("Started reading files... ");
		long tStart = System.currentTimeMillis();		// gets the starting time
		int docId=0;									// used for mapping each file to a Id
		ArrayList<String> termsInDoc;					// terms in the current(single) document 
		fileList=new ArrayList<String>();				//list of documents
		File folder = new File(foldername);		
		int i=0;										//used during testing of the number of files read
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile() && !file.isHidden()) 		// do not consider the hidden files
			{
				termsInDoc= new ArrayList<String>();
				//System.out.println(file.getName() +" > "+(++i) );
				fileList.add(file.getName());			// add file to the document list
				docListMapToInt.put(file.getName(), docId++);	// add a mapping between file and the index when it appear in the loop
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) 
				{
					String line=scanner.nextLine();
					line=line.trim().toLowerCase();		// trim change to lower chase
					line=line.replaceAll("\\s+", " ").replaceAll("[:.,;']", "");  //remove puntuation symbol	
					String arr[]=line.split(" ");		// split into array of terms
					for(String word: arr)
					{
						if(word.length()>2 && (word.compareToIgnoreCase("the")!=0))		// as per requirement do not consider these terms
						{
							if(!listOfTerms.contains(word)) 		
								listOfTerms.add(word);		// add to the global list of terms
							if(!termsInDoc.contains(word))
								termsInDoc.add(word);		// add to the list of terms for this document
						}
					}
				}
				docTermMapping.put(file.getName(), termsInDoc);	// map the terms in this document to the document
				scanner.close();			// close the scanner to avoid the memory leak
			}
		}
		long tREnd = System.currentTimeMillis();	// check time for reading the files
		long tRDelta = tREnd - tStart;
		double elapsedRSec = tRDelta / 1000;
		System.out.println("Time elapsed to read files : "+  (int)elapsedRSec + " sec");
		System.out.println("Started preprocessing the files ...... ");
		generateBinFreq();	// generate the binary frequency
		setPermValues(listOfTerms.size());	// generate the hash functions
		minHashMatrix();			// generate the minHash matrix

		long tEnd = System.currentTimeMillis();		// check time for the whole preprocessing of documents
		long tDelta = tEnd - tStart;
		double elapsedSec = tDelta / 1000;
		System.out.println("Total time elapsed to pre process data : "+  (int)elapsedSec + " sec");

	}
	/*
	 * This function returns the Array of all the docs
	 * @return	the array of al the documents
	 */
	public String[] allDocs() throws IOException
	{
		return fileList.toArray(new String[0]);
	}
	/*
	 * This function generates the binary frequency for the documents
	 * <p> We are only saving the index of terms which are present corresponding to the index number in the global list of terms
	 * <p> Result : This increases speed tremendously and also uses less pace
	 */
	protected void generateBinFreq()
	{
		System.out.println("FileList Size >> "+ fileList.size());
		System.out.println("No of terms >> "+ listOfTerms.size());
		binMatrix=new HashMap<String,TreeSet<Integer>>();	// the binary matrix is a HahMap(<FileName>, TreeSet<index of terms>) 
		TreeSet<Integer> termIdListInDoc;
		for(int i=0;i<fileList.size();i++)
		{
			String file=fileList.get(i);
			ArrayList<String> termInThisDoc=docTermMapping.get(file);	// get the terms present in this document
			termIdListInDoc= new TreeSet<Integer>();
			for(int j=0;j<listOfTerms.size();j++)
			{
				String term=listOfTerms.get(j);
				if(termInThisDoc.contains(term))
					termIdListInDoc.add(j);			// add the index for this term in this variable
			}
			binMatrix.put(file, termIdListInDoc);	// map the file with its binary vector
		}
	}
	/*
	 * This function calculates the exact Jaccard similarity for two documents
	 * <p> This function has been optimized for speed:
	 * <p> We do not check each and every value of the binary vector for individual doc
	 * <p>  Instead we take the intersection of the of the two Set of indexId. This corresponds to the dot product
	 * <p>	We take the length of a treeSet as the Lta ~> length of the doc
	 * @param file1		the first file name
	 * @param file2		the second file name
	 * @return 		the exact Jaccard similarity for the two documents
	 */
	public double exactJaccard(String file1, String file2)
	{
		long tStart = System.currentTimeMillis();
		double result=0f;
		double dotP=0,Lta=0,Ltb=0;
		TreeSet termsInDoc1=new TreeSet(binMatrix.get(file1));
		Lta=termsInDoc1.size();

		TreeSet termsInDoc2=new TreeSet(binMatrix.get(file2));
		Ltb=termsInDoc2.size();
		termsInDoc1.retainAll(termsInDoc2);
		dotP=termsInDoc1.size();

		result=dotP/(Lta+Ltb-dotP);

		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSec = tDelta / 1000;

		return result;
	}
	/*
	 * This function generates the prime number and 'a' and 'b' used in the hash function (ax+b)%p  
	 * @param 	base	the prime number should be greater than this variable
	 */
	protected void setPermValues(int base)
	{
		BigInteger bg= BigInteger.valueOf(base);
		reqPrime=bg.nextProbablePrime().intValue();
		a=new int[noOfPerm];b=new int[noOfPerm];
		for(int i=0;i<noOfPerm;i++)
		{
			Random rand= new Random();
			int t,p;
			do{				// do not allow same value of 'a' 
				t=1+rand.nextInt(reqPrime-1);
			}while(Arrays.asList(a).contains(t));
			a[i]=t;
			do{				// do not allow same value of 'a'
				p=1+rand.nextInt(reqPrime-1);;	
			}while(Arrays.asList(b).contains(p));
			b[i]=p;
		}
	}
	/*
	 * This function creates the minhash signature for a document
	 * @param fileName	the document name for which minHash signature is generated
	 * @return the arrays of integers which is the minHash signatures corresponding to each hash function
	 */
	public int[] minHashSig(String fileName)
	{
		int result[]=new int[noOfPerm];
		int minHashSig;
		for(int i=0;i<noOfPerm;i++)
		{
			minHashSig=Integer.MAX_VALUE;;
			TreeSet<Integer> wordIdList=binMatrix.get(fileName);	// get the index of the words in this document
			for(int t:wordIdList)
			{
				int h=(a[i]*(t+1) + b[i])%reqPrime;		// generate hash function. Using (t+1) as t may be 0 as index starts from 0 in Java
				if(h<minHashSig)
				{
					minHashSig=h;
				}
			}
			result[i]=minHashSig;
		}
		return result;
	}
	/*
	 * This function calculates the approximate Jaccard similarity for the two input files
	 * <p> As we are using inverted minHashMatrix we can get the minHash signature for a file as a row;
	 * <p> had we used a column we would have to iterate in a loop to get the value
	 * <p> ~> This increases the efficiency
	 * @param file1		the first document name
	 * @param file2		the second document name 
	 * @return 	the approximate Jaccard similarity for the two documents 
	 */
	protected double approximateJaccard(String file1, String file2)
	{
		long tStart = System.currentTimeMillis();
		double result=0;
		int file1Idx=docListMapToInt.get(file1);
		int file2Idx=docListMapToInt.get(file2);

		int hash1[]= minHashMatrix[file1Idx];	// get the minHash signature directly as a row
		int hash2[]= minHashMatrix[file2Idx];
		double count=0;
		for(int i=0;i<noOfPerm;i++)
		{
			if(hash1[i]==hash2[i])
				count++;
		}
		result=count/noOfPerm;

		long tEnd = System.currentTimeMillis();	// was used during testing
		long tDelta = tEnd - tStart;
		double elapsedSec = tDelta / 1000;

		return result;
	}
	/*
	 * This function generates the minHash matrix. Here we are using inverted matrix for increasing efficiency
	 * <p> Rows corresponds to the documents and columns corresponds to hash functions 
	 * @return the 2D array for the collection document 
	 */
	protected int[][] minHashMatrix()
	{
		System.out.println("MinHash matrix generation start");
		long tStart = System.currentTimeMillis();
		minHashMatrix=new int[fileList.size()][noOfPerm];
		for(int i=0;i<fileList.size();i++)
		{
			int hashSign[]=minHashSig(fileList.get(i));
			minHashMatrix[i]=hashSign;
		}
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSec = tDelta ;
		System.out.println("Time taken to generate Minhash matrix : "+  (int)elapsedSec + " millisec");
		return minHashMatrix;
	}
	/*
	 * This function returns the number of terms in the document
	 * @return the number of terms in the collection of documents after removing the stop words 
	 */
	protected static int numTerms() 
	{
		return listOfTerms.size();
	}
	/*
	 * This function returns the number of permutations( hash function) used for the generation of the matrix
	 * return the number of permutations
	 */
	protected static int numPermutations()
	{
		return noOfPerm;
	}
	/* 
	 * Main method was used by us for testng purpose
	 */
	public static void main(String[] args) 
	{
		try{
			String foldername="/Users/dipanjankarmakar/Documents/Isu Google Drive/Isu Studies Google Drive/BigDataAlgo/Assignments/PA2/space";
			//String foldername="/Users/dipanjankarmakar/Documents/Isu Google Drive/Isu Studies Google Drive/BigDataAlgo/Assignments/pa_data";
			MinHash minHash= new MinHash(foldername, noOfPermFromInput);

			System.out.println("No of docs : "+ minHash.allDocs().length);
			System.out.println("Number of terms : " +numTerms());

			//String file1="space-2.txt", file2="space-172.txt";
			String file1="space-0.txt", file2="space-102.txt";
			System.out.println("Exact Jaccard  > "+ minHash.exactJaccard(file1,file2));
			System.out.println("Approximate Jaccard  > "+ minHash.approximateJaccard(file1,file2));
			System.out.println("Num of Permutations >> " + minHash.numPermutations());

		}
		catch(Exception e)		// catch the exceptions and report appropriately
		{
			e.printStackTrace();
		}
	}

}
