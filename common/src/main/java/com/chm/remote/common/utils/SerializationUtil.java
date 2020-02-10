package com.chm.remote.common.utils;

import com.google.common.collect.Maps;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;

/**
 * @author caihongming
 * @version v1.0
 * @title SerializationUtil
 * @package com.chm.remote.common.utils
 * @since 2019-11-29
 * description 序列化工具类
 **/
public final class SerializationUtil {

  private static Map<Class<?>, Schema<?>> cachedSchema = Maps.newConcurrentMap();

  private static Objenesis objenesis = new ObjenesisStd(true);

  private SerializationUtil() {
  }

  @SuppressWarnings("unchecked")
  private static <T> Schema<T> getSchema(Class<T> cls) {
    Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
    if (schema == null) {
      schema = RuntimeSchema.createFrom(cls);
      if (schema != null) {
        cachedSchema.put(cls, schema);
      }
    }
    return schema;
  }


  @SuppressWarnings({"unchecked"})
  public static <T> byte[] serializer(T obj) {
    Class<T> cls = (Class<T>) obj.getClass();
    LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    try {
      Schema<T> schema = getSchema(cls);
      return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    } catch (Exception e) {
      LogUtil.logError("Protostuf序列化失败");
      throw new IllegalStateException(e.getMessage(), e);
    } finally {
      buffer.clear();
    }
  }

  public static <T> T deserializer(byte[] bytes, Class<T> clazz) {
    try {
      T message = (T) objenesis.newInstance(clazz);
      Schema<T> schema = getSchema(clazz);
      ProtostuffIOUtil.mergeFrom(bytes, message, schema);
      return message;
    } catch (Exception e) {
      LogUtil.logError("Protostuf反序列化失败");
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
}
