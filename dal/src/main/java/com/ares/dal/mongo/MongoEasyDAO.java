package com.ares.dal.mongo;

import com.ares.dal.mongo.annotation.CollectionName;
import com.ares.dal.mongo.annotation.MdbName;
import com.ares.dal.mysql.EasyDO;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Slf4j
public class MongoEasyDAO<T extends EasyDO> implements InitializingBean {
    private Class<T> doClass;
    private String dbName;
    @Autowired
    private AresMonogClient aresMonogClient;
    private MongoDatabase database;
    private MongoCollection<T> collection;

    private static ReplaceOptions UPINSERT_OPTIONS = new ReplaceOptions().upsert(true);

    public MongoEasyDAO(Class<T> doClass) {
        this.doClass = doClass;
    }

    public boolean insert(T obj) {
        InsertOneResult insertOneResult = collection.insertOne(obj);
        return insertOneResult.wasAcknowledged();
    }

    public boolean bathInsert(List<T> objs) {
        InsertManyResult insertManyResult = collection.insertMany(objs);
        return insertManyResult.wasAcknowledged();
    }

    public T findOne(String fieldName, Object fieldValue) {
        return collection.find(eq(fieldName, fieldValue)).first();
    }

    public FindIterable<T> findMany(String fieldName, Object fieldValue) {
        return collection.find(eq(fieldName, fieldValue));
    }

    public FindIterable<T> findMany(Bson condition) {
        return collection.find(condition);
    }

    public long replaceOne(Bson condition, T obj) {
        return collection.replaceOne(condition, obj).getMatchedCount();
    }

    public long deleteMany(Bson condition) {
        return collection.deleteMany(condition).getDeletedCount();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MdbName mdbName = this.doClass.getAnnotation(MdbName.class);
        if (mdbName == null) {
            String tmpdbName = this.doClass.getSimpleName();
            dbName = tmpdbName.substring(0, tmpdbName.length() - 2);
        } else {
            dbName = mdbName.value();
        }
        database = aresMonogClient.getMongoClient().getDatabase(dbName);
        CollectionName collectionName = this.doClass.getAnnotation(CollectionName.class);
        String tableName;
        if (collectionName == null) {
            tableName = this.doClass.getSimpleName();
            tableName = tableName.substring(0, tableName.length() - 2);
        } else {
            tableName = collectionName.value();
        }
        collection = database.getCollection(tableName, this.doClass);
    }
}
