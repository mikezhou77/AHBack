package per.macc.ahback.bean;

/**
 * 客房的Bean
 * Created by Macc on 2016/6/21.
 */
public class Room {
    private String rno;
    private int rtno;

    public Room(){}

    public Room(String rno, int rtno)
    {
        this.rno = rno;
        this.rtno = rtno;
    }

    public String getRno() {
        return rno;
    }

    public void setRno(String rno) {
        this.rno = rno;
    }

    public int getRtno() {
        return rtno;
    }

    public void setRtno(int rtno) {
        this.rtno = rtno;
    }

}