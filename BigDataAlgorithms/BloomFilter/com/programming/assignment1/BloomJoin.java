package com.programming.assignment1;


/* BloomJoin class  will use the deterministic bloom filter. It will load the data 
 * from a file, check for common attributes in another dataset by looking up in the 
 * bloomfilter. And finally join the common relations and store it into a separate 
 * relation table.
 * 
 * @author  Dipanjan Karmakar
 * @author  Gaurav Bhatt
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class BloomJoin {
	private static final int noOfElements=2000000;
	private static final int bitsPerElmnts=8; 
	static String filepath1 = "/Users/dipanjankarmakar/Documents/Isu Google Drive/Isu Studies Google Drive/BigDataAlgo/Assignments/PA1/xid-29480704_1.txt";
	static String filepath2 = "/Users/dipanjankarmakar/Documents/Isu Google Drive/Isu Studies Google Drive/BigDataAlgo/Assignments/PA1/xid-29480705_1.txt";
	static List<storeRelation> listb = new ArrayList<>();
	static List<finalRelation> listc = new ArrayList<>();
	static HashMap<String,String> storeData=new HashMap<String, String>();
	
	public static void main(String[] args) {
		try{
			long tStart = System.currentTimeMillis();
			BloomFilterDet bfd = new BloomFilterDet(noOfElements,bitsPerElmnts);
			String test; 
			int finalCount=0;
			
			/* Load the data from a file */
			System.out.println("Data load1 starts ....");
			loadData(bfd,filepath1);
			System.out.println("Data load ends !");
			
			/* Cleck for common attributes */
			System.out.println("Starting to check common elements ....");
			checkCommonAndAdd(bfd);
			
			
			/* Join the Relations based on common attributes */
			System.out.println("Joining the relations ...");
			finalCount = joinRelations();
			
			/* Print the final relation table */
	//		System.out.println("Final Relation R3 is:");
	//		for(finalRelation R: listc){
	//			System.out.println(R.s1 +" "+ R.s2 +" "+ R.s3);
	//		}
			System.out.println("");
			System.out.println("After join, no. of elements in Relation R3: "+finalCount);
				
			/* Calculate the runtime */
			long tEnd = System.currentTimeMillis();
			long tDelta = tEnd - tStart;
			double elapsedMin = tDelta / 60000.0;
			
			System.out.println("");
			System.out.println("Data entered into the Filter : " +bfd.dataSize());
			System.out.println("Number of hash functions : " +bfd.numHashes());
			System.out.println("Time elapsed : "+  (int)elapsedMin + " minutes");
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}
	
	/* This method will will run a loop for each element in listb 
	 * and check if it is present in Relation1. If its there it will 
	 * store it in a arraylist "listc"
	 */
	private static int joinRelations() {
		// TODO Auto-generated method stub
		int finalCount=0;
		for (storeRelation sb: listb){
			if (storeData.containsKey(sb.s1)) {
				finalCount ++;
				String str1,str2,str3;
				str1 = storeData.get(sb.s1);
				str2 = sb.s1;
				str3 = sb.s2;
				finalRelation r3 = new finalRelation(str1, str2, str3);
				listc.add(r3);
			}
		}
		return(finalCount);
	}

	/*  This method will read input from Relation2 file
	 *  and match first element of Relation1 with first element 
	 *  of Relation2. If there is a match it will add matched
	 *  elements to a list. 
	 */
	private static void checkCommonAndAdd(BloomFilterDet bfd) {
		// TODO Auto-generated method stub
		Path file = Paths.get(filepath2);
		int count=0;
		
		try (InputStream in = Files.newInputStream(file);
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(in))) {
		    	String line = null; 
		    	while ((line = reader.readLine()) != null) {
		    		String strArr[]=line.split("   ");
		    		String strToPut1=strArr[0].trim();
		    		String strToPut2=strArr[1].trim();
		    		if(bfd.appears(strToPut1)) {
		    			count++;
		    			listb.add(new storeRelation(strToPut1, strToPut2));
		    		}			
		    	}
		    	System.out.println("Number of common attributes in the relations >> " + count);
			}
		catch (IOException x) {
			System.err.println(x);
			x.printStackTrace();
		}
		
	}	

	/* Method to load data to bloom filter from a file.
	 * It will store the elements to a hash table and 
	 * add the first string to the bloom filter.
	 */
	private static void loadData(BloomFilterDet bdf, String filepath) throws IOException
	{
		Path file = Paths.get(filepath);
		int count=0;
		 
		try (InputStream in = Files.newInputStream(file);
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(in))) {
		    	String line = null; 
		    	while ((line = reader.readLine()) != null) {
		    		count++;
		    		String strArr[]=line.split("   ");
		    		String strToPut1=strArr[0].trim();
		    		String strToPut2=strArr[1].trim();

		    		/* Store this into a Hash table */
		    		storeData.put(strToPut1,strToPut2);
		    		bdf.add(strToPut1);
		    	}
		    	System.out.println("Number of lines added >> " + count);
				}
		catch (IOException x) {
			System.err.println(x);
			x.printStackTrace();
		}
	}
}

/* Class to store strings as two separate entries */
class storeRelation
{
	String s1, s2;
	public storeRelation(String str1, String str2) {
		s1 = str1;
		s2 = str2;
	}
}

/* Class to store final Relation elements */
class finalRelation
{
	String s1,s2,s3;
	public finalRelation(String str1,String str2,String str3) {
		s1=str1;
		s2=str2;
		s3=str3;
	}
}