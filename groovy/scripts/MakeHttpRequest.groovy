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


class MakeGetRequestHttp implements AutoCloseable {

  @Override
  public void close() {
    // what ever to shutdown opensearch resources
    System.out.println("Resource released.");
  }

  static void main(String[] args) {
    try {
      myHttpRequest myhttpRequest = new myHttpRequest()    
      myhttpRequest.close()
      myhttpRequest = new myHttpRequest(System.getenv('''WEBADR'''))    
      myhttpRequest.close()
      myhttpRequest = new myHttpRequest(System.getenv('''WEBADR'''),'''/api/v2/alerts''')
      myhttpRequest.close()
      myhttpRequest = new myHttpRequest(System.getenv('''WEBADR'''),'''/api/v2/alerts''','''GET''')
      myhttpRequest.close()
      myhttpRequest = new myHttpRequest(System.getenv('''WEBADR'''),'''/api/v2/alerts''','''GET''','''{"Content-type":"application/json"}''')
      myhttpRequest.close()
    } catch (Exception e) {
      e.printStackTrace()
    }
  }
}


//
class myHttpRequest implements AutoCloseable {
  private String       url         = ""
  private String       addr        = ""
  private String       port        = -1
  private String       path        = ""
  private String       method      = ""
  private String       data        = ""
  private List<String> header      = []
  private String       httpRequest = ""
  private List<String> validProt   = ["http","https"]
  private List<String> validMethod = ["get","put","post","delete","cancel"]


  @Override
  public void close() {
    // what ever to shutdown opensearch resources
    System.out.println("Resource released.");
  }
  
  private Boolean isValidUrl(String url) {
    String prot = url.split(":")[0].toLowerCase()
    if (validProt.contains(prot)) {
      List uparts = url.split("/")
      if (uparts.size() >= 3) {
        List aparts = uparts[2].split(":")
        this.addr = aparts[0]
        if (aparts.size() == 2) {
          try {
            this.port = aparts[1].toInteger()
          }
          catch(all) {
            this.addr = ""
            return false
          }
        } else {
          switch(a) {            
            case '''https''':
              this.port = 443 
              break 
            case '''http''':
              this.port = 80 
              break 
            default :
              this.addr = ""
              return false
              break
          }
        }
        this.url = url
        return true
      }
    }
    return false
  }

  public String buildHttpRequest() {

  }

  myHttpRequest(String url, String path, String method, String data) {
    println('''Given url Parameter : ''' + url)
    if (isValidUrl(url)) {
      println('''which was found to be valid''')
      this.path = path.split('''\\?''')[0]
      println('''and a path given as ''' + this.path)
      println('''for method ''' + method)
      if (validMethod.contains(method.toLowerCase())) {
        this.method = method.toUpperCase()
        println('''method set to ''' + this.method + ''' found to be valid''')
        println('''and body data '''+data)
        return
      }
      this.path = ""
    }
  }

  myHttpRequest(String url, String path, String method) {
    println('''Given url Parameter : ''' + url)
    if (isValidUrl(url)) {
      println('''which was found to be valid''')
      this.path = path.split('''\\?''')[0]
      println('''and a path given as ''' + this.path)
      println('''for method ''' + method)
      if (validMethod.contains(method.toLowerCase())) {
        this.method = method.toUpperCase()
        println('''method set to ''' + this.method + ''' found to be valid''')
        return
      }
      this.path = ""
    }
  }

  myHttpRequest(String url, String path) {
    println('''Given url Parameter : ''' + url)
    if (isValidUrl(url)) {
      println('''which was found to be valid''')
      this.path = path.split('''\\?''')[0]
      println('''and a path given as ''' + this.path)
    }
  }

  myHttpRequest(String url) {
    println('''Given url Parameter : ''' + url + ''' which is ''' + isValidUrl(url).toString())
  }

  myHttpRequest() {
    println('''Given No Parameter''')
  }
}
