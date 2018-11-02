package site.binghai.biz.utils;


public class AppPushMsg {
    private int id;
    private String title;
    private String content;
    private String toWho;
    private String pass;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToWho() {
        return this.toWho;
    }

    public void setToWho(String toWho) {
        this.toWho = toWho;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}

