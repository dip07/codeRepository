package com.neo.communityDetect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
/**
* <h1>Overlapping community detection in social network using seed expansion</h1>
* In this project we take the social graph as input (may be weighted or un-weighted)
* The inputs to the project are 
* <p>
* <b>Note:</b> We have to give proper input in the argument to the program.
*
* @author  Arjun Bhattacharya
* @author  Dipanjan Karmakar
* @author  Bijon Kumar Bose
* @version 1.0
* @since   2015-12-11
*/
public class OverlappingCommunityDetectionModifiedFinal {

	static  int N;
	static String delimeter=" ";
	static int status[];
	static boolean isFirstIteration=true;
	static int adjMat[][];
	static int E;
	static int recursionCount;
	static int communitiesToDetect;
	static boolean isWeighted=false;
	static boolean isDataStartFromZero=false;
	
	static int nodeFrequency[];
	static ArrayList<SortedSet<Integer>> listOfComm= new ArrayList<SortedSet<Integer>>();
	static boolean isPrintToConsole=false;
	static String [][]dataSetList={{"/Users/dipanjankarmakar/Documents/ZacharyKarateGraph.txt","34"}, // starts with 1
						{"/Users/dipanjankarmakar/Documents/PowerGridDataset.txt","4941"},			  // starts with 1	
						{"/Users/dipanjankarmakar/Documents/DolphinSocialNetwork.txt","62"},		// starts with 1
						{"/Users/dipanjankarmakar/Documents/ZacharyKarateClubWt.txt","34"},			// starts with 1
						{"/Users/dipanjankarmakar/Documents/facebook_combined.txt","4039"},			// starts with 0
						{"/Users/dipanjankarmakar/Documents/CA_GRQ_Modified.txt","5242"},			// starts with 0
						{"/Users/dipanjankarmakar/Documents/CA_Hep_Th_Modified.txt","9877"}			//starts with 0
						};	
	
	/**
	 * These variables will be needed for weighted dataset only
	 */
	static float avgDeg[],avgRep[],qualSeeds[];
	static int seedRanks[];
	
	/**
	 * The main function for  the program
	 * @param args
	 * In the argument we have to provide the following details
	 * <p>dataSet  the file number from which the input is to be picked up</p>
	 * <p>isWt  if the input dataset file if weighted or not (w in case of weighted graph)</p>
	 * <p>isDataStartFromZero  if the input dataset starts from 0 or not , the adjacency matrix is to be loaded as per  (y if dataset starts from 0)</p>
	 */
	public static void main(String[] args) {

		int dataSet=-1;
		try{
			if(args.length!=3)
			{
				System.out.println("Required number of arguments not passed");
				throw new Exception("Invalid input");
			}

			dataSet=Integer.valueOf(args[0]);
			System.out.println("DataSet number: " + dataSet);

			String isWt=args[1];
			System.out.println("IsWeighted : " + isWt);
			if(isWt.toLowerCase().compareToIgnoreCase("w")==0)
				isWeighted=true;

			String _isDataStartFromZero=args[2];
			System.out.println("isDataStartFromZero: " + _isDataStartFromZero);

			if(dataSet<0 || (dataSet>dataSetList.length-1))
			{
				System.out.println("DataSet number out of range");
				throw new Exception("Invalid input");
			}
			if(!((_isDataStartFromZero.compareToIgnoreCase("y")==0)||(_isDataStartFromZero.compareToIgnoreCase("n")==0)))
			{
				System.out.println("Please input correct input for whether data starts from 0 or not (y/n) ");
				throw new Exception("Invalid input");
			}
			if(_isDataStartFromZero.toLowerCase().compareToIgnoreCase("y")==0)
				isDataStartFromZero=true;


		}catch(Exception ex)
		{
			System.out.println("Error in input to program");
			ex.printStackTrace();
			System.exit(0);

		}
		try{

			int toDetCom=1;
			String filePath=dataSetList[dataSet][0];
			N=Integer.valueOf(dataSetList[dataSet][1]);
			System.out.println("FilePath >> " +filePath);
			System.out.println("Number of Nodes >> " + N);

			initVariables();

			populateMatrix(adjMat,filePath);

			if(isWeighted)
			{
				avgDeg = getDegree(adjMat);
				avgRep = getRep(adjMat);
				/*for(int i = 0; i<N; i++)
					System.out.println("Average Degree of " + i +"th Node: " + avgDeg[i]);
				for(int i = 0; i<N; i++)
					System.out.println("Average Reputation of " + i +"th Node: " + avgRep[i]);*/
				for(int i = 0; i<N; i++)
				{
					qualSeeds[i] = avgDeg[i]*avgRep[i]*10000000;
				}
				Scanner pt=new Scanner(System.in);
				seedRanks = getSeedRank(qualSeeds);
				//System.out.println("SeedRanks >> " + Arrays.toString(seedRanks));
			}
			Scanner ab=new Scanner(System.in);
			E=getTotalNumberOfEdges(adjMat);
			System.out.println("Total number of edges : "+ E);
			//printAdjMat(adjMat);		print Adjacency matrix if required
			System.out.println("Enter the number of cummunity to be detected : ");
			Scanner sc=new Scanner(System.in);
			toDetCom=Integer.parseInt(sc.nextLine());
			if(isPrintToConsole)
				System.out.println("We have to detect number of community: "+ toDetCom);
			Date startDateTime= new Date();

			if(!isWeighted)
				processCommunityDetection(adjMat,toDetCom);
			else
				processCommunityDetectionWeighted(adjMat, toDetCom, seedRanks);
			Date endDateTime= new Date();
			System.out.println("Final Modularity : "+getModularity(listOfComm) +" & final list of Community : ");
			printCommunities(listOfComm);
			//printNodeCoverage(listOfComm);   print node coverage if needed
			//printAdjacencyMatrixForCommunities(listOfComm);         print Adjacency Matrix for Communities if needed
			long diffSeconds = (endDateTime.getTime()-startDateTime.getTime()) ;
			System.out.println("Start TimeStamp >> "+ startDateTime);
			System.out.println("Start TimeStamp >> "+ endDateTime);
			long diffTime=diffSeconds/1000;
			System.out.println("Execution time : "+ diffTime+" sec");
			System.out.println("Final Modularity : "+getModularity(listOfComm));
			sc.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	/**
	   * This method initializes all required variables 
	   * N being static, so, available to all the methods 
	   */
	private static void initVariables() 
	{
		adjMat=new int[N][N];
		status= new int[N];
		avgDeg = new float[N];
		avgRep = new float[N];
		qualSeeds = new float[N];
		seedRanks = new int[N];
		nodeFrequency=new int[N];
	}	
	
	/**
	   * This method process the main community detection method  for unweighted graphs
	   * @param adjMat		the adjacency matrix
	   * @param toDetCom 	the number of community to be detected
	   */
	private static void processCommunityDetection(int adjMat[][],int toDetCom)
	{
		boolean isUnvisitedNodeLeft=false;
		do{
			communitiesToDetect=0;
			do{
				communitiesToDetect++;
				int maxDegree=0;int maxDegreeRow=-1;
				for(int i=0;i<N;i++)
				{
					int localMaxDegree=0;
					if(status[i]==0)		// 0 means unvisited
					{
						if(!isFirstIteration && isPrintToConsole)
						{
							System.out.println("Unvisited nodes : "+i );
						}
						for(int j=0;j<N;j++)
						{
							if(adjMat[i][j]!=0 && status[j]==0)
							{
								localMaxDegree++;
							}
						}
						if(localMaxDegree>=maxDegree)
						{
							maxDegree=localMaxDegree;
							maxDegreeRow=i;
						}
					}
				}
				if(isPrintToConsole)
					System.out.println("Node with max degree : "+maxDegreeRow +" with degree "+ maxDegree);
				if(maxDegreeRow==-1)
				{
					if(isFirstIteration)
						System.out.println("We will not be able to detect "+ toDetCom +" number of communities with this method");
					return;
				}
				ArrayList<Integer> currentComm= getAdjacentNodes(adjMat,maxDegreeRow);
				currentComm.add(maxDegreeRow);
				for(Integer node:currentComm)
				{
					status[node]=1;
				}
				if(isFirstIteration)
				{
					SortedSet<Integer> tempComp=new TreeSet<Integer>(currentComm);
					listOfComm.add(tempComp);
				}
				else
				{
					ArrayList<Integer> toMergeToComm= getToWhichCommunityToMergeOverlapping(currentComm);
					for(int p:toMergeToComm)
					{
						listOfComm.get(p).addAll(currentComm);
					}

				}
			}while(communitiesToDetect<toDetCom);
			isFirstIteration=false;

			float modularity=getModularity(listOfComm);
			//if(isPrintToConsole)
				System.out.println("Current modularity  for recursion  "+(++recursionCount)+" is "+modularity);

			isUnvisitedNodeLeft=checkStatus();
		}while(isUnvisitedNodeLeft);

	}
	
	/**
	   * This method takes input of a temporary community and finds to which community it should be merged such that the modularity is increased
	   * <p>This method can be used in case we do not want to detect overlapping communities</p>  
	   * @param currentComm		the temporary community
	   * @return the community to which it should be merged 
	   */
	private static int getToWhichCommunityToMerge(ArrayList<Integer> currentComm) 
	{	
		int toBeMergedTo=0;float maxModularity=-99f;
		for(int i=0;i<listOfComm.size();i++)
		{
			SortedSet<Integer> singleComm=listOfComm.get(i);
			boolean isPathExistBet=isPathExists(adjMat,singleComm,currentComm);
			if(!isPathExistBet)
			{
				if(isPrintToConsole)
					System.out.println("Path does not exists between " + currentComm +"  & " + singleComm);
				continue;
			}
			else if(isPrintToConsole)
			{
				System.out.println("Path exists between " + currentComm +"  & " + singleComm);
			}
			ArrayList<SortedSet<Integer>> templistOfComm= new ArrayList<SortedSet<Integer>>(listOfComm);
			templistOfComm.get(i).addAll(currentComm);
			float tempMod=getModularity(templistOfComm);
			if(isPrintToConsole)
				System.out.println("Tmp modul for i=" + i + " is >> "+tempMod);
			if(tempMod>maxModularity)
			{
				toBeMergedTo=i;
				maxModularity=tempMod;
			}
		}
		return toBeMergedTo;
	}
	
	/**
	   * This method takes input of a temporary community and finds to which communities it should be merged such that the modularity is increased
	   * @param currentComm the temporary community
	   * @return the arrayList of communities to which it should be merged 
	   */
		private static ArrayList<Integer> getToWhichCommunityToMergeOverlapping(ArrayList<Integer> currentComm) 
		{	
			ArrayList<Integer> toBeMergedTo=new ArrayList<Integer>();
			for(int i=0;i<listOfComm.size();i++)
			{
				SortedSet<Integer> singleComm=listOfComm.get(i);
				boolean isPathExistBet=isPathExists(adjMat,singleComm,currentComm);
				if(!isPathExistBet)
				{
					if(isPrintToConsole)
						System.out.println("Path does not exists between " + currentComm +"  & " + singleComm);
					continue;
				}
				else if(isPrintToConsole)
				{
					System.out.println("Path exists between " + currentComm +"  & " + singleComm);
				}
				//ArrayList<SortedSet<Integer>> templistOfComm= new ArrayList<SortedSet<Integer>>(listOfComm);
				ArrayList<SortedSet<Integer>> templistOfComm= new ArrayList<SortedSet<Integer>>();
				for(SortedSet<Integer> temp:listOfComm)	// perform deep copy
				{
					SortedSet<Integer> tp= new TreeSet<Integer>();
					for(Integer t:temp)
					{
						tp.add(t);
					}
					templistOfComm.add(tp);
				}

				float initialModul=getModularity(templistOfComm);
				templistOfComm.get(i).addAll(currentComm);
				float tempMod=getModularity(templistOfComm);
				if(isPrintToConsole)
					System.out.println("Initial modularity for i=" + i + " is >> "+initialModul+ " tempModularity >> " + tempMod);
				if(tempMod>initialModul)
				{
					toBeMergedTo.add(i);
				}
			}
			return toBeMergedTo;
		}
	
	/**
	 * This method checks if a path exists between two communities
	 * 
	 * @param adjMat the 	adjacency matrix
	 * @param community1	one of the two communities between which we need to check if path exists 
	 * @param community2	one of the two communities between which we need to check if path exists
	 * @return true if path exists between the two communities
	 */
	static boolean isPathExists(int adjMat[][],SortedSet<Integer> community1, ArrayList<Integer> community2)
	{
		boolean result=false;
		for(int a:community1)
		{
			for(int b:community2)
			{
				if(adjMat[a][b]!=0)
				{
					result=true;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * This method get the adjacent nodes for a node that has not been put inside a community
	 * @param adjMat 	the adjacency matrix
	 * @param node 		the node for which we need to get the adjacency list
	 * @return list of Adjacent nodes for the input node
	 */
	static ArrayList<Integer> getAdjacentNodes(int adjMat[][],Integer node)
	{
		ArrayList<Integer> adjacentList = new ArrayList<Integer>() ;
		for(int i=0;i<N;i++)
		{
			if(adjMat[node][i]!=0 && status[i]==0 )
				adjacentList.add(i);
		}
		return adjacentList;
	}
	
	/**
	 * This method prints the nodes within the community detected
	 * @param listOfComm the communities detected
	 */
	private static void printCommunities(ArrayList<SortedSet<Integer>> listOfComm)
	{
		int number=1;
		for(SortedSet<Integer> outer: listOfComm)
		{
			System.out.print("Nodes in community "+(number++) +" >> ");
			for(Integer node : outer)
			{
				int nodeNum=node;
				System.out.print(nodeNum+" - ");
			}
			System.out.println();
		}
	}
	/**
	 * This method prints the node covered by the detected communities
	 * @param listOfComm the list of communities
	 */
	private static void printNodeCoverage(ArrayList<SortedSet<Integer>> listOfComm)
	{
		int number=1,index[]=new int[N],countNodeCovered=0;
		for(SortedSet<Integer> outer: listOfComm)
		{
			for(Integer node : outer)
			{
				nodeFrequency[node]++;
			}
		}
		for(int i=0;i<N;i++)
			index[i]=i;
		for(int i=0;i<N-1;i++)
		{
			
			for(int j=0;j<N-i-1;j++)
			{
				if(nodeFrequency[i]>nodeFrequency[j])
				{
					int temp=nodeFrequency[i];
					nodeFrequency[i]=nodeFrequency[j];
					nodeFrequency[j]=temp;
					
					temp=index[i];
					index[i]=index[j];
					index[j]=temp;
					
				}
			}
		}
		for(int i=0;i<N;i++)
			if(nodeFrequency[i]!=0)
				countNodeCovered++;
		System.out.println("Node "+index[0] +" has highest frequency of "+ nodeFrequency[0]);
		System.out.println("Percentage of nodes covered : " +((float)(countNodeCovered/N*100)));
		System.out.println(Arrays.toString(nodeFrequency));
		System.out.println(Arrays.toString(index));
		
	}
	
	/**
	 * This method check whether there is any node left that has not been put into any community 
	 * @return true in case any node has not been put into any community
	 */
	static boolean checkStatus()
	{
		/*System.out.print("Status :\n");
		for(int i=0;i<N;i++)
		{
		if(i<10)
			System.out.print(i+" - ");
		else
			System.out.print(i+"- ");
		}
		System.out.println();
		for(int i=0;i<N;i++)
			System.out.print(status[i]+" - ");
		System.out.println();*/
		for(int i=0;i<N;i++)
		{
			if(status[i]==0)
				return true;
		}
		//System.out.println();
		return false;
	}
	
	
	/**
	   * This method takes take the data from the file and populate the adjacency matrix
	   * @param adjMat 		the method will populate to this matrix
	   * @param filePath 	the file path from which the data is to be fetched
	   */
	private static void populateMatrix(int adjMat[][],String filePath) throws Exception
	{
		try{
			FileReader reader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				String []edge=line.split(delimeter);
				Integer node1=Integer.parseInt(edge[0]),node2=Integer.parseInt(edge[1]);
				if(node1!=null && node2!=null)
				{
					if(!isWeighted)
					{
						if(!isDataStartFromZero)
						{
							adjMat[node1-1][node2-1]=1;
							adjMat[node2-1][node1-1]=1;
						}
						else{
							adjMat[node1][node2]=1;
							adjMat[node2][node1]=1;
						}
					}
					else{
						Integer weight=Integer.parseInt(edge[2]);
						if(isDataStartFromZero)
						{
							adjMat[node1][node2]=weight;
							adjMat[node2][node1]=weight;
						}
						else{
							adjMat[node1-1][node2-1]=1;
							adjMat[node2-1][node1-1]=1;
						}
					}
				}
			}
			reader.close();
		}catch(Exception e)
		{
			if(isPrintToConsole)
			{
				System.out.println("Error while reading input file >> ");
			}
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(0);
		}

	}
	
	
	/**
	   * This method calculates the modularity of the communities
	   * @param listOfComm 		list of communities detected
	   * @return 				the modularity for the graph with current partition
	   */
	private static float getModularity(ArrayList<SortedSet<Integer>> listOfComm)
	{
		float modul=0f,tempModul=0f;
		for(SortedSet<Integer> comm:listOfComm)
		{
			int Ein=0,Eout=0;
			Ein=getNoOfEdgesInCommunity(comm);
			Eout=getNoOfEdgesOutOfCommunity(comm);
			if(isPrintToConsole)
				System.out.println("Ein >> "+ Ein +" Eout >> "+Eout+" E >> "+E);
			float firstTerm=(float)Ein/E;
			float secondterm=((float)( (2*Ein)  +Eout )/ (2*E));
			if(isPrintToConsole)
				System.out.println("FirstTerm >> "+firstTerm +" secondterm >> "+ secondterm);
			tempModul=firstTerm - (float)Math.pow((secondterm),2);
			modul+=tempModul;
			if(isPrintToConsole)
				System.out.println("Temp Modul >"+tempModul +" Overall Modul >> "+ modul);
		}
		return modul;
	}
	
	/**
	   * This method calculates the number of edges from to community to the outside world
	   * @param community 		the community for which to calculate
	   * @return 				the number of edges out of the community
	   */
	private static int getNoOfEdgesOutOfCommunity(SortedSet<Integer> community) 
	{
		SortedSet<Integer> fullComm=new TreeSet<Integer>();
		SortedSet<Integer> remainingComm=new TreeSet<Integer>();
		for(int i=0;i<N;i++)
		{
			fullComm.add(i);
		}
		remainingComm.addAll(fullComm);
		remainingComm.removeAll(community);
		//System.out.println("Remaining Community : " +remainingComm);
		int noOfEdges=0;
		for(Integer i:community)
		{
			for(Integer j:remainingComm)
			{
				if(adjMat[i][j]!=0)
					noOfEdges++;
			}
		}
		
		
		return noOfEdges;
	}
	
	/**
	   * This method prints the Adjacency matrix for the detected communities
	   * <p>can be used for analyzing purpose</p> 
	   */
	private static void printAdjacencyMatrixForCommunities(ArrayList<SortedSet<Integer>> listOfComm2) 
	{
		for(SortedSet<Integer> outer: listOfComm)
		{
			System.out.print("Nodes in community :>> " + outer+"\n");
			for(Integer node1 : outer)
			{
				for(Integer node2 : outer)
				{
						if(node1==node2)
							System.out.println("_  ");
						System.out.print(adjMat[node1][node2] + "  ");
				}
				System.out.println();
			}
			System.out.println();
		}
		
	}
	
	/**
	   * This method calculates the number of edges inside the community
	   * @param community 		the community in current context for which no of edges is to be calculated
	   * @return 				the number of edges in the community
	   */
	static int getNoOfEdgesInCommunity(SortedSet<Integer> community) 
	{
		//System.out.println("Calculating NoOfEdgesInCommunity for >> "+ community);
		int noOfEdges=0;
		for(Integer i:community)
		{
			for(Integer j:community)
			{
				if(i==j)
					continue;
				if(adjMat[i][j]!=0)
					{
					//System.out.println("Found edge between "+i +" and " + j);
					noOfEdges++;
					}
			}
		}
		return (int)(noOfEdges/2);
	}
	
	/**
	   * This method prints input Adjacency matrix
	   * @param matrix 		the adjacency matrix
	   */
	private static void printAdjMat(int matrix[][])
	{
		for(int i=0;i<N;i++)
		{
			for(int j=0;j<N;j++)
			{
				System.out.print(matrix[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	/**
	   * This method calculates the total number of edges in the graph
	   * @param adjMat 		the adjacency matrix
	   * @return 			the total number of edges
	   */
	static int getTotalNumberOfEdges(int adjMat[][])
	{
		int edges=0;
		for(int i=0;i<N;i++)
		{
			for(int j=0;j<i;j++)
			{
				if(adjMat[i][j]!=0)
					edges++;
			}
		}
		return edges;
	}
	
	
	/**
	   * This method calculates the degree of each node
	   * @param adjMat 		the adjacency matrix
	   * @return 			the average degree of each node
	   */
	static float[] getDegree(int adjMat[][])
	{
		float totalDegree = getTotalDegree(adjMat);
		System.out.println("Total Degree :" + totalDegree);
		float degree[] = new float[N];
		for(int i = 0; i<N; i++)
		{
			float deg = 0;
			for(int j=0;j<N;j++)
			{
				if(adjMat[i][j]!=0)
					deg++;
			}
			degree[i] = deg/totalDegree;
		}
		return degree;
	}
	
	/**
	   * This method calculates the total degree of the graph
	   * @param adjMat 		the adjacency matrix
	   * @return 			the sum of degrees for all the nodes of the graph
	   */
	static int getTotalDegree(int adjMat[][])
	{
		int totDeg = 0;
		for(int i = 0; i<N; i++)
		{
			for(int j=0;j<N;j++)
			{
				if(adjMat[i][j]!=0)
					totDeg++;
			}
		}
		return totDeg;
	}
	
	/**
	   * This method calculates the total reputation of the graph
	   * @param adjMat 		the adjacency matrix
	   * @return 			total reputation for all the nodes of the graph
	   */
	static int getTotalRep(int adjMat[][])
	{
		int totalRep = 0;
		for(int i = 0; i<N; i++)
		{
			for(int j=0;j<i;j++)
			{
				if(adjMat[i][j]!=0)
					totalRep += adjMat[i][j] ;
			}
		}
		return totalRep;
	}
	
	/**
	   * This method calculates the reputation of individual nodes
	   * @param adjMat 		the adjacency matrix
	   * @return 			the reputation of the nodes
	   */
	static float[] getRep(int adjMat[][])
	{
		float totalRep = getTotalRep(adjMat);
		float rep[] = new float[N];
		for(int i = 0; i<N; i++)
		{
			float rept = 0;
			for(int j=0;j<N;j++)
			{
				if(adjMat[i][j]!=0)
					rept += adjMat[i][j] ;
			}
			rep[i] = rept/totalRep;
		}
		return rep;
	}
	/**
	   * This method calculates the rank of all the seeds of the input graph
	   * @param qualSeed 	the list of seeds
	   * @return 			the array of seeds in order of their rank
	   */
	static int[] getSeedRank(float qualSeed[])
	{
		int ranks[]=new int[N];
		for(int i=0;i<N;i++)
			ranks[i]=i;
		for(int i=0;i<N;i++)
		{
			for(int j=i;j<N;j++)
			{
				Float a=qualSeed[i];
				Float b=qualSeed[j];
				if(a.compareTo(b)<0)
				{
					float tmp=qualSeed[i];
					qualSeed[i]=qualSeed[j];
					qualSeed[j]=tmp;
					
					int t=ranks[i];
					ranks[i]=ranks[j];
					ranks[j]=t;
				}
			}
		}
		return ranks;
	}
	/**
	 * This method process the main community detection method  for weighted graphs
	 * @param adjMat the adjacency matrix
	 * @param toDetCom number of communities to be detected
	 * @param seedRanks the rank of the seed nodes
	 */
	private static void processCommunityDetectionWeighted(int adjMat[][],int toDetCom, int[] seedRanks) 
	{
		boolean isUnvisitedNodeLeft=false;
		do{
			communitiesToDetect=0;
			do{
				communitiesToDetect++;
				int maxDegree=0;int maxDegreeRow=-1,seed;
				for(int i=0;i<N;i++)
				{
					seed=seedRanks[i];
					if(status[seed]==0)		// 0 means unvisited
					{
						maxDegreeRow=seed;
						break;
						
					}
				}
				System.out.println("Node with max degree : "+maxDegreeRow +" with degree "+ maxDegree);
				if(maxDegreeRow==-1)
				{
					System.out.println("We will not be able to detect "+ toDetCom +" number of communities with this method");
					return;
				}
				ArrayList<Integer> currentComm= getAdjacentNodes(adjMat,maxDegreeRow);
				currentComm.add(maxDegreeRow);
				for(Integer node:currentComm)
				{
					status[node]=1;
				}
				if(isFirstIteration)
				{
					SortedSet<Integer> tempComp=new TreeSet<Integer>(currentComm);
					listOfComm.add(tempComp);
				}
				else
				{
					ArrayList<Integer> toMergeToComm= getToWhichCommunityToMergeOverlapping(currentComm);
					for(int p:toMergeToComm)
					{
						listOfComm.get(p).addAll(currentComm);
						System.out.println("Community " + Arrays.asList(currentComm) +" is merged with " + p);
					}

				}
			}while(communitiesToDetect<toDetCom);
			isFirstIteration=false;

			float modularity=getModularity(listOfComm);
			System.out.println("Current modularity  for recursion  "+(++recursionCount)+" is "+modularity);
			
			isUnvisitedNodeLeft=checkStatus();
		}while(isUnvisitedNodeLeft);

	}

}
