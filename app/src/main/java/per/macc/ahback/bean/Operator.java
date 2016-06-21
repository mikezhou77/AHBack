package per.macc.ahback.bean;

import java.util.Date;

/**
 * 操作员Bean
 * Created by Macc on 2016/6/5.
 */
public class Operator {
    private int ono;
    private String ousername;
    private String opassword;
    private Date ologin_time;
    private Date ologout_time;
    public Operator(){}

    public Operator(int ono, String ousername, String opassword, Date ologin_time, Date ologout_time)
    {
        this.ono = ono;
        this.ousername = ousername;
        this.opassword = opassword;
        this.ologin_time = ologin_time;
        this.ologout_time = ologout_time;
    }

    public int getOno() {
        return ono;
    }

    public void setOno(int ono) {
        this.ono = ono;
    }

    public String getOusername() {
        return ousername;
    }

    public void setOusername(String ousername) {
        this.ousername = ousername;
    }

    public String getOpassword() {
        return opassword;
    }

    public void setOpassword(String opassword) {
        this.opassword = opassword;
    }

    public Date getOlogin_time() {
        return ologin_time;
    }

    public void setOlogin_time(Date ologin_time) {
        this.ologin_time = ologin_time;
    }

    public Date getOlogout_time() {
        return ologout_time;
    }

    public void setOlogout_time(Date ologout_time) {
        this.ologout_time = ologout_time;
    }

}