package com.ares.dal.mysql;

import java.io.Serializable;

/**
 * note: if we use common dao to save DO we should use  DO extends BaseDO
 *
 * @author zhongwq
 */

public class BaseDO {

    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return  Long.hashCode(id);
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        }
        if (target instanceof BaseDO) {
            BaseDO baseDO = (BaseDO) target;
            return this.getId() == baseDO.getId();
        }
        return false;
    }

    /**
     * note: this is the default table sharding method use  'id'
     * if your sharding table no use override this method  return null;
     * otherwise  you shoud in your own DO object rewrite this method
     *
     */
//	@JsonIgnore
//	public String getTableName(){
//		 return DoSqlUtil.getTableName(this.getClass(), id);
//	}
}
