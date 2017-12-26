package tilda.bot.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

public class AWSUtil {
    private static AmazonDynamoDB awsDB;

    public static void setupDB(AmazonDynamoDB db) {
        awsDB = db;
    }

    public static Table getTable(String tableName) {
        DynamoDB db = new DynamoDB(awsDB);
        return db.getTable(tableName);
    }
}
