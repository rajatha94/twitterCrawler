import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import jdk.nashorn.internal.parser.JSONParser;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SearchTweets {

  static Queue<String> queue = new LinkedList<>();

  private static final int MAX_DEPTH = 2;
  private static final int MAX_LINKS = 10000;
  private static int numberOfImages = 0;
  private static int numberOfDocs = 0;

  public static void searchTweets()
      throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, IOException, InterruptedException {

    HashSet<String> urlSet = new HashSet<>();
//    try (FileWriter fw = new FileWriter("data.txt", true);
//        BufferedWriter bw = new BufferedWriter(fw);
//        PrintWriter out = new PrintWriter(bw)) {
//      out.println("the text");
//      //more code
//      out.println("more text");
//      //more code
//    } catch (IOException e) {
//      //exception handling left as an exercise for the reader
//    }
//    FileWriter fileWriter = new FileWriter("data.txt");
    String nextToken = "";
    String consumerKeyStr = "akM4LLez5ADNc8hNnVB3dBIrf";
    String consumerSecretStr = "G1HfmFW3t69mzX10yCk4f9tVYDLgw2hhCosDL7KEep1EvqGSGG";
    String accessTokenStr = "464528427-ehdOgAFkDOCcPDVpUcTlgoYvkeYas78jqLQLNH0t";
    String accessTokenSecretStr = "zpuWxmR2BaiJ3Nxblv3TbCRZXFg7QJSAtrynF05HrdpsA";
    OAuthConsumer oAuthConsumer = new CommonsHttpOAuthConsumer(consumerKeyStr, consumerSecretStr);
    oAuthConsumer.setTokenWithSecret(accessTokenStr, accessTokenSecretStr);
    HttpGet httpGet;
    while (urlSet.size() < 200) {
      if (nextToken.equals("")) {
        httpGet = new HttpGet(
            "https://api.twitter.com/1.1/tweets/search/fullarchive/dev.json?&query=%23oscar&fromDate=201902210000");
      } else {
        httpGet = new HttpGet(
            "https://api.twitter.com/1.1/tweets/search/fullarchive/dev.json?&query=%23oscar&fromDate=201902210000&next="
                + URLEncoder.encode(nextToken, "UTF-8"));
      }
      oAuthConsumer.sign(httpGet);
      HttpClient httpClient = new DefaultHttpClient();
      HttpResponse httpResponse = httpClient.execute(httpGet);
      InputStream is = httpResponse.getEntity().getContent();
      String json = IOUtils.toString(is);
      JSONObject jsonObject = new JSONObject(json);
      try {
        nextToken = jsonObject.getString("next");
      } catch (JSONException e) {
        break;
      }
      //System.out.println(nextToken);
      JSONArray jsonArray = jsonObject.getJSONArray("results");
      int i = 0;
      while (i < jsonArray.length() - 1) {
        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
        // if (i < 10) {
        JSONArray urlArray = jsonObject1.getJSONObject("entities").getJSONArray("urls");
        int j = 0;
        while (j < urlArray.length() - 1) {
          String url = String.valueOf(urlArray.getJSONObject(j).get("url"));
          urlSet.add(url);
          //System.out.println(url);
          ++j;
        }
        ++i;
      }
    }
    FileWriter fw = new FileWriter("data.txt", true);
    for (String url : urlSet) {
      fw.write(url + "\n");
    }
    fw.close();
    System.out.println(urlSet);

//    //TimeUnit.SECONDS.sleep(2);
//    httpGet = new HttpGet(
//        "https://api.twitter.com/1.1/tweets/search/30day/dev.json?&query=%23oscars&next="
//            + URLEncoder.encode(nextToken, "UTF-8"));
//    oAuthConsumer.sign(httpGet);
//    httpClient = new DefaultHttpClient();
//    httpResponse = httpClient.execute(httpGet);
//    is = httpResponse.getEntity().getContent();
//    json = IOUtils.toString(is);
//    jsonObject = new JSONObject(json);
//    nextToken = jsonObject.getString("next");
//    jsonArray = jsonObject.getJSONArray("results");
//    i = 0;
//    while (i < jsonArray.length() - 1) {
//      JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//      if (i < 2) {
//        JSONArray jsonArray1 = jsonObject1.getJSONObject("entities").getJSONArray("urls");
//        System.out.println("urls1");
//        System.out.println(jsonArray1);
//      }
//      ++i;
//    }
//    System.out.println(nextToken);
//    //System.out.println(jsonObject.getJSONArray("results"));

  }


  static void crawler() throws IOException, URISyntaxException {
    Scanner rel = new Scanner(new FileReader("data.txt"));
    String urlString;
//    Map<String, List<Integer>> urlSet = new HashMap<>();//list of depth, incoming links, outgoing links
//    HashSet<String> visited = new HashSet<>();
//    Queue<String> queue = new LinkedList<>();
    HttpURLConnection urlConnection = null;
    while (rel.hasNextLine()) {
      urlString = rel.nextLine();
      //List<Integer> urlData = new ArrayList<>();
      //urlData.add(1);
      final URL url = new URL(urlString);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setInstanceFollowRedirects(false);
      final String location = urlConnection.getHeaderField("location");
      getPageLinks(location, 0);
    }
  }

  public static Map<String, List<Integer>> links = new HashMap<>();
  public static Map<String, Integer> frequencyDistributionOfDomains = new HashMap<>();

  static int numberOfLinksScanned = 0;
  public static void getPageLinks(String URL, int depth) {
    URL = URL.replaceFirst("/#(.+)", "");
    if ((depth < MAX_DEPTH) && (numberOfLinksScanned < MAX_LINKS)) {
      System.out.println(">> Depth: " + depth + " [" + URL + "]");
      try {
        Document document = Jsoup.connect(URL).get();
        URL url = new URL(URL);
        String domain = url.getHost();
        ++numberOfLinksScanned;
        if (frequencyDistributionOfDomains.containsKey(domain)) {
          int freq = frequencyDistributionOfDomains.get(domain);
          frequencyDistributionOfDomains.put(domain, ++freq);
        } else {
          frequencyDistributionOfDomains.put(domain, 1);
        }
        Elements linksOnPage = document.select("a[href]");
        List<Integer> data = new ArrayList<>();
        if (!links.containsKey(URL)) {
          data.add(depth);
          data.add(1);
          data.add(linksOnPage.size());
          links.put(URL, data);
          depth++;
          for (Element page : linksOnPage) {
            getPageLinks(page.attr("abs:href"), depth);
          }
        } else {
          data.add(links.get(URL).get(0));
          data.add(1, links.get(URL).get(1) + 1);
          data.add(2, linksOnPage.size());
          links.put(URL, data);
        }
      } catch (Exception e) {
        try {
          URL url = new URL(URL);
          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          urlConnection.setInstanceFollowRedirects(false);
          String contentType = urlConnection.getContentType();
          if (contentType != null) {
            if (contentType.contains("image/bmp") || contentType.contains("image/gif")
                || contentType
                .contains("image/vnd.microsoft.icon") || contentType.contains("image/jpeg")
                || contentType.contains("image/png") || contentType.contains("image/svg+xml")
                || contentType.contains("image/tiff") || contentType.contains("image/webp")) {
              ++numberOfImages;
            } else if (contentType.contains("application/pdf") || contentType
                .contains("application/json")) {
              ++numberOfDocs;
            }
          }
        } catch (Exception e1) {
          //e1.printStackTrace();
        }

        System.err.println("For '" + URL + "': " + e.getMessage());
      }
    } else {
      return;
    }
  }

  public static void main(String[] args)
      throws OAuthExpectationFailedException, OAuthCommunicationException, OAuthMessageSignerException, IOException, InterruptedException, URISyntaxException {
    //searchTweets();
    crawler();
    //getPageLinks("http://noticiasalsur.co/wp-content/uploads/2019/03/5ab14f9a39278.jpg", 0);
    System.out.println("Links of document type: " + numberOfDocs);
    System.out.println("Links of image type: " + numberOfImages);
    System.out.println("Links of HTML/ text type: " + links.size());
    System.out.println("Freq distribution of Domains: \n");
    for (String domain : frequencyDistributionOfDomains.keySet()) {
      System.out.println(domain + ": " + frequencyDistributionOfDomains.get(domain));
    }
    double avgDepth = 0;
    Map<String, Integer> incomingLinks = new HashMap<>();
    Map<String, Integer> outGoingLinks = new HashMap<>();
    for (String link : links.keySet()) {
      incomingLinks.put(link, links.get(link).get(1));
      outGoingLinks.put(link, links.get(link).get(2));
      avgDepth += links.get(link).get(0);
    }

    Map<String, Integer> sortedIncoming = incomingLinks
        .entrySet()
        .stream()
        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
            LinkedHashMap::new));

    Map<String, Integer> sortedOutgoing = outGoingLinks
        .entrySet()
        .stream()
        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
            LinkedHashMap::new));

    avgDepth = avgDepth * 1.0 / links.size();
    System.out.println("Average link depth: " + avgDepth + "\n");
    System.out.println("Incoming top 25 links: \n");
    int i = 1;
    for (String link : sortedIncoming.keySet()) {
      if (i > 25) {
        break;
      }
      System.out.println(link + ": " + sortedIncoming.get(link));
      ++i;
    }
    i = 1;
    System.out.println("Outgoing top 25 links: \n");
    for (String link : sortedOutgoing.keySet()) {
      if (i > 25) {
        break;
      }
      System.out.println(link + ": " + sortedOutgoing.get(link));
      ++i;
    }
  }


}
