begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectObjectMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectObjectOpenHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|analysis
operator|.
name|Analyzer
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
name|document
operator|.
name|Field
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
name|IndexableField
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|Nullable
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
name|lucene
operator|.
name|all
operator|.
name|AllEntries
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
name|settings
operator|.
name|Settings
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
name|XContentParser
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
name|analysis
operator|.
name|AnalysisService
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
name|RootObjectMapper
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ParseContext
specifier|public
class|class
name|ParseContext
block|{
comment|/** Fork of {@link org.apache.lucene.document.Document} with additional functionality. */
DECL|class|Document
specifier|public
specifier|static
class|class
name|Document
implements|implements
name|Iterable
argument_list|<
name|IndexableField
argument_list|>
block|{
DECL|field|fields
specifier|private
specifier|final
name|List
argument_list|<
name|IndexableField
argument_list|>
name|fields
decl_stmt|;
DECL|field|keyedFields
specifier|private
name|ObjectObjectMap
argument_list|<
name|Object
argument_list|,
name|IndexableField
argument_list|>
name|keyedFields
decl_stmt|;
DECL|method|Document
specifier|public
name|Document
parameter_list|()
block|{
name|fields
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|IndexableField
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|fields
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|getFields
specifier|public
name|List
argument_list|<
name|IndexableField
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|IndexableField
name|field
parameter_list|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
comment|/** Add fields so that they can later be fetched using {@link #getByKey(Object)}. */
DECL|method|addWithKey
specifier|public
name|void
name|addWithKey
parameter_list|(
name|Object
name|key
parameter_list|,
name|IndexableField
name|field
parameter_list|)
block|{
if|if
condition|(
name|keyedFields
operator|==
literal|null
condition|)
block|{
name|keyedFields
operator|=
operator|new
name|ObjectObjectOpenHashMap
argument_list|<
name|Object
argument_list|,
name|IndexableField
argument_list|>
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|keyedFields
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"Only one field can be stored per key"
argument_list|)
throw|;
block|}
name|keyedFields
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
comment|/** Get back fields that have been previously added with {@link #addWithKey(Object, IndexableField)}. */
DECL|method|getByKey
specifier|public
name|IndexableField
name|getByKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|keyedFields
operator|==
literal|null
condition|?
literal|null
else|:
name|keyedFields
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getFields
specifier|public
name|IndexableField
index|[]
name|getFields
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|IndexableField
argument_list|>
name|f
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexableField
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|f
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|f
operator|.
name|toArray
argument_list|(
operator|new
name|IndexableField
index|[
name|f
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|getField
specifier|public
name|IndexableField
name|getField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|IndexableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|field
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|IndexableField
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|f
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|f
operator|.
name|stringValue
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getBinaryValue
specifier|public
name|BytesRef
name|getBinaryValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|IndexableField
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
name|f
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|f
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|f
operator|.
name|binaryValue
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|field|docMapper
specifier|private
specifier|final
name|DocumentMapper
name|docMapper
decl_stmt|;
DECL|field|docMapperParser
specifier|private
specifier|final
name|DocumentMapperParser
name|docMapperParser
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|ContentPath
name|path
decl_stmt|;
DECL|field|parser
specifier|private
name|XContentParser
name|parser
decl_stmt|;
DECL|field|document
specifier|private
name|Document
name|document
decl_stmt|;
DECL|field|documents
specifier|private
name|List
argument_list|<
name|Document
argument_list|>
name|documents
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
annotation|@
name|Nullable
DECL|field|indexSettings
specifier|private
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|sourceToParse
specifier|private
name|SourceToParse
name|sourceToParse
decl_stmt|;
DECL|field|source
specifier|private
name|BytesReference
name|source
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|listener
specifier|private
name|DocumentMapper
operator|.
name|ParseListener
name|listener
decl_stmt|;
DECL|field|uid
DECL|field|version
specifier|private
name|Field
name|uid
decl_stmt|,
name|version
decl_stmt|;
DECL|field|stringBuilder
specifier|private
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|ignoredValues
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ignoredValues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|mappingsModified
specifier|private
name|boolean
name|mappingsModified
init|=
literal|false
decl_stmt|;
DECL|field|withinNewMapper
specifier|private
name|boolean
name|withinNewMapper
init|=
literal|false
decl_stmt|;
DECL|field|withinCopyTo
specifier|private
name|boolean
name|withinCopyTo
init|=
literal|false
decl_stmt|;
DECL|field|withinMultiFields
specifier|private
name|boolean
name|withinMultiFields
init|=
literal|false
decl_stmt|;
DECL|field|externalValueSet
specifier|private
name|boolean
name|externalValueSet
decl_stmt|;
DECL|field|externalValue
specifier|private
name|Object
name|externalValue
decl_stmt|;
DECL|field|allEntries
specifier|private
name|AllEntries
name|allEntries
init|=
operator|new
name|AllEntries
argument_list|()
decl_stmt|;
DECL|field|docBoost
specifier|private
name|float
name|docBoost
init|=
literal|1.0f
decl_stmt|;
DECL|method|ParseContext
specifier|public
name|ParseContext
parameter_list|(
name|String
name|index
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|indexSettings
parameter_list|,
name|DocumentMapperParser
name|docMapperParser
parameter_list|,
name|DocumentMapper
name|docMapper
parameter_list|,
name|ContentPath
name|path
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|docMapper
operator|=
name|docMapper
expr_stmt|;
name|this
operator|.
name|docMapperParser
operator|=
name|docMapperParser
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|Document
name|document
parameter_list|,
name|SourceToParse
name|source
parameter_list|,
name|DocumentMapper
operator|.
name|ParseListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
if|if
condition|(
name|document
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|documents
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|documents
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|documents
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|analyzer
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|uid
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|version
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|id
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|sourceToParse
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
operator|==
literal|null
condition|?
literal|null
else|:
name|sourceToParse
operator|.
name|source
argument_list|()
expr_stmt|;
name|this
operator|.
name|path
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|mappingsModified
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|withinNewMapper
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
operator|==
literal|null
condition|?
name|DocumentMapper
operator|.
name|ParseListener
operator|.
name|EMPTY
else|:
name|listener
expr_stmt|;
name|this
operator|.
name|allEntries
operator|=
operator|new
name|AllEntries
argument_list|()
expr_stmt|;
name|this
operator|.
name|ignoredValues
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|docBoost
operator|=
literal|1.0f
expr_stmt|;
block|}
DECL|method|flyweight
specifier|public
name|boolean
name|flyweight
parameter_list|()
block|{
return|return
name|sourceToParse
operator|.
name|flyweight
argument_list|()
return|;
block|}
DECL|method|docMapperParser
specifier|public
name|DocumentMapperParser
name|docMapperParser
parameter_list|()
block|{
return|return
name|this
operator|.
name|docMapperParser
return|;
block|}
DECL|method|mappingsModified
specifier|public
name|boolean
name|mappingsModified
parameter_list|()
block|{
return|return
name|this
operator|.
name|mappingsModified
return|;
block|}
DECL|method|setMappingsModified
specifier|public
name|void
name|setMappingsModified
parameter_list|()
block|{
name|this
operator|.
name|mappingsModified
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setWithinNewMapper
specifier|public
name|void
name|setWithinNewMapper
parameter_list|()
block|{
name|this
operator|.
name|withinNewMapper
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|clearWithinNewMapper
specifier|public
name|void
name|clearWithinNewMapper
parameter_list|()
block|{
name|this
operator|.
name|withinNewMapper
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|isWithinNewMapper
specifier|public
name|boolean
name|isWithinNewMapper
parameter_list|()
block|{
return|return
name|withinNewMapper
return|;
block|}
DECL|method|setWithinCopyTo
specifier|public
name|void
name|setWithinCopyTo
parameter_list|()
block|{
name|this
operator|.
name|withinCopyTo
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|clearWithinCopyTo
specifier|public
name|void
name|clearWithinCopyTo
parameter_list|()
block|{
name|this
operator|.
name|withinCopyTo
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|isWithinCopyTo
specifier|public
name|boolean
name|isWithinCopyTo
parameter_list|()
block|{
return|return
name|withinCopyTo
return|;
block|}
DECL|method|setWithinMultiFields
specifier|public
name|void
name|setWithinMultiFields
parameter_list|()
block|{
name|this
operator|.
name|withinMultiFields
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|clearWithinMultiFields
specifier|public
name|void
name|clearWithinMultiFields
parameter_list|()
block|{
name|this
operator|.
name|withinMultiFields
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
annotation|@
name|Nullable
DECL|method|indexSettings
specifier|public
name|Settings
name|indexSettings
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexSettings
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|sourceToParse
operator|.
name|type
argument_list|()
return|;
block|}
DECL|method|sourceToParse
specifier|public
name|SourceToParse
name|sourceToParse
parameter_list|()
block|{
return|return
name|this
operator|.
name|sourceToParse
return|;
block|}
DECL|method|source
specifier|public
name|BytesReference
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
comment|// only should be used by SourceFieldMapper to update with a compressed source
DECL|method|source
specifier|public
name|void
name|source
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
DECL|method|path
specifier|public
name|ContentPath
name|path
parameter_list|()
block|{
return|return
name|this
operator|.
name|path
return|;
block|}
DECL|method|parser
specifier|public
name|XContentParser
name|parser
parameter_list|()
block|{
return|return
name|this
operator|.
name|parser
return|;
block|}
DECL|method|listener
specifier|public
name|DocumentMapper
operator|.
name|ParseListener
name|listener
parameter_list|()
block|{
return|return
name|this
operator|.
name|listener
return|;
block|}
DECL|method|rootDoc
specifier|public
name|Document
name|rootDoc
parameter_list|()
block|{
return|return
name|documents
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|docs
specifier|public
name|List
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|()
block|{
return|return
name|this
operator|.
name|documents
return|;
block|}
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|()
block|{
return|return
name|this
operator|.
name|document
return|;
block|}
DECL|method|addDoc
specifier|public
name|void
name|addDoc
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|this
operator|.
name|documents
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|switchDoc
specifier|public
name|Document
name|switchDoc
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|Document
name|prev
init|=
name|this
operator|.
name|document
decl_stmt|;
name|this
operator|.
name|document
operator|=
name|doc
expr_stmt|;
return|return
name|prev
return|;
block|}
DECL|method|root
specifier|public
name|RootObjectMapper
name|root
parameter_list|()
block|{
return|return
name|docMapper
operator|.
name|root
argument_list|()
return|;
block|}
DECL|method|docMapper
specifier|public
name|DocumentMapper
name|docMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|docMapper
return|;
block|}
DECL|method|analysisService
specifier|public
name|AnalysisService
name|analysisService
parameter_list|()
block|{
return|return
name|docMapperParser
operator|.
name|analysisService
return|;
block|}
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|ignoredValue
specifier|public
name|void
name|ignoredValue
parameter_list|(
name|String
name|indexName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|ignoredValues
operator|.
name|put
argument_list|(
name|indexName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|ignoredValue
specifier|public
name|String
name|ignoredValue
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
return|return
name|ignoredValues
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
return|;
block|}
comment|/**      * Really, just the id mapper should set this.      */
DECL|method|id
specifier|public
name|void
name|id
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|uid
specifier|public
name|Field
name|uid
parameter_list|()
block|{
return|return
name|this
operator|.
name|uid
return|;
block|}
comment|/**      * Really, just the uid mapper should set this.      */
DECL|method|uid
specifier|public
name|void
name|uid
parameter_list|(
name|Field
name|uid
parameter_list|)
block|{
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|Field
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|version
specifier|public
name|void
name|version
parameter_list|(
name|Field
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|includeInAll
specifier|public
name|boolean
name|includeInAll
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|,
name|FieldMapper
name|mapper
parameter_list|)
block|{
return|return
name|includeInAll
argument_list|(
name|includeInAll
argument_list|,
name|mapper
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Is all included or not. Will always disable it if {@link org.elasticsearch.index.mapper.internal.AllFieldMapper#enabled()}      * is<tt>false</tt>. If its enabled, then will return<tt>true</tt> only if the specific flag is<tt>null</tt> or      * its actual value (so, if not set, defaults to "true") and the field is indexed.      */
DECL|method|includeInAll
specifier|private
name|boolean
name|includeInAll
parameter_list|(
name|Boolean
name|specificIncludeInAll
parameter_list|,
name|boolean
name|indexed
parameter_list|)
block|{
if|if
condition|(
name|withinCopyTo
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|withinMultiFields
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|docMapper
operator|.
name|allFieldMapper
argument_list|()
operator|.
name|enabled
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// not explicitly set
if|if
condition|(
name|specificIncludeInAll
operator|==
literal|null
condition|)
block|{
return|return
name|indexed
return|;
block|}
return|return
name|specificIncludeInAll
return|;
block|}
DECL|method|allEntries
specifier|public
name|AllEntries
name|allEntries
parameter_list|()
block|{
return|return
name|this
operator|.
name|allEntries
return|;
block|}
DECL|method|analyzer
specifier|public
name|Analyzer
name|analyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
DECL|method|analyzer
specifier|public
name|void
name|analyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|externalValue
specifier|public
name|void
name|externalValue
parameter_list|(
name|Object
name|externalValue
parameter_list|)
block|{
name|this
operator|.
name|externalValueSet
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|externalValue
operator|=
name|externalValue
expr_stmt|;
block|}
DECL|method|externalValueSet
specifier|public
name|boolean
name|externalValueSet
parameter_list|()
block|{
return|return
name|this
operator|.
name|externalValueSet
return|;
block|}
DECL|method|externalValue
specifier|public
name|Object
name|externalValue
parameter_list|()
block|{
name|externalValueSet
operator|=
literal|false
expr_stmt|;
return|return
name|externalValue
return|;
block|}
comment|/**      * Try to parse an externalValue if any      * @param clazz Expected class for external value      * @return null if no external value has been set or the value      */
DECL|method|parseExternalValue
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|parseExternalValue
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
operator|!
name|externalValueSet
argument_list|()
operator|||
name|externalValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|clazz
operator|.
name|isInstance
argument_list|(
name|externalValue
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"illegal external value class ["
operator|+
name|externalValue
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]. Should be "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|(
name|T
operator|)
name|externalValue
argument_list|()
return|;
block|}
DECL|method|docBoost
specifier|public
name|float
name|docBoost
parameter_list|()
block|{
return|return
name|this
operator|.
name|docBoost
return|;
block|}
DECL|method|docBoost
specifier|public
name|void
name|docBoost
parameter_list|(
name|float
name|docBoost
parameter_list|)
block|{
name|this
operator|.
name|docBoost
operator|=
name|docBoost
expr_stmt|;
block|}
comment|/**      * A string builder that can be used to construct complex names for example.      * Its better to reuse the.      */
DECL|method|stringBuilder
specifier|public
name|StringBuilder
name|stringBuilder
parameter_list|()
block|{
name|stringBuilder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|stringBuilder
return|;
block|}
block|}
end_class

end_unit

