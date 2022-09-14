package top.kwseeker.mybatis.tk.base.mybatis;


import tk.mybatis.mapper.entity.Condition;

/**
 * 分表 Condition
 * 使用时注意传入实体对象，并且需要 set 分表字段
 */
public class ShareTableCondition extends Condition {

    private final DynamicTableNameEntity entity;

    public ShareTableCondition(DynamicTableNameEntity entity) {
        super(entity.getClass());
        this.entity = entity;
    }

    @Override
    public String getDynamicTableName() {
        return entity.getDynamicTableName();
    }
}
