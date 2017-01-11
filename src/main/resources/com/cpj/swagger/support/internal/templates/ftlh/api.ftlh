<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>API  |  ${apiTitle }</title>
  
  <link href='${basePath}api/statics/css/typography.css${suffix}' media='screen' rel='stylesheet' type='text/css'/>
  <link href='${basePath}api/statics/css/reset.css${suffix}' media='screen' rel='stylesheet' type='text/css'/>
  <link href='${basePath}api/statics/css/screen.css${suffix}' media='screen' rel='stylesheet' type='text/css'/>
  <link href='${basePath}api/statics/css/reset.css${suffix}' media='print' rel='stylesheet' type='text/css'/>
  <link href='${basePath}api/statics/css/print.css${suffix}' media='print' rel='stylesheet' type='text/css'/>
  <script src='${basePath}api/statics/lib/object-assign-pollyfill.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/jquery-1.8.0.min.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/jquery.slideto.min.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/jquery.wiggle.min.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/jquery.ba-bbq.min.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/handlebars-4.0.5.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/lodash.min.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/backbone-min.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/swagger-ui.min.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/highlight.9.1.0.pack.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/highlight.9.1.0.pack_extended.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/jsoneditor.min.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lib/marked.js${suffix}' type='text/javascript'></script>
  <script src='${basePath}api/statics/lang/translator.js${suffix}' type='text/javascript'></script> 
  <script src='${basePath}api/statics/lang/${lang}.js${suffix}' type='text/javascript'></script> 
  
  <script type="text/javascript">
    $(function () {
      //var url = window.location.search.match(/url=([^&]+)/);
     // if (url && url.length > 1) {
      //  url = decodeURIComponent(url[1]);
      //} else {
      //  url = "restful.json";
     // }
      var url = "${getApisUrl}";
      // Pre load translate...
      if(window.SwaggerTranslator) {
        window.SwaggerTranslator.translate();
      }
      window.swaggerUi = new SwaggerUi({
        url: url,
        dom_id: "swagger-ui-container",
        supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],
        onComplete: function(swaggerApi, swaggerUi){
          if(typeof initOAuth == "function") {
            initOAuth({
              clientId: "your-client-id",
              clientSecret: "your-client-secret",
              realm: "your-realms",
              appName: "your-app-name", 
              scopeSeparator: ","
            });
          }

          if(window.SwaggerTranslator) {
            window.SwaggerTranslator.translate();
          }

          $('pre code').each(function(i, e) {
            hljs.highlightBlock(e)
          });

          addApiKeyAuthorization();
        },
        onFailure: function(data) {
          log("Unable to Load SwaggerUI");
        },
        docExpansion: "none",
        apisSorter: "alpha",
        showRequestHeaders: false
      });

      function addApiKeyAuthorization()	{
        //var key = encodeURIComponent($('#input_apiKey')[0].value);
        //if(key && key.trim() != "") {
        //    var apiKeyAuth = new SwaggerClient.ApiKeyAuthorization("api_key", key, "query");
        //    window.swaggerUi.api.clientAuthorizations.add("api_key", apiKeyAuth);
        //    log("added key " + key);
        //}
      }

      $('#input_apiKey').change(addApiKeyAuthorization);

      // if you have an apiKey you would like to pre-populate on the page for demonstration purposes...
      /*
        var apiKey = "myApiKeyXXXX123456789";
        $('#input_apiKey').val(apiKey);
      */

      window.swaggerUi.load();

      function log() {
        if ('console' in window) {
          console.log.apply(console, arguments);
        }
      }
  });
  </script>
</head>

<body class="swagger-section">
<div id='header'>
  <div class="swagger-ui-wrap">
    <a id="logo" href="#">${apiTitle }</a>
    
    <form id='api_selector'>
      <div class='input'><input disabled placeholder="http://example.com/api" id="input_baseUrl" name="baseUrl" type="text"/></div>
      <#-- <div class='input'><input placeholder="api_key" id="input_apiKey" name="apiKey" type="text"/></div> -->
      <div class='input'><a id="explore" href="#" data-sw-translate>Explore</a></div>
    </form>
    
  </div>
</div>

<div id="message-bar" class="swagger-ui-wrap" data-sw-translate>&nbsp;</div>
<div id="swagger-ui-container" class="swagger-ui-wrap"></div>
</body>
</html>