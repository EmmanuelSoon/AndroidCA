package nus.iss.androidca.wrapper;

public class RankingList {

    private String userName;
    private Integer score;

    public RankingList(){}

    public RankingList(String userName, Integer score)
    {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName()
    {
        return userName;
    }

    public Integer getScore()
    {
        return score;
    }

}
