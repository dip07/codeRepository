package com.programming.assignment2;

import java.util.Scanner;

public class MinHashAccuracy {
	static private int noOfPermFromInput ; //=400;
	static private double errParam; //=0.04;
	public static void main(String args[])
	{
		String foldername=""; // ="/Users/dipanjankarmakar/Documents/Isu Google Drive/Isu Studies Google Drive/BigDataAlgo/Assignments/PA2/space";
		
		Scanner sc= new Scanner(System.in);
		do{
			System.out.println("Please enter the folder path : ");
			foldername=sc.nextLine();
		}while(foldername.isEmpty());
		do{
			System.out.println("Please enter the number of permutataions : ");
			noOfPermFromInput=sc.nextInt();
		}while(noOfPermFromInput<=0);
		
		do{
			System.out.println("Please enter the  error param value");
			errParam=sc.nextDouble();
		}while(errParam==0);
		sc.close();
		MinHash minHash; int count=0;
		double apprxJac=0;
		double exactJac=0;
		try {
			minHash = new MinHash(foldername, noOfPermFromInput);

			for(int i=0;i<minHash.allDocs().length-1;i++)
			{
				//System.out.println("i >> " + i);
				for(int j=i+1;j<minHash.allDocs().length;j++)
				{	
					if(i==j)
						continue;
					String file1=minHash.fileList.get(i);
					String file2=minHash.fileList.get(j);
					apprxJac=minHash.approximateJaccard(file1, file2);
					exactJac=minHash.exactJaccard(file1, file2);
					double diff=Math.abs(apprxJac-exactJac);
					if(diff>errParam)
						count++;

				}
			}
			System.out.println("Number of pairs with difference more than error param : " + count);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
