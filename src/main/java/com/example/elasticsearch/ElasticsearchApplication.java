package com.example.elasticsearch;

import com.example.elasticsearch.config.EmptyTool;
import com.example.elasticsearch.repository.OrderEsRepo;
import com.example.elasticsearch.repository.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication
public class ElasticsearchApplication implements CommandLineRunner {

    @Value("${bet.quantity}")
    private Integer betQuantity;
    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static Calendar DATE_INSTANCE = Calendar.getInstance();
    public static final ObjectMapper JSON_PARSER = new ObjectMapper();
    public static final Random RANDOM = new Random();
    @Autowired
    private OrderEsRepo orderEsRepo;

    public static void main(String[] args)  {
//        SpringApplication.run(ElasticsearchApplication.class, args);
        Order order = new Order();
        order.setProfitAmount(25f);
        boolean isContainsEmptyField = EmptyTool.containsEmptyField(Order.class,order);
        System.out.println("檢測 空白數據 isContainsEmptyField:" + isContainsEmptyField);
        Order emptyOrder = new Order();
        emptyOrder.setProfitAmount(0f);
        boolean isContainsEmptyField2 = EmptyTool.containsEmptyField(Order.class,emptyOrder);
        System.out.println("檢測 空白數據 isContainsEmptyField:" + isContainsEmptyField2);
    }

    @Override
    public void run(String... args) throws JsonProcessingException {
        int count = 0;
        String[] platform = {"AGIN", "BBIN", "AGQJ", "CQK", "IM", "AG"};
        String[] loginName = {"iccEnn57864", "kenny5172", "qwertty4111", "benbii55gg", "kitty01", "Mariafishc222", "MariafloodSx",
                "Jerdogfoxx", "Jerantpieusdt", "Jerwall·eusdt", "pqieiok-1usdt", "pqieseausdt", "ppdasd54waterusdt", "sunspirit98usdt",
                "sunspirit111usdt", "louisaskisusdt","louisadogusdt","louisaharpusdt",
                "DC518operausdt","accordiontubausdt","accordionrockyusdt"};
        String[] gameKind = {"1", "3", "9"};

        //gameType
        String[] sportArray = {"football", "baseball", "basketball"};
        String[] tableArray = {"BAC", "ROL", "Texas", "SicBo", "FamilyBAC"};
        String[] lottery = {"110PercentLottery"};

        Integer[] yearsArray = {2023};
        Integer[] monthArray = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE,
                Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER};
        int dateLimit = 28;
        int hourLimit = 24;
        int minLimit = 59;
        int secondLimit = 59;
        List<Order> orderEntityList = new ArrayList<>();
        /**
         * ES 製作單子的次數
         */
        for (int n = 0; n < betQuantity; n++) {
            Order orderEntity = new Order();
            double betAmount = Math.random() * 1000;
            randomOrderContent(orderEntity, platform, loginName, gameKind, sportArray, tableArray, lottery);
            betAmount(orderEntity, betAmount);
            winOrLose(orderEntity, betAmount);
            betTimeLast48Hours(orderEntity, yearsArray, hourLimit, minLimit, secondLimit);
            System.out.println(JSON_PARSER.writeValueAsString(orderEntity));
            count += 1;
            System.out.println("Elasticsearch 注單生成計數:" + count);
            orderEntityList.add(orderEntity);
        }
        orderEsRepo.saveAll(orderEntityList);
    }

    /**
     * 隨機平台的遊戲注單類型
     */
    private void randomOrderContent(Order orderEntity,
                                    String[] platform,
                                    String[] loginName,
                                    String[] gameKind,
                                    String[] sportArray,
                                    String[] tableArray,
                                    String[] lottery) { 
        int platformIndex = (int) (Math.random() * platform.length);
        orderEntity.setPlatform(platform[platformIndex]);

        int loginNameIndex = (int) (Math.random() * loginName.length);
        orderEntity.setLoginName(loginName[loginNameIndex]);

        int gameKindIndex = (int) (Math.random() * gameKind.length);
        switch (gameKindIndex) {
            case 0:
                int sportArrayIndex = (int) (Math.random() * sportArray.length);
                orderEntity.setGameKind(gameKind[0]);
                orderEntity.setGameType(sportArray[sportArrayIndex]);
                break;
            case 1:
                int tableArrayIndex = (int) (Math.random() * tableArray.length);
                orderEntity.setGameKind(gameKind[1]);
                orderEntity.setGameType(tableArray[tableArrayIndex]);
                break;
            case 2:
                int lotteryIndex = (int) (Math.random() * lottery.length);
                orderEntity.setGameKind(gameKind[2]);
                orderEntity.setGameType(lottery[lotteryIndex]);
        }
        orderEntity.setOrderNo(UUID.randomUUID()+"_"+platform[platformIndex]);
    }


    /**
     * 輸贏計算
     */
    private void winOrLose(Order orderEntity, double betAmount) {
        boolean winOrLose = ((int) (Math.random() * 10.9999)) == 2;
        if (winOrLose) {
            int profit = (int) (Math.random() * 1000);
            orderEntity.setProfitAmount(BigDecimal.valueOf((float) betAmount + profit).setScale(2, RoundingMode.HALF_UP).floatValue());
        } else {
            orderEntity.setProfitAmount(-orderEntity.getValidBetAmount());
        }
    }

    /**
     * 投注金額
     */
    private void betAmount(Order orderEntity, double betAmount) {
        float v = BigDecimal.valueOf(betAmount).setScale(3, BigDecimal.ROUND_HALF_DOWN).floatValue();
        orderEntity.setBetAmount(v);
        orderEntity.setValidBetAmount(v);
    }

    /**
     * 投注時間
     */
    private void betTime(Order orderEntity,
                         Integer[] yearsArray,
                         Integer[] monthArray,
                         Integer dateLimit,
                         Integer hourLimit,
                         Integer minLimit,
                         Integer secondLimit
    ) {
        int month = (int) (Math.random() * monthArray.length);
        int date = ((int) (Math.random() * dateLimit)) + 1;
        int hour = (int) (Math.random() * hourLimit);
        int min = (int) (Math.random() * minLimit);
        int second = (int) (Math.random() * secondLimit);
        DATE_INSTANCE.set(yearsArray[0], monthArray[month], date, hour, min, second);
        orderEntity.setBetDate(DATE_INSTANCE.getTime());
    }

    /**
     * 投注時間
     */
    private void betTimeLast48Hours(Order orderEntity,
                         Integer[] yearsArray,
                         Integer hourLimit,
                         Integer minLimit,
                         Integer secondLimit
    ) {
        Date now = new Date();
        int day = now.getDate();
        if(now.getDate() == 1){
            if(now.getMonth() == Calendar.FEBRUARY){
                //潤年
                if(now.getYear() % 4 == 0){
                    day = 29;
                } else {
                    day = 28;
                }
            } else {
                if(now.getMonth() == Calendar.JANUARY || now.getMonth() == Calendar.MARCH || now.getMonth() == Calendar.MAY ||
                        now.getMonth() == Calendar.JULY || now.getMonth() == Calendar.AUGUST || now.getMonth() == Calendar.OCTOBER ||
                        now.getMonth() == Calendar.DECEMBER){
                    day = 31;
                } else {
                    day = 30;
                }
            }
        }
        int date = ((int) (Math.random() * 1.9999)) == 0 ? day : day - 1;
        int hour = (int) (Math.random() * hourLimit);
        int min = (int) (Math.random() * minLimit);
        int second = (int) (Math.random() * secondLimit);
        DATE_INSTANCE.set(yearsArray[0],now.getMonth(), date, hour, min, second);
        orderEntity.setBetDate(DATE_INSTANCE.getTime());
    }

    private int[] test(){
        return new int[]{0};
    }
}
