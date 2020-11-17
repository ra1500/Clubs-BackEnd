package db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

@Entity
@Table
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date created;

    @Column
    private Long lengthX;

    @Column
    private Long lengthY;

    //@JsonIgnore
    @OneToMany(mappedBy = "id", fetch = FetchType.EAGER)
    private List<GameCell> gameCells = new ArrayList<>();

    public Game() {
        super();
    }

    public Game(Long id, Date created, Long lengthX, Long lengthY, List<GameCell> gameCells) {
        this.id = id;
        this.created = created;
        this.lengthX = lengthX;
        this.lengthY = lengthY;
        this.gameCells = gameCells;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getLengthX() {
        return lengthX;
    }

    public void setLengthX(Long lengthX) {
        this.lengthX = lengthX;
    }

    public Long getLengthY() {
        return lengthY;
    }

    public void setLengthY(Long lengthY) {
        this.lengthY = lengthY;
    }

    public List<GameCell> getGameCells() {
        return gameCells;
    }

    public void setGameCells(List<GameCell> gameCells) {
        this.gameCells = gameCells;
    }
}

