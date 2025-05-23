import groovy.json.JsonSlurper

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom;

def apiKey = "api-key"
def url = "https://somedomain.com/api/endpoint?private_token="+apiKey

//bypass SSL cert verification
def sc = SSLContext.getInstance("SSL")
def trustAll = [getAcceptedIssuers: {}, checkClientTrusted: { a, b -> }, checkServerTrusted: { a, b -> }]
sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
hostnameVerifier = [verify: { hostname, session -> true }]
HttpsURLConnection.defaultSSLSocketFactory = sc.socketFactory
HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier as HostnameVerifier)
String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));  //Java 8

println url

//make REST call
def response = new URL(url).openConnection() as HttpsURLConnection
response.setRequestProperty("Authorization", "Basic "+encoded);

println response.responseCode
if(response.responseCode.equals(200)){
    responseBody = response.inputStream.text
    println responseBody
}
