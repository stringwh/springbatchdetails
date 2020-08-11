package com.jrx.springbatchgather.sumjob;

import com.jrx.springbatchgather.pojo.customerinfo;
import com.jrx.springbatchgather.pojo.detailsinfo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wh
 * @Description TODO
 * @date 2020/8/10-13:53
 */
@Configuration
@EnableBatchProcessing
public class sumConfig {
    private static final int ZERO = 0;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Autowired
    private DataSource dataSource;

    @Bean
    public Job sumJob12(@Qualifier("sumStep") Step sumStep) {
        return jobBuilderFactory.get("sumJob12").start(sumStep).build();

    }

    @Bean
    public Step sumStep(@Qualifier("sumReader") ItemReader sumReader,
                        @Qualifier("sumProcessor") ItemProcessor sumProcessor,
                        @Qualifier("sumWriter") ItemWriter sumWriter) {
        return stepBuilderFactory.get("sumStep").<customerinfo, List<detailsinfo>>chunk(10)
                .reader(sumReader)
                .processor(sumProcessor).writer(sumWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<? extends List<customerinfo>> sumReader() {
        JdbcPagingItemReader<List<customerinfo>> reader = new JdbcPagingItemReader<>();

        List<customerinfo> list = new ArrayList<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(2);//分页读取一次取两个
        //转成对象
        reader.setRowMapper(new RowMapper<List<customerinfo>>() {
            @Override
            public List<customerinfo> mapRow(ResultSet resultSet, int i) throws SQLException {


                customerinfo c1 = new customerinfo();
                c1.setCustId(resultSet.getInt(1));
                c1.setSurname(resultSet.getString(2));
                c1.setGender(resultSet.getString(3));
                c1.setEducaDes(resultSet.getString(4));
                c1.setMarDes(resultSet.getString(5));
                c1.setBirthday(resultSet.getInt(6));
                c1.setAddress(resultSet.getString(7));
                c1.setTransId(resultSet.getInt(8));
                c1.setAccount(resultSet.getString(9));
                c1.setCardNbr(resultSet.getString(10));
                c1.setTranno(resultSet.getInt(11));
                c1.setMonthNbr(resultSet.getInt(12));
                c1.setBill(resultSet.getBigDecimal(13));
                c1.setTransType(resultSet.getString(14));
                c1.setTxnDatetime(resultSet.getDate(15));
                list.add(c1);
                list.stream().forEach(p -> System.out.println(p + "read"));
                System.out.println(" ");
                return list;
            }
        });
        //指定sql语句
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("select c.cust_id,c.surname,c.gender,c.educa_des,c.mar_des,c.birthday,c.address,t.trans_id,t.account,t.card_nbr,t.tranno,t.month_nbr,t.bill,t.trans_type,t.txn_datetime");//指定字段
        provider.setFromClause("from customer c left join transaction_details t on c.cust_id = t.cust_id");//指定那个表
        reader.setPageSize(30);
        //根据哪个字段进行排序
        Map<String, Order> sort = new HashMap<>(2);//指定初始大小
        sort.put("cust_id", Order.ASCENDING);//Order.ASCENDING升序,Order.DESCENDING降序
        provider.setSortKeys(sort);//把Map给provider
        reader.setQueryProvider(provider);//把provider赋给reader对
        System.out.println("  1");
        return reader;
    }

    @Bean
    public ItemProcessor<List<customerinfo>, detailsinfo> sumProcessor() throws ParseException {
        //更新时间
        Date nowdate = new Date();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//          driver.setRelDate(Timestamp.valueOf(simpleDate.format(nowdate)));
        Date update_time = Timestamp.valueOf(simpleDate.format(nowdate));
        List<customerinfo> list = new ArrayList<>();
        //日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowdayTime = dateFormat.format(nowdate);
        Date trans_date = dateFormat.parse(nowdayTime);
        return new ItemProcessor<List<customerinfo>, detailsinfo>() {
            int i=0;
            @Override

            public detailsinfo process(List<customerinfo> customerinfo1) throws Exception {
                i=i+1;
                System.out.println("进入："+i+"次");
                List<detailsinfo> list1 = new ArrayList<>();
                System.out.println("pocess");
                customerinfo1.stream().forEach(p -> System.out.println(p));
                Map<Integer, List<customerinfo>> map =
                        customerinfo1.stream().collect(Collectors.groupingBy(customerinfo::getCustId));
                map.values().forEach(customerinfo2 -> {
                    detailsinfo detais = new detailsinfo();
                    BigDecimal tran_max_amt = customerinfo2.stream().map(customerinfo::getBill).filter(Objects::nonNull).
                            max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                    Integer Cust_id = customerinfo2.get(ZERO).getCustId();
                    detais.setSurname(customerinfo2.get(0).getSurname());
                    detais.setUpdate_time(update_time);
                    detais.setS_index(Utiluuid.getUuid());
                    detais.setTran_max_amt(tran_max_amt);
                    detais.setCust_id(Cust_id);
                    detais.setTrans_date(trans_date);
                    BigDecimal pay_amt = customerinfo2.stream().filter(item -> item.getTransType() != null && item.getTransType().equals("还款")).map(customerinfo::getBill)
                            .filter(Objects::nonNull)
                            .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                    detais.setPay_amt(pay_amt);
                    int tran_cnt = (int) customerinfo2.stream().filter(item -> item.getTransType() != null && item.getTransType().equals("消费")).count();
                    detais.setTran_cnt(tran_cnt);
                    int pay_cnt = (int) customerinfo2.stream().filter(item -> item.getTransType() != null && item.getTransType().equals("还款")).count();
                    detais.setPay_cnt(pay_cnt);
                    BigDecimal tran_amt = customerinfo2.stream().map(customerinfo::getBill).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                    detais.setTran_amt(tran_amt);
                    list1.add(detais);
                    System.out.println(detais + "detais");
                });
                if((i-1)>=list1.size()){
                    System.out.println("错误");
                    return null;
                }else {
                    System.out.println(": "+list1.size()+" :"+"进入"+i+"次");
                    return list1.get(i - 1);
                }
            }
        };

    }

    @Bean
    @StepScope
    public ItemWriter<? extends List<detailsinfo>> sumWriter() {

        System.out.println("writer");
        System.out.println();
        JdbcBatchItemWriter<List<detailsinfo>> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("insert into detailsinfo(s_index,cust_id,update_time,trans_date,surname,tran_max_amt,pay_amt,tran_cnt,pay_cnt,tran_amt) values "
                + "(:s_index,:cust_id,:update_time,:trans_date,:surname,:tran_max_amt,:pay_amt,:tran_cnt,:pay_cnt,:tran_amt)");
        //把对象属性的值映射到对应的占位符中
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());//*/
        return writer;


    }
}
