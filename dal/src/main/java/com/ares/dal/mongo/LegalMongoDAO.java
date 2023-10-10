package com.ares.dal.mongo;

import com.ares.dal.mongo.annotation.CollectionName;
import com.ares.dal.mongo.annotation.MdbName;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.InsertOneResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class LegalMongoDAO<T> implements InitializingBean {
    private final static String _ID = "_id";
    private final static String _VER = "ver";
    private Class<T> doClass;
    private String dbName;
    @Autowired
    private AresMonogClient aresMonogClient;
    private MongoDatabase database;
    protected MongoCollection<T> collection;

    private final static ReplaceOptions UPINSERT_OPTIONS = new ReplaceOptions().upsert(true);

    public LegalMongoDAO(Class<T> doClass) {
        this.doClass = doClass;
    }

    public boolean insert(T obj) {
        InsertOneResult insertOneResult = collection.insertOne(obj);
        return insertOneResult.wasAcknowledged();
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
