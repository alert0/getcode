import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.http.HttpHost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.xsoup.Xsoup;



public class Test implements PageProcessor  {

    final File newFile = new File("行政区化.txt");
    //	Cookie Name	Value
//	ASPSESSIONIDAABBABTS	PAONOBJBHABHJMDMGMHMNNEG
   // HttpHost  httpProxy =new HttpHost("223", 808);
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3);
            //.setHttpProxy(httpProxy)
            //.//addCookie("ASPSESSIONIDAABBABTS", "PAONOBJBHABHJMDMGMHMNNEG");

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page)  {
        // 部分二：定义如何抽取页面信息，并保存下来
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name") == null) {
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

//        if(page.getResultItems().getRequest().getUrl().indexOf("show_nr") > -1 ) {
//            page.putField("test",page.getHtml().xpath("//BODY").toString() );
//            System.out.println(page.getHtml().xpath("//BODY").toString() );
//        }


     //   page.putField("test",page.getHtml().xpath("//td").toString() );


        String baseurl =  page.getUrl().toString().substring(0,page.getUrl().toString().lastIndexOf("/") + 1 ) ;

        List<String> countytr =   page.getHtml().xpath("//tr[@class='countytr']").all();

        List<String> towntr =   page.getHtml().xpath("//tr[@class='towntr']").all();

        List<String> villagetr =   page.getHtml().xpath("//tr[@class='villagetr']").all();


        for(String temp : countytr){

                //  String[] datas= temp.split("','");
                // String p = datas[3].substring(0, datas[3].indexOf("^^"));
                System.out.println("......."+ temp) ;

                Document document = Jsoup.parse(temp);
                List<String> list = Xsoup.compile("a/text()").evaluate(document).list();


                if(list!= null && !list.isEmpty() )
                try {
                    System.out.println(list.get(0 ) );
                    System.out.println(list.get(1 ) );
                    Files.append(list.get(0 )+ "  " + list.get(1 )  +"              "+ page.getUrl().toString() +"\r\n", newFile, Charsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            //  urls.add("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/33/" + temp) ;
        }


        for(String temp : towntr){

            try {
            //  String[] datas= temp.split("','");
            // String p = datas[3].substring(0, datas[3].indexOf("^^"));
                Document document = Jsoup.parse(temp);
                List<String> list = Xsoup.compile("a/text()").evaluate(document).list();

                System.out.println(list.get(0 ) );
                System.out.println(list.get(1 ) );

                Files.append(list.get(0 )+ "  " + list.get(1 ) +"               "+ page.getUrl().toString() +"\r\n", newFile, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  urls.add("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/33/" + temp) ;
        }


        for(String temp : villagetr){

            try {
            //  String[] datas= temp.split("','");
            // String p = datas[3].substring(0, datas[3].indexOf("^^"));
            Document document = Jsoup.parse(temp);
            String[] body = document.body().toString().split( " ");

                System.out.println(body[1] );
                System.out.println(body[3] );
                Files.append(body[1]+ "  " + body[3]  +"\r\n", newFile, Charsets.UTF_8);

            } catch (Exception e) {
                e.printStackTrace();
            }
//            Elements links = document.getElementsByTag("td");
//            for (Element link : links) {
//              //  String linkHref = link.attr("href");
//                String linkText = link.text();
//                System.out.println(linkText );
//            }
            //List<String> list = Xsoup.compile("/td").evaluate(document).list();

          //  System.out.println(list.get(0 ) );
//            //  urls.add("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/33/" + temp) ;
        }


        List<String> tempurls =   page.getHtml().xpath("//a/@href").all();


      //  List<String> tempurls =   page.getHtml().xpath("//a/@href").all();

        List<String > urls = new ArrayList<String>();

        for(String temp : tempurls){
          //  String[] datas= temp.split("','");
           // String p = datas[3].substring(0, datas[3].indexOf("^^"));
           System.out.println(temp) ;
            urls.add(baseurl + temp) ;
        }

         page.addTargetRequests(urls);




        //  System.out.print(url);


        // 部分三：从页面发现后续的url地址来抓取
        // page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new Test())
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/23/2305.html")
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
               // .addPipeline(new JsonFilePipeline("D:\\aaaa\\"))
                .run();
    }
}