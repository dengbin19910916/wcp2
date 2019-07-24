package io.xxx.wcp.provider;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;

public class Test {

    public static void main(String[] args) {
        int pageSize = 50000;

        try {
            // Since 2.10.0, uses MongoClient
            MongoClient mongo = new MongoClient("localhost", 27017);

            // if database doesn't exists, MongoDB will create it for you
            DB db = mongo.getDB("test");

            // if collection doesn't exists, MongoDB will create it for you
            DBCollection table = db.getCollection("user_info");
            DBCursor dbObjects;
            Long cnt = table.count();
            //System.out.println(table.getStats());
            Long page = getPageSize(cnt, pageSize);
            ObjectId lastIdObject = new ObjectId("5d34363cac71d18fa3a8bc10");

            for (Long i = 0L; i < page; i++) {
                long start = System.currentTimeMillis();
                dbObjects = getCursorForCollection(table, lastIdObject, pageSize);
                System.err.println("第" + (i + 1) + "次查询，耗时:" + (System.currentTimeMillis() - start) / 1000 + "秒");
                List<DBObject> objs = dbObjects.toArray();
                System.err.println("记录数：" + objs.size() + ", NEXT_USER=" + objs.get(objs.size() - 1).toString());
                lastIdObject = (ObjectId) objs.get(objs.size() - 1).get("_id");
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    private static DBCursor getCursorForCollection(DBCollection collection, ObjectId lastIdObject, int pageSize) {
        DBCursor dbObjects;
        if (lastIdObject == null) {
            //TODO 排序sort取第一个，否则可能丢失数据
            lastIdObject = (ObjectId) Objects.requireNonNull(collection.findOne()).get("_id");
        }
        BasicDBObject query = new BasicDBObject();
        query.append("_id", new BasicDBObject("$gt", lastIdObject));
        BasicDBObject sort = new BasicDBObject();
        sort.append("_id", 1);
        dbObjects = collection.find(query).limit(pageSize).sort(sort);
        return dbObjects;
    }

    private static Long getPageSize(Long cnt, int pageSize) {
        return cnt % pageSize == 0 ? cnt / pageSize : cnt / pageSize + 1;
    }
}
