package ch.nedlox.chattermatter;
import java.util.Date;

public class Post {

    private String location, voice;
    private Date date;
    private int userId, postId;

    public Post(String location, String voice, Date date, int userId, int postId){
        this.location = location;
        this.date = date;
        this.userId = userId;
        this.postId = postId;
        // TODO implement base64 decode
        this.voice = voice;
    }

    public String getLocation(){
        return this.location;
    }
    public String getVoice(){
        return this.voice;
    }
    public Date getDate(){
        return this.date;
    }
    public int getUserId(){
        return this.userId;
    }
    public int getPostId(){
        return this.postId;
    }

}