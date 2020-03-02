package com.polaris.technicalintegration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;


/**
 * json处理工具
 * 
 * @author jicexosl
 * 
 *         2014-5-5
 */
public final class JsonUtils {
	private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class); 
	private JsonUtils() {}
	
	/**
	 * ObjectMapper 提供单例供全局使用
	 */
	
	private static class SingletonHolder{
		private static ObjectMapper mapper;
		static{
			mapper = new ObjectMapper();
			//设置日期对象的输出格式
			mapper.setDateFormat(new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss",Locale.CHINESE));
			//设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//禁止使用int代表Enum的order()来反序列化Enum
			mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
			//mapper.setSerializationInclusion(Include.NON_EMPTY);
			//设置  null 自动转换成 ""
			/*mapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>(){
				@Override
				public void serialize(Object value, JsonGenerator jgen,SerializerProvider provider) throws IOException,JsonProcessingException {
					jgen.writeString("");
				}
			});*/
		}
	}
	
	private static ObjectMapper getMapper() {
		return SingletonHolder.mapper;
	}

	/**
	 * 将对象转换为json字符串
	 * @param pojo
	 * @return
	 * @throws IOException
	 */
	public static String toJsonString(Object pojo) throws IOException {
        if (pojo==null){
            return null;
        }
		try {
			return getMapper().writeValueAsString(pojo);
		} catch (IOException e) {
			LOG.error("pojo parse  json string error",e);
			throw e;
		}
	}

	/**
	 * 将字符串转换为json对象
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static JsonNode parseJson(String input) throws IOException {
		if (input==null) {
			return null;
		}
		try {
			return getMapper().readTree(input);
		} catch (IOException e) {
			LOG.error("json processing error,input: "+input,e);
			throw e;
		}
	}

	
	/**
	 * 将inputStream 转换为json对象
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static JsonNode getJsonNodefromStream(InputStream in) throws IOException {
		try {
			return getMapper().readTree(in);
		} catch (JsonProcessingException e) {
			LOG.error("json processing error",e);
			throw e;
		} catch (IOException e) {
			LOG.error("read file error",e);
			throw e;
		}
	}

	/**
	 * 将json字符串转换为java对象，只支持返回简单对象（非集合类型）
	 * @param jsonString
	 * @param valueType
	 * @return
	 * @throws IOException
	 */
	public static  <T> T jsonToObject(String jsonString, Class<T> valueType) throws IOException {
        if (StringUtils.hasText(jsonString)){
            return getMapper().readValue(jsonString, valueType);
        }
	    return null;
    }

	/**
	 * 将json字符串转换为java对象，只支持返回简单对象（非集合类型）
	 * @param jsonString
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static  HashMap<String,String> jsonToObjectAsHashMap(String jsonString) throws IOException {
		HashMap<String,String> paramMap = null;
        if (StringUtils.hasText(jsonString)){
        	paramMap = getMapper().readValue(jsonString, HashMap.class);
        	//去除通用的
        	paramMap.remove("clientInfo");
        	paramMap.remove("authority");
            return paramMap;
        }
	    return paramMap;
    }
	
	
	 /**
	 * 将json字符串转为集合类型 List、Map等
	 * @param jsonStr json字符串
	 * @param collectionClass 集合类型
	 * @param elementClasses 泛型类型
	 */
	public static <T> T jsonToObject(String jsonStr, Class<?> collectionClass, Class<?>... elementClasses) throws IOException {
		if (!StringUtils.hasText(jsonStr)) {
			return null;
		}
		JavaType javaType = getMapper().getTypeFactory().constructParametricType(collectionClass, elementClasses);
		return getMapper().readValue(jsonStr, javaType);
	}
	
	/*public static <T> T jsonToObject(JsonNode node, Class<?> collectionClass, Class<?>... elementClasses) throws IOException {
		if (node==null) {
			return null;
		}
		JavaType javaType = getMapper().getTypeFactory().constructParametricType(collectionClass, elementClasses);
		return getMapper().readValue(node, javaType);
	}*/

	/**
	 * 创建一个空的json对象
	 * @return
	 */
	public static ObjectNode createObjectNode(){
		return getMapper().createObjectNode();
	}
	
	/**
	 * 创建一个空的json数组对象
	 * @return
	 */
	public static ArrayNode createArrayNode(){
		return getMapper().createArrayNode();
	}
	
	/**
	 * 生成json格式的响应结果
	 * @param msgCode
	 * @param message
	 * @param result
	 * @return
	 */
	public static String generateResponse(MsgCode msgCode,String message,Object result){
		String response;
		ObjectNode node = createObjectNode();
		node.put("resultCode", msgCode.getMsgCode());
		if (message!=null) {
			node.put("resultMsg",message);
		}else{
			node.put("resultMsg",msgCode.getMessage());
		}
        node.putPOJO("data", result);
		try {
			response = toJsonString(node);
		} catch (IOException e) {
			node = createObjectNode();
			node.put("resultCode", MsgCode.SYSTEM_ERROR.getMsgCode());
			response = node.toString();
		}
		return response;
	}

	/**
	 * 生成json格式的响应结果
	 * @param msgCode
	 * @param message
	 * @param result
	 * @return
	 */
	public static String generateResponse(Integer msgCode,String message,Object result){
		String response;
		ObjectNode node = createObjectNode();
		node.putPOJO("resultCode", msgCode);
		node.putPOJO("resultMsg", message);
        node.putPOJO("data", result);
		try {
			response = toJsonString(node);
		} catch (IOException e) {
			node = createObjectNode();
			node.put("resultCode",MsgCode.SYSTEM_ERROR.getMsgCode());
			response = node.toString();
		}
		LOG.info("返回结果：\n" + response);
		return response;
	}
	
	
	/**
     * 对用户物品JSON串进行排序，升序
     * 即使是格式如{"2005":"1","2078":"1","2077":"1","2000":"1"}这种的串依然可以排序
     * 输出：{"2000":"1","2005":"1","2077":"1","2078":"1"}
     * 
     * @author wenyubin
     * @param str JSON串
     * @return
     */
	public static String sortString(String str) {
        
        //判断是否为多值
        if (str.indexOf(",") > -1) {
            
            //如果为多值，去掉串的 {} ，解析为数组，再排序
            str = str.substring(1, str.length() - 1);
            String[] strs = str.split(",");
            Arrays.sort(strs);
            
            //调用开源包的方法，将数组整合成以","分隔的字符串，然后再加{}形成JSON格式
            str = org.apache.commons.lang3.StringUtils.join(strs, ",");
            str = "{" + str + "}";
        }
        return str;
    }
	
	
	public static String generateResponse(Integer msgCode,String message){
		return generateResponse(msgCode, message, null);
	}
	public static String generateResponse(MsgCode msgCode,Object result){
		return generateResponse(msgCode,null,result);
	}
	
	public static String generateSuccessResponse(Object result){
		return generateResponse(MsgCode.SYSTEM_SUCCESS,null,result);
	}
	
    public static  String generateResponse(String message, MsgCode msgCode){
        return  generateResponse(msgCode,message,null);
    }
    
    public static String generateResponse(String message,MsgCode msgCode,Object result){
    	return generateResponse(msgCode,message,result);
    }
    
    public static  String generateResponse(MsgCode msgCode){
        return  generateResponse(msgCode,null,null);
    }
    
    public static <T> T jacksonToCollection(String src,Class<?> collectionClass, Class<?>... valueType) throws Exception {  
        JavaType javaType= getMapper().getTypeFactory().constructParametricType(collectionClass, valueType);   
        return getMapper().readValue(src, javaType);  
    }  

}
