package com.jrx.springbatchgather.sumjob;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Utiluuid {
    public static String getUuid(){
        String uuid = "";
        LocalDateTime date = LocalDateTime.now();
        for(int i=0;i<10;i++){
            uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0,9);
            String format = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            uuid= uuid +"_"+ format.replace("-","");
        }
        return uuid;
    }
}