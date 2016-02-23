package com.programming.assignment1;

/*
 * FalsePositives class checks the number of prabability of False Positives in our Bloom Filter
 * <p>Logic:
 * <p>We add 1000000 elements in the Bloom Filter.
 * <p>We check another 1000000 elements (those are not put into the filters) to check if the filters returns true
 * <p>Data is synthetic
 * 
 * @author Dipanjan Karmakar
 * @author Gaurav Bhatt
 * 
 */

public class FalsePositives {

	private static final int noOfElements=1000000;
	private static final int bitsPerElmnts=8; 
	
	public static void main(String[] args) {
		try{
			//Start of Deterministic Bloom Filter
			
			System.out.println("Start of Deterministic bloom filter...................................... ");
			long tStart = System.currentTimeMillis();
			BloomFilterDet bfd= new BloomFilterDet(noOfElements,bitsPerElmnts);
			String test; int noOfFalsePositive=0,noOfFalseNegatives=0;
			
			//Put 1000000 data into the Deterministic Bloom Filter
			System.out.println("Data load starts ....");
			for(int k=0;k<1000000;k++)
			{
				test="myName"+k;
				bfd.add(test);
			}
			System.out.println("Data load ends ....");
			
			//Check another 1000000 is that appears in the filter
			System.out.println("Starting to check false positives in it ....");
			for(int i=1000000;i<2000000;i++)
			{
				boolean res=bfd.appears("myName"+i);
				if(res)
					noOfFalsePositive++;
			}
			System.out.println("End of check of false positives in it ....");
			
			//Also check the false negatives using the strings added to the filter
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
			System.out.println("False Positives : " + noOfFalsePositive);
			System.out.println("False Negatives : " + noOfFalseNegatives);
			System.out.println("Data entered into the Filter : " +bfd.dataSize());
			System.out.println("Size of filter : " +bfd.filterSize());
			System.out.println("Number of hash functions : " +bfd.numHashes());
			System.out.println("Time elapsed : "+  (int)elapsedMin + " minutes");
			
			System.out.println("\n\n\n Start of Random bloom filter........................................ ");
			

			tStart = System.currentTimeMillis();
			BloomFilterRan bfr= new BloomFilterRan(noOfElements,bitsPerElmnts);
			System.out.println("Size of filter : " +bfr.filterSize());
			System.out.println("Data load starts ....");
			//Put 1000000 data into the Random Bloom Filter
			for(int k=0;k<1000000;k++)
			{
				test="myName"+k;
				bfr.add(test);
			}
			System.out.println("Data load ends ....");
			System.out.println("Size of filter : " +bfr.filterSize());
			
			//Check another 1000000 is that appears in the filter
			System.out.println("Starting to check false positives in it ....");
			for(int i=1000000;i<2000000;i++)
			{
				boolean res=bfr.appears("myName"+i);
				if(res)
					noOfFalsePositive++;
			}
			System.out.println("End of check of false positives in it ....");
			System.out.println("Size of filter : " +bfr.filterSize());
			
			//Also check the false negatives using the strings added to the filter
			System.out.println("Starting to check false negative in it ....");
			for(int i=0;i<1000000;i++)
			{
				boolean res=bfr.appears("myName"+i);
				if(!res)
					noOfFalseNegatives++;
			}
			System.out.println("End of check of false negative in it ....");
			System.out.println("Size of filter : " +bfr.filterSize());
			tEnd = System.currentTimeMillis();
			tDelta = tEnd - tStart;
			elapsedMin = tDelta / 60000.0;
			
			System.out.println("Bits per element : "+ bitsPerElmnts);
			System.out.println("False Positives : " + noOfFalsePositive);
			System.out.println("False Negatives : " + noOfFalseNegatives);
			System.out.println("Data entered into the Filter : " +bfr.dataSize());
			System.out.println("Size of filter : " +bfr.filterSize());
			System.out.println("Number of hash functions : " +bfr.numHashes());
			System.out.println("Time elapsed : "+  (int)elapsedMin + " minutes");
			
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	

}
