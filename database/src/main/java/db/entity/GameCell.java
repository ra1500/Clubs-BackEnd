package db.entity;

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
public class GameCell implements Serializable, Comparable<GameCell> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date created;

    @Column
    private Long locationX;

    @Column
    private Long locationY;

    @Column
    private Long locationNumber;

    @Column
    private Long points;

    @Column
    private Long type;  // 1regular, 2key, 3escape

    @Column
    private Long upValue;

    @Column
    private Long downValue;

    @Column
    private Long leftValue;

    @Column
    private Long rightValue;

    @Column
    private Long upRightValue;

    @Column
    private Long upLeftValue;

    @Column
    private Long downLeftValue;

    @Column
    private Long downRightValue;

    public GameCell() {
        super();
    }

    public GameCell(Long id, Date created, Long locationX, Long locationY, Long locationNumber, Long points, Long type, Long upValue, Long downValue, Long leftValue, Long rightValue, Long upRightValue, Long upLeftValue, Long downLeftValue, Long downRightValue) {
        this.id = id;
        this.created = created;
        this.locationX = locationX;
        this.locationY = locationY;
        this.locationNumber = locationNumber;
        this.points = points;
        this.type = type;
        this.upValue = upValue;
        this.downValue = downValue;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.upRightValue = upRightValue;
        this.upLeftValue = upLeftValue;
        this.downLeftValue = downLeftValue;
        this.downRightValue = downRightValue;
    }

    @Override
    public int compareTo(GameCell u) {
        if (getLocationNumber() == null || u.getLocationNumber() == null) {
            return 0;
        }
        return getLocationNumber().compareTo(u.getLocationNumber());
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

    public Long getLocationX() {
        return locationX;
    }

    public void setLocationX(Long locationX) {
        this.locationX = locationX;
    }

    public Long getLocationY() {
        return locationY;
    }

    public void setLocationY(Long locationY) {
        this.locationY = locationY;
    }

    public Long getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(Long locationNumber) {
        this.locationNumber = locationNumber;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getUpValue() {
        return upValue;
    }

    public void setUpValue(Long upValue) {
        this.upValue = upValue;
    }

    public Long getDownValue() {
        return downValue;
    }

    public void setDownValue(Long downValue) {
        this.downValue = downValue;
    }

    public Long getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(Long leftValue) {
        this.leftValue = leftValue;
    }

    public Long getRightValue() {
        return rightValue;
    }

    public void setRightValue(Long rightValue) {
        this.rightValue = rightValue;
    }

    public Long getUpRightValue() {
        return upRightValue;
    }

    public void setUpRightValue(Long upRightValue) {
        this.upRightValue = upRightValue;
    }

    public Long getUpLeftValue() {
        return upLeftValue;
    }

    public void setUpLeftValue(Long upLeftValue) {
        this.upLeftValue = upLeftValue;
    }

    public Long getDownLeftValue() {
        return downLeftValue;
    }

    public void setDownLeftValue(Long downLeftValue) {
        this.downLeftValue = downLeftValue;
    }

    public Long getDownRightValue() {
        return downRightValue;
    }

    public void setDownRightValue(Long downRightValue) {
        this.downRightValue = downRightValue;
    }
}
