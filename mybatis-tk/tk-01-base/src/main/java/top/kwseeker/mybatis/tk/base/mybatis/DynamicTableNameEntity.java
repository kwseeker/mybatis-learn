package top.kwseeker.mybatis.tk.base.mybatis;

import tk.mybatis.mapper.entity.IDynamicTableName;

import javax.persistence.Table;
import java.lang.reflect.Field;

/**
 * 动态表名生成
 */
public abstract class DynamicTableNameEntity implements IDynamicTableName {

    @Override
    public String getDynamicTableName() {
        Class<? extends DynamicTableNameEntity> thisClass = this.getClass();
        StringBuilder tableName = new StringBuilder(thisClass.getAnnotation(Table.class).name());
        Field[] fields = thisClass.getDeclaredFields();
        int length = fields.length;
        for (int i = 0; i < length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(DefineShareField.class)){
                int tableNum = field.getAnnotation(DefineShareField.class).value();
                if (tableNum <= 1){
                    return tableName.toString();
                }
                tableName.append("_");
                try {
                    field.setAccessible(true);
                    long id = (long) field.get(this);
                    long value = id % tableNum;
                    if(value < 10) {
                        tableName.append("0").append(value);
                    }else {
                        tableName.append(value);
                    }
                    return tableName.toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableName.toString();
    }
}