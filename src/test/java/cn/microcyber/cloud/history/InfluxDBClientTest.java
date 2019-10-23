package cn.microcyber.cloud.history;

import com.microcyber.cloud.history.model.KafkaModel;
import com.microcyber.cloud.history.task.DataItemScheduleTask;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Influx DB客户端，支持常用的数据库访问接口。
 * @author zhaiyunfeng
 */

/**
 * @author Administrator
 *
 */
public class InfluxDBClientTest {
    private static final Logger logger = LoggerFactory.getLogger(InfluxDBClientTest.class);
    private String username;// 用户名
    private String password;// 密码
    private String url;// 连接地址
    private String database;// 数据库
    private InfluxDB influxDB;
    
    public InfluxDBClientTest(String username, String password, String url, String database) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.database = database;
    }

    /**
     * 连接时序数据库；获得InfluxDB
     **/
    public InfluxDB influxDbBuild() {
        if (influxDB == null) {
            influxDB = InfluxDBFactory.connect(url, username, password);
            //influxDB.enableBatch(0,0,TimeUnit.MILLISECONDS);
            //启用批量写入
            influxDB.enableBatch(BatchOptions.DEFAULTS);
            influxDB.createDatabase(database);
        }
        return influxDB;
    }

    /**
     * 设置数据保存策略 defalut 策略名 /database 数据库名/ 30d 数据保存时限30天/ 1 副本个数为1/ 结尾DEFAULT
     * 表示 设为默认的策略
     */
    public void createRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                "defalut", database, "30d", 1);
        this.query(command);
    }

    /**
     * 查询
     *
     * @param command 查询语句
     * @return
     */
    public QueryResult query(String command) {
        return influxDB.query(new Query(command, database));
    }

    /**
     * 插入
     *
     * @param measurement 表
     * @param tags        标签
     * @param fields      字段
     */
    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields) {
        Builder builder = Point.measurement(measurement);
        //builder.time(((long)fields.get("currentTime"))*1000000, TimeUnit.MILLISECONDS);
        builder.tag(tags);
        builder.fields(fields);
        //
        influxDB.write(database, "", builder.build());
    }

    /**
     * 历史数据批量写入
     * @param
     * @throws Exception
     */
    public void batchInsert(List<Map> data,String measurement) throws Exception {
        BatchPoints batchPoints = BatchPoints.database(database).build();
        for(int i = 0;i<data.size();i++){
            Map m = data.get(i);
            Point point = null;
            String id = m.get("id").toString();
            String room = m.get("room").toString();
            String type = m.get("type").toString();
            String value1 =m.get("value1").toString();
            Integer value2 = Integer.valueOf(m.get("value2").toString());
            Double value3 = Double.valueOf(m.get("value3").toString());
            Double value4 = Double.valueOf(m.get("value4").toString());
            Double value5 = Double.valueOf(m.get("value5").toString());
            Double value6 = Double.valueOf(m.get("value6").toString());

            point = Point.measurement(measurement)
                    .tag("ID", id)
                    .tag("room",room)
                    .tag("type",type)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField("desc",value1).addField("size",value2).addField("yali",value3).build();
            batchPoints.point(point);
        }
        logger.error("写入数量：" +batchPoints.getPoints().size());
        influxDB.write(batchPoints);
    }

    /**
     * 删除Measurement数据库
     *
     * @param command 删除语句
     * @return 返回错误信息
     */
    public String deleteMeasurementData(String command) {
        QueryResult result = influxDB.query(new Query(command, database));
        return result.getError();
    }
    
     /**
      * param id 条目名称对应的id
     * @return 返回数据类型名称
     */
    public String getDataItemName(String data_item_id) {
    	 String data_item_name = "";
    	 Map data_item_names = DataItemScheduleTask.data_item_names;
    	 data_item_name = data_item_names.get(data_item_id).toString();
    	 return data_item_name;
     }
    /**
     * 创建数据库
     *
     * @param dbName
     */
    public void createDB(String dbName) {
        influxDB.createDatabase(dbName);
    }

    /**
     * 删除数据库
     *
     * @param dbName
     */
    public void deleteDB(String dbName) {
        influxDB.deleteDatabase(dbName);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
    public static void main(String[] args){

        int a = 0;
        InfluxDBClientTest influx=new InfluxDBClientTest("root","123456","http://192.168.2.196:8086","testRp");
        influx.influxDbBuild();
        List<Map> lm = new ArrayList<Map>();
        String value = "你好中国";
        String value15 = "15";
        for (int i = 0;i<50;i++){
            a++;
            Map m = new HashMap();
            m.put("id",new Random().nextInt(10));
            m.put("room",new Random().nextInt(10));
            m.put("type",new Random().nextInt(10));
            m.put("value1",value+i);
            m.put("value2",i);
            m.put("value3",value15+i/100/100);
            m.put("value4",value15+i/100/50);
            m.put("value5",value15+i/100/40);
            m.put("value6",value15+i/100/30);

            lm.add(m);
            if(a%10==0){
                try {
                    influx.batchInsert(lm,"countData");
                    lm.clear();
                    a = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.exit(-1);
    }
}