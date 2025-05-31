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
      String queryString = '''{"size":1000,"query":{"match_all":{}}}'''
      println(json)

      try (client = new osAPIClient(System.getenv('''OSURL'''),System.getenv('''OSCRD'''),args[0])) {
        def qres = client.osAPIQuery(queryString)
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
  private String           urlString            = ""
  private String           auth                 = ""
  private List<String>     indexList            = []
  private SSLContext       sslContext           = SSLContext.getInstance("SSL")
  private Map              nullTrustManager     = [ checkClientTrusted: { chain, authType ->  }
                                                   ,checkServerTrusted: { chain, authType ->  }
                                                   ,getAcceptedIssuers: { [] as java.security.cert.X509Certificate[] }
                                                  ]

  private Map              nullHostnameVerifier = [  verify: { hostname, session -> true }
                                                  ]


  @Override
  public void close() {
    // what ever to shutdown opensearch resources
    System.out.println("Resource released.");
  }

  Map osAPIQuery(String req) {
    def Map res = [ "docs": []
                   ,"aggr": []
                  ]
    this.indexList.each() { idx ->
      URL                addr  = new URL(this.urlString+'''/'''+idx+'''/_search''')
      println(this.urlString+'''/'''+idx+'''/_search''')
      println(req)
      HttpsURLConnection ucon  = (HttpsURLConnection) addr.openConnection();
      ucon.setRequestProperty('''Authorization''',  this.auth)
      ucon.setRequestProperty('''Accept''',         '''application/json''')
      ucon.setRequestProperty('''Content-Typ''',    '''application/json''')
      ucon.setRequestProperty('''Content-Length''', Integer.toString(req.getBytes().length))
      ucon.setDoOutput(true)
      ucon.setRequestMethod('''GET''')

      try {
     //   println("Vor Connect") 
     //   ucon.connect()
     //   println("Vor Write Stream") 
        DataOutputStream wr = new DataOutputStream (ucon.getOutputStream ());
        println("Vor Write Bytes") 
        wr.writeBytes (req)
        println("Vor Flush") 
        wr.flush ()
        println("Vor Close") 
        wr.close ()

        println("Vor Get Response") 
        int resp = ucon.getResponseCode()
        println("GET Response Code : " + resp)
        if (resp == HttpsURLConnection.HTTP_OK) {
          println("hallo") 
          try(BufferedReader br = new BufferedReader(new InputStreamReader(ucon.getInputStream(), "utf-8"))) {
            println("hab InputStream")
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
              println(responseLine.trim())
              response.append(responseLine.trim());
            }
            def slurper = new groovy.json.JsonSlurper()
            def result = slurper.parseText(response.toString())
            if (result["hits"]) {
              if (result["hits"]["hits"]) {
                result["hits"]["hits"].each() { hit ->
                  if (hits["_source"]) {
                    println(new groovy.json.JsonBuilder(hits["_source"]).toPrettyString())
                  }
                }
              }
            }
          }
          catch(all) {
            println(all)
          }
        }
      }
      catch(all) {
        println(all)
      }

    }

    return res
  }

  private static final String getBasicAuthenticationHeader(String auth) {
    return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
  }

  osAPIClient(String url,String auth,String idxname) {
    this.urlString = url
    this.sslContext.init(null, [nullTrustManager as X509TrustManager] as TrustManager[], null)
    HttpsURLConnection.setDefaultSSLSocketFactory(this.sslContext.getSocketFactory())
    HttpsURLConnection.setDefaultHostnameVerifier(this.nullHostnameVerifier as HostnameVerifier)

    this.auth                = getBasicAuthenticationHeader(auth) 
    URL                addr  = new URL(this.urlString+'''/_cat/indices''')
    HttpsURLConnection ucon  = (HttpsURLConnection) addr.openConnection();
    ucon.setRequestProperty('''Authorization''',this.auth)
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
      result.each() { i ->
        if (i.index ==~ /$idxname/ ) {
          this.indexList.add(i.index)
        }
      }
    } else {
      println("GET request did not work.");
    }
  }
}
