package com.cnwanj.esjd.utils;

import com.cnwanj.esjd.entity.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: cnwanj
 * @date: 2021-06-14 11:37:27
 * @version: 1.0
 * @desc: 解析爬取页面信息
 */
public class JsoupUtil {

    public List<Content> parseJD(String keyword) throws Exception {
        // 要解析的页面链接
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        // 解析网页获取文档
        Document document = Jsoup.parse(new URL(url), 3000);
        // 获取标签元素
        Element element = document.getElementById("J_goodsList");
        Elements lis = element.getElementsByTag("li");
        List<Content> list = new ArrayList<>();
        for (Element li : lis) {
            String title = li.getElementsByClass("p-name").eq(0).text();
            String img = li.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = li.getElementsByClass("p-price").eq(0).text();
            list.add(new Content(title, img, price));
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        List<Content> java = new JsoupUtil().parseJD("java");
        System.out.println(java.size());
        java.forEach(a -> {
            System.out.println("================");
            System.out.println(a.toString());
        });
    }
}
