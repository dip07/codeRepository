package com.programming.assignment2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

/*
 * Class Name: LSH
 * This class is used to implement Local Sensitive Hashing in order to detect near duplicates of a document.
 * <p> Takes minHashMatrix, List of documents and number of bands as input and create
 * <p> a Hash Table of size equal to #of bands and store similar documents in a list.
 * 
 * @author: Dipanjan Karmakar
 * @author: Gaurav Bhatt
 */

public class LSH {
	static private int[][] minHashMatrix;		// the minHash matrix
	static String[] docNames;					// the document name array
	static MinHash minHash;						// the minHash object
	static int bands;							// the number of bands
	public static HashMap<Integer, ArrayList<String>>[] hm_arr ;		// the hash table 
	private static final BigInteger  FNV64PRIME=new BigInteger("109951168211");			//constant
	private static final BigInteger FNV64INIT=new BigInteger("14695981039346656037");		//constant
	private static final BigInteger TWO=BigInteger.valueOf(2l);						//constant

	/* Constructor 
	 * Creates an instance of the LSH
	 * @param minHashM	the minHashMatrix
	 * @param doc[]	the documents collections
	 * @param b		the number of bands
	 * @return an object of LSH 
	 */
	public LSH(int [][] minHashM, String[] doc, int b) throws IOException {
		minHashMatrix = minHashM;
		docNames = doc;
		bands = b;
		BigInteger bg= BigInteger.valueOf(8*doc.length);	// size taken to be 8 times the number of documents	
		int reqPrime=bg.nextProbablePrime().intValue();
		
		int N = minHashMatrix.length; 				/* No. of documents or columns in minHashMatrix */
		int K = minHashMatrix[0].length; 			/* Permutation count */
		int R = K%bands==0?(K/bands):K/bands+1; 	/* No. of rows in each bands */
		//System.out.println("N is: "+N +" R is: "+R);
		hm_arr = new HashMap[bands];
		for(int i=0;i< bands;i++){					//initialize the table
			//hm_arr[i]= new HashMap();
			HashMap<Integer,ArrayList<String>> curMap= new HashMap<Integer,ArrayList<String>>(); 
			for(int j=0;j<reqPrime;j++){
					ArrayList<String> tmpList= new ArrayList<String>();
					curMap.put(j, tmpList);
			}
			hm_arr[i]=curMap;
		}
		
		StringBuffer s=null;
		for (int i=0; i< bands; i++) {   // for all bands
			for (int j=0; j<N; j++) {    // for all documents
				s = new StringBuffer();
				for (int k=0;k< R && (i*R + k) <K; k++){  // for each row in a band
					s.append("701"+minHashMatrix[j][i*R + k]);		// to be used to create hash for a particular band
				}									//randomly taken 143
 
				/* create a hashcode of calculated string */
				int hashcode=(Math.abs(randomHashOfString(s.toString())))%reqPrime;	// using FNV for hash function

				ArrayList<String> currList=hm_arr[i].get(hashcode);			//point to the appropriate arrayList (bucket)
				if(currList==null)
				{
					System.out.println("currList is null");
					System.out.println("HashCode > " + hashcode);
				}
				if(doc[j]==null)
					System.out.println("filelist is null");
				currList.add(docNames[j]);						//add the document to this bucket
			}
		}
	}
	
	/*
	 * Method Name: nearDuplicatesOf
	 * <p>This method takes a document name as input and return the list of duplicates among given documents.
	 * @param docName	the name of the file for which we need to find the similar documents
	 * @return	the array of files which are similar to the input document
	 */
	public static String[] nearDuplicatesOf(String docName)
	{
		TreeSet<String> result=new TreeSet<String>();
		
		for(int i=0;i<bands;i++)		//loop in the bands
		{
			HashMap<Integer, ArrayList<String>> listTab=hm_arr[i];
			for(Integer tp:listTab.keySet())
			{
				if(listTab.get(tp).contains(docName))		// if if the doc is present in this bucket
				{
					for(String docs:listTab.get(tp))
					{
						if(!docs.equalsIgnoreCase(docName))		//skip the particular file
							result.add(docs);					//add others
					}
				}
			}
		}		
		String[] retStr = result.toArray(new String[result.size()]);
		return retStr;
	}
	
	/*
	 * Method Name: randomHashOfString
	 * <p> This method takes a string s and return a random Hash value generated using it with FNV hash.
	 * @param s  String for which we need to get the hash value
	 * @return the FNNV hash value for the string 
	 */
	public static int randomHashOfString(String s)
	{
		BigInteger hash=FNV64INIT;
		for(int j=0;j<s.length();j++)
		{
			hash=hash.xor(BigInteger.valueOf(s.charAt(j)));
			hash=hash.multiply(FNV64PRIME).mod(TWO.pow(64));
		}
		return hash.intValue();
	}
}
	