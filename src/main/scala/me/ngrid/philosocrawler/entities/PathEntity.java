package me.ngrid.philosocrawler.entities;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name = "philosophy_paths")
public class PathEntity {

    @Id
    @Column(name = "page_id")
    public String pageId;

    @Column(name = "page_url")
    public String pageUrl;

    @Lob
    @Column(name = "path_to_philosophy")
    public String path;
}
