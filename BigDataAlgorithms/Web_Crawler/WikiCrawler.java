/*
 * WikiCrawler class will crawl specific web pages in en.wikipedia.org
 * <p> We are using BFS search techniques for the search starting with the seed (relative URL) provided as a constructor param   
 * <p> We are pausing for some time after each 100 requests
 * 
 * @author Dipanjan Karmakar
 * @author Gaurav Bhatt
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiCrawler 
{
	final static String BASE_URL="https://en.wikipedia.org";
	String seedUrl,keywords[],fileName;
	int max;
	int waitSec=5; 
	HashSet<String> visited;	
	Set<String> disallowedUrl;
	HashSet<String> allPagesCollected;
	int countConnection;
	LinkedList<ArrayList<String>> finalQ;
	LinkedList<String> tmpQ;
	
	/*
	 * WikiCrawler Constructor
	 * <p>This function creates a WikiCrawler object and returns the object
	 * @param seedUrl		The starting relative Url for the crawler
	 * @param keywords		the keywords that needs to be searched in the wiki page
	 * @param max			the maximum number of pages to be collected
	 * @param fileName		the name of the file in which the edges are to be saved
	 * @return 				an object of this class
	 */
	public WikiCrawler(String seedUrl,String[] keywords,int max, String fileName)
	{
		if(seedUrl.isEmpty())		// check if the seedUrl is empty
		{
			System.out.println("Enter valid seed Url");
			return;
		}
		if(keywords.length<=0)		// check if any keywords have been entered or not
		{
			System.out.println("Please enter some keywords");
			return;
		}	
		if(max<=0)					// check if the value of max 
		{
			System.out.println("Enter valid number of pages to be collected");
			return;
		}
		if(fileName.length()<=0)	// check if valid file name has been provided
		{
			System.out.println("File name empty");
			return;
		}
		this.seedUrl=seedUrl;
		this.keywords=keywords;
		this.max=max;
		this.fileName=fileName;
		this.disallowedUrl= new HashSet<String>();
		this.tmpQ= new LinkedList<String>();
		this.finalQ= new LinkedList<ArrayList<String>>();
		this.visited= new HashSet<String>();
		this.countConnection=0;
		this.allPagesCollected=new LinkedHashSet<String>();
	}
	
	
	/* 
	 * The crawl method will do the actual crawling as per instruction 
	 * It starts with the seed url and checks if the links in that page is valid or not
	 * After that it saves in a list(this list contains all the edge)
	 */
	public void crawl(){

		try {
			System.out.println("StartTime " + new Date());
			File file = new File(fileName);
			file.createNewFile();
			FileWriter writer = new FileWriter(file); 
			parseRobots(BASE_URL);
			int i=0;
			allPagesCollected.add(seedUrl);
			tmpQ.add(seedUrl);
			writer.write(max+"\n");
			while(!tmpQ.isEmpty())		// till the queue is empty
			{
				String urlToVisit=tmpQ.remove(0);
				String actualUrlToHit=BASE_URL+urlToVisit;
				visited.add(urlToVisit);
				LinkedHashSet<String> pgLinks;
				try{
					pgLinks= getPageContent(actualUrlToHit);		// get all the links in the pages
				}catch(Exception e)
				{
					e.printStackTrace();
					System.out.println("Parent page: " + actualUrlToHit);
					continue;
				}
				
				for(String indLink:pgLinks)
				{
					if(visited.contains(indLink)||tmpQ.contains(indLink))	//if already visited do not add to queue
							continue;
					tmpQ.add(indLink);
					
					ArrayList<String> eleFinal= new ArrayList<String>();
					eleFinal.add(urlToVisit);
					eleFinal.add(indLink);
					
				}
				for(String indLink:pgLinks)
				{
					if(urlToVisit.equals(indLink))		// discard self loops
						continue;
					ArrayList<String> eleFinal= new ArrayList<String>();
					eleFinal.add(urlToVisit);
					eleFinal.add(indLink);
					finalQ.add(eleFinal);				// add to final edges list
					i++;
				}
				System.out.println("Page covered >> " + urlToVisit + " i >> "+ i);// +" queSize >>" + queSaveEdges.size());
			}
			
			System.out.println("Size of final queue : " + finalQ.size());
			for(ArrayList<String>  lines:finalQ)
			{
				
				String indivLine=lines.get(0)+ " " + lines.get(1);
				writer.write(indivLine+"\n");			// write each edges to the file
			}
			
			writer.flush();
			writer.close();
			System.out.println("End time " + new Date());
			System.out.println("Total request made " + countConnection);

		} catch (Exception me) {
			System.out.println(me); 
			me.printStackTrace();
		}
	}
	/*
	 * This function will remove the extra space in the line 
	 */
	private static String removeUnWantedWords(String inputLine) 
	{
		String result=null;
		inputLine=inputLine.trim();
		result=inputLine.replaceAll("\\s+", " ");
		return result;
	}
	
	/*
	 * We parse the robots.txt present in the website and collect the disallowed Urls
	 * @param homeUrl 		the homeUrl for the website for which we want to download robots.txt
	 */
	private void parseRobots(String homeUrl)
	{
		URL page;
		boolean skip=true;
		try {
			page = new URL(homeUrl+"/robots.txt");
			BufferedReader in = getReaderObject(page);
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if(inputLine.contains("User-agent: *"))			// only collect the disallowed Urls after the User-agent: *
					skip=false;
				if(skip)
					continue;
				if (inputLine.startsWith("Disallow")) {
					inputLine = inputLine.trim();
					
					String arr[]=inputLine.split("/");
					String toAdd=arr[arr.length-1];
					if(toAdd.contains(":")|| toAdd.contains("#"))
						continue;
					disallowedUrl.add(toAdd);
				}
			}
			//System.out.println("Disallowed list : " + Arrays.asList(disallowedUrl.toArray( new String[0])));
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * This function gets the Url to be fetched and returns
	 * @param url	the url of the page for which we want to download the Urls
	 */
	private LinkedHashSet<String> getPageContent(String url) throws IOException
	{
		Boolean isAfterPtag=false;		// gets the content only after the first <p>
		
		URL page = new URL(url);
		BufferedReader in=null;
		String inputLine;
		LinkedHashSet<String> pageLinks=new LinkedHashSet<String>();
		try{
			in = getReaderObject(page);
			while ((inputLine = in.readLine()) != null) {
				inputLine=removeUnWantedWords(inputLine);
				if(inputLine==null || inputLine.isEmpty())
					continue;
				if(inputLine.startsWith("<p>"))
					isAfterPtag=true;
				if(!isAfterPtag)
					continue;
				if(allPagesCollected.size()<max){

					final Pattern pattern = Pattern.compile("href=\"/wiki/(.*?)\"");
					final Matcher matcher = pattern.matcher(inputLine);
					while(matcher.find())
					{
						String actualLink=matcher.group(1);
						if(pageLinks.contains("/wiki/"+actualLink))
							continue;
						if(actualLink.contains("#") || actualLink.contains(":"))		// ignore if it contains # or :
							continue;
						if(allPagesCollected.size()<max && (!pageLinks.contains("/wiki/"+actualLink)) && linkDoesContainKeyWords(actualLink) && notInRobots(actualLink))
						{
							String toAdd="/wiki/"+actualLink;
							pageLinks.add(toAdd);
							allPagesCollected.add(toAdd);
							System.out.println("Size > "+ allPagesCollected.size()+ " :-> " + toAdd);
						}
					}
				}
				else{		// if pages already collected do not search for new nodes
					final Pattern pattern = Pattern.compile("href=\"/wiki/(.*?)\"");
					final Matcher matcher = pattern.matcher(inputLine);
					while(matcher.find())
					{
						String actualLink=matcher.group(1);
						String toAddLink="/wiki/"+actualLink;
						if(allPagesCollected.contains(toAddLink))
							pageLinks.add(toAddLink);
					}

				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException eio) {
					eio.printStackTrace();
				}
			}
		}
		return pageLinks;
	}
	/*
	 * This function checks if the Url is disallowed or not
	 * @param actualLink 	the Url for which we need to check if that is disallowed or not
	 * @return 	true if it is allowed, else false
	 */

	private boolean notInRobots(String actualLink) 
	{
		if(disallowedUrl.size()<=0)
			return true;
		if(disallowedUrl.contains(actualLink))
			{
			System.out.println(" Disallowed >>" + actualLink);
			return false;
			}
		return true;
	}
	
	/*
	 * Function that checks whether the page contains the keywords or not
	 * <p> This function downloads contents from raw page 
	 * @param 	actualLink	the url that we have to use to fetch the content
	 */
	private boolean linkDoesContainKeyWords(String actualLink) throws IOException 
	{
		boolean result=false;
		ArrayList<String> keyWordList= new ArrayList<String>();
		keyWordList.addAll(Arrays.asList(keywords));
		
		String line = "";
		String rawUrl = BASE_URL + "/w/index.php?title=" + actualLink	+ "&action=raw";
		URL urlToHit = new URL(rawUrl);
		BufferedReader br=null;
		try{
			br = getReaderObject(urlToHit);
		}
		catch(Exception e)
		{
			System.out.println("Could not get Raw URL >> "+ rawUrl);
			e.printStackTrace();
			if(br!=null)
				try {
					br.close();
				} catch (IOException eio) {
					eio.printStackTrace();
				}
			return false;
		}
		while ((line = br.readLine()) != null) 		// do not check the remaining file if the keywords are already found
		{
			line=line.toLowerCase();
			for (Iterator<String> iterator = keyWordList.iterator(); iterator.hasNext();) {
			    String word = iterator.next();
			    if (!word.isEmpty() && line.contains(word.toLowerCase())) {
			        iterator.remove();
			    }
			}
			if(keyWordList.size()==0)
				return true;
		}
		br.close();
		return result;
		
	}
	/*
	 * This function gets a Url  and returns a BufferedReader object
	 * @param 	url 	the url to which we need to create connection
	 * @return the buffered reader object
	 */
	public BufferedReader getReaderObject(URL url) throws IOException 
	{
		countConnection++;
		if ((countConnection % 100) == 0) {
			try {
				System.out.println("Pausing for "+ waitSec + " sec");
				Thread.sleep(waitSec*1000); 
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		return reader;
	}
}
	
