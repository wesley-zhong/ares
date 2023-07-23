package com.ares.dal.mysql;

import java.io.Serializable;
/**
 * 针对非long类型ID  需要实现这个接口 自己加字段  @BsonId
 * */
public interface IGenericsID<T extends Serializable> {


     void setGenericsId(T id);

     T getGenericsId();
}
