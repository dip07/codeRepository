/*
 * PageRank class will take the Web Graph generated from the WikiCrawler
 * and calculate the page ranks of the nodes(web pages)
 * @author: Dipanjan Karmakar
 * @author: Gaurav Bhatt
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class PageRank {
	
	HashMap<String, ArrayList<String>> curr_list;
	HashMap<String, Integer>  fileIndex;
	HashMap<Integer, ArrayList<Integer>> adjList; 
	HashMap<String, Integer>  inDegree;
	HashMap<String, Integer> outDegree;
	HashMap<String, Float> pageRankMap;
	static int noOfNodes;
	int numedges=0;
	Float beta = 0.85f;
	Float pNVector[]=null;
 	
	/*
	 * PageRank constructor returns an object of PageRank class
	 * @param 	fileName	the filename that stores the edges
	 * @param	epse		the approximation parameter
	 * @return 	the object for this class
	 */
	public PageRank(String fileName, Float epsi ){
		
		Path file = Paths.get(fileName);
		curr_list = new LinkedHashMap<String, ArrayList<String>>();
		adjList = new LinkedHashMap<Integer,ArrayList<Integer>>();
		fileIndex  = new LinkedHashMap<String, Integer>();
		outDegree = new LinkedHashMap<String,Integer>();
		inDegree = new LinkedHashMap<String,Integer>();
		
		
		try (InputStream in = Files.newInputStream(file);
				BufferedReader reader =	new BufferedReader(new InputStreamReader(in))) {
			String line = null; 
			boolean isFirst=true;
			int fileIdxCount=0;
			

	    	while ((line = reader.readLine()) != null) {
		    	if(isFirst){
		    		isFirst=false;
		    		continue;
		    	}
	    		numedges++;
	    		String strArr[]=line.split(" ");
		    	String fromNode=strArr[0].trim();
		    	String toNode=strArr[1].trim();
		    	
		    	// Fill the fileIndex hashmap which maps node to an index.
		    	if(!fileIndex.keySet().contains(fromNode))
		    		fileIndex.put(fromNode, fileIdxCount++);
		    	if(!fileIndex.keySet().contains(toNode))
		    		fileIndex.put(toNode, fileIdxCount++);
		    				    	
		    	ArrayList<String> toNodeList;
		    	// Fill the curr_list which is a hashmap of nodes and its outDegrees
		    	if(!curr_list.keySet().contains(fromNode))
		    	{
		    		toNodeList=curr_list.get(fromNode);
		    		if(toNodeList==null) {
		    			toNodeList=new ArrayList<String>();
		    		}
		    		toNodeList.add(toNode);
		    		curr_list.put(fromNode, toNodeList);
		    	} 
		    	else {
		    		toNodeList=curr_list.get(fromNode);
		    		toNodeList.add(toNode);
		    		curr_list.put(fromNode, toNodeList);
		    	}
		    	
		    	// Fill outDegree
		    	if(!outDegree.containsKey(fromNode)){
		    		outDegree.put(fromNode, 1);
		    	} 
		    	else {
		    		outDegree.put(fromNode,(outDegree.get(fromNode)+1));
		    	}
		    	
		    	//Fill InDegree
		    	if (!inDegree.containsKey(toNode)){
		    		inDegree.put(toNode, 1);
		    	} 
		    	else {
		    		inDegree.put(toNode,(inDegree.get(toNode)+1));
		    	}
	    	}
	    	
	    	//print for debugging
	    	//for (String key : curr_list.keySet()){
	    		//System.out.println(key +" : "+curr_list.get(key));	
	    		//System.out.println(key +" : "+outDegree.get(key));
	    		//System.out.println(key +" : "+inDegree.get(key));
	    	//}
	    	
	    	/* Calculate the pageRank */
	    	calculatePageRank(epsi);
			
		} catch (IOException e) {
			e.printStackTrace();
		}			
		
	}
	
	
	/* Calculate the pageRank */
	public void calculatePageRank(Float epsi) {
		noOfNodes = fileIndex.size();
		int N = fileIndex.keySet().size();
		Float pZeroVector[] = new Float[noOfNodes],iniVal=(float)1/noOfNodes,pN1Vector[];
		boolean converged=false;
		Arrays.fill(pZeroVector,iniVal);
		pNVector=pZeroVector;
		int steps = 0;
		while(!converged)
		{
			steps++;
			pN1Vector=innerFunc(pNVector);
			if(norm(pN1Vector,pNVector)<=epsi) {
				converged=true;
			}			
			pNVector=pN1Vector;
		}
		System.out.println("Number of steps page rank algorithm took to converge is: "+steps);
		System.out.println("Page Ranks :) >>");
		pageRankMap = new HashMap<String, Float>();
		for(String file:fileIndex.keySet()) {
			pageRankMap.put(file, pNVector[fileIndex.get(file)]);
			//	System.out.println(file+"~" +String.format("%.4f",pNVector[fileIndex.get(file)]));
		}
	}

	/* Calculate the Norm of two vectors */
	private Float norm(Float[] pN1Vector, Float[] pNVector) {
		float diff=0f;
		for(int i=0;i<fileIndex.size();i++)
		{
			diff+=Math.abs(pN1Vector[i]-pNVector[i]);
		}
		return diff;
	}

	/* Algorithm to calculate the P(n+1) vector using P(n) */
	private Float[] innerFunc(Float[] pNVector) 
	{
		noOfNodes = fileIndex.size();
		Float pN1Vector[]=new Float[noOfNodes];
		float iniVal=(float)(1-beta)/noOfNodes;
		Arrays.fill(pN1Vector, iniVal);
		for(String linkP:fileIndex.keySet())
		{
			ArrayList<String> outNodesList=curr_list.get(linkP);
			if(outNodesList!=null && outNodesList.size()>0)
			{
				int p=-1,q=-1;
				for(String page:outNodesList)
				{
					try{
						q=fileIndex.get(page);
						p=fileIndex.get(linkP);
					}catch(Exception e)
					{
						System.out.println("page >> " + page);
						e.printStackTrace();
						System.exit(1);
					}
					pN1Vector[q]=pN1Vector[q]+ (beta*pNVector[p]/outNodesList.size());
				}
			}
			else{
				for(String lowerPage:fileIndex.keySet())
				{
					int q=fileIndex.get(lowerPage);
					int p=fileIndex.get(linkP);
					pN1Vector[q]=pN1Vector[q]+ (beta*pNVector[p]/fileIndex.size());
					
				}
			}
		}
		
		return pN1Vector;
	}

	/* Method to get PageRank of a vertex */
	Float pageRankOf(String vertex){
		Float pageRank;
		pageRank = pageRankMap.get(vertex);
		return pageRank;
	}
	
	/* Method which returns the outDegree of a given vertex */
	public int outDegreeOf(String vertex){
        Integer out=0;
        out = outDegree.get(vertex);
        if(out!=null)
        	return out;
        else
        	return 0;
	}
	
	/* Method which returns the inDegree of  the given vertex */
	public int inDegreeOf(String vertex){
		Integer in =0;
		in = inDegree.get(vertex);
		if(in!=null)
			return in;
		else
			return 0;
	}
	
	/* Returns total Number of edges*/
	public int numEdges(){
		return numedges;
	}
	
	
	// Returns top k pagerank pages
	@SuppressWarnings("unchecked")
	public String[] topKPageRank(int k) {
		ArrayList<String> pageRankList=new ArrayList<String>();
		int i=0;
		Float temp=0f;
		Object[] a = pageRankMap.entrySet().toArray();
		
	    Arrays.sort(a, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
	            return ((Map.Entry<String, Float>) o2).getValue().compareTo(
	                    ((Map.Entry<String, Float>) o1).getValue());
	        }
	    });
	    for (Object e : a) {
	    	
	    	if(i<k || temp==((Map.Entry<String, Float>) e).getValue()){
	    	  //temp = (((Map.Entry<String, Float>) e).getValue());
	      //  System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
	      //          + ((Map.Entry<String, Integer>) e).getValue());
	        
	        	pageRankList.add(((Map.Entry<String, Float>) e).getKey());
	        	if (temp!=((Map.Entry<String, Float>) e).getValue()){
	        		temp = ((Map.Entry<String, Float>) e).getValue();
	        		i++;
	        	}
	        }
	    }

	    String[] pageRankArr = new String[pageRankList.size()];
	    pageRankArr = pageRankList.toArray(pageRankArr);

		return pageRankArr;
	}
	
	
	//return top k in degree
	@SuppressWarnings("unchecked")
	public String[] topKInDegree(int k){
		ArrayList<String> inDegreeList=new ArrayList<String>();
		int i=0,temp=0;
		Object[] a = inDegree.entrySet().toArray();
		
	    Arrays.sort(a, new Comparator<Object>() {
	        public int compare(Object o1, Object o2) {
	            return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
	                    ((Map.Entry<String, Integer>) o1).getValue());
	        }
	    });
	    for (Object e : a) {
	        //System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
	        //        + ((Map.Entry<String, Integer>) e).getValue());
	        if(i<k || temp==((Map.Entry<String, Integer>) e).getValue()){
	        	inDegreeList.add(((Map.Entry<String, Integer>) e).getKey());
	        	if (temp!=((Map.Entry<String, Integer>) e).getValue()){
	        		temp = ((Map.Entry<String, Integer>) e).getValue();
	        		i++;
	        	}
	        }
	    }
	    
	    String[] inDegreeArr = new String[inDegreeList.size()];
	    inDegreeArr = inDegreeList.toArray(inDegreeArr);
		return inDegreeArr;
	}
	
	
	// Return top k out degrees
	@SuppressWarnings("unchecked")
	public String[] topKOutDegree(int k){
		ArrayList<String> outDegreeList=new ArrayList<String>();
		int i=0,temp=0;
		Object[] a = outDegree.entrySet().toArray();
		
	    Arrays.sort(a, new Comparator<Object>() {
	        public int compare(Object o1, Object o2) {
	            return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
	                    ((Map.Entry<String, Integer>) o1).getValue());
	        }
	    });
	    for (Object e : a) {
	       // System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
	       //         + ((Map.Entry<String, Integer>) e).getValue());
	        if(i<k || temp ==((Map.Entry<String, Integer>) e).getValue()){
	        	outDegreeList.add(((Map.Entry<String, Integer>) e).getKey());
	        	if (temp!=((Map.Entry<String, Integer>) e).getValue()){
	        		temp = ((Map.Entry<String, Integer>) e).getValue();
	        		i++;
	        	}
	        }
	    }
	    String[] outDegreeArr = new String[outDegreeList.size()];
	    outDegreeArr = outDegreeList.toArray(outDegreeArr);
	    
		return outDegreeArr;
	}
	
	
	/* Method to calculate the jaccard similarity of two string arrays */
	public static Float calculate_jack(String[] topIn_100, String[] topOut_100) {
		// TODO Auto-generated method stub
		Float jack = 0f;
		HashSet<String> h1 = new HashSet<>();
		HashSet<String> h2 = new HashSet<>();
		HashSet<String> union = new HashSet<>();
		
	//	System.out.println(topIn_100.length+" "+topOut_100.length);
		
		for(String s : topIn_100){
			h1.add(s);
			union.add(s);
		}
		
		for(String s : topOut_100) {
			h2.add(s);
			union.add(s);
		}
			
		
		h1.retainAll(h2);
		
		jack = (float)h1.size()/union.size();	
		return jack;
	}
	
}
