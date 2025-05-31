import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import java.lang.Integer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
class MakeGetRequestHttp implements AutoCloseable {

  @Override
  public void close() {
    // what ever to shutdown opensearch resources
    System.out.println("Resource released.");
  }

  static void main(String[] args) {
    String response = "";
    try {
      // Fetch IP address by getByName()
      final InetAddress      ip      = InetAddress.getByName(new URL(System.getenv('''WEBADR''')+'''/api/v2/alerts''').getHost());
//      final SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
//      final SSLSocket        socket  = (SSLSocket)factory.createSocket(ip.getHostAddress(), 443);
//      final SocketFactory factory = (SocketFactory)SocketFactory.getDefault();
      final Socket       socket  = (Socket)new Socket(ip.getHostAddress(), 9093);
        
//      socket.startHandshake();
        
      final PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter( socket.getOutputStream())));
     
      String alertSetup  = '''\r\n[{"startsAt":"'''+"2025-05-28T20:30:00.000Z"+'''",\r\n"endsAt":"'''+"2025-05-28T22:00:00.000Z"+'''",\r\n"annotations":{"additionalProp1":"string",\r\n"additionalProp2":"string",\r\n"additionalProp3":"string"},\r\n"labels":{"additionalProp1":"string",\r\n"additionalProp2":"string",\r\n"additionalProp3":"string"},\r\n"generatorURL":"http://127.0.0.1/hallo"}]\r\n'''

      println('''GET /api/v2/alerts HTTP/1.1\r''')
      println('''Host: 127.0.0.1\r''')
      println('''User-Agent: groovy/3.0\r''')
      println('''Content-Type: application/json\r''')
      println('''Accept: application/json\r''')
      println('''Content-Length: '''+(alertSetup.length()).toString()+'''\r''')
      println('''\r''')
      println(alertSetup) 
      
      out.println('''POST /api/v2/alerts HTTP/1.1\r''')
      out.println('''Host: 127.0.0.1\r''')
      out.println('''User-Agent: groovy/3.0\r''')
      out.println('''Content-Type: application/json\r''')
      out.println('''Accept: application/json\r''')
      out.println('''Content-Length: '''+(alertSetup.length()).toString()+'''\r''')
      out.println('''\r''')
      out.println(alertSetup) 
      // Send the raw HTTP document to the TCP stream
      out.flush()
      println('''Request gesendet''')
        
      if (out.checkError()) {
        System.out.println("SSLSocketClient:  java.io.PrintWriter error")
      }
      println('''Nach Error Check''')
        
      final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
      println('''BufferReader allocated''')
            
      String inputLine = "start"
      final StringBuffer responseBuffer = new StringBuffer()
      while (!inputLine.equals("") && ( inputLine = in.readLine()) != null) {
        println(inputLine)
        responseBuffer.append(inputLine)
        responseBuffer.append("\r\n")
      }
      println('''response gelesen''')
      println(responseBuffer)
      int contentLength = 0
      final String responseBufferString = responseBuffer.toString()

      // The TCP connection doesn't close unless it times out, so we use Content-Length
      if (responseBufferString.contains("Content-Length: ")) {
        final String contentLengthStr = responseBufferString.split("Content-Length: ")[1].split("\n")[0].trim()
        contentLength = Integer.parseInt(contentLengthStr)
      }
      int lastChar = -1;
      while (contentLength > 0 && (lastChar = in.read())!=-1) {
        responseBuffer.append((char)lastChar)
        contentLength--
      }
      in.close();
        
      // get the result
      response = responseBuffer.toString();
      println(response)        
      out.close();
      socket.close()
        
    } catch (Exception e) {
      e.printStackTrace()
    }
  }
}
