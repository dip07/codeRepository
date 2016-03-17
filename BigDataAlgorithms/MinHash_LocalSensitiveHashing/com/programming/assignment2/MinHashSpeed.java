package com.programming.assignment2;

/*
 *  MinHashSpeed class will check the speed of calculation of exact Jaccard similarity vs that of approximate Jaccard similarity 
 * @author Dipanjan Karmakar
 * @author Gaurav Bhatt
 */

import java.util.Scanner;

public class MinHashSpeed {
	static private int noOfPermFromInput;

	/*
	 * In the main method we do the actual calculation
	 * <p> We take the folder path, number of permutations as input from the user and the do the calculations 
	 */
	public static void main(String[] args) 
	{

		String foldername;
		// ="/Users/dipanjankarmakar/Documents/Isu Google Drive/Isu Studies Google Drive/BigDataAlgo/Assignments/PA2/space";
		MinHash minHash; 
		try {
			Scanner sc= new Scanner(System.in);
			do{
				System.out.println("Please enter the folder path : ");
				foldername=sc.nextLine();
			}while(foldername.isEmpty());		// do not allow empty input
			do{
				System.out.println("Please enter the number of permutataions : ");
				noOfPermFromInput=sc.nextInt();
			}while(noOfPermFromInput<=0);		// do not allow non negative numbers
			
			minHash = new MinHash(foldername, noOfPermFromInput);	// generate the minHash object
			int toCount=minHash.allDocs().length;

			long tStart = System.currentTimeMillis();		// start counting time

			for(int i=0;i<toCount-1;i++)
			{
				for(int j=i+1;j<toCount;j++)
				{	
					if(i==j)
						continue;
					String file1=minHash.fileList.get(i);
					String file2=minHash.fileList.get(j);
					minHash.exactJaccard(file1, file2);		// calculate exact Jaccard similarity

				}
			}
			long tEnd = System.currentTimeMillis();		// stop the counter
			long tDelta = tEnd - tStart;
			double elapsedSec = tDelta;
			System.out.println("Time elapsed for exact jaccard : "+  (int)elapsedSec + " millisec");

			tStart = System.currentTimeMillis();		// start the timer again
			for(int i=0;i<toCount-1;i++)
			{
				for(int j=i+1;j<toCount;j++)
				{	
					if(i==j)
						continue;
					String file1=minHash.fileList.get(i);
					String file2=minHash.fileList.get(j);
					minHash.approximateJaccard(file1, file2);	// calculate exact Jaccard similarity

				}
			}
			tEnd = System.currentTimeMillis();		// stop the counter
			tDelta = tEnd - tStart;
			elapsedSec = tDelta;
			System.out.println("Time elapsed for approx jaccard : "+  (int)elapsedSec + " millisec");	// print the time
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
