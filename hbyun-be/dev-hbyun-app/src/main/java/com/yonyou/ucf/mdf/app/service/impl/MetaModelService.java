package com.yonyou.ucf.mdf.app.service.impl;

import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.model.model.MetaColumnModel;
import com.yonyou.ucf.mdd.common.model.model.MetaModel;
import com.yonyou.ucf.mdd.common.utils.Toolkit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.imeta.core.lang.BooleanUtils;
import org.imeta.core.model.*;
import org.imeta.spring.base.MetaBeanFactory;

import java.util.*;

@Slf4j
public class MetaModelService {
	private List<MetaModel> metaModels=new ArrayList<>();
	private Set<String> topModels=new HashSet<>();
	private Set<String> existsModels=new HashSet<>();
	private Map<String,Entity> cacheEntities=new HashMap<>();
	private Set<String> preFullnames;  //预热
	private boolean onlyOneLevel=false; //仅一级
	public List<MetaModel> getModels(List<String> fullnames, Map<String,List<String>> fields, boolean onlyOneLevel) throws Exception {
		try{
			this.onlyOneLevel=onlyOneLevel;
			Map<String,Object> params=new HashMap<>();
			params.put("tenantId", MddBaseContext.getTenantId());
			if(null==fullnames || fullnames.size()==0){
				fullnames=MddBaseContext.uimetaDao().selectList("com.yonyou.ucf.mdd.bill.dao.design.getMetaModels",params);
			}
			long d=System.currentTimeMillis();
			if(null!=fields && fields.size()>0) {
				try{
					preFullnames=null;//todo:PreProcessUtil.preHandleCFNames(fields);
				}catch(Exception e){
					log.error("preHandleCFNames",e);
				}
			}
			log.info("preHandleCFNames耗时:{},fullnames:{}", (System.currentTimeMillis()-d),preFullnames);
			d=System.currentTimeMillis();
			if(null==preFullnames && !onlyOneLevel) {  //仅一级,不预热
				for(String fullname:fullnames){
//					try{
//						PreProcessUtil.preHandleRelation(fullname);
//					}catch(Exception e){
//						log.error("preHandleRelation",e);
//					}
				}
			}
			log.info("preHandleRelation耗时:{}", (System.currentTimeMillis()-d));
			for(String fullname:fullnames){
				if(!Toolkit.isEmpty(fullname)) {
					d=System.currentTimeMillis();
					createMetaModelByName(fullname,true,false);
					log.info("createMetaModelByName:{}耗时:{}",fullname, (System.currentTimeMillis()-d));
					existsModels.clear();
				}
			}
			cacheEntities.clear();
			return metaModels;
		}catch(Exception e){
			log.error("getModels",e);
			throw e;
		}
	}

	private Entity getEntity(String entityName){
		Entity entity = null;
		try{
			if(null!=preFullnames){
				if(!preFullnames.contains(entityName)) return null;
			}
			entity=cacheEntities.get(entityName);
			if(null==entity){
				long d=System.currentTimeMillis();
				entity=MetaBeanFactory.getMetaRepository().entity(entityName);
				log.info("getEntity:{}耗时:{}",entityName, (System.currentTimeMillis()-d));
				cacheEntities.put(entityName, entity);
			}
		}catch(Exception e){
			log.error("getEntity:{},reason:{}", entityName,e.getMessage());
		}
		return entity;
	}

	private MetaModel createMetaModelByName(String entityName, boolean isMain, boolean isRelation) throws Exception {
		Entity entity = getEntity(entityName);
		MetaModel metaModel=null;
		if(entity!=null){
			metaModel=new MetaModel(entity.fullname(),entity.title(),true);
			if(null!=entity.keyAttribute()) metaModel.setPkcolumn(entity.keyAttribute().name());
			if(isMain) {
				if(!topModels.contains(metaModel.getCode())) {
					metaModels.add(metaModel);
					topModels.add(metaModel.getCode());
				}else{
					return metaModel;
				}
			}else{
				metaModel.setCode(null);
				metaModel.setLabel(null);
				metaModel.setRoot(false);
			}

			Set<String> thisRelations=new HashSet<>();

			if(!CollectionUtils.isEmpty(entity.attributes())){
				List<MetaColumnModel> columns=new ArrayList<>();
				metaModel.setColumns(columns);
				for(Property attr:entity.attributes()){
					MetaColumnModel column = new MetaColumnModel(attr.name(),attr.title(),getType(attr));
					if(attr!=null &&!BooleanUtils.b(attr.isAssociationAttribute()) && attr.mode()==PersistentMode.All){
						columns.add(column);
					}else if(attr!=null && BooleanUtils.b(attr.isAssociationAttribute())
							&& !BooleanUtils.b(attr.isDependencied()) //&& BooleanUtils.b(attr.isRoleA())
							&& !BooleanUtils.b(attr.isRedundant()) ){
						columns.add(column);
						String thisFullname=attr.get("typeUri");//attr.type().fullname()
						if(BooleanUtils.b(attr.isCompositionAttribute())){  //子实体
							if(isRelation && null==preFullnames){
								continue;
							}else{
								if(BooleanUtils.b(attr.isRoleA())){
									Association asso = attr.association();
									column.setParentcol(asso.roleB().name());
									Entity subEntity = getEntity(asso.roleA().type().fullname());
									if(null==subEntity) continue;
									column.setLabel(subEntity.title());

									String pcFullname=entityName+"-"+thisFullname;
									if(existsModels.contains(pcFullname) && existsModels.contains(thisFullname+"-"+entityName)){
										System.out.println(pcFullname+" exists");
										continue;
									}
									existsModels.add(pcFullname);

									MetaModel childref=null;
									if(!onlyOneLevel) {  //仅一级，不展开下级
										childref=createMetaModelByName(thisFullname,false,false);
									}

									column.setChildref(childref);
								}else{
									column.setRef(thisFullname);
									column.setType("entity");
								}
							}
						}else if(BooleanUtils.b(attr.isAssociationAttribute())){  //引用
							column.setRef(thisFullname);
							column.setType("entity");
							if(isRelation && null==preFullnames){
								continue;
							}else{
								if(null!=preFullnames){
									if(preFullnames.contains(thisFullname)) thisRelations.add(thisFullname);
								}else{
									thisRelations.add(thisFullname);
								}
							}
						}

					}
				}
				if(!CollectionUtils.isEmpty(thisRelations)){
					for (String relation : thisRelations) {
						if(!topModels.contains(relation)) {
							createMetaModelByName(relation,true,true);
							topModels.add(relation);
						}
					}

				}
			}
		}
		return metaModel;
	}
	private String getType(Property attr){
		try{
			if(null==attr) return "varchar";
			Object oType=attr.get("type");
			String type=null;
			if(oType instanceof org.imeta.core.model.Enumeration){
				return "tinyint";
			}else{
				if(oType instanceof org.imeta.core.model.DataType){
					type=((DataType)oType).name();
				}
			}

			if(BooleanUtils.b(attr.isAssociationAttribute())){
				return "entity";
			}else{
				String jdbcType="";
				switch(type){
					case "Integer":
						jdbcType="smallint";
						break;
					case "Byte":
					case "Short":
						jdbcType="tinyint";
						break;
					case "Long":
						jdbcType="bigint";
						break;
					case "Decimal":
						jdbcType="decimal";
						break;
					case "Boolean":
						jdbcType="bit";
						break;
					case "Date":
					case "DateTime":
					case "Time":
					case "IntDate":
					case "IntDateTime":
						jdbcType="datetime";
						break;
					case "String":
					case "Text":
					default:
						jdbcType="varchar";
				}
				return jdbcType;
			}
		}catch(Exception e){
			log.error("getType:{},error:{}",attr,e);
		}
		return "varchar";
	}

	public static boolean b(Object o){
		if(o == null) return false;
		if(o instanceof String){
			return Boolean.parseBoolean((String)o);
		}
		return BooleanUtils.b(o);
	}
}
