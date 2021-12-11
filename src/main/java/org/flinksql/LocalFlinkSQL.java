package org.flinksql;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import java.io.*;

public class LocalFlinkSQL {
    public static void main( String[] args ) throws Exception {
        StreamExecutionEnvironment execEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment env = StreamTableEnvironment.create(execEnv, settings);

        String filePath = LocalFlinkSQL.class.getClassLoader().getResource("FlinkRunning.sql").getPath();
        String flinkSQLs = readFlinkSQL(filePath);
        String[] sqls = flinkSQLs.split(";");
        for (String sql : sqls) {
            sql = sql.replaceAll("[ ]+", " ");
            System.out.println(sql);
            env.executeSql(sql).print();
        }
    }

    public static String readFlinkSQL(String filePath) throws IOException {
        String sqlString = "";
        InputStreamReader read = null;

        File file = new File(filePath);
        read = new InputStreamReader(new FileInputStream(file), "UTF-8");
        if(file.isFile() && file.exists()) {
            @SuppressWarnings("resource")
            BufferedReader bufferedReader = new BufferedReader(read);
            String txt = null;
            while((txt = bufferedReader.readLine()) != null){
                sqlString += "\n" + txt;
            }
        } else {
           System.out.println("Error: FlinkSQL file not found!");
        }
        read.close();

        return sqlString;
    }
}
