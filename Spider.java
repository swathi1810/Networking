import java.util.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
public class Spider extends Thread {
	private final static int max_threads=4;
    static Hashtable pageDatabase =new Hashtable();
    static HashMap<String, Integer> map = new HashMap<>(); 
    static HashMap<String, Date> date_map = new HashMap<>(); 
    HTTPRequest http;
    URL pageToFetch;
    InetAddress addr ;
    private String name1;
    public Pattern patternTag,patternLink,patternTag_img,patternLink_img;
    public Matcher matcherTag,matcherLink,matcherTag_img,matcherLink_img;
    public static final String a_tag="(?i)<a([^>]+)>(.+?)</a>";
	public static final String img_tag="(?i)<img([^>]+)>";
	public static final String src_tag="\\s*(?i)src\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
    public static final String href_tag="\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
    public static int numberoflinks=0;
    public static int count=0;
	public static int html_count=0;
	public static int nonhtml_count=0;
    ArrayList<Integer> sortedlength=new ArrayList<Integer>();
    ArrayList<String> sortedlinks=new ArrayList<String>();
    ArrayList<String>link_date=new ArrayList<String>();
    ArrayList<Date> get_date=new ArrayList<Date>();
    ArrayList<Long>diff=new ArrayList<Long>();
	static ArrayList<String> redirecting_link=new ArrayList<String>();
	static ArrayList<String>redirectedto_link=new ArrayList<String>();
	static ArrayList<String> invalid_urls=new ArrayList<String>();
    String uu;
    static String absolutePath;
    int size1=0;
    Date now=new Date();
    int index=0;
    int index1=0;
	int index2=0;
	int number=0;
    public SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",Locale.US); 
    //Default constructor to start the HTTPrequest with the initial link
    public Spider()
    {
    	patternTag = Pattern.compile(a_tag);
        patternLink = Pattern.compile(href_tag);
		patternTag_img = Pattern.compile(img_tag);
        patternLink_img = Pattern.compile(src_tag);
		
        try{
        	pageToFetch = new URL("http://comp3310.ddns.net:7880");
            //setName();
            start();  
		    join();
		}
        catch(InterruptedException ie) {
        	ie.printStackTrace();}
      catch (IOException ex) {
        System.err.println(ex);
      }

}
//Parameterized constructor
 public Spider(String pageAddress)
    {
    	patternTag = Pattern.compile(a_tag);
        patternLink = Pattern.compile(href_tag);
		patternTag_img = Pattern.compile(img_tag);
        patternLink_img = Pattern.compile(src_tag);
		
        try{
        	pageToFetch = new URL(pageAddress);
            setName(pageAddress);
            start(); 
//the next thread starts only when the preceeding one is completed 
		    join();
		}
        catch(InterruptedException ie) {
        	ie.printStackTrace();}
      catch (IOException ex) {
        System.err.println(ex);
      }

}
    public void run()
    {
    	String webpage;
        Vector pageLinks;
        count+=1;
        if(count==1){
        	uu=pageToFetch.toString();
	        absolutePath=uu;
	        markvisited(uu);
	        }
        //gets the response page 
        webpage=getPage(pageToFetch);
        //extracts thhe links from the page
        pageLinks=extractLinks(webpage,pageToFetch.toString());
        numberoflinks+=pageLinks.size();
        Enumeration enu =pageLinks.elements();
		String page;
        while(enu.hasMoreElements()){
        	page=(String)enu.nextElement();
			//System.out.println("-------links------------"+page);
			number+=1;
            //if not visited mark visisted and start a new thread
            if(!alreadyvisited(	page)){
            	markvisited(page);
                new Spider(page);
                }
				else{
					if(number!=pageLinks.size()){
						page=(String)enu.nextElement();
				        markvisited(page);
				        new Spider(page);
			}		
            	//once the crawling is done print the result
                System.out.println("Number of links: "+numberoflinks);
				System.out.println("Number of HTML objects: "+html_count);
				System.out.println("Number of Non HTML objects: "+nonhtml_count);
                //sorts the map according to decreasing length
                Map sortedMap = new TreeMap(new ValueComparator(map));
                sortedMap.putAll(map);
                Iterator it = sortedMap.entrySet().iterator();
                while (it.hasNext()) {
                	Map.Entry<String,Integer> pair = (Map.Entry)it.next();
		            sortedlength.add(pair.getValue());
		            sortedlinks.add(pair.getKey());
                    it.remove();
                    }
                size1=sortedlinks.size();
                System.out.println("The longest link: "+ sortedlinks.get(0)+":"+sortedlength.get(0));
                System.out.println("The smallest link: "+sortedlinks.get(size1-1)+":"+sortedlength.get(size1-1));
                Iterator it1 = date_map.entrySet().iterator();
                while (it1.hasNext()) {
                	Map.Entry<String,Date> pair1 = (Map.Entry)it1.next();
		            get_date.add(pair1.getValue());
		            link_date.add(pair1.getKey());
                    it1.remove();
                    }
               String pass=formatter.format(now);
               int n=link_date.size();
               //get the timestamp, calculates the difference between the system time and timestamp and sort the difference in time in decreasing order
               try{
            	   Date new1=formatter.parse(pass);
                   for(int j=0;j<n;j++){
                	   long duration=new1.getTime()-get_date.get(j).getTime();
	                   long diffinSec=TimeUnit.MILLISECONDS.toSeconds(duration);
	                   diff.add(diffinSec);
	                   }
                  long min=diff.get(0);
                  long max=diff.get(0);
                  for(int k=1;k<n;k++){
                	  if(min>diff.get(k)){ min=diff.get(k); index=k;}
                	  }
				   for(int k=1;k<n;k++){
                	  if(max<diff.get(k)){ max=diff.get(k); index2=k;}
                	  }
                  System.out.println("The last modified: "+link_date.get(index) +"with timestamp "+ get_date.get(index));
                  System.out.println("The oldest: "+link_date.get(index2)+" with timestamp "+get_date.get(index2));
                  }catch(Exception ex){System.err.println(ex);}
				  System.out.println("The redirected links list");
				  for(int p=0;p<redirectedto_link.size();p++){
					  System.out.println(redirecting_link.get(p)+" --> "+redirectedto_link.get(p));
					  }
				  System.out.println("Invalid urls");
				  for(int p=0;p<invalid_urls.size();p++){
					  System.out.println(invalid_urls.get(p));
					  }
				}
	   }
		
        }
//calls the HTTP request function 
    protected String getPage(URL pages){
    	http=new HTTPRequest();
        return http.WWWPage(pages);
        }
   protected boolean alreadyvisited(String add)
   {
	   if(add.endsWith("/")){
		   add=add.substring(0,(add.length()-2));
		   }
	   return pageDatabase.containsKey(add);
	   }
   protected void markvisited(String add)
   {
	   pageDatabase.put(add,add);
	   }
  //extracts the links from the received response page
   protected Vector extractLinks(String page,String url)
   {
	   int f=0;
	   int k=0;
	   int l=0;
	   String line1=null;
       BufferedReader bufReader2 = new BufferedReader(new StringReader(page));
       try{
    	   while((line1=bufReader2.readLine())!=null)
    		   {
    		   if(line1.contains("Last-Modified")){
    			   k=1;
		           break;
		           }
    		   }
    	   if(k==1){
    		   String parts1=line1.substring(15,(line1.length()));
               Date d = formatter.parse(parts1);
               date_map.put(url,d);
               }
    	   }	
       catch(IOException ex){System.err.println(ex);}
       catch(Exception ex1){System.err.println(ex1);}
	   BufferedReader bufReader3 = new BufferedReader(new StringReader(page));
	   try{
		   while((line1=bufReader3.readLine())!=null)
		   {
			   if(line1.contains("Location")){
				   String[] splits=line1.split(" ");
				   redirecting_link.add(url);
				   redirectedto_link.add(splits[1]);
			   }
		   }
	   }
	   catch(IOException ex){System.err.println(ex);}
	   BufferedReader bufReader4 = new BufferedReader(new StringReader(page));
	   try{
		   line1=bufReader4.readLine();
		   //String[] splits=line1.split(" ");
		   if(line1.contains("404")){
		   invalid_urls.add(url);}
	   }
	   catch(IOException ex){System.err.println(ex);}
	    BufferedReader bufReader5 = new BufferedReader(new StringReader(page));
	   try{
		   line1=bufReader5.readLine();
		   if(line1.contains("200")){
			   while((line1=bufReader5.readLine())!=null){
				   if(line1.contains("Content-Type")){
					   //System.out.println("---really found you----");
					   l=1;
				   break;}
			   }
		   }
		   if(l==1){
			   String[] splits=line1.split(" ");
			   if(splits[1].equals("text/html")){html_count+=1; check_length(url,page);}  else nonhtml_count+=1;
		   }
					   
	   }
	   catch(IOException ex){System.err.println(ex);}
       String link;
       Vector bagOfLinks = new Vector();
       matcherTag = patternTag.matcher(page);
	   matcherTag_img=patternTag_img.matcher(page);
       while(matcherTag.find()){
    	   String href=matcherTag.group(1);
           String linkText=matcherTag.group(2);
           matcherLink=patternLink.matcher(href);
           while(matcherLink.find()){
        	   link=matcherLink.group(1);
               boolean tp=link.contains("http://");
			   //int endindex=url.lastIndexOf("/");
			   //url=url.substring(0,endindex);
			   //System.out.println(url);
               if(link.startsWith("\""))
            	   link=link.substring(1,(link.length()-1));
               if(!tp)
            	   link=url+"/"+link;
               bagOfLinks.addElement(link);
               }
           }
	   while(matcherTag_img.find()){
		   System.out.println("--------found-----");
		   String src_img=matcherTag_img.group(1);
		   matcherLink_img=patternLink_img.matcher(src_img);
		   while(matcherLink_img.find()){
			   //nonhtml_count+=1;
			   link=matcherLink_img.group(1);
			   link = link.replaceAll("^\"|\"$", "");
			   int endindex=url.lastIndexOf("/");
			   url=url.substring(0,endindex);
			   boolean tp=link.contains("http://");
			   if(!tp)
            	   link=url+"/"+link;
               bagOfLinks.addElement(link);
               }
	   }
	   return bagOfLinks;
       }
	   public void check_length(String url,String page){
		   int f=0;
		    BufferedReader bufReader1 = new BufferedReader(new StringReader(page));
	   //System.out.println("-----the page-----"+page);
       String line1=null;
       try{
		   line1=bufReader1.readLine();
		   String[] splits=line1.split(" ");
		   if(splits[1].equals("200")){
			   while((line1=bufReader1.readLine())!=null)
    		   {
				   if(line1.contains("Content-Length")){
					   f=1;
	                   break;}
    		   }
		   }
    	   if(f==1){
    		   String[] parts=line1.split(" ");
               int size=Integer.parseInt(parts[1]);
               map.put(url,size);
               }
           line1=null;
           }
       catch(IOException ex){System.err.println(ex);}
	   }
   }








