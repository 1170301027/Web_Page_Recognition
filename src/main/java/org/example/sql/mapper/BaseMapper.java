package org.example.sql.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.example.sql.mybatis.CrudTemplet;

/**
 * 实现基本的 增,删,改,查接口,不需要重复写 所有mapper都继承这个BaseMapper
 */
public interface BaseMapper {

    /**
     * 通用的数据库插入操作
     *
     * @param obj pojo对象
     * @return int 数据库更新的行数
     */
    @InsertProvider(type = CrudTemplet.class, method = "insert")
    public int insert(Object obj) throws Exception;

    //	@InsertProvider(type = CrudTemplet.class, method = "insertBatching")
    //	public <T> int insertBatching(List<T> list, T t);

    /**
     * <p>
     * 这里要求数据库表的自增主键名和pojo字段名必须为id 否则需要继承重写
     * </p>
     *
     * 通用的数据库插入操作，主键自增，插入后id在obj.getId()里
     *
     * @param obj pojo对象
     * @return int 数据库更新的行数
     */
    @InsertProvider(type = CrudTemplet.class, method = "insert")
    @Options(keyColumn = "id", keyProperty = "id", useGeneratedKeys = true)
    public int insertIdAutoIncrease(Object obj) throws Exception;

    /**
     * 通用的数据库删除操作，通过主键删除
     *
     * @param obj pojo对象
     * @return int 数据库更新的行数
     */
    @DeleteProvider(type = CrudTemplet.class, method = "delete")
    public int delete(Object obj) throws Exception;

    /**
     * 通用的数据库删除操作，通过条件删除，慎用--验证字段不为空，防止多删
     *
     * @param obj pojo对象
     * @return int 数据库更新的行数
     */
    @DeleteProvider(type = CrudTemplet.class, method = "deleteByWhere")
    public int deleteByWhere(Object obj) throws Exception;

    /**
     * 通用的数据库更新操作，通过主键更新
     *
     * @param obj pojo对象
     * @return int 数据库更新的行数
     */
    @UpdateProvider(type = CrudTemplet.class, method = "update")
    public int update(Object obj) throws Exception;

    /**
     * 通用的数据库更新操作，通过where更新
     *
     * @param obj pojo对象
     * @return int 数据库更新的行数
     */
    @UpdateProvider(type = CrudTemplet.class, method = "updateByWhere")
    public <T> int updateByWhere(@Param("o") T obj, @Param("w") T where) throws Exception;

    /*
     * <h5>用法</h5>
     *
     * <pre>
     * &#064;ResultType(Role.class)//(可选)
     * //（或者&#064;ResultMap("roleMap")//roleMap定义在roleMapper xml文件里）(可选)
     * <span style="color: red">&#064;Override</span>
     * &#064;SelectProvider(type = CrudTemplet.class, method = &quot;select&quot;)
     * public Role select(Role role) throws Exception;
     * </pre>
     * @Description: 通用的查找操作，暂时这么用，根据主键查询
     */
    //public Object select(Object obj) throws Exception;

    /*
     * <h5>用法</h5>
     *
     * <pre>
     * &#064;ResultType(Role.class)//(可选)
     * //（或者&#064;ResultMap("roleMap")//roleMap定义在roleMapper xml文件里）(可选)
     * <span style="color: red">&#064;Override</span>
     * &#064;SelectProvider(type = CrudTemplet.class, method = &quot;selectByWhere&quot;)
     * public List<Role> selectByWhere(Role role) throws Exception;
     * </pre>
     * @Description: 通用的查找操作，暂时这么用，根据主键查询
     */
    //public Object selectByWhere(Object obj) throws Exception;

    /**
     * 通用的数量查询，通过pojo不为空的属性查询,条件为 与
     *
     * @param obj pojo对象
     * @return int 符合条件的项数
     */
    @SelectProvider(type = CrudTemplet.class, method = "count")
    public int count(Object obj) throws Exception;

    /**
     * 通用的表项总数查询，通过表名查询
     *
     * @param table 数据库表名字
     * @return int 总数
     */
    @SelectProvider(type = CrudTemplet.class, method = "countAll")
    public int countAll(String table) throws Exception;
}
