# Search payload for /search endpoints

## Endpoints

Endpoints are in the following structure

POST /api/{object}/search

{object} will be any of the objects that we have tables for in the UI and the database

## Query Parameters

### Page

This will be a zero based page of results that is pulled from the database. 

### Limit 

This will be the size of the page that comes back. 

## POST Search Payload Example

```json
{
    "searchFilters": {
        "nameFilter": {
            "name": {
                "queryString": "pax6 pax7",
                "tokenOperator": "OR",
                "queryType": "matchQuery"
            }
        },
        "obsoleteFilter": {
            "obsolete": {
                "queryString": "false",
                "tokenOperator": "AND"
            },
            "internal": {
                "queryString": "false",
                "tokenOperator": "AND"
            }
        },
        "uniqueidFilter": {
            "uniqueId": {
                "queryString": "wb",
                "tokenOperator": "AND",
                "useKeywordFields": false
            }
            "nonNullFields": [],
            "nullFields": [],
        }
    },
    "searchFilterOperator": "OR",
    "sortOrders": [
        {
            "field": "diseaseAnnotationSubject.symbol",
            "order": 1
        },
        {
            "field": "diseaseAnnotationSubject.name",
            "order": 1
        },
        {
            "field": "diseaseAnnotationSubject.primaryExternalId",
            "order": 1
        }
    ],
    "aggregations": ["secondaryDataProvider.sourceOrganization.abbreviation"],
    "nonNullFieldsTable": [],
    "debug": "true",
}

```

### Search filters (required)

#### Filters

All search filters need to have a unique name. Names only have meaning to the caller of the endpoint. In the above example "nameFilter" is only the name of the filter. It could be called "filter1" or any other name that makes sense to the caller.

Filters by default will be "AND"ed together so in the above example "nameFilter" AND "obsoleteFilter" AND "uniqueidFilter". However if you wish to have them "OR"ed together set the "searchFilterOperator" to "OR".

#### Fields

Inside a single filter there can be multiple items listed. These items are the names of the fields in the linkML model starting with {object} and its field. These fields can be chanined together to get access to nested properties as in if {object} is a disease annotation then a field could be "diseaseAnnotationSubject.taxon.curie", which would query against the tax id that is on the subject of a disease annotation.

Fields inside a single filter are "OR"ed together, in the above example for "obsoleteFilter" we are saying where field "obsolete" is false OR field "internal" is false.

##### Query String (required)

The queryString inside a field will be tokenized and a match will be performed on each token.

##### Query Type (optional)

Query type only has one option "matchQuery" this will do a "match" query in Elastic Search vs any other value it will do a simpleQueryString search instead. Default is to use Simple Query String.

##### Use Keyword Fields (optional)

This option will run against the _keyword fields vs the regular field names. In the above example for the uniqueId field the uniqueId_keyword field will be used instead. Default is false.

##### Token Operator (optional)

This option controls how the tokens are matched. Values are "AND" or "OR". So in the above example the name field will have to match pax6 OR pax. Default value is AND.

#### Non Null Fields (optional)

In the context of a single filter we can specify that certain fields needs to be populated, this will be AND'ed to the field criteria. As in this list of fields must have a non null value.

#### Null Fields (optional)

In the context of a single filter we can specify that certain field needs to be empty or null, this will be AND'ed to the field criteria. As in this list of fields must have a non null value.

### Sort Orders (optional)

Sort orders is a list of objects that contain two fields on called "field" and the other called "order" field is the field in elasticSearch that we are going to order on which can also be a nest object field. Order is of two values 1 meaning ascending and -1 descending.

### Non Null Fields Table (optional)

This is a list of fields that have to be non null across the whole "table" or query. This will filter out all records that have null's in the fields in this list.

### Aggregations (optional)

This is a list of fields that will be aggregated in the results. This will be in the returned object see the response object below.

### Debug (optional)

Debug true will turn on some extra debugging in order to see the query getting sent to ElasticSearch and some duration statistics. Default value is false.

## Return Object "SearchResults"

```json
{
    "results": [
        { ... },
        { ... },
        { ... },
        { ... },
        { ... }
    ],
    "aggregations": {
        "secondaryDataProvider.sourceOrganization.abbreviation": {
            "rgd": 11297,
            "omim": 200,
            "alliance": 14
        }
    }
    "esQuery": "..."
    "totalResults": 1163,
    "returnedRecords": 5
}
```

### Results

This is the list of objects coming back from the system.

### Aggregations

Aggregations will be an object containing fields by field name. Each entry will have all the values under, with the counts.

### Total Results

This is the count of the total results found on the whole query.

### Returned Results

This is the count of the page of results, for each page should be the count that is in the result set. May be smaller then limit on the last page.

### Examples

POST /api/disease-annotation/search?limit=1&page=0

Search Payload:

```json
{
    "searchFilters": {
        "internalFilter": {
            "internal": {
                "queryString": "false",
                "tokenOperator": "OR"
            }
        },
        "obsoleteFilter": {
            "obsolete": {
                "queryString": "false",
                "tokenOperator": "OR"
            }
        },
        "uniqueidFilter": {
            "uniqueId": {
                "queryString": "wb",
                "tokenOperator": "AND"
            }
        }
    },
    "sortOrders": [
        {
            "field": "diseaseAnnotationSubject.symbol",
            "order": 1
        },
        {
            "field": "diseaseAnnotationSubject.name",
            "order": 1
        },
        {
            "field": "diseaseAnnotationSubject.primaryExternalId",
            "order": 1
        }
    ],
    "aggregations": [],
    "nonNullFieldsTable": [],
    "debug": "true"
}
```

Return Payload

```json
{
    "results": [
        { ... }
    ],
    "totalResults": 1163,
    "returnedRecords": 1,
    "debug": "true",
    "esQuery": "{\"query\":{\"bool\":{\"must\":[{\"simple_query_string\":{\"boost\":31000.0,\"query\":\"false\",\"default_operator\":\"or\",\"fields\":[\"internal\"]}},{\"simple_query_string\":{\"boost\":21000.0,\"query\":\"false\",\"default_operator\":\"or\",\"fields\":[\"obsolete\"]}},{\"simple_query_string\":{\"boost\":11000.0,\"query\":\"wb\",\"default_operator\":\"and\",\"fields\":[\"uniqueId\"]}},{\"match_all\":{}}],\"minimum_should_match\":\"0\"}},\"sort\":[{\"diseaseAnnotationSubject.symbol_keyword\":{\"order\":\"asc\",\"unmapped_type\":\"keyword\"}},{\"diseaseAnnotationSubject.name_keyword\":{\"order\":\"asc\",\"unmapped_type\":\"keyword\"}},{\"diseaseAnnotationSubject.primaryExternalId_keyword\":{\"order\":\"asc\",\"unmapped_type\":\"keyword\"}}],\"docvalue_fields\":[\"_entity_type\"],\"_source\":false}"
}
```

Destringify the esQuery:

```json
{
    "query": {
        "bool": {
            "must": [
                {
                    "simple_query_string": {
                        "boost": 31000,
                        "query": "false",
                        "default_operator": "or",
                        "fields": [
                            "internal"
                        ]
                    }
                },
                {
                    "simple_query_string": {
                        "boost": 21000,
                        "query": "false",
                        "default_operator": "or",
                        "fields": [
                            "obsolete"
                        ]
                    }
                },
                {
                    "simple_query_string": {
                        "boost": 11000,
                        "query": "wb",
                        "default_operator": "and",
                        "fields": [
                            "uniqueId"
                        ]
                    }
                },
                {
                    "match_all": {}
                }
            ],
            "minimum_should_match": "0"
        }
    },
    "sort": [
        {
            "diseaseAnnotationSubject.symbol_keyword": {
                "order": "asc",
                "unmapped_type": "keyword"
            }
        },
        {
            "diseaseAnnotationSubject.name_keyword": {
                "order": "asc",
                "unmapped_type": "keyword"
            }
        },
        {
            "diseaseAnnotationSubject.primaryExternalId_keyword": {
                "order": "asc",
                "unmapped_type": "keyword"
            }
        }
    ],
    "docvalue_fields": [
        "_entity_type"
    ],
    "_source": false
}
```

