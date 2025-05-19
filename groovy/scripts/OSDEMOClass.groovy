import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
// https://github.com/janbodnar/Groovy-Examples/blob/main/httpclient.md
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.X509ExtendedTrustManager
import java.security.cert.X509Certificate


class DemoClass implements AutoCloseable {
  private String jsonString

  DemoClass() {
    this.jsonString = '''{}'''
  }

  DemoClass(String js) {
    this.jsonString = js
  }

  @Override
  public void close() {
     System.out.println("Resource released.");
  }

  void setJsonString(String js) {
    jsonString = js
  }

  Map getDemoJSON() {
    return new JsonSlurper().parseText(this.jsonString)
  }

  static void main(String[] args) {
    String jsonAsText = '''{ "SCHOOL_INFO": { "SCHOOL_COUNTRY": "Finland"   
                                             ,"SCHOOL NAME":    "Findland Higher Learning"              
                                            }
                            ,"LOCATION":    { "LONGITUDE": "24.999"
                                             ,"LATITUDE":  "61.001"
                                            }
                        }'''
    try (DemoClass demo = new DemoClass(jsonAsText)) {
      def json = demo.getDemoJSON()
      println(json)

      try (client = new osAPIClient(System.getenv('''OSURL'''),System.getenv('''OSCRD'''))) {
        def qres = client.osAPIQuery(["query": [],"aggr":[]])
        client.close()
      }
      catch(all) {
        println(all)
      }
    }
    catch(all) {
      println(all)
    }
    def a = 0
    args.each() { arg ->
      println('''Argument Nummer ''' + a.toString() + ''' ist "''' + arg + '''"\n''')
      a = a + 1
    }
  }
}

class osAPIClient implements AutoCloseable {
  private String     auth
  private HttpClient osaclnt
  private SSLContext sslContext     = SSLContext.getInstance("SSL")
  private X509TrustManager trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }] as X509TrustManager

  @Override
  public void close() {
    // what ever to shutdown opensearch resources
    System.out.println("Resource released.");
  }

  Map osAPIQuery(Map req) {
    def Map res = [ "docs": []
                   ,"aggr": []
                  ]

    return res
  }

  private static final String getBasicAuthenticationHeader(String username, String password) {
    String valueToEncode = username + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
  }

  osAPIClient(String url,String auth) {
    //bypass SSL cert verification
    println('''--header "Authorisation: Basic '''+auth.bytes.encodeBase64().toString()+'''" '''+url+'''\n''')
    this.sslContext.init(null, [trustAll as TrustManager] as TrustManager[], new SecureRandom())
    def hostnameVerifier = [verify: { hostname, session -> true }]
    HttpsURLConnection.defaultSSLSocketFactory = this.sslContext.socketFactory
    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier as HostnameVerifier)

    URLConnection uc = new URL(url).openConnection() as URLConnection
    uc.setRequestProperty ("Authorization", "Basic " + auth.bytes.encodeBase64().toString());
    InputStream in = uc.getInputStream();

    println( resp.responseCode)
    if(resp.responseCode.equals(200)) {
      respBody = resp.inputStream.text
      println respBody
    }

  }

}
