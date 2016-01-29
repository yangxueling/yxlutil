package com.yxl.util.solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxl.util.map.MapUtil;
import com.yxl.util.math.NumberUtil;
import com.yxl.util.resource.PropertiesUtil;

/**
 *     ┏┓　　　┏┓
 *   ┏┛┻━━━┛┻┓
 *   ┃　　　　　　　┃ 　
 *   ┃　　　━　　　┃
 *   ┃　┳┛　┗┳　┃
 *   ┃　　　　　　　┃
 *   ┃　　　┻　　　┃
 *   ┃　　　　　　　┃
 *   ┗━┓　　　┏━┛
 *   　　┃　　　┃神兽保佑
 *   　　┃　　　┃永无BUG！
 *   　　┃　　　┗━━━┓
 *   　　┃　　　　　　　┣┓
 *   　　┃　　　　　　　┏┛
 *   　　┗┓┓┏━┳┓┏┛
 *   　　　┃┫┫　┃┫┫
 *   　　　┗┻┛　┗┻┛
 *
 * Solr搜索引擎工具类

	<dependency>
		<groupId>org.apache.solr</groupId>
		<artifactId>solr-solrj</artifactId>
		<version>5.3.1</version>
	</dependency>
	
	solr_home conf schema.xml 字段配置示例：
	<field name="id" type="string" indexed="true" stored="true" required="true" multiValued="false" />
	<field name="name" type="text_mmseg4j_Complex" indexed="true" stored="true" />
	<field name="update_time" type="long" indexed="true" stored="true"/>
	<field name="type" type="string" indexed="true" stored="true" />
	......
 * @author 杨雪令
 */
public class SolrUtil {
	
	private static Logger logger = LoggerFactory.getLogger(SolrUtil.class);
	/** solr服务器 */
	private static SolrClient solrServer;
	/** solr 配置文件 */
	protected static Properties properties = null;
	/** 连接是否存活 */
	protected static boolean connectionAlive = false;
	/** solr  collection*/
	protected static String collection = null;

	// 静态方式加载redis 配置文件
	static {
		//读取配置文件
		readConfig();
		//连接服务器
		if(connectionAlive==false) connectServer();
	}
	
	/**
	 * 读取配置文件
	 * @date 2015年12月15日 上午10:54:01 
	 * @author yxl
	 */
	protected static void readConfig(){
		properties = PropertiesUtil.readProperties("solr.properties", SolrUtil.class);
	}
	
	/**
	 * 连接服务器
	 * @date 2015年12月15日 上午10:54:01 
	 * @author yxl
	 */
	protected synchronized static void connectServer(){
		if(connectionAlive) return;
		logger.info("连接solr server...");
		try{
			//集群模式
			if (properties.getProperty("solr.cluster").equals("1")) {
				collection = properties.getProperty("ZK_DEFAULT_COLLECTION");
				CloudSolrClient cloudSolrServer = new CloudSolrClient(properties.getProperty("ZK_HOST"));//服务器地址(集群)
				cloudSolrServer.setParser(new XMLResponseParser());
				cloudSolrServer.setDefaultCollection(properties.getProperty("ZK_DEFAULT_COLLECTION"));//zk collection(集群)
				cloudSolrServer.setZkConnectTimeout(NumberUtil.parseInt(properties.getProperty("ZK_CONNECTION_TIMEOUT")));//zk 连接超时时间(集群)，单位毫秒
				cloudSolrServer.setZkClientTimeout(NumberUtil.parseInt(properties.getProperty("ZK_CLIENT_TIMEOUT"))); //zk client 超时时间(集群)，单位毫秒
				cloudSolrServer.connect();
				solrServer = cloudSolrServer;
			} else {
				HttpSolrClient httpSolrServer = new HttpSolrClient(properties.getProperty("SERVER_ADDR"));//服务器地址
				httpSolrServer.setParser(new XMLResponseParser());
				httpSolrServer.setConnectionTimeout(NumberUtil.parseInt(properties.getProperty("CONNECTION_TIMEOUT")));//连接超时时间，单位毫秒
				httpSolrServer.setSoTimeout(NumberUtil.parseInt(properties.getProperty("READ_TIMEOUT")));//读取数据超时时间，单位毫秒
				httpSolrServer.setDefaultMaxConnectionsPerHost(NumberUtil.parseInt(properties.getProperty("MAXCONNECTIONS_PER_HOST")));//一台服务器最多可以建立的连接数
				httpSolrServer.setMaxTotalConnections(NumberUtil.parseInt(properties.getProperty("MAXCONNECTIONS_TOTAL")));//服务器最多可以建立的连接总数
				httpSolrServer.setFollowRedirects(Boolean.parseBoolean(properties.getProperty("FOLLOW_REDIRECTS")));//服务器宕机时，是否允许重定向
				httpSolrServer.setAllowCompression(Boolean.parseBoolean(properties.getProperty("ALLOW_COMPRESSION")));//是否允许压缩数据，压缩后会节省空间，但是会使查询速度变慢
				solrServer = httpSolrServer;
			}
			connectionAlive = true;
			logger.info("连接solr server 成功");
		} catch(Exception e){
			connectionAlive = false;
			logger.error("连接solr server 失败", e);
		}
	}
	
	
	/**
	 * 添加索引
	 * @param id 索引ID
	 * @param indexData 索引数据
	 * @date 2015年12月31日 下午4:33:48 
	 * @author yxl
	 */
	@SuppressWarnings("rawtypes")
	public static void addIndex(Map<String, Object> indexData){
 		if(indexData==null) return;
		logger.info("添加索引：" + MapUtil.toString(indexData, 10));
		SolrInputDocument solrInputDocument = new SolrInputDocument();
		for(Iterator it = indexData.entrySet().iterator(); it.hasNext();){
			Entry entry = (Entry) it.next();
			Object val = entry.getValue();
			solrInputDocument.addField(entry.getKey().toString(), val);
		}
		try {
			if(collection!=null && collection.trim().length()>0) solrServer.add(collection, solrInputDocument);
			else solrServer.add(solrInputDocument);
			UpdateResponse response = solrServer.commit();
			logger.info("添加索引 : status : " + response.getStatus() + " , qTime : " + response.getQTime() + "ms");
		} catch (Exception e) {
			logger.error("添加索引失败", e);
		}
	}
	
	
	/**
	 * 刪除索引
	 * @param id 索引ID
	 * @date 2015年12月31日 下午4:33:48 
	 * @author yxl
	 */
	public static void delIndex(String id){
 		if(id==null) return;
		logger.info("刪除索引：" + id);
		try {
			if(collection!=null && collection.trim().length()>0) solrServer.deleteById(collection, id);
			else solrServer.deleteById(id);
			UpdateResponse response = solrServer.commit();
			logger.info("刪除索引 : status : " + response.getStatus() + " , qTime : " + response.getQTime() + "ms");
		} catch (Exception e) {
			logger.error("刪除索引失败", e);
		}
	}
	
	/**
	 * 批量刪除索引
	 * @param ids 索引ID集合
	 * @date 2015年12月31日 下午4:33:48 
	 * @author yxl
	 */
	public static void delIndex(List<String> ids){
 		if(ids==null || ids.size()<1) return;
		logger.info("批量刪除索引：" + ids);
		try {
			if(collection!=null && collection.trim().length()>0) solrServer.deleteById(collection, ids);
			else solrServer.deleteById(ids);
			UpdateResponse response = solrServer.commit();
			logger.info("批量刪除索引 : status : " + response.getStatus() + " , qTime : " + response.getQTime() + "ms");
		} catch (Exception e) {
			logger.error("批量刪除索引失败", e);
		}
	}
	
	/**
	 * 根據條件刪除索引
	 * 需要在 solr_home conf schema.xml加入配置：
	 * <field name="_version_" type="long" indexed="true" stored="true"/>
	 * @param queryStr 查詢條件(如：*:*，ID:video1，ID:video1 AND NAME:h*)
	 * @date 2015年12月31日 下午4:33:48 
	 * @author yxl
	 */
	public static void deleteByQuery(String queryStr){
 		if(queryStr==null) return;
		logger.info("刪除索引：" + queryStr);
		try {
			if(collection!=null && collection.trim().length()>0) solrServer.deleteByQuery(collection, queryStr);
			else solrServer.deleteByQuery(queryStr);
			UpdateResponse response = solrServer.commit();
			logger.info("刪除索引 : status : " + response.getStatus() + " , qTime : " + response.getQTime() + "ms");
		} catch (Exception e) {
			logger.error("刪除索引失败", e);
		}
	}
	
	/** 关键词，不作为字段名称 */
	private static String keyWords = "pn,pageSize,returnCol";
	
	
	/**
	 * 添加索引
	 * @param params 查询条件
	 * 		分页查询：查询条件包含参数pn（页码），pageSize：每页条数，默认10条数据
	 * @return Map{pn（页码）, pageSize（每页数量）, total（总数）, totalPage（总页数）, rows（查询结果）}
	 * @date 2015年12月31日 下午4:33:48 
	 * @author yxl
	 */
	@SuppressWarnings("rawtypes")
	public static Map query(Map params){
		return query(params, null);
	}
	
	/**
	 * 添加索引
	 * @param params 查询条件
	 * 		分页查询：查询条件包含参数pn（页码），pageSize：每页条数，默认10条数据
	 * @param 排序字段，可以为null（字段名称：asc/desc）
	 * @return Map{pn（页码）, pageSize（每页数量）, total（总数）, totalPage（总页数）, rows（查询结果）}
	 * @date 2015年12月31日 下午4:33:48 
	 * @author yxl
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map query(Map params, LinkedHashMap orderMap){
		//返回值
		Map returnMap = new HashMap();
		
		//初始化Solr查询
		SolrQuery query =  new SolrQuery();
		query.setHighlight(true);//开启高亮
		query.setParam("hl.fl", "VIDEO_NAME");
		query.setHighlightSimplePre("<font color=\"red\">");//前缀
        query.setHighlightSimplePost("</font>");//后缀
        
        //组合查询条件
        for(Iterator it = params.entrySet().iterator(); it.hasNext();){
			Entry entry = (Entry) it.next();
			String key = entry.getKey().toString();
			if(!keyWords.contains(key)) query.setQuery(key + ":" + entry.getValue());
		}
        
        //组合排序Map
        if(orderMap!=null)
        for(Iterator it = orderMap.entrySet().iterator(); it.hasNext();){
			Entry entry = (Entry) it.next();
			String key = entry.getKey().toString();
			if(entry.getValue().toString().toUpperCase().equals("ASC")) query.setSort(key, ORDER.asc);
			else query.setSort(key, ORDER.desc);
		}
        
        //是否包含分页参数
        if(params.containsKey("pn")) {
        	int pn = NumberUtil.parseInt(params.get("pn"));
        	int pageSize = 10;
        	if(params.containsKey("pageSize")) pageSize = NumberUtil.parseInt(params.get("pageSize"));
        	int start = pageSize * (pn-1);
        	if(start<0) start = 0;
        	query.setStart(start);
        	query.setRows(pageSize);
        	
        	returnMap.put("pn", pn);
    		returnMap.put("pageSize", pageSize);
        }
		
        QueryResponse response = null;
		try {
			if(collection!=null && collection.trim().length()>0) response = solrServer.query(collection, query);
			else response = solrServer.query(query);
		} catch (Exception e) {
			logger.error("Sorl查询失败", e);
		}
		
		//组合返回值
		long total = ((SolrDocumentList)response.getResponse().get("response")).getNumFound();
		returnMap.put("total", total);
		//计算总页数
		if(returnMap.containsKey("pageSize")) {
			int pageSize = NumberUtil.parseInt(returnMap.get("pageSize"));
			int totalPage = (int) (total / pageSize);
			if(total % pageSize != 0) totalPage++;
			returnMap.put("totalPage", totalPage);
			//页码大于最大页数，查询不到数据，重新查询
			if(NumberUtil.parseInt(returnMap.get("pn"))>totalPage) {
				params.put("pn", totalPage);
				return query(params, orderMap);
			}
		}
		
		//格式化查询结果
		Map<String, Map<String, List<String>>> highlightMap = response.getHighlighting();//高亮字符
		List<Map> rows = new ArrayList<Map>();
		for(Iterator it=response.getResults().iterator(); it.hasNext();){
			Map resultMap = MapUtil.parse((SolrDocument) it.next());
			String id = resultMap.get("ID").toString();
			//从高亮Map中获取每个参数的值，一一替换
			if(highlightMap.containsKey(id)){
				for(Iterator it2 = ((Map) highlightMap.get(id)).entrySet().iterator(); it2.hasNext();){
					Entry highlightEntry = (Entry) it2.next();
					resultMap.put(highlightEntry.getKey(), ((List)highlightEntry.getValue()).get(0));
				}
			}
			rows.add(resultMap);
		}
		returnMap.put("rows", rows);
		return returnMap;
	}
	
	
	/**
	 * 测试
	 * @date 2015年12月31日 下午4:30:15 
	 * @author yxl
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		/*for(int i=1; i<100; i++){
			Map map = new HashMap();
			map.put("VIDEO_NAME", "这是一个视频" + i);
			SolrUtil.addIndex("video_"+i, map);
		}*/
		
		//修改
		Map map = new HashMap();
		map.put("ID", "test_update");
		map.put("VIDEO_NAME", "修改3");
		SolrUtil.addIndex(map);
		
		//刪除
		//SolrUtil.deleteByQuery("ID:test*");
		//SolrUtil.delIndex("test_update");
		
		//查询
		Map queryMap = new HashMap();
		queryMap.put("VIDEO_NAME", "视频5");
		//queryMap.put("VIDEO_NAME", "修改");
		queryMap.put("pn", 2);
		queryMap.put("pageSize", 5);
		LinkedHashMap orderMap = new LinkedHashMap();
		orderMap.put("VIDEO_NAME", "desc");
		Map returnMap = SolrUtil.query(queryMap, orderMap);
		System.out.println(MapUtil.toString(returnMap, 100000));
	}
}