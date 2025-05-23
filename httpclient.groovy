https://loopednetwork.medium.com/groovy-programming-httpclient-3f1f7be11419



import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

def client = HttpClient.newHttpClient()
def request = HttpRequest.newBuilder()
        .uri(URI.create("http://webcode.me"))
        .GET() // GET is default
        .build()

HttpResponse<Void> res = client.send(request,
        HttpResponse.BodyHandlers.discarding())

println res.statusCode()

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

def client = HttpClient.newHttpClient()
def req = HttpRequest.newBuilder()
        .uri(URI.create("http://webcode.me"))
        .build()

def res = client.send(req,
        HttpResponse.BodyHandlers.ofString())

println res.body()


import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import groovy.json.JsonOutput

def vals = ["name": "John Doe", "occupation": "gardener"]
def body = JsonOutput.toJson(vals)

def client = HttpClient.newHttpClient()
def request = HttpRequest.newBuilder()
        .uri(URI.create("https://httpbin.org/post"))
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build()

def res = client.send(request, HttpResponse.BodyHandlers.ofString())

println res.body()


import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

def client = HttpClient.newHttpClient()

var request = HttpRequest.newBuilder(URI.create("http://webcode.me"))
        .method("HEAD", HttpRequest.BodyPublishers.noBody())
        .build()

def res = client.send(request,
        HttpResponse.BodyHandlers.discarding())

def headers = res.headers()

println headers 

headers.map().each { k, v  -> 

   println "$k: $v"
}


import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

import groovy.json.JsonSlurper
import groovy.json.JsonParserType

def url = "http://webcode.me/users.json"

def client = HttpClient.newHttpClient()
def request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build()

HttpResponse<String> res = client.send(request,
        HttpResponse.BodyHandlers.ofString())


def body = res.body()

def data = new JsonSlurper().parseText(body)
def users = data["users"]

for (user: users)
{
    println "${user.id} ${user.first_name} ${user.last_name} ${user.email}"
}

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URI
import java.nio.file.Paths
import java.nio.file.Path

String uri = "https://webcode.me"

HttpClient client = HttpClient.newHttpClient()
HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .build()

String fileName = "index.html"
Path path = Paths.get(fileName)

HttpResponse<Path> resp = client.send(req,
        HttpResponse.BodyHandlers.ofFile(path))

System.out.println(resp.statusCode())

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URI
import java.util.stream.Stream

String uri = 'https://webcode.me/'

HttpClient client = HttpClient.newHttpClient()
HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .build()

HttpResponse<Stream<String>> resp = client.send(req,
        HttpResponse.BodyHandlers.ofLines())

System.out.println(resp.statusCode())

boolean show = false
resp.body().forEach(line -> {
    if (line.trim().startsWith('<p>')) {
        show = true
    }

    if (line.trim().startsWith('</p>')) {
        System.out.println(line)
        show = false
    }

    if (show) {
        System.out.println(line)
    }
})

import java.net.URI
import java.net.ProxySelector
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

def port = 7878
def url = "http://webcode.me"
def proxy = '143.208.200.26'

def client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .proxy(ProxySelector.of(new InetSocketAddress(proxy, port)))
        .build()

def req = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build()

def res = client.send(req, HttpResponse.BodyHandlers.ofString())
println res.body()

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

int nThreads = 30

def executor = Executors.newFixedThreadPool(nThreads)

def urls = [
    "https://crunchify.com",
    "https://yahoo.com",
    "https://www.ebay.com",
    "https://google.com",
    "https://www.example.co",
    "https://paypal.com",
    "http://bing.com/",
    "https://techcrunch.com/",
    "http://mashable.com/",
    "https://pro.crunchify.com/",
    "https://wordpress.com/",
    "https://wordpress.org/",
    "https://example.com/",
    "https://sjsu.edu/",
    "https://ask.crunchify.com/",
    "https://test.com.au/",
    "https://www.wikipedia.org/",
    "https://en.wikipedia.org"
]

for (String url in urls ) {

    executor.execute(() -> {

        worker(url)

        // try {
        //     worker(url)
        // } catch (Exception e) {
        //     e.printStackTrace()
        // }

    })
}

executor.shutdown()

executor.awaitTermination(30, TimeUnit.SECONDS)
println("finished")

def worker(url) {

    def client = HttpClient.newHttpClient()
    def request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build()

    HttpResponse<Void> res = client.send(request,
            HttpResponse.BodyHandlers.discarding())

    println "${url}: ${res.statusCode()}"
}


