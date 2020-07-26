package db.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

public class VoteCountsClubAlpha {

    private String voteCast;
    private Integer countVotesCast;

    public VoteCountsClubAlpha() {
        super();
    }

    public VoteCountsClubAlpha(String voteCast, Integer countVotesCast) {
        this.voteCast = voteCast;
        this.countVotesCast = countVotesCast;
    }

    public String getVoteCast() {return voteCast;}

    public void setVoteCast(String voteCast) {this.voteCast = voteCast;}

    public Integer getCountVotesCast() {return countVotesCast;}

    public void setCountVotesCast(Integer countVotesCast) {this.countVotesCast = countVotesCast;}
}
