package com.programming.assignment1;

/*
 * BloomFilterDet class use the Deterministic hash functions to create the Bloom Filter
 * <p>We are using FNV 64 Bit hash function for creating the hash function.  
 * <p>For creating the <i>k</i> hash functions we are using bit shift and XOR operations on the hash function returned by FNV hash.
 * 
 * @author Dipanjan Karmakar
 * @author Gaurav Bhatt
 */

import java.math.BigInteger;
import java.util.BitSet;

class BloomFilterDet {
	
	private int filterSize;    		// Size of the filter ~ table size
	private BitSet bloomFilter;		// actual Bloom Filter variable 
	private long noOfHashFn;		// # of hash function to be created
	private long dataSize;			// size of the data that has been added to the Filter
	
	private static final int noOfElements=1000000;		//number of elements to be hashed
	private static final int bitsPerElmnts=4; 			// bits per elements
	private final BigInteger  FNV64PRIME=new BigInteger("109951168211");			//constant
	private final BigInteger FNV64INIT=new BigInteger("14695981039346656037");		//constant
	private static final BigInteger TWO=BigInteger.valueOf(2l);						//Big Integer representation of 2
	
	/*
	 * BloomFilter Constructor
	 * <p>This function determines the size of the Bloom Filter  and the number of hash functions to be created
	 * @param setSize			size of the input elements to hash
	 * @param bitsPerElement	bits per elements in the bloom filter
	 * @return 					an object of this class
	 */
	
	BloomFilterDet(int setSize, int bitsPerElement)			
	{
		filterSize=setSize*bitsPerElement;
		bloomFilter= new BitSet(filterSize);
		noOfHashFn=(long)(filterSize*(Math.log(2))/setSize);
		dataSize=0;
		System.out.println("Bloom Filter created of size : "+filterSize);
	}
	
	/*
	 * This function adds a string to the bloom filter
	 * <p>This function calls the randomHashOfString method to create <i>k</i> hash function 
	 * <p>After that it sets the hash value to the corresponding bit in Bloom Filter 
	 * @param s		the input string
	 */
	
	void add(String s)
	{
		s=s.toLowerCase();
		dataSize++;
		for(int i=1; i <= noOfHashFn; i++)
		{
			long hashValue=Math.abs(randomHashOfString(i,s));
			int mappingToSize=(int) (hashValue % bloomFilter.size());
			bloomFilter.set(mappingToSize);
		}
	}
	/* This function created the hash of the string using basic FNV function and then Left Shift followed by XOR 
	 * @param 	i variable to indicate the n th hash function to create
	 * @param 	s string for which we need to create the hash
	 */
	
	long randomHashOfString(int i,String s)
	{
		BigInteger hash=FNV64INIT;
		for(int j=0;j<s.length();j++)
		{
			hash=hash.xor(BigInteger.valueOf(s.charAt(j)));
			hash=hash.multiply(FNV64PRIME).mod(TWO.pow(64));
		}
		BigInteger temp=hash.shiftLeft(i);
		hash=hash.xor(temp);
		return hash.longValue();
	}
	
	/*
	 * Checks if the String is in Bloom Filter or not
	 * @param s 	input string for which we need to check whether it is present in the Bloom Filter
	 * @returns 	true if the string is in the Bloom Filter or not
	 */
	boolean appears(String s)
	{
		s=s.toLowerCase();
		boolean result=true;
		for(int i=1;i<=noOfHashFn;i++)
		{
			long hashValue=Math.abs(randomHashOfString(i, s));
			int mappingToSize=(int) (hashValue % bloomFilter.size());
			if(!bloomFilter.get(mappingToSize))
				return false;
		}
		return result;
	}
	
	/*
	 * Function to check the number of number of elements added to the Bloom Filter
	 * @return 	the number of elements added to the Bloom Filter
	 */
	long dataSize()
	{
		return dataSize;
	}
	
	/*
	 * Function to check the size of the Bloom Filter
	 * @return 	the size of the Bloom Filter
	 */
	long filterSize()
	{
		return bloomFilter.size();
	}
	/*
	 * Function to check the number of hash functions created
	 * @return 	the number of hash functions created for this Bloom Filter 
	 */
	
	long numHashes()
	{
		return noOfHashFn;
	}
	
	/*
	 * We place 1000000 elements in the Bloom Filter and check another 1000000 elements for False Positives
	 * <p>For checking the correctness of the program we also check the False Negatives in a similar manner
	 * <p>We output the results of our analysis 
	 */
	public static void main(String args[])
	{
		try{
			long tStart = System.currentTimeMillis();
			BloomFilterDet bfd= new BloomFilterDet(noOfElements,bitsPerElmnts);
			String test; int noOfFalsePositive=0,noOfFalseNegatives=0;

			System.out.println("Data load starts ....");
			for(int k=0;k<1000000;k++)
			{
				test="myName"+k;
				bfd.add(test);
			}
			System.out.println("Data load ends ....");

			System.out.println("Starting to check false positives in it ....");
			for(int i=1000000;i<2000000;i++)
			{
				boolean res=bfd.appears("myName"+i);
				if(res)
					noOfFalsePositive++;
			}
			System.out.println("End of check of false positives in it ....");

			System.out.println("Starting to check false negative in it ....");
			for(int i=0;i<1000000;i++)
			{
				boolean res=bfd.appears("myName"+i);
				if(!res)
					noOfFalseNegatives++;
			}
			System.out.println("End of check of false negative in it ....");
			long tEnd = System.currentTimeMillis();
			long tDelta = tEnd - tStart;
			double elapsedMin = tDelta / 60000.0;

			System.out.println("Bits per element : "+ bitsPerElmnts);
			System.out.println("False Positives : " + noOfFalsePositive+" out of "+ noOfElements + " data");
			System.out.println("False Negatives : " + noOfFalseNegatives+" out of "+ noOfElements + " data");
			System.out.println("Data entered into the Filter : " +bfd.dataSize());
			System.out.println("Size of filter : " +bfd.filterSize());
			System.out.println("Number of hash functions : " +bfd.numHashes());
			System.out.println("Time elapsed : "+  (int)elapsedMin + " minutes");
		}catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
