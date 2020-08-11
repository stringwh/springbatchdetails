package com.jrx.springbatchgather.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wh
 * @Description TODO
 * @date 2020/8/10-14:33
 */
public class detailsinfo {

    private String s_index;//索引项
    private int cust_id;//客户号
    private Date update_time;//更新时间
    private Date trans_date;//日期
    private String surname;
    private BigDecimal tran_max_amt;//最大单笔交易金额
    private BigDecimal pay_amt;//当天还款总金额；
    private int tran_cnt;//当天消费笔数；
    private int pay_cnt;//当天还款笔数
    private BigDecimal tran_amt;//当天交易总金额;
    public String getS_index() {
        return s_index;
    }

    public void setS_index(String s_index) {
        this.s_index = s_index;
    }

    public int getCust_id() {
        return cust_id;
    }

    public void setCust_id(int cust_id) {
        this.cust_id = cust_id;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Date getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(Date trans_date) {
        this.trans_date = trans_date;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public BigDecimal getTran_max_amt() {
        return tran_max_amt;
    }

    public void setTran_max_amt(BigDecimal tran_max_amt) {
        this.tran_max_amt = tran_max_amt;
    }

    public BigDecimal getPay_amt() {
        return pay_amt;
    }

    public void setPay_amt(BigDecimal pay_amt) {
        this.pay_amt = pay_amt;
    }

    public int getTran_cnt() {
        return tran_cnt;
    }

    public void setTran_cnt(int tran_cnt) {
        this.tran_cnt = tran_cnt;
    }

    public int getPay_cnt() {
        return pay_cnt;
    }

    public void setPay_cnt(int pay_cnt) {
        this.pay_cnt = pay_cnt;
    }

    public BigDecimal getTran_amt() {
        return tran_amt;
    }

    public void setTran_amt(BigDecimal tran_amt) {
        this.tran_amt = tran_amt;
    }

    @Override
    public String toString() {
        return "detailsinfo{" +
                "s_index='" + s_index + '\'' +
                ", cust_id=" + cust_id +
                ", update_time=" + update_time +
                ", trans_date=" + trans_date +
                ", surname='" + surname + '\'' +
                ", tran_max_amt=" + tran_max_amt +
                ", pay_amt=" + pay_amt +
                ", tran_cnt=" + tran_cnt +
                ", pay_cnt=" + pay_cnt +
                ", tran_amt=" + tran_amt +
                '}';
    }
}
