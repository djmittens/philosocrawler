package me.ngrid.philosocrawler.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.HashSet;

/**
 *
 */
@Entity
@Table(name = "philosophy_paths")
public class PathToPhilosophy {
    @Id
    @NotEmpty
    @Column(name = "url_path")
    public String urlPath;

    @Lob
    @Column(name = "path_to_philosophy")
    public HashSet<String> pages = new HashSet<>();
}
