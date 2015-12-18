begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|ReaderUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Weight
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
operator|.
name|BytesReference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Tuple
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
operator|.
name|Queries
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|regex
operator|.
name|Regex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|text
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fieldvisitor
operator|.
name|CustomFieldsVisitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fieldvisitor
operator|.
name|FieldsVisitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|DocumentMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|internal
operator|.
name|SourceFieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchHit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchHitField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchParseElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchPhase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|innerhits
operator|.
name|InnerHitsFetchSubPhase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|source
operator|.
name|FetchSourceContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|InternalSearchHit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|InternalSearchHitField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|InternalSearchHits
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|SourceLookup
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|contentBuilder
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FetchPhase
specifier|public
class|class
name|FetchPhase
implements|implements
name|SearchPhase
block|{
DECL|field|fetchSubPhases
specifier|private
specifier|final
name|FetchSubPhase
index|[]
name|fetchSubPhases
decl_stmt|;
annotation|@
name|Inject
DECL|method|FetchPhase
specifier|public
name|FetchPhase
parameter_list|(
name|Set
argument_list|<
name|FetchSubPhase
argument_list|>
name|fetchSubPhases
parameter_list|,
name|InnerHitsFetchSubPhase
name|innerHitsFetchSubPhase
parameter_list|)
block|{
name|innerHitsFetchSubPhase
operator|.
name|setFetchPhase
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|fetchSubPhases
operator|=
name|fetchSubPhases
operator|.
name|toArray
argument_list|(
operator|new
name|FetchSubPhase
index|[
name|fetchSubPhases
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|fetchSubPhases
index|[
name|fetchSubPhases
operator|.
name|size
argument_list|()
index|]
operator|=
name|innerHitsFetchSubPhase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseElements
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchParseElement
argument_list|>
name|parseElements
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|SearchParseElement
argument_list|>
name|parseElements
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|parseElements
operator|.
name|put
argument_list|(
literal|"fields"
argument_list|,
operator|new
name|FieldsParseElement
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FetchSubPhase
name|fetchSubPhase
range|:
name|fetchSubPhases
control|)
block|{
name|parseElements
operator|.
name|putAll
argument_list|(
name|fetchSubPhase
operator|.
name|parseElements
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|unmodifiableMap
argument_list|(
name|parseElements
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|preProcess
specifier|public
name|void
name|preProcess
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|FieldsVisitor
name|fieldsVisitor
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldNamePatterns
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|context
operator|.
name|hasFieldNames
argument_list|()
condition|)
block|{
comment|// no fields specified, default to return source if no explicit indication
if|if
condition|(
operator|!
name|context
operator|.
name|hasScriptFields
argument_list|()
operator|&&
operator|!
name|context
operator|.
name|hasFetchSourceContext
argument_list|()
condition|)
block|{
name|context
operator|.
name|fetchSourceContext
argument_list|(
operator|new
name|FetchSourceContext
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fieldsVisitor
operator|=
operator|new
name|FieldsVisitor
argument_list|(
name|context
operator|.
name|sourceRequested
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|context
operator|.
name|fieldNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fieldsVisitor
operator|=
operator|new
name|FieldsVisitor
argument_list|(
name|context
operator|.
name|sourceRequested
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|context
operator|.
name|fieldNames
argument_list|()
control|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|SourceFieldMapper
operator|.
name|NAME
argument_list|)
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|hasFetchSourceContext
argument_list|()
condition|)
block|{
name|context
operator|.
name|fetchSourceContext
argument_list|()
operator|.
name|fetchSource
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|.
name|fetchSourceContext
argument_list|(
operator|new
name|FetchSourceContext
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|Regex
operator|.
name|isSimpleMatchPattern
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|fieldNamePatterns
operator|==
literal|null
condition|)
block|{
name|fieldNamePatterns
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|fieldNamePatterns
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|smartNameFieldType
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
comment|// Only fail if we know it is a object field, missing paths / fields shouldn't fail.
if|if
condition|(
name|context
operator|.
name|getObjectMapper
argument_list|(
name|fieldName
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field ["
operator|+
name|fieldName
operator|+
literal|"] isn't a leaf field"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|fieldNames
operator|==
literal|null
condition|)
block|{
name|fieldNames
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|fieldNames
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|loadSource
init|=
name|context
operator|.
name|sourceRequested
argument_list|()
decl_stmt|;
name|fieldsVisitor
operator|=
operator|new
name|CustomFieldsVisitor
argument_list|(
name|fieldNames
operator|==
literal|null
condition|?
name|Collections
operator|.
name|emptySet
argument_list|()
else|:
name|fieldNames
argument_list|,
name|fieldNamePatterns
operator|==
literal|null
condition|?
name|Collections
operator|.
name|emptyList
argument_list|()
else|:
name|fieldNamePatterns
argument_list|,
name|loadSource
argument_list|)
expr_stmt|;
block|}
name|InternalSearchHit
index|[]
name|hits
init|=
operator|new
name|InternalSearchHit
index|[
name|context
operator|.
name|docIdsToLoadSize
argument_list|()
index|]
decl_stmt|;
name|FetchSubPhase
operator|.
name|HitContext
name|hitContext
init|=
operator|new
name|FetchSubPhase
operator|.
name|HitContext
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|context
operator|.
name|docIdsToLoadSize
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|int
name|docId
init|=
name|context
operator|.
name|docIdsToLoad
argument_list|()
index|[
name|context
operator|.
name|docIdsToLoadFrom
argument_list|()
operator|+
name|index
index|]
decl_stmt|;
name|int
name|readerIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docId
argument_list|,
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
argument_list|)
decl_stmt|;
name|LeafReaderContext
name|subReaderContext
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|readerIndex
argument_list|)
decl_stmt|;
name|int
name|subDocId
init|=
name|docId
operator|-
name|subReaderContext
operator|.
name|docBase
decl_stmt|;
specifier|final
name|InternalSearchHit
name|searchHit
decl_stmt|;
try|try
block|{
name|int
name|rootDocId
init|=
name|findRootDocumentIfNested
argument_list|(
name|context
argument_list|,
name|subReaderContext
argument_list|,
name|subDocId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootDocId
operator|!=
operator|-
literal|1
condition|)
block|{
name|searchHit
operator|=
name|createNestedSearchHit
argument_list|(
name|context
argument_list|,
name|docId
argument_list|,
name|subDocId
argument_list|,
name|rootDocId
argument_list|,
name|fieldNames
argument_list|,
name|fieldNamePatterns
argument_list|,
name|subReaderContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|searchHit
operator|=
name|createSearchHit
argument_list|(
name|context
argument_list|,
name|fieldsVisitor
argument_list|,
name|docId
argument_list|,
name|subDocId
argument_list|,
name|subReaderContext
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|ExceptionsHelper
operator|.
name|convertToElastic
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|hits
index|[
name|index
index|]
operator|=
name|searchHit
expr_stmt|;
name|hitContext
operator|.
name|reset
argument_list|(
name|searchHit
argument_list|,
name|subReaderContext
argument_list|,
name|subDocId
argument_list|,
name|context
operator|.
name|searcher
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FetchSubPhase
name|fetchSubPhase
range|:
name|fetchSubPhases
control|)
block|{
if|if
condition|(
name|fetchSubPhase
operator|.
name|hitExecutionNeeded
argument_list|(
name|context
argument_list|)
condition|)
block|{
name|fetchSubPhase
operator|.
name|hitExecute
argument_list|(
name|context
argument_list|,
name|hitContext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|FetchSubPhase
name|fetchSubPhase
range|:
name|fetchSubPhases
control|)
block|{
if|if
condition|(
name|fetchSubPhase
operator|.
name|hitsExecutionNeeded
argument_list|(
name|context
argument_list|)
condition|)
block|{
name|fetchSubPhase
operator|.
name|hitsExecute
argument_list|(
name|context
argument_list|,
name|hits
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|fetchResult
argument_list|()
operator|.
name|hits
argument_list|(
operator|new
name|InternalSearchHits
argument_list|(
name|hits
argument_list|,
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|totalHits
argument_list|,
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|findRootDocumentIfNested
specifier|private
name|int
name|findRootDocumentIfNested
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|LeafReaderContext
name|subReaderContext
parameter_list|,
name|int
name|subDocId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|hasNested
argument_list|()
condition|)
block|{
name|BitSet
name|bits
init|=
name|context
operator|.
name|bitsetFilterCache
argument_list|()
operator|.
name|getBitSetProducer
argument_list|(
name|Queries
operator|.
name|newNonNestedFilter
argument_list|()
argument_list|)
operator|.
name|getBitSet
argument_list|(
name|subReaderContext
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|bits
operator|.
name|get
argument_list|(
name|subDocId
argument_list|)
condition|)
block|{
return|return
name|bits
operator|.
name|nextSetBit
argument_list|(
name|subDocId
argument_list|)
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|createSearchHit
specifier|private
name|InternalSearchHit
name|createSearchHit
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|FieldsVisitor
name|fieldsVisitor
parameter_list|,
name|int
name|docId
parameter_list|,
name|int
name|subDocId
parameter_list|,
name|LeafReaderContext
name|subReaderContext
parameter_list|)
block|{
name|loadStoredFields
argument_list|(
name|context
argument_list|,
name|subReaderContext
argument_list|,
name|fieldsVisitor
argument_list|,
name|subDocId
argument_list|)
expr_stmt|;
name|fieldsVisitor
operator|.
name|postProcess
argument_list|(
name|context
operator|.
name|mapperService
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SearchHitField
argument_list|>
name|searchFields
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|searchFields
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|fieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|searchFields
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|InternalSearchHitField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|DocumentMapper
name|documentMapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|fieldsVisitor
operator|.
name|uid
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|Text
name|typeText
decl_stmt|;
if|if
condition|(
name|documentMapper
operator|==
literal|null
condition|)
block|{
name|typeText
operator|=
operator|new
name|Text
argument_list|(
name|fieldsVisitor
operator|.
name|uid
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|typeText
operator|=
name|documentMapper
operator|.
name|typeText
argument_list|()
expr_stmt|;
block|}
name|InternalSearchHit
name|searchHit
init|=
operator|new
name|InternalSearchHit
argument_list|(
name|docId
argument_list|,
name|fieldsVisitor
operator|.
name|uid
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|typeText
argument_list|,
name|searchFields
argument_list|)
decl_stmt|;
comment|// Set _source if requested.
name|SourceLookup
name|sourceLookup
init|=
name|context
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
decl_stmt|;
name|sourceLookup
operator|.
name|setSegmentAndDocument
argument_list|(
name|subReaderContext
argument_list|,
name|subDocId
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsVisitor
operator|.
name|source
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sourceLookup
operator|.
name|setSource
argument_list|(
name|fieldsVisitor
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|searchHit
return|;
block|}
DECL|method|createNestedSearchHit
specifier|private
name|InternalSearchHit
name|createNestedSearchHit
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|int
name|nestedTopDocId
parameter_list|,
name|int
name|nestedSubDocId
parameter_list|,
name|int
name|rootSubDocId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNamePatterns
parameter_list|,
name|LeafReaderContext
name|subReaderContext
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Also if highlighting is requested on nested documents we need to fetch the _source from the root document,
comment|// otherwise highlighting will attempt to fetch the _source from the nested doc, which will fail,
comment|// because the entire _source is only stored with the root document.
specifier|final
name|FieldsVisitor
name|rootFieldsVisitor
init|=
operator|new
name|FieldsVisitor
argument_list|(
name|context
operator|.
name|sourceRequested
argument_list|()
operator|||
name|context
operator|.
name|highlight
argument_list|()
operator|!=
literal|null
argument_list|)
decl_stmt|;
name|loadStoredFields
argument_list|(
name|context
argument_list|,
name|subReaderContext
argument_list|,
name|rootFieldsVisitor
argument_list|,
name|rootSubDocId
argument_list|)
expr_stmt|;
name|rootFieldsVisitor
operator|.
name|postProcess
argument_list|(
name|context
operator|.
name|mapperService
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SearchHitField
argument_list|>
name|searchFields
init|=
name|getSearchFields
argument_list|(
name|context
argument_list|,
name|nestedSubDocId
argument_list|,
name|fieldNames
argument_list|,
name|fieldNamePatterns
argument_list|,
name|subReaderContext
argument_list|)
decl_stmt|;
name|DocumentMapper
name|documentMapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|rootFieldsVisitor
operator|.
name|uid
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|SourceLookup
name|sourceLookup
init|=
name|context
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
decl_stmt|;
name|sourceLookup
operator|.
name|setSegmentAndDocument
argument_list|(
name|subReaderContext
argument_list|,
name|nestedSubDocId
argument_list|)
expr_stmt|;
name|ObjectMapper
name|nestedObjectMapper
init|=
name|documentMapper
operator|.
name|findNestedObjectMapper
argument_list|(
name|nestedSubDocId
argument_list|,
name|context
argument_list|,
name|subReaderContext
argument_list|)
decl_stmt|;
assert|assert
name|nestedObjectMapper
operator|!=
literal|null
assert|;
name|InternalSearchHit
operator|.
name|InternalNestedIdentity
name|nestedIdentity
init|=
name|getInternalNestedIdentity
argument_list|(
name|context
argument_list|,
name|nestedSubDocId
argument_list|,
name|subReaderContext
argument_list|,
name|documentMapper
argument_list|,
name|nestedObjectMapper
argument_list|)
decl_stmt|;
name|BytesReference
name|source
init|=
name|rootFieldsVisitor
operator|.
name|source
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|Tuple
argument_list|<
name|XContentType
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|tuple
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|source
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAsMap
init|=
name|tuple
operator|.
name|v2
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|nestedParsedSource
decl_stmt|;
name|SearchHit
operator|.
name|NestedIdentity
name|nested
init|=
name|nestedIdentity
decl_stmt|;
do|do
block|{
name|Object
name|extractedValue
init|=
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
name|nested
operator|.
name|getField
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|sourceAsMap
argument_list|)
decl_stmt|;
if|if
condition|(
name|extractedValue
operator|==
literal|null
condition|)
block|{
comment|// The nested objects may not exist in the _source, because it was filtered because of _source filtering
break|break;
block|}
elseif|else
if|if
condition|(
name|extractedValue
operator|instanceof
name|List
condition|)
block|{
comment|// nested field has an array value in the _source
name|nestedParsedSource
operator|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|extractedValue
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|extractedValue
operator|instanceof
name|Map
condition|)
block|{
comment|// nested field has an object value in the _source. This just means the nested field has just one inner object, which is valid, but uncommon.
name|nestedParsedSource
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|extractedValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"extracted source isn't an object or an array"
argument_list|)
throw|;
block|}
name|sourceAsMap
operator|=
name|nestedParsedSource
operator|.
name|get
argument_list|(
name|nested
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|nested
operator|=
name|nested
operator|.
name|getChild
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|nested
operator|!=
literal|null
condition|)
do|;
name|context
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|setSource
argument_list|(
name|sourceAsMap
argument_list|)
expr_stmt|;
name|XContentType
name|contentType
init|=
name|tuple
operator|.
name|v1
argument_list|()
decl_stmt|;
name|BytesReference
name|nestedSource
init|=
name|contentBuilder
argument_list|(
name|contentType
argument_list|)
operator|.
name|map
argument_list|(
name|sourceAsMap
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|context
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|setSource
argument_list|(
name|nestedSource
argument_list|)
expr_stmt|;
name|context
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|setSourceContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
block|}
name|InternalSearchHit
name|searchHit
init|=
operator|new
name|InternalSearchHit
argument_list|(
name|nestedTopDocId
argument_list|,
name|rootFieldsVisitor
operator|.
name|uid
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|documentMapper
operator|.
name|typeText
argument_list|()
argument_list|,
name|nestedIdentity
argument_list|,
name|searchFields
argument_list|)
decl_stmt|;
return|return
name|searchHit
return|;
block|}
DECL|method|getSearchFields
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|SearchHitField
argument_list|>
name|getSearchFields
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|int
name|nestedSubDocId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNamePatterns
parameter_list|,
name|LeafReaderContext
name|subReaderContext
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|SearchHitField
argument_list|>
name|searchFields
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|hasFieldNames
argument_list|()
operator|&&
operator|!
name|context
operator|.
name|fieldNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|FieldsVisitor
name|nestedFieldsVisitor
init|=
operator|new
name|CustomFieldsVisitor
argument_list|(
name|fieldNames
operator|==
literal|null
condition|?
name|Collections
operator|.
name|emptySet
argument_list|()
else|:
name|fieldNames
argument_list|,
name|fieldNamePatterns
operator|==
literal|null
condition|?
name|Collections
operator|.
name|emptyList
argument_list|()
else|:
name|fieldNamePatterns
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|nestedFieldsVisitor
operator|!=
literal|null
condition|)
block|{
name|loadStoredFields
argument_list|(
name|context
argument_list|,
name|subReaderContext
argument_list|,
name|nestedFieldsVisitor
argument_list|,
name|nestedSubDocId
argument_list|)
expr_stmt|;
name|nestedFieldsVisitor
operator|.
name|postProcess
argument_list|(
name|context
operator|.
name|mapperService
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|nestedFieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|searchFields
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|nestedFieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|entry
range|:
name|nestedFieldsVisitor
operator|.
name|fields
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|searchFields
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|InternalSearchHitField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|searchFields
return|;
block|}
DECL|method|getInternalNestedIdentity
specifier|private
name|InternalSearchHit
operator|.
name|InternalNestedIdentity
name|getInternalNestedIdentity
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|int
name|nestedSubDocId
parameter_list|,
name|LeafReaderContext
name|subReaderContext
parameter_list|,
name|DocumentMapper
name|documentMapper
parameter_list|,
name|ObjectMapper
name|nestedObjectMapper
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|currentParent
init|=
name|nestedSubDocId
decl_stmt|;
name|ObjectMapper
name|nestedParentObjectMapper
decl_stmt|;
name|ObjectMapper
name|current
init|=
name|nestedObjectMapper
decl_stmt|;
name|String
name|originalName
init|=
name|nestedObjectMapper
operator|.
name|name
argument_list|()
decl_stmt|;
name|InternalSearchHit
operator|.
name|InternalNestedIdentity
name|nestedIdentity
init|=
literal|null
decl_stmt|;
do|do
block|{
name|Query
name|parentFilter
decl_stmt|;
name|nestedParentObjectMapper
operator|=
name|documentMapper
operator|.
name|findParentObjectMapper
argument_list|(
name|current
argument_list|)
expr_stmt|;
if|if
condition|(
name|nestedParentObjectMapper
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nestedParentObjectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
operator|==
literal|false
condition|)
block|{
name|current
operator|=
name|nestedParentObjectMapper
expr_stmt|;
continue|continue;
block|}
name|parentFilter
operator|=
name|nestedParentObjectMapper
operator|.
name|nestedTypeFilter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|parentFilter
operator|=
name|Queries
operator|.
name|newNonNestedFilter
argument_list|()
expr_stmt|;
block|}
name|Query
name|childFilter
init|=
name|nestedObjectMapper
operator|.
name|nestedTypeFilter
argument_list|()
decl_stmt|;
if|if
condition|(
name|childFilter
operator|==
literal|null
condition|)
block|{
name|current
operator|=
name|nestedParentObjectMapper
expr_stmt|;
continue|continue;
block|}
specifier|final
name|Weight
name|childWeight
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|createNormalizedWeight
argument_list|(
name|childFilter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|childIter
init|=
name|childWeight
operator|.
name|scorer
argument_list|(
name|subReaderContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|childIter
operator|==
literal|null
condition|)
block|{
name|current
operator|=
name|nestedParentObjectMapper
expr_stmt|;
continue|continue;
block|}
name|BitSet
name|parentBits
init|=
name|context
operator|.
name|bitsetFilterCache
argument_list|()
operator|.
name|getBitSetProducer
argument_list|(
name|parentFilter
argument_list|)
operator|.
name|getBitSet
argument_list|(
name|subReaderContext
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|int
name|nextParent
init|=
name|parentBits
operator|.
name|nextSetBit
argument_list|(
name|currentParent
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docId
init|=
name|childIter
operator|.
name|advance
argument_list|(
name|currentParent
operator|+
literal|1
argument_list|)
init|;
name|docId
operator|<
name|nextParent
operator|&&
name|docId
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|docId
operator|=
name|childIter
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|offset
operator|++
expr_stmt|;
block|}
name|currentParent
operator|=
name|nextParent
expr_stmt|;
name|current
operator|=
name|nestedObjectMapper
operator|=
name|nestedParentObjectMapper
expr_stmt|;
name|int
name|currentPrefix
init|=
name|current
operator|==
literal|null
condition|?
literal|0
else|:
name|current
operator|.
name|name
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
decl_stmt|;
name|nestedIdentity
operator|=
operator|new
name|InternalSearchHit
operator|.
name|InternalNestedIdentity
argument_list|(
name|originalName
operator|.
name|substring
argument_list|(
name|currentPrefix
argument_list|)
argument_list|,
name|offset
argument_list|,
name|nestedIdentity
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|originalName
operator|=
name|current
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|current
operator|!=
literal|null
condition|)
do|;
return|return
name|nestedIdentity
return|;
block|}
DECL|method|loadStoredFields
specifier|private
name|void
name|loadStoredFields
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|,
name|FieldsVisitor
name|fieldVisitor
parameter_list|,
name|int
name|docId
parameter_list|)
block|{
name|fieldVisitor
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|document
argument_list|(
name|docId
argument_list|,
name|fieldVisitor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FetchPhaseExecutionException
argument_list|(
name|searchContext
argument_list|,
literal|"Failed to fetch doc id ["
operator|+
name|docId
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

