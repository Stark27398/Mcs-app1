package app.mcs.projectoneeight.techhub;

import java.util.Date;

public class Forum {
    String title;
    String creator;
    long updateTime;
    public Forum(String t, String creator){
        this.title=t;
        this.creator=creator;
        this.updateTime=new Date().getTime();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
