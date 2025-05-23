import groovy.json.*
// https://github.com/janbodnar/Groovy-Examples/blob/main/httpclient.md
import java.lang.Object
import java.net.URLConnection
import java.net.HttpURLConnection
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

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

  private static final String getBasicAuthenticationHeader(String auth) {
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
  }

  osAPIClient(String url,String auth) {
    def nullTrustManager = [ checkClientTrusted: { chain, authType ->  }
                            ,checkServerTrusted: { chain, authType ->  }
                            ,getAcceptedIssuers: { [] as java.security.cert.X509Certificate[] }
                           ]

    def nullHostnameVerifier = [  verify: { hostname, session -> true }
                               ]

    SSLContext sc = SSLContext.getInstance("SSL")
    sc.init(null, [nullTrustManager as X509TrustManager] as TrustManager[], null)
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
    HttpsURLConnection.setDefaultHostnameVerifier(nullHostnameVerifier as HostnameVerifier)
    URL                addr  = new URL(url+'''/_cat/indices''')
    HttpsURLConnection ucon  = (HttpsURLConnection) addr.openConnection();
    ucon.setRequestProperty('''Authorization''',getBasicAuthenticationHeader(auth))
    ucon.setRequestProperty('''Accept''',       '''application/json''')
    ucon.setRequestProperty('''Content-Typ''',  '''application/json''')
    ucon.setRequestMethod("GET")
    int resp = ucon.getResponseCode()
    println("GET Response Code : " + resp)
    if (resp == HttpsURLConnection.HTTP_OK) { // success
      BufferedReader in = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
      String inputLine
      String reply = ""
      while ((inputLine  = in.readLine()) != null) {
        reply = reply + inputLine
      }
      in.close()
      def slurper = new groovy.json.JsonSlurper()
      def result = slurper.parseText(reply)
      println(JsonOutput.prettyPrint(reply))
    } else {
      println("GET request did not work.");
    }
  }
}
