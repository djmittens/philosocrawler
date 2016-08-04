package me.ngrid.philosocrawler.entities;

public class PageEntity {
    public String id ;
    public String title;
    public String url;

    public PageEntity(){}

    public PageEntity(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }
}
