package org.flinksql;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.io.*;

public class LocalFlinkSQL {
    public static void main( String[] args ) throws Exception {
        StreamExecutionEnvironment execEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment env = StreamTableEnvironment.create(execEnv, settings);

        String runningSQL = readRunningSQL();
        String[] sqls = runningSQL.split(";");
        for (String sql : sqls) {
            sql = sql.replace("\n", " ").replaceAll("\\s+", " ");
            System.out.println(sql);
            env.executeSql(sql).print();
        }
    }

    public static String readRunningSQL() throws IOException {
        String sqlString = "";
        InputStreamReader read = null;
        String filePath = LocalFlinkSQL.class.getClassLoader().getResource("running.sql").getPath();

        File file = new File(filePath);
        read = new InputStreamReader(new FileInputStream(file), "UTF-8");
        if(file.isFile() && file.exists()) {
            @SuppressWarnings("resource")
            BufferedReader bufferedReader = new BufferedReader(read);
            String txt = null;
            while((txt = bufferedReader.readLine()) != null){
                sqlString += " " + txt;
            }
        } else {
           System.out.println("Error: 文件不存在");
        }
        read.close();

        return sqlString;
    }
}
