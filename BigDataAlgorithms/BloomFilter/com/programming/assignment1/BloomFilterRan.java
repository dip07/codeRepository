package com.programming.assignment1;

/*
 * BloomFilterRan class use the Random hash functions to create the Bloom Filter
 * <p>We are using a hash function of the following equation for creating the hash function : (ax+b)%p
 * <p> p  	is a prime number greater than the number of elements to be added to the Bloom Filter
 * <p> a	any number {0,1,2,....,p}
 * <p> b	any number {0,1,2,....,p}   
 * <p>For creating the <i>k</i> hash functions we are different a,b for different hash functions
 * 
 * @author Dipanjan Karmakar
 * @author Gaurav Bhatt
 * 
 */
import java.math.BigInteger;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

class BloomFilterRan {
	
	private int filterSize;    		// Size of the filter ~ table size
	private BitSet bloomFilter;		// actual Bloom Filter variable 
	private long noOfHashFn;		// # of hash function to be created
	private long dataSize;			// size of the data that has been added to the Filter
	BigInteger a,b,prime,nextPrime;	
	BigInteger arrayAB[][];			// we store the different a,b in this array
	private static final BigInteger ONE=BigInteger.valueOf(1l);		// Big Integer representation of 1 
	private static final int noOfElements=1000000;					//number of elements to be hashed
	private static final int bitsPerElmnts=8; 						// bits per element
	
	/*
	 * BloomFilter Constructor
	 * @param setSize			size of the input elements to hash
	 * @param bitsPerElement	bits per elements in the bloom filter
	 * @return 					an object of this class
	 */
	BloomFilterRan(int setSize, int bitsPerElement)
	{
		filterSize=setSize*bitsPerElement;
		bloomFilter= new BitSet(filterSize);
		noOfHashFn=(long)(filterSize*(Math.log(2))/setSize);
		arrayAB=new BigInteger[(int)noOfHashFn][2];
		dataSize=0;													// size of the data that has been added to the Filter
		Set<BigInteger> setA=new HashSet<BigInteger>();
		Set<BigInteger> setB=new HashSet<BigInteger>();
		System.out.println("Bloom Filter created of size : "+filterSize);

		prime=BigInteger.valueOf(filterSize).nextProbablePrime();				//probable prime greater than setSize
		
		for(int i=0;i<noOfHashFn;i++)
		{
			do{																	//do not pick 'a' that has already been picked
				a=BigInteger.valueOf((long)(Math.random()*(prime.subtract(ONE).longValue())));
			}while(setA.contains(a));
			setA.add(a);	
			do{																	//do not pick 'b' that has already been picked
				b=BigInteger.valueOf((long)(Math.random()*(prime.subtract(ONE).longValue())));
			}while(setB.contains(b));
			setB.add(b);
		arrayAB[i][0]=a;arrayAB[i][1]=b;										//store the 'a' and 'b' in the array
		}

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
			long hashValue=randomHashOfString(i,s);
			int mappingToSize=(int) (hashValue % bloomFilter.size());
			bloomFilter.set(mappingToSize);
		}
	}
	/* This function creates the hash of the string using hash function of the following equation for creating the hash function : (ax+b)%p 
	 * @param 	i variable to indicate the n th hash function to create
	 * @param 	s string for which we need to create the hash
	 */
	long randomHashOfString(int i,String s)
	{
		BigInteger stringHashCode=BigInteger.valueOf(s.hashCode()).abs();
		BigInteger hashVal=((arrayAB[i-1][0].multiply(stringHashCode)) .add(arrayAB[i-1][1])).mod(prime);
		return hashVal.longValue();
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
			long hashValue=randomHashOfString(i, s);
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
			BloomFilterRan bfr= new BloomFilterRan(noOfElements,bitsPerElmnts);
			String test; int noOfFalsePositive=0,noOfFalseNegatives=0;
			System.out.println("Size of filter : " +bfr.filterSize());
			System.out.println("Data load starts ....");
			for(int k=0;k<1000000;k++)
			{
				test="myName"+k;
				bfr.add(test);
			}
			System.out.println("Data load ends ....");
			System.out.println("Size of filter : " +bfr.filterSize());

			System.out.println("Starting to check false positives in it ....");
			for(int i=1000000;i<2000000;i++)
			{
				boolean res=bfr.appears("myName"+i);
				if(res)
					noOfFalsePositive++;
			}
			System.out.println("End of check of false positives in it ....");
			System.out.println("Size of filter : " +bfr.filterSize());

			System.out.println("Starting to check false negative in it ....");
			for(int i=0;i<1000000;i++)
			{
				boolean res=bfr.appears("myName"+i);
				if(!res)
					noOfFalseNegatives++;
			}
			System.out.println("End of check of false negative in it ....");
			System.out.println("Size of filter : " +bfr.filterSize());
			long tEnd = System.currentTimeMillis();
			long tDelta = tEnd - tStart;
			double elapsedMin = tDelta / 60000.0;

			System.out.println("Bits per element : "+ bitsPerElmnts);
			System.out.println("False Positives : " + noOfFalsePositive);
			System.out.println("False Negatives : " + noOfFalseNegatives);
			System.out.println("Data entered into the Filter : " +bfr.dataSize());
			System.out.println("Size of filter : " +bfr.filterSize());
			System.out.println("Number of hash functions : " +bfr.numHashes());
			System.out.println("Time elapsed : "+  (int)elapsedMin + " minutes");

		}catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
}
