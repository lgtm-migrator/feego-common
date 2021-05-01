package io.github.lvyahui8.sdk.logging.logger;

import io.github.lvyahui8.sdk.logging.configuration.LogConstants;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2020/2/22 0:12
 */
@SuppressWarnings({"unused"})
public final class LogSchema {
    /// private Map<String,Object> items = Collections.synchronizedMap(new ListOrderedMap<>());
    private final ListOrderedMap<String,Object> items = new ListOrderedMap<>();

    private LogSchema() {
        init();
    }

    public void init() {
    }

    public static LogSchema empty() {
        return new LogSchema();
    }

    public static LogSchema biz(String biz) {
        return empty().of(LogConstants.ReversedKey.BUSINESS,biz);
    }

    public LogSchema of(String key,Object value) {
        items.put(key,simplifyValue(value));
        return this;
    }

    public LogSchema traceId(String traceId) {
        return of(LogConstants.ReversedKey.TRACE_ID,traceId);
    }

    public LogSchema prepend(String key,Object value) {
        items.put(0,key,simplifyValue(value));
        return this;
    }

    public LogSchema success(boolean  suc) {
        return of(LogConstants.ReversedKey.SUCCESS,suc);
    }

    public LogSchema clear() {
        items.clear();
        return this;
    }

    private Object simplifyValue(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean)  value) ? 'Y' : 'N' ;
        }
        return value;
    }

    Detail buildDetail(String sp) {
        return buildDetail(sp,0);
    }

    Detail buildDetail(String sp,int reserved) {
        Detail detail  = new Detail();
        detail.args = new Object[items.size() + reserved ];
        StringBuilder sb = new StringBuilder();
        int i = 0 ;
        for (Map.Entry<String,Object> item : items.entrySet()) {
            sb.append(item.getKey()).append(":{}");
            if (i < items.size() - 1 || reserved > 0) {
                sb.append(sp);
            }
            detail.args[i++] = item.getValue();
        }
        detail.pattern = sb.toString();
        return detail;
    }

    Detail buildDetail(String sp,String format,Object ... arguments) {
        LogSchema.Detail detail = this.buildDetail(sp);
        String pattern = detail.getPattern() + sp + LogConstants.ReversedKey.MESSAGE + ":" + format;
        Object[] args ;
        if (arguments != null && arguments.length > 0 ) {
            args = new Object[detail.getArgs().length + arguments.length];
            System.arraycopy(detail.getArgs(),0,args,0,detail.getArgs().length);
            System.arraycopy(arguments,0,args,detail.getArgs().length,arguments.length);
        } else {
            args = detail.getArgs();
        }
        detail.pattern = pattern;
        detail.args = args;
        return detail;
    }

    static class Detail {
        String pattern;
        Object [] args;

        public Object[] getArgs() {
            return args;
        }

        public String getPattern() {
            return pattern;
        }

    }
}
