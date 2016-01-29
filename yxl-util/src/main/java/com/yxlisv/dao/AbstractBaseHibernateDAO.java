package com.yxlisv.dao;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.yxlisv.util.Page;
import com.yxlisv.util.reflect.ClassProxy;

/**
 * 基础的dao类
 * @author yxl
 */
public abstract class AbstractBaseHibernateDAO extends HibernateDaoSupport{

	//定义一个全局的记录器，通过LoggerFactory获取  
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 获取当前线程中绑定的 session（spring 事物会自动关闭改session）
	 * HibernateDaoSupport 类中的 getSession，spring事物会纳入管理，但是不会自动关闭
	 * @autor yxl
	 */
	protected Session getCurrentSession(){
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		return session;
	}
	
	/**
	 * 保存对象
	 * @autor yxl
	 * createtime Feb 31, 2012
	 */
	public void save(Object obj) {
		getHibernateTemplate().save(obj);
	}
	
	/**
	 * 更新对象
	 * @autor yxl
	 * createtime Feb 31, 2012
	 */
	public void update(Object obj) {
		if(obj != null) getHibernateTemplate().update(obj);
	}
	
	/**
	 * 执行更新HQL
	 * @autor yxl
	 * createtime Feb 31, 2012
	 */
	protected int update(String hql) {
		return getHibernateTemplate().bulkUpdate(hql);
	}
	
	/**
	 * 更新对象
	 * @autor yxl
	 * createtime Feb 31, 2012
	 */
	public void merge(Object obj) {
		getHibernateTemplate().merge(obj);
	}
	
	/**
	 * 保存/更新对象
	 * @autor yxl
	 * createtime Feb 31, 2012
	 */
	public void saveOrUpdate(Object obj) {
		getHibernateTemplate().saveOrUpdate(obj);
	}
	
	/**
	 * 从hibernate缓存清除对象
	 * @autor yxl
	 * createtime Feb 31, 2012
	 */
	public void evict(Object obj) {
		getHibernateTemplate().evict(obj);
		//清空list中的数据
		Class<? extends Object> beanClass = obj.getClass();
		 Method[] methlist = beanClass.getDeclaredMethods();
         for (int i = 0; i < methlist.length; i++) {
             Method m = methlist[i];
             String methodName = m.getName();
             if(m.getReturnType().toString().equals("void") && methodName.startsWith("set") && methodName.endsWith("List")){
            	 try {
					m.invoke(obj, new Object[]{null});
				} catch (Exception e) {
					e.printStackTrace();
				}
             }
         }
	}

	
	/**
	 * 根据ID查询对象
	 * @param objClass 对象Class
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public Object get(String id, Class objClass) {
		return getHibernateTemplate().get(objClass, id);
	}
	
	/**
	 * 根据HQL查询对象
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	protected Object findOne(String hql){
		return getOne(hql);
	}
	
	/**
	 * 根据HQL查询对象
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	protected Object getOne(String hql){
		List list = getList(hql, 1);
		if(list==null || list.size()<1) return null;
		return list.get(0);
	}
	
	/**
	 * 根据HQL查询
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	protected int getInt(String hql){
		return (int)getDouble(hql);
	}
	
	/**
	 * 根据HQL查询
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	protected double getDouble(String hql){
		Object obj = getOne(hql);
		if(obj == null) return 0d;
		return Double.parseDouble(obj.toString());
	}
	
	/**
	 * 根据ID查询对象
	 * @param objClass 对象Class
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public Object get(int id, Class objClass) {
		return getHibernateTemplate().get(objClass, id);
	}

	/**
	 * 根据ID查询对象
	 * @param objClass 对象Class
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public Object load(String id, Class objClass) {
		return getHibernateTemplate().load(objClass, id);
	}
	
	/**
	 * 根据ID查询对象
	 * @param Object 对象
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public int delete(Object obj) {
		if(obj == null) return 0;
		getHibernateTemplate().delete(obj);
		return 1;
	}

	/**
	 * 刷新对象对象
	 * @param objClass 对象Class
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public void refresh(Object obj) {
		getHibernateTemplate().refresh(obj);
	}
	
	/**
	 * 刷新session
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public void flush() {
		getHibernateTemplate().flush();
	}
	
	/**
	 * session clear
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public void clear() {
		getHibernateTemplate().clear();
	}
	
	/**
	 * 根据hql查询数据集合
	 * @param hql hql语句
	 * @author yxl
	 */
	protected List getList(String hql) {
		
		return getHibernateTemplate().find(hql);
	}
	
	/**
	 * 根据hql查询数据集合
	 * @param hql hql语句
	 * @author yxl
	 */
	protected List find(String hql) {
		
		return getHibernateTemplate().find(hql);
	}
	
	/**
	 * 根据hql查询数据集合
	 * @param hql hql语句
	 * @param maxCount 返回最大记录条数
	 * @author yxl
	 */
	protected List getList(String hql, int maxCount) {
		
		HibernateTemplate hibernateTemplate =  getHibernateTemplate();
		hibernateTemplate.setMaxResults(maxCount);
		List list = hibernateTemplate.find(hql);
		hibernateTemplate.setMaxResults(Integer.MAX_VALUE);
		return list;
	}
	
	/**
	 * 分页查询
	 * @param pSql hql 语句
	 * @param args 查询参数
	 * @param page 分页工具类
	 * @autor yxl
	 * createtime Feb 21, 2012
	 */
	protected void pageQuery(String pSql, String[] args, Page page) {
		if (args != null)
			for (int i = 0; i < args.length; i++) {
				if (pSql.indexOf("?") == -1 && i < args.length) {// 查询参数过多
					try {
						throw new Exception("查询参数过多");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				pSql = pSql.replaceFirst("[?]", args[i]);
				if (i == args.length - 1 && pSql.indexOf("?") != -1) {// 查询参数不足
					try {
						throw new Exception("查询参数不足");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		pageQuery(pSql, page);
	}
	
	
	
	/**
	 * 分页查询
	 * @param hql hql 语句
	 * @param page 分页工具类
	 * @autor yxl
	 * createtime Feb 21, 2012
	 */
	protected void pageQuerySimple(final String hql, Page page) {
		
		//先查询所有数据
		List allDataList = getHibernateTemplate().find(hql);
		
		// 总行数
		int totalRows = allDataList.size();
		page.setTotalRows(totalRows);

		// 数据从多少行开始，到多少行结束
		int endRow = page.getStartRow()+page.getPageSize();
		if(endRow > totalRows) endRow = totalRows;
		page.setResult(allDataList.subList(page.getStartRow(), endRow));
	}
	
	
	/**
	 * 分页查询
	 * @param hql hql 语句
	 * @param page 分页工具类
	 * @autor yxl
	 * createtime Feb 21, 2012
	 */
	protected void pageQuery(final String hql, final Page page) {
		
		List list = getHibernateTemplate().executeFind(new HibernateCallback(){
            public Object doInHibernate(Session session)
                throws HibernateException, SQLException {
            	
            	//查询总页数
            	String countHql = AbstractBaseHibernateDAO.getCountHql(hql);
        		Query query = session.createQuery(countHql);
        		long totalCount = 0l;
        		try{
        			if(query.uniqueResult() != null) totalCount = (Long) query.uniqueResult();
        		} catch(Exception e){
        			query = session.createQuery(hql);
        			ScrollableResults sr = query.scroll(ScrollMode.SCROLL_SENSITIVE);
            	    sr.last();
            	    totalCount = sr.getRowNumber()+1;
            	    sr.close();
        		}
        		
        		page.setTotalRows(totalCount);
        		
        		//查询数据
        		query = session.createQuery(hql);
        		// 数据从多少行开始
        		query.setFirstResult(page.getStartRow());
        		query.setMaxResults(page.getPageSize());
        		
        		return query.list();
            }
        });
		page.setResult(list);
	}
	
	/**
	 * 分页查询
	 * @param sql sql 语句
	 * @param page 分页工具类
	 * @autor yxl
	 * createtime Feb 21, 2012
	 */
	protected void pageSqlQuery(final String sql, final Page page) {
		
		List list = getHibernateTemplate().executeFind(new HibernateCallback(){
            public Object doInHibernate(Session session)
                throws HibernateException, SQLException {
            	
            	//查询总页数
            	String countSql = AbstractBaseHibernateDAO.getCountSql(sql);
        		Query query = session.createSQLQuery(countSql);
        		long totalCount = 0l;
        		if(query.uniqueResult() != null) totalCount = Long.parseLong(query.uniqueResult().toString());
        		page.setTotalRows(totalCount);
        		
        		//查询数据
        		query = session.createSQLQuery(sql);
        		
        		
        		//转换所有字段为字符串类型
        		Pattern patt = Pattern.compile("\\s+([a-zA-Z0-9_]*)[\\s]*,");
        		Matcher matcher;
        		String paStr = sql;
        		while(paStr.contains("(")) paStr = paStr.replaceAll("\\([^\\(\\)]*\\)", " ");
        		paStr = paStr.replaceAll(" from ", " ,FROM ");
        		if(paStr.contains(" ,FROM ")) paStr = paStr.substring(0, paStr.indexOf(" ,FROM ")+2);
        		matcher = patt.matcher(paStr);
        		while (matcher.find()){
        			String colname = matcher.group(1).trim();
        			if(colname.length()>0) ((SQLQuery) query).addScalar(colname, Hibernate.STRING);
        		}
        		
        		// 数据从多少行开始
        		query.setFirstResult(page.getStartRow());
        		query.setMaxResults(page.getPageSize());
        		
        		return query.list();
            }
        });
		page.setResult(list);
	}
	
	
	//sql 语句的正则表达式
	private static Pattern sqlPt = Pattern.compile(
			"(^\\s*(select|SELECT|Select)\\s*[\\.\\(\\)a-zA-Z_,\\s]+\\s+)(from|From|FROM)"
			);
	//sql 语句的正则表达式处理
	private static Matcher sqlMatcher;
	
	/**
	 * 获取查询数量的hql
	 * @param hql hql语句
	 * @autor yxl
	 */
	protected static String getCountHql(String hql){
		
		sqlMatcher = sqlPt.matcher(hql);
		if(sqlMatcher.find()){
			hql = sqlMatcher.replaceFirst("SELECT count(*) FROM");
		} else {
			hql = "SELECT count(*) " + hql;
		}
		
		return hql;
	}
	
	
	/**
	 * 获取查询数量的sql
	 * @param sql sql语句
	 * @autor yxl
	 */
	protected static String getCountSql(String sql){
		
		return "SELECT count(*) FROM (" + sql + ") ct";
	}
	
	/*public static void main(String[] args) {
		String hql = 
				"select kw.keywordsIndexInfo.url,  kw.keywordsIndexInfo.productNews.productNewsInfo.name,  kw.keywordsIndexInfo.snapshot, iw.icpWebsiteInfo.homeUrl from KeywordsIndex kw, IcpWebsite iw where kw.websiteKey=iw.website.id and ( kw.keywords.keywords like '%轮奸%' or kw.keywords.keywords like '%少女%') group by kw.keywordsIndexInfo.productNews.id order by kw.score desc, kw.keywordsIndexInfo.densityScore desc, kw.keywordsIndexInfo.createTime asc";
		System.out.println(getCountSql(hql));
	}*/
	
	/**
	 * 获取数据的insert语句
	 * @param hql hql语句
	 * @autor yxl
	 */
	protected String exportInsert(final String hql) {
		
		List list = getHibernateTemplate().executeFind(new HibernateCallback(){
            public Object doInHibernate(Session session)
                throws HibernateException, SQLException {
            	
            	String insertSql = "";//insert语句
            	Map tableInfoMap = null;//列名
            	//查询总页数
        		Query query = session.createQuery(hql);
        		//循环数据
        		boolean isFirstObj = true;
        		for(Object obj : query.list()){
        			if(tableInfoMap == null){//初始化insert 语句
        				tableInfoMap = AbstractBaseHibernateDAO.getTableInfo(session, obj.getClass());
        				insertSql = "INSERT INTO " + tableInfoMap.get("tableName")+"(";
        				boolean isFirst = true;
        				for(String tableColumnName : (List<String>) tableInfoMap.get("tableColumnName")){
        					if(!isFirst) insertSql += ",";
        					else isFirst = false;
        					insertSql += tableColumnName;
        				}
        				insertSql += ") VALUES ";
        			}
        			//匹配属性值
        			try {
        				if(!isFirstObj) insertSql += ", ";
        				else isFirstObj = false;
        				insertSql += "(";
        				boolean isFirst = true;
        				for(String entryColumnName : (List<String>) tableInfoMap.get("entryColumnName")){
        					if(!isFirst) insertSql += ",";
        					else isFirst = false;
        					insertSql += AbstractBaseHibernateDAO.wrapSqlVal(ClassProxy.getProperty(obj, entryColumnName), session);
            			}
        				insertSql += ")";
					} catch (Exception e) {
						e.printStackTrace();
						insertSql = "error: " + e.getMessage();
						break;
					}
        		}
        		insertSql += ";";
        		
        		List insertTempList = new ArrayList();
        		insertTempList.add(insertSql);
        		return insertTempList;
            }
        });
		return list.get(0).toString();
	}
	
	/**
	 * 获取表信息
	 * @param session hibernate session
	 * @param entryClass 实体类的class、
	 * @return Map{tableName, entryColumnName, tableColumnName}
	 * @autor yxl
	 */
	protected static Map getTableInfo(Session session, Class entryClass){
		Map map = new HashMap();
		ClassMetadata hibernateMetadata = session.getSessionFactory().getClassMetadata(entryClass);
		if (hibernateMetadata instanceof AbstractEntityPersister){
			AbstractEntityPersister persister = (AbstractEntityPersister) hibernateMetadata;
			map.put("tableName", persister.getTableName());//表名
			
			List entryColumnName = new ArrayList();//列名
			List tableColumnName = new ArrayList();//表列名
			tableColumnName.add(persister.getIdentifierColumnNames()[0]);//主键
			entryColumnName.add(persister.getIdentifierPropertyName());//主键
			for (String propertyName : hibernateMetadata.getPropertyNames()) {
		    	//判断是否一对多的对像
		        boolean isCollection = hibernateMetadata.getPropertyType(propertyName).isCollectionType();
		        if(!isCollection){
		        	tableColumnName.add(persister.toColumns(propertyName)[0]);
		        	entryColumnName.add(propertyName);
		        }
		    }
			
			map.put("entryColumnName", entryColumnName);
			map.put("tableColumnName", tableColumnName);
		}
		return map;
	}
	
	
	/**
	 * 对sql的值进行包裹
	 * @param val sql值
	 * @autor yxl
	 */
	protected static String wrapSqlVal(Object val, Session session){
		if(val == null) return "NULL";
		
		//如果值是一个hibernate实体类，则去获取他的主键值
		ClassMetadata hibernateMetadata = session.getSessionFactory().getClassMetadata(val.getClass());
		if(hibernateMetadata != null){
			if (hibernateMetadata instanceof AbstractEntityPersister){
				AbstractEntityPersister persister = (AbstractEntityPersister) hibernateMetadata;
				String PKName = persister.getIdentifierPropertyName();//主键名称
				return AbstractBaseHibernateDAO.wrapSqlVal(ClassProxy.getProperty(val, PKName), session);
			}
			return "NULL";
		} else {
			String wrapTag = "'";//包裹符号
			if(val instanceof Integer || val instanceof Long || val instanceof Float || val instanceof Double){
				wrapTag = "";
			}
			return wrapTag + val.toString() + wrapTag;
		}
	}
	
	public static void main(String[] args) {
		String hql = "select "
				+ "sum(case when t.visit_time>0 then 1 else 0 end) as visited,"
				+ "sum(case when t.visit_time>0 then 0 else 1 end) as novisited,"
				+ "str_to_date(from_unixtime(t.plan_time/1000),'%Y-%m-%d') as planDate,"
				+ "e.id as employeeid_str ,e.name as employeename_str "
				+ "from customer_visit t ,employee e ";
		//筛选需要转换为字符串的字段，以_str结尾
		Pattern patt = Pattern.compile("\\s+([a-zA-Z0-9_]*)[\\s]*,");
		Matcher matcher;
		String paStr = hql;
		while(paStr.contains("(")) paStr = paStr.replaceAll("\\([^\\(\\)]*\\)", " ");
		paStr = paStr.replaceAll(" from ", " ,FROM ");
		if(paStr.contains(" ,FROM ")) paStr = paStr.substring(0, paStr.indexOf(" ,FROM ")+2);
		System.out.println(paStr);
		matcher = patt.matcher(paStr);
		while (matcher.find()){
			String colname = matcher.group(1).trim();
			if(colname.length()>0) System.out.println(matcher.group(1));
		}
	}
}