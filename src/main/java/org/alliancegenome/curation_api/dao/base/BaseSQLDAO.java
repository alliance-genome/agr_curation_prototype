package org.alliancegenome.curation_api.dao.base;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.transaction.Transactional;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.processing.IndexProcessDisplayService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.elasticsearch.index.query.Operator;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.tree.domain.SqmPluralValuedSimplePath;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.common.BooleanOperator;
import org.hibernate.search.engine.search.common.ValueConvert;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.engine.search.sort.dsl.CompositeSortComponentsStep;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.massindexing.MassIndexingMonitor;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.TypesAnnotated;

public class BaseSQLDAO<E extends AuditedObject> extends BaseEntityDAO<E> {

	@Inject
	protected EntityManager entityManager;
	@Inject
	protected SearchSession searchSession;
	@Inject
	protected IndexProcessDisplayService indexProcessDisplayService;

	protected BaseSQLDAO(Class<E> myClass) {
		super(myClass);
		Log.debug("EntityManager: " + entityManager);
		Log.debug("SearchSession: " + searchSession);
	}

	@Transactional
	public E persist(E entity) {
		Log.debug("SqlDAO: persist: " + entity);
		try {
			entityManager.persist(entity);
		} catch (Exception e) {
			Log.error("Entity Persist Failed: " + entity);
			throw e;
		}
		return entity;
	}

	@Transactional
	public List<E> persist(List<E> entities) {
		Log.debug("SqlDAO: persist: " + entities);
		try {
			entityManager.persist(entities);
		} catch (Exception e) {
			Log.error("Entity Persist Failed: " + entities);
			throw e;
		}
		return entities;
	}

	@Transactional
	public E merge(E entity) {
		Log.debug("SqlDAO: merge: " + entity);
		try {
			entityManager.merge(entity);
		} catch (Exception e) {
			Log.error("Entity Persist Failed: " + entity);
			throw e;
		}
		return entity;
	}

	@Transactional
	public E remove(Long id) {
		Log.debug("SqlDAO: remove: " + id);
		E entity = find(id);
		try {
			entityManager.remove(entity);
			entityManager.flush();
		} catch (PersistenceException e) {
			handlePersistenceException(entity, e);
		}
		return entity;
	}

	public E find(Long id) {
		Log.debug("SqlDAO: find: " + id + " " + myClass);
		if (id != null) {
			E entity = entityManager.find(myClass, id);
			Log.debug("Entity Found: " + entity);
			return entity;
		} else {
			Log.debug("Input Param is null: " + id);
			return null;
		}
	}

	private List<Predicate> buildRestrictions(Root<E> root, Map<String, Object> params, Logger.Level level) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		List<Predicate> restrictions = new ArrayList<>();

		for (String key : params.keySet()) {
			Path<Object> column = null;
			Log.log(level, "Key: " + key);
			if (key.contains(".")) {
				String[] objects = key.split("\\.");
				for (String s : objects) {
					Log.log(level, "Looking up: " + s);
					if (column != null) {
						Log.log(level, "Looking up via column: " + s);
						Path<Object> pathColumn = column.get(s);
						if (pathColumn.getJavaType().equals(List.class)) {
							column = ((Join) column).joinList(s, JoinType.LEFT);
						} else {
							column = pathColumn;
						}
					} else {
						Log.log(level, "Looking up via root: " + s);
						column = root.get(s);
						if (column.getJavaType().equals(List.class)) {
							column = root.joinList(s, JoinType.LEFT);
						}
					}

					Log.log(level, "Column Alias: " + column.getAlias() + " Column Java Type: " + column.getJavaType() + " Column Model: " + column.getModel() + " Column Parent Path Alias: " + column.getParentPath().getAlias());
				}
			} else {
				Log.log(level, "Looking up via root: " + key);
				column = root.get(key);
				// Don't need to join to these tables if value is null, the isEmpty will catch
				// the condition later
				Object value = params.get(key);
				if (value != null) {
					if (column instanceof SqmPluralValuedSimplePath) {
						column = root.joinList(key, JoinType.LEFT);
					}
				}
			}

			Log.log(level, "Column Alias: " + column.getAlias() + " Column Java Type: " + column.getJavaType() + " Column Model: " + column.getModel() + " Column Parent Path Alias: " + column.getParentPath().getAlias());

			Object value = params.get(key);

			if (value == null) {
				restrictions.add(builder.isEmpty(root.get(key)));
			} else if (value instanceof Integer) {
				Log.log(level, "Integer Type: " + value);
				Integer desiredValue = (Integer) value;
				restrictions.add(builder.equal(column, desiredValue));
			} else if (value instanceof Enum) {
				Log.log(level, "Enum Type: " + value);
				restrictions.add(builder.equal(column, value));
			} else if (value instanceof Long) {
				Log.log(level, "Long Type: " + value);
				Long desiredValue = (Long) value;
				restrictions.add(builder.equal(column, desiredValue));
			} else if (value instanceof Boolean) {
				Log.log(level, "Boolean Type: " + value);
				Boolean desiredValue = (Boolean) value;
				restrictions.add(builder.equal(column, desiredValue));
			} else if (value instanceof String) {
				Log.log(level, "String Type: " + value);
				String desiredValue = (String) value;
				restrictions.add(builder.equal(column, desiredValue));
			} else {
				// Not sure what to do here as we have a non supported value
				Log.info("Unsupprted Value: " + value);
			}
		}

		return restrictions;
	}

	public List<Long> findIdsByParams(Map<String, Object> params) {
		Logger.Level level = Level.DEBUG;
		if (params.containsKey("debug")) {
			level = params.remove("debug").equals("true") ? Level.INFO : Level.DEBUG;
		}

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<E> root = query.from(myClass);

		Operator queryOperator = Operator.AND;
		if (params.containsKey("query_operator")) {
			queryOperator = params.remove("query_operator").equals("or") ? Operator.OR : Operator.AND;
		}

		List<Predicate> restrictions = buildRestrictions(root, params, level);

		query.orderBy(builder.asc(root.get("id")));

		if (queryOperator == Operator.AND) {
			query.where(builder.and(restrictions.toArray(new Predicate[0])));
		} else {
			query.where(builder.or(restrictions.toArray(new Predicate[0])));
		}

		query.select(root.get("id"));

		List<Long> filteredIds = entityManager.createQuery(query).getResultList();

		return filteredIds;
	}

	public SearchResponse<Long> findAllIds() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> findQuery = cb.createQuery(myClass);
		Root<E> rootEntry = findQuery.from(myClass);

		CriteriaQuery<E> all = findQuery.select(rootEntry);

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		countQuery.select(cb.count(countQuery.from(myClass)));
		Long totalResults = entityManager.createQuery(countQuery).getSingleResult();

		TypedQuery<E> allQuery = entityManager.createQuery(all);
		SearchResponse<Long> results = new SearchResponse<Long>();

		List<Long> primaryKeys = new ArrayList<>();

		for (E entity : allQuery.getResultList()) {
			Long pk = returnId(entity);
			if (pk != null) {
				primaryKeys.add(pk);
			}
		}

		results.setResults(primaryKeys);
		results.setTotalResults(totalResults);
		return results;
	}

	private Long returnId(E entity) {
		Long pk = null;
		try {
			pk = (Long) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
		} catch (ClassCastException e) {
			Log.error("Could not convert entity ID to string: " + e.getMessage());
		}
		return pk;
	}

	public SearchResponse<E> findAll() {
		return findAll(null);
	}

	public SearchResponse<E> findAll(Pagination pagination) {
		Log.debug("SqlDAO: findAll: " + myClass);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> findQuery = cb.createQuery(myClass);
		Root<E> rootEntry = findQuery.from(myClass);

		Metamodel metaModel = entityManager.getMetamodel();
		IdentifiableType<E> of = (IdentifiableType<E>) metaModel.managedType(myClass);

		CriteriaQuery<E> all = findQuery.select(rootEntry).orderBy(cb.asc(rootEntry.get(of.getId(of.getIdType().getJavaType()).getName())));

		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		countQuery.select(cb.count(countQuery.from(myClass)));
		Long totalResults = entityManager.createQuery(countQuery).getSingleResult();

		TypedQuery<E> allQuery = entityManager.createQuery(all);
		if (pagination != null && pagination.getLimit() != null && pagination.getPage() != null) {
			int first = pagination.getPage() * pagination.getLimit();
			if (first < 0) {
				first = 0;
			}
			allQuery.setFirstResult(first);
			allQuery.setMaxResults(pagination.getLimit());
		}
		SearchResponse<E> results = new SearchResponse<E>();
		results.setResults(allQuery.getResultList());
		results.setTotalResults(totalResults);
		return results;
	}

	private void handlePersistenceException(E entity, Exception e) {
		ObjectResponse<E> response = new ObjectResponse<E>(entity);
		Throwable rootCause = e.getCause();
		while (rootCause.getCause() != null) {
			rootCause = rootCause.getCause();
		}
		if (rootCause instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException) rootCause;
			response.setErrorMessage("Violates database constraint " + cve.getConstraintName());
		} else {
			response.setErrorMessage(rootCause.getMessage());
		}
		throw new ApiErrorException(response);
	}

	// DB Count
	public Long dbCount(Class<?> clazz) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = qb.createQuery(Long.class);
		cq.select(qb.count(cq.from(clazz)));
		return entityManager.createQuery(cq).getSingleResult();
	}

	// ES Count
	public Long esCount(Class<?> clazz) {
		return searchSession.search(clazz).where(f -> f.matchAll()).fetchTotalHitCount();
	}

	public void flush() {
		entityManager.flush();
	}

	public void commit() {
		entityManager.getTransaction().commit();
	}

	public void reindex() {
		reindex(myClass, 1000, 10000, 0, 4, 14400, 1);
	}

	public void reindex(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		reindex(myClass, batchSizeToLoadObjects, idFetchSize, limitIndexedObjectsTo, threadsToLoadObjects, transactionTimeout, typesToIndexInParallel);
	}

	public void reindexEverything(Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {
		Reflections reflections = new Reflections("org.alliancegenome.curation_api");
		Set<Class<?>> annotatedClasses = reflections.get(TypesAnnotated.with(Indexed.class).asClass(reflections.getConfiguration().getClassLoaders()));

		ProcessDisplayHelper ph = new ProcessDisplayHelper(5000);
		ph.addDisplayHandler(indexProcessDisplayService);
		ph.startProcess("Mass Index Everything");
		MassIndexer indexer = searchSession.massIndexer(annotatedClasses).batchSizeToLoadObjects(batchSizeToLoadObjects).idFetchSize(idFetchSize).dropAndCreateSchemaOnStart(true).mergeSegmentsOnFinish(false).typesToIndexInParallel(typesToIndexInParallel).threadsToLoadObjects(threadsToLoadObjects)
			.monitor(new MassIndexingMonitor() {
				@Override
				public void documentsAdded(long increment) {
				}

				@Override
				public void entitiesLoaded(long increment) {
				}

				@Override
				public void addToTotalCount(long increment) {
				}

				@Override
				public void documentsBuilt(long increment) {
					ph.progressProcess();
				}

				@Override
				public void indexingCompleted() {
					ph.finishProcess();
				}
			});

		indexer.transactionTimeout(transactionTimeout);
		if (limitIndexedObjectsTo > 0) {
			indexer.limitIndexedObjectsTo(limitIndexedObjectsTo);
		}
		indexer.start();
	}

	public void reindex(Class<?> objectClass, Integer batchSizeToLoadObjects, Integer idFetchSize, Integer limitIndexedObjectsTo, Integer threadsToLoadObjects, Integer transactionTimeout, Integer typesToIndexInParallel) {

		Log.debug("Starting Indexing for: " + objectClass);
		MassIndexer indexer = searchSession.massIndexer(objectClass).batchSizeToLoadObjects(batchSizeToLoadObjects).idFetchSize(idFetchSize).dropAndCreateSchemaOnStart(true).mergeSegmentsOnFinish(false).typesToIndexInParallel(typesToIndexInParallel).threadsToLoadObjects(threadsToLoadObjects)
			.monitor(new MassIndexingMonitor() {

				ProcessDisplayHelper ph = new ProcessDisplayHelper(5000);

				@Override
				public void documentsAdded(long increment) {
				}

				@Override
				public void entitiesLoaded(long increment) {
				}

				@Override
				public void addToTotalCount(long increment) {
					ph.addDisplayHandler(indexProcessDisplayService);
					ph.startProcess("Mass Indexer for: " + objectClass.getSimpleName(), increment);
				}

				@Override
				public void documentsBuilt(long increment) {
					ph.progressProcess();
				}

				@Override
				public void indexingCompleted() {
					ph.finishProcess();
				}

			});

		indexer.transactionTimeout(transactionTimeout);
		if (limitIndexedObjectsTo > 0) {
			indexer.limitIndexedObjectsTo(limitIndexedObjectsTo);
		}
		indexer.start();
	}

	public SearchResponse<E> searchAll(Pagination pagination) {
		return searchByParams(pagination, null);
	}

	public SearchResponse<E> searchByField(Pagination pagination, String field, String value) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(field, value);
		return searchByParams(pagination, params);
	}

	public SearchResponse<E> searchByParams(Pagination pagination, Map<String, Object> params) {
		Logger.Level level = Level.DEBUG;
		if (params.containsKey("debug")) {
			level = params.remove("debug").equals("true") ? Level.INFO : Level.DEBUG;
		}

		Log.log(level, "Search: " + pagination + " Params: " + params);

		SearchQueryOptionsStep<?, E, SearchLoadingOptionsStep, ?, ?> step = searchSession.search(myClass).where(p -> {
			AtomicInteger outerBoost = new AtomicInteger();
			return p.bool().with(b -> {
				if (params.containsKey("searchFilters")) {
					HashMap<String, HashMap<String, HashMap<String, Object>>> searchFilters = (HashMap<String, HashMap<String, HashMap<String, Object>>>) params.get("searchFilters");
					outerBoost.set(searchFilters.keySet().size());
					String filterOperator = (String) params.get("searchFilterOperator");
					for (String filterName : searchFilters.keySet()) {
						BooleanPredicateClausesStep<?> bpStep = p.bool().with(s -> {
							s.must(f -> f.bool().with(q -> {
								int innerBoost = searchFilters.get(filterName).keySet().size();
								for (String field : searchFilters.get(filterName).keySet()) {
									if (field.equals("nonNullFields") || field.equals("nullFields")) {
										continue;
									}
									float boost = (outerBoost.get() * 10000) + (innerBoost * 1000);
									if (boost <= 0) {
										boost = 0;
									}

									String op = (String) searchFilters.get(filterName).get(field).get("tokenOperator");
									BooleanOperator booleanOperator = op == null ? BooleanOperator.AND : BooleanOperator.valueOf(op);

									Boolean useKeywordFields = (Boolean) searchFilters.get(filterName).get(field).get("useKeywordFields");

									String queryType = (String) searchFilters.get(filterName).get(field).get("queryType");
									if (queryType != null && queryType.equals("matchQuery")) {
										BooleanPredicateClausesStep<?> clause = p.bool();
										if (useKeywordFields != null && useKeywordFields) {
											clause.should(p.match().field(field + "_keyword").matching(searchFilters.get(filterName).get(field).get("queryString").toString()).boost(boost + 500));
										} else {
											clause.should(p.match().field(field).matching(searchFilters.get(filterName).get(field).get("queryString").toString()).boost(boost));
										}
										q.should(clause);
									} else { // assume simple query
										BooleanPredicateClausesStep<?> clause = p.bool();
										if (useKeywordFields != null && useKeywordFields) {
											clause.should(p.simpleQueryString().fields(field + "_keyword").matching(searchFilters.get(filterName).get(field).get("queryString").toString()).defaultOperator(booleanOperator).boost(boost + 500));
										}
										clause.should(p.simpleQueryString().fields(field).matching(searchFilters.get(filterName).get(field).get("queryString").toString()).defaultOperator(booleanOperator).boost(boost));
										q.should(clause);
									}
									innerBoost--;
								}
							}));
							if (searchFilters.get(filterName).containsKey("nonNullFields")) {
								s.must(f -> f.bool().with(q -> {
									List<String> fields = (List<String>) searchFilters.get(filterName).get("nonNullFields");
									fields.forEach(field -> q.must(p.exists().field(field)));
								}));
							}
							if (searchFilters.get(filterName).containsKey("nullFields")) {
								s.must(f -> f.bool().with(q -> {
									List<String> fields = (List<String>) searchFilters.get(filterName).get("nullFields");
									fields.forEach(field -> q.mustNot(p.exists().field(field)));
								}));
							}
						});
						if (filterOperator != null && filterOperator.equals("OR")) {
							b.should(bpStep);
						} else {
							b.must(bpStep);
						}
						outerBoost.decrementAndGet();
					}
					if (searchFilters.keySet().size() == 0) {
						b.must(p.matchAll());
					}
				} else {
					b.must(p.matchAll());
				}
				if (params.containsKey("nonNullFieldsTable")) {
					List<String> fields = (List<String>) params.get("nonNullFieldsTable");
					fields.forEach(field -> b.must(m -> m.bool().with(s -> s.should(p.exists().field(field)))));
				}
			});
		});

		if (params.containsKey("sortOrders")) {
			step = step.sort(f -> {
				CompositeSortComponentsStep<?> com = f.composite();
				ArrayList<HashMap<String, Object>> sortOrders = (ArrayList<HashMap<String, Object>>) params.get("sortOrders");
				if (sortOrders != null) {
					for (HashMap<String, Object> map : sortOrders) {
						String key = (String) map.get("field");
						int value = (int) map.get("order");
						if (value == 1) {
							com.add(f.field(key + "_keyword").asc());
						}
						if (value == -1) {
							com.add(f.field(key + "_keyword").desc());
						}
					}
				}
				return com;
			});
		}

		List<AggregationKey<Map<String, Long>>> aggKeys = new ArrayList<>();

		if (params.containsKey("aggregations")) {
			ArrayList<String> aggList = (ArrayList<String>) params.get("aggregations");
			for (String aggField : aggList) {
				AggregationKey<Map<String, Long>> aggKey = AggregationKey.of(aggField);
				aggKeys.add(aggKey);
				step = step.aggregation(aggKey, p -> p.terms().field(aggField + "_keyword", String.class, ValueConvert.NO).maxTermCount(30));
			}
		}

		SearchQuery<E> query = step.toQuery();

		SearchResponse<E> results = new SearchResponse<E>();
		if (level == Level.INFO) {
			results.setDebug("true");
			results.setEsQuery(query.queryString());
		}
		Log.log(level, query);

		SearchResult<E> result = query.fetch(pagination.getPage() * pagination.getLimit(), pagination.getLimit());

		if (aggKeys.size() > 0) {
			Map<String, Map<String, Long>> aggregations = aggKeys.stream().collect(Collectors.toMap(AggregationKey::name, result::aggregation));
			results.setAggregations(aggregations);
		}

		results.setResults(result.hits());
		results.setTotalResults(result.total().hitCount());

		return results;

	}

	public SearchResponse<E> findByFields(List<String> fields, String value) {
		Log.debug("SqlDAO: findByFields: " + fields + " " + value);
		HashMap<String, Object> params = new HashMap<>();
		for (String field : fields) {
			params.put(field, value);
		}
		params.put("query_operator", "or");
		SearchResponse<E> results = findByParams(params);
		// Log.debug("Result List: " + results);
		if (results.getResults().size() > 0) {
			return results;
		} else {
			return null;
		}
	}

	public List<Long> findIdsByFields(List<String> fields, String value) {
		Log.debug("SqlDAO: findByFields: " + fields + " " + value);
		HashMap<String, Object> params = new HashMap<>();
		for (String field : fields) {
			params.put(field, value);
		}
		params.put("query_operator", "or");
		List<Long> results = findIdsByParams(params);
		// Log.debug("Result List: " + results);
		if (results.size() > 0) {
			return results;
		} else {
			return null;
		}
	}

	public SearchResponse<E> findByField(String field, Object value) {
		Log.debug("SqlDAO: findByField: " + field + " " + value);
		HashMap<String, Object> params = new HashMap<>();
		params.put(field, value);
		SearchResponse<E> results = findByParams(params);
		// Log.debug("Result List: " + results);
		if (results.getResults().size() > 0) {
			return results;
		} else {
			return null;
		}
	}

	public SearchResponse<E> findByParams(Map<String, Object> params) {
		return findByParams(null, params, null);
	}

	public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params) {
		return findByParams(pagination, params, null);
	}

	public SearchResponse<E> findByParams(Pagination pagination, Map<String, Object> params, String orderByField) {
		Logger.Level level = Level.DEBUG;
		if (params.containsKey("debug")) {
			level = params.remove("debug").equals("true") ? Level.INFO : Level.DEBUG;
		}

		Log.log(level, "Pagination: " + pagination + " Params: " + params + " Order by: " + orderByField + " Class: " + myClass);

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> query = builder.createQuery(myClass);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<E> root = query.from(myClass);
		Root<E> countRoot = countQuery.from(myClass);

		Operator queryOperator = Operator.AND;
		if (params.containsKey("query_operator")) {
			queryOperator = params.remove("query_operator").equals("or") ? Operator.OR : Operator.AND;
		}

		// System.out.println("Root: " + root);
		List<Predicate> restrictions = buildRestrictions(root, params, level);
		List<Predicate> countRestrictions = buildRestrictions(countRoot, params, level);

		countQuery.select(builder.count(countRoot));

		if (orderByField != null) {
			query.orderBy(builder.asc(root.get(orderByField)));
		} else {
			// Else always order by the ID field to prevent random lists when using different page,limit combinations
			Metamodel metaModel = entityManager.getMetamodel();
			IdentifiableType<E> of = (IdentifiableType<E>) metaModel.managedType(myClass);
			query.orderBy(builder.asc(root.get(of.getId(of.getIdType().getJavaType()).getName())));
		}

		if (queryOperator == Operator.AND) {
			query.where(builder.and(restrictions.toArray(new Predicate[0])));
			countQuery.where(builder.and(countRestrictions.toArray(new Predicate[0])));
		} else {
			query.where(builder.or(restrictions.toArray(new Predicate[0])));
			countQuery.where(builder.or(countRestrictions.toArray(new Predicate[0])));
		}

		TypedQuery<E> allQuery = entityManager.createQuery(query);
		if (pagination != null && pagination.getLimit() != null && pagination.getPage() != null) {
			int first = pagination.getPage() * pagination.getLimit();
			if (first < 0) {
				first = 0;
			}
			allQuery.setFirstResult(first);
			allQuery.setMaxResults(pagination.getLimit());
		}

		Log.log(level, query);
		Log.log(level, allQuery);
		Log.log(level, countQuery);

		List<E> dbResults = allQuery.getResultList();
		SearchResponse<E> results = new SearchResponse<E>();
		results.setResults(dbResults);

		if (level == Level.INFO) {
			results.setDebug("true");
			results.setEsQuery(((QuerySqmImpl) allQuery).getQueryString());
			results.setDbQuery(((SqmSelectStatement) query).toHqlString());
		}

		if (pagination != null) { // If pagination is null then there is no point in getting the total results
			Long totalResults = entityManager.createQuery(countQuery).getSingleResult();
			results.setTotalResults(totalResults);
		}

		return results;

	}

	public E getNewInstance() {
		try {
			return myClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public E getShallowEntity(Class<E> entityClass, long id) {
		return entityManager.getReference(entityClass, id);
	}

}
