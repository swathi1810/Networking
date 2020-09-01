import java.net.*;
import java.io.*;


class HTTPRequest {

    public static int port = 80;
BufferedReader br;
StringBuffer pagebuffer;

 public String WWWPage(URL pageURL){
      try {
         //URL u = new URL(args[i]);
         if (pageURL.getPort() != -1) port = pageURL.getPort();
         Socket s = new Socket(pageURL.getHost(), port);
		 String path=pageURL.getPath();
		 if (path == null || path.length( ) == 0) {
			 path = "/";
			 } 

		 //System.out.println("-----------Path------"+path);
         OutputStream theOutput = s.getOutputStream();
         PrintWriter pw = new PrintWriter(theOutput, false);
         pw.print("GET "+path+" HTTP/1.0\r\n");
         pw.print("Accept: text/plain, text/html, text/*, */*\r\n");
         pw.print("\r\n");
         pw.flush();
         InputStream in = s.getInputStream();
         InputStreamReader isr = new InputStreamReader(in);
          br = new BufferedReader(isr);
         pagebuffer=new StringBuffer();
          String line;
         while ((line = br.readLine()) != null) {
           pagebuffer.append(line);
		   pagebuffer.append("\n");

         }
        
      }
      
      catch (IOException ex) {
        System.err.println(ex);
      }
      try {
        br.close();
    } catch (Exception ignored) {}


     return pagebuffer.toString();


  }


}
