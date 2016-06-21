package per.macc.ahback.bean;

/**
 * 客房类型Bean
 * Created by Macc on 2016/6/13.
 */
public class Roomtype
{
    private int rtno;
    private int rtsize;
    private int rtnum;
    private String rtname;
    private String rtinfo;
    private String otherinfo;
    private int baseprice;
    private int newprice;
    public Roomtype(){}

    public Roomtype(int rtno, int rtsize, int rtnum, String rtname, String rtinfo, String otherinfo, int baseprice, int newprice)
    {
        this.rtno = rtno;
        this.rtsize = rtsize;
        this.rtnum = rtnum;
        this.rtinfo = rtinfo;
        this.otherinfo = otherinfo;
        this.rtname = rtname;
        this.baseprice = baseprice;
        this.newprice = newprice;
    }

    public int getRtno() {
        return rtno;
    }

    public void setRtno(int rtno) {
        this.rtno = rtno;
    }

    public int getRtsize() {
        return rtsize;
    }

    public void setRtsize(int rtsize) {
        this.rtsize = rtsize;
    }

    public int getRtnum() {
        return rtnum;
    }

    public void setRtnum(int rtnum) {
        this.rtnum = rtnum;
    }

    public String getRtname() {
        return rtname;
    }

    public void setRtname(String rtname) {
        this.rtname = rtname;
    }

    public String getRtinfo() {
        return rtinfo;
    }

    public void setRtinfo(String rtinfo) {
        this.rtinfo = rtinfo;
    }

    public String getOtherinfo() {
        return otherinfo;
    }

    public void setOtherinfo(String otherinfo) {
        this.otherinfo = otherinfo;
    }

    public int getBaseprice() {
        return baseprice;
    }

    public void setBaseprice(int baseprice) {
        this.baseprice = baseprice;
    }

    public int getNewprice() {
        return newprice;
    }

    public void setNewprice(int newprice) {
        this.newprice = newprice;
    }
}
