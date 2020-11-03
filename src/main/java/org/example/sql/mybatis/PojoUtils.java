package org.example.sql.mybatis;

import com.staui.kit.StringKit;
import com.staui.wpi.web.annotation.Column;
import com.staui.wpi.web.annotation.Pk;
import com.staui.wpi.web.annotation.Table;
import com.staui.wpi.web.annotation.Transient;
import com.staui.wpi.web.exception.PojoException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version v1.0
 */
public class PojoUtils {

    static class Infos {

        List<ColInfo> mappings;
        List<ColInfo> pks;
        String table;

        Infos(List<ColInfo> mappings, List<ColInfo> pks, String table){
            this.mappings = mappings;
            this.pks = pks;
            this.table = table;
        }
    }

    static class ColInfo {

        Method method;
        String column;
        String prop;
        boolean eq;
        boolean isPk;
        String op = "=";

        ColInfo(Method method, String prop){
            this.method = method;
            this.prop = prop;
        }
    }

    private static Map<Class<?>,Infos> columnMap = new HashMap<>();

    public static String from(Object obj){
        return columnMap.get(obj.getClass()).table;
    }

    public static List<ColInfo> primaryKeys(Object obj){
        return columnMap.get(obj.getClass()).pks;
    }

    public static void calculateColumnList(Object obj){
        if(columnMap.containsKey(obj.getClass()))
            return;
        String tableName;
        List<ColInfo> pks = new ArrayList<>();
        Table table = obj.getClass().getAnnotation(Table.class);
        if(table != null)
            tableName = table.name().equals("") ? camelToUnderline(obj.getClass().getSimpleName()) : table.name();
        else
            throw new PojoException("POJO Annotation \"@Table(tableName)\" undefined ");

        Method[] methods = obj.getClass().getMethods();
        ArrayList<ColInfo> columnList = new ArrayList<>(methods.length);

        for(Method method : methods){
            String name = method.getName();
            if(!name.startsWith("get") && !name.startsWith("is"))
                continue;
            if(name.equals("getClass") || method.getAnnotation(Transient.class) != null)
                continue;
            if(name.startsWith("get"))
                name = name.substring(3);
            else if(!(method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class)))
                continue;

            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            String column;
            ColInfo info = new ColInfo(method, name);
            columnList.add(info);

            Pk pk = method.getAnnotation(Pk.class);
            if(pk != null){
                info.isPk = true;
                column = pk.name();
                pks.add(info);
            }else{
                Column col = method.getAnnotation(Column.class);
                column = col == null ? null : col.name();
            }
            info.column = StringKit.isEmpty(column) ? camelToUnderline(name) : column;
            info.eq = StringKit.isEmpty(column);
        }
        columnMap.put(obj.getClass(), new Infos(columnList, pks, tableName));
    }

    private static String camelToUnderline(String param){
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++){
            char c = param.charAt(i);
            if(Character.isUpperCase(c)){
                if(i > 0)
                    sb.append('_');
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String underlineToCamel(String param){
        if(param == null || "".equals(param.trim())){
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++){
            char c = param.charAt(i);
            if(c == '_'){
                if(++i < len){
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String foreach(List<ColInfo> map, String separator, boolean col, boolean prop){
        StringBuilder sb = new StringBuilder();
        if(col && !prop){
            for(ColInfo entry : map){
                sb.append(entry.column);
                sb.append(separator);
            }
            sb.deleteCharAt(sb.length() - 1);
        }else if(!col && prop){
            for(ColInfo entry : map){
                sb.append("#{");
                sb.append(entry.prop);
                sb.append("}");
                sb.append(separator);
            }
            sb.deleteCharAt(sb.length() - 1);
        }else{
            for(ColInfo entry : map){
                sb.append(entry.column);
                sb.append("=#{");
                sb.append(entry.prop);
                sb.append("}");
                if(map.size() > 1)
                    sb.append(separator);
            }
            if(map.size() > 1)
                sb.delete(sb.length() - separator.length(), sb.length());
        }
        return sb.toString();
    }

    public static String foreach(List<ColInfo> map, String separator, boolean col, boolean prop, String pre){
        StringBuilder sb = new StringBuilder();
        if(col && !prop){
            for(ColInfo entry : map){
                sb.append(entry.column);
                sb.append(separator);
            }
            sb.deleteCharAt(sb.length() - 1);
        }else if(!col && prop){
            for(ColInfo entry : map){
                sb.append("#{");
                sb.append(entry.prop);
                sb.append("}");
                sb.append(separator);
            }
            sb.deleteCharAt(sb.length() - 1);
        }else{
            for(ColInfo entry : map){
                sb.append(entry.column);
                sb.append("=#{").append(pre);
                sb.append(entry.prop);
                sb.append("}");
                if(map.size() > 1)
                    sb.append(separator);
            }
            if(map.size() > 1)
                sb.delete(sb.length() - separator.length(), sb.length());
        }
        return sb.toString();
    }

    private static boolean isNull(Method method, Object obj){
        try{
            return method.invoke(obj) == null;
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public static List<ColInfo> notNullColumns(Object obj){
        List<ColInfo> list = columnMap.get(obj.getClass()).mappings;
        List<ColInfo> nl = new ArrayList<>();
        for(ColInfo column : list){
            if(isNull(column.method, obj))
                continue;
            nl.add(column);
        }
        return nl;
    }

    public static List<ColInfo> updateColumns(Object obj){
        List<ColInfo> list = columnMap.get(obj.getClass()).mappings;
        List<ColInfo> nl = new ArrayList<>();
        for(ColInfo column : list){
            if(isNull(column.method, obj) || column.isPk)
                continue;
            nl.add(column);
        }
        return nl;
    }

    public static String where(List<ColInfo> map, String separator){
        StringBuilder sb = new StringBuilder();
        int x = 0;
        for(ColInfo entry : map){
            if(x > 0)
                sb.append(separator);
            sb.append(entry.column);
            if(entry.op.length() < 4){
                sb.append(entry.op);
                sb.append("#{");
                sb.append(entry.prop);
                sb.append("}");
            }else{
                sb.append(" LIKE CONCAT(CONCAT('%',#{");
                sb.append(entry.prop);
                sb.append("}),'%')");
            }
            x++;
        }
        //sb.delete(sb.length() - separator.length(), sb.length());

        return sb.toString();
    }

    public static String selectColumns(Object obj){
        StringBuilder sb = new StringBuilder();

        List<ColInfo> list = columnMap.get(obj.getClass()).mappings;
        for(ColInfo column : list){
            sb.append(column.column);
            if(!column.eq){
                sb.append(" ");
                sb.append(column.prop);
            }
            sb.append(",");
        }
        if(sb.length() > 1)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
