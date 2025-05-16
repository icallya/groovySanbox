groovy.json.JsonBuilder
groovy.json.JsonSlurper
// https://github.com/janbodnar/Groovy-Examples/blob/main/httpclient.md




class DemoClass {
  def jsonAsText = '''[{
    "SCHOOL_INFO": {
        "SCHOOL_COUNTRY": "Finland",   
        "SCHOOL NAME": "Findland Higher Learning"              
    },
    "LOCATION": {                  
        "LONGITUDE": "24.999",                   
        "LATITUDE": "61.001"
    }
  }]'''
  static void main(String[] args) {       
    // 
    def json = new JsonSlurper().parseText(jsonAsText)
    def a = 0
    args.each() { arg ->
      println('''Argument Nummer ''' + a.toString() + ''' ist "''' + arg + '''"\n''')
      a = a + 1
    }
  }
}
