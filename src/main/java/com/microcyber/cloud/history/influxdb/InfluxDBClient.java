package com.microcyber.cloud.history.influxdb;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.microcyber.cloud.history.web.RawDataListener;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import com.alibaba.fastjson.JSONObject;
import com.microcyber.cloud.history.model.KafkaModel;
import com.microcyber.cloud.history.task.DataItemScheduleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Influx DB客户端，支持常用的数据库访问接口。
 * @author zhaohongwei
 */
public class InfluxDBClient {
    private static final Logger logger = LoggerFactory.getLogger(InfluxDBClient.class);
    private String username;// 用户名
    private String password;// 密码
    private String url;// 连接地址
    private String database;// 数据库
    private InfluxDB influxDB;
    
    public InfluxDBClient(String username, String password, String url, String database) {
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
    public void batchInsert(List<KafkaModel> datas,Map json_data_types,Map json_data_save) throws Exception {
        BatchPoints batchPoints = BatchPoints.database(database).build();
        for(KafkaModel data : datas) {
        data.getDataItems().forEach((k, v) -> {
        	String data_item_id = k.toString();
        	Point point = null;
        	String data_type = json_data_types.get(data_item_id).toString();
        	String data_save_db = json_data_save.get(data_item_id).toString();
        	//Boolean.valueOf(data_save_db)
            data_save_db="1";
        	if("1".equals(data_save_db)) {
        	long utc = 0;//influxdb中正常存储的是北京时间-8小时的时间,原生存储可以进行group by 分组操作
        	if("2".equals(data_type)){
        		 point = Point.measurement(data.getDeviceId())
                        .tag("ID", data_item_id)
                        .tag("Name",getDataItemName(data_item_id))
                        .tag("type",data_type)
                        .time(Long.parseLong(data.getTime())+utc, TimeUnit.MILLISECONDS)
                        .addField("float_value",Double.valueOf(v.toString())).build();
            }else if("1".equals(data_type)){
            	 point = Point.measurement(data.getDeviceId())
                        .tag("ID", data_item_id)
                        .tag("Name",getDataItemName(data_item_id))
                        .tag("type",data_type)
                        .time(Long.parseLong(data.getTime())+utc, TimeUnit.MILLISECONDS)
                        .addField("int_value",Integer.valueOf(v.toString())).build();
            }else if("4".equals(data_type)){
            	 point = Point.measurement(data.getDeviceId())
                        .tag("ID", data_item_id)
                        .tag("Name",getDataItemName(data_item_id))
                        .tag("type",data_type)
                        .time(Long.parseLong(data.getTime())+utc, TimeUnit.MILLISECONDS)
                        .addField("bool_value",Boolean.valueOf(v.toString())).build();
            }else if("3".equals(data_type) ){
            	 point = Point.measurement(data.getDeviceId())
                        .tag("ID", data_item_id)
                        .tag("Name",getDataItemName(data_item_id))
                        .tag("type",data_type)
                        .time(Long.parseLong(data.getTime())+utc, TimeUnit.MILLISECONDS)
                        .addField("str_value",v.toString()).build();
            }
            batchPoints.point(point);
        	}
        });
        }
        logger.info("写入数量：" +batchPoints.getPoints().size());
        try {
            influxDB.write(batchPoints);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
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
}