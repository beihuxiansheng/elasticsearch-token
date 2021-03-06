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
name|ObjectObjectHashMap
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
name|ObjectObjectMap
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
name|IndexOptions
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
name|Iterator
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

begin_class
DECL|class|ParseContext
specifier|public
specifier|abstract
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
DECL|field|parent
specifier|private
specifier|final
name|Document
name|parent
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|prefix
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
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
specifier|private
name|Document
parameter_list|(
name|String
name|path
parameter_list|,
name|Document
name|parent
parameter_list|)
block|{
name|fields
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|path
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
name|path
operator|+
literal|"."
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
DECL|method|Document
specifier|public
name|Document
parameter_list|()
block|{
name|this
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**          * Return the path associated with this document.          */
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**          * Return a prefix that all fields in this document should have.          */
DECL|method|getPrefix
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
comment|/**          * Return the parent document, or null if this is the root document.          */
DECL|method|getParent
specifier|public
name|Document
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
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
comment|// either a meta fields or starts with the prefix
assert|assert
name|field
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
operator|||
name|field
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
operator|:
name|field
operator|.
name|name
argument_list|()
operator|+
literal|" "
operator|+
name|prefix
assert|;
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
name|ObjectObjectHashMap
argument_list|<>
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
name|IllegalStateException
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
argument_list|<>
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
comment|/**          * Returns an array of values of the field specified as the method parameter.          * This method returns an empty array when there are no          * matching fields.  It never returns null.          * If you want the actual numeric field instances back, use {@link #getFields}.          * @param name the name of the field          * @return a<code>String[]</code> of field values          */
DECL|method|getValues
specifier|public
specifier|final
name|String
index|[]
name|getValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
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
operator|&&
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|result
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
DECL|class|FilterParseContext
specifier|private
specifier|static
class|class
name|FilterParseContext
extends|extends
name|ParseContext
block|{
DECL|field|in
specifier|private
specifier|final
name|ParseContext
name|in
decl_stmt|;
DECL|method|FilterParseContext
specifier|private
name|FilterParseContext
parameter_list|(
name|ParseContext
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docMapperParser
specifier|public
name|DocumentMapperParser
name|docMapperParser
parameter_list|()
block|{
return|return
name|in
operator|.
name|docMapperParser
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isWithinCopyTo
specifier|public
name|boolean
name|isWithinCopyTo
parameter_list|()
block|{
return|return
name|in
operator|.
name|isWithinCopyTo
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isWithinMultiFields
specifier|public
name|boolean
name|isWithinMultiFields
parameter_list|()
block|{
return|return
name|in
operator|.
name|isWithinMultiFields
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|indexSettings
specifier|public
name|Settings
name|indexSettings
parameter_list|()
block|{
return|return
name|in
operator|.
name|indexSettings
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sourceToParse
specifier|public
name|SourceToParse
name|sourceToParse
parameter_list|()
block|{
return|return
name|in
operator|.
name|sourceToParse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|path
specifier|public
name|ContentPath
name|path
parameter_list|()
block|{
return|return
name|in
operator|.
name|path
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parser
specifier|public
name|XContentParser
name|parser
parameter_list|()
block|{
return|return
name|in
operator|.
name|parser
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rootDoc
specifier|public
name|Document
name|rootDoc
parameter_list|()
block|{
return|return
name|in
operator|.
name|rootDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|in
operator|.
name|docs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|()
block|{
return|return
name|in
operator|.
name|doc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addDoc
specifier|protected
name|void
name|addDoc
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|in
operator|.
name|addDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|root
specifier|public
name|RootObjectMapper
name|root
parameter_list|()
block|{
return|return
name|in
operator|.
name|root
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docMapper
specifier|public
name|DocumentMapper
name|docMapper
parameter_list|()
block|{
return|return
name|in
operator|.
name|docMapper
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|mapperService
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|in
operator|.
name|mapperService
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|version
specifier|public
name|Field
name|version
parameter_list|()
block|{
return|return
name|in
operator|.
name|version
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|version
specifier|public
name|void
name|version
parameter_list|(
name|Field
name|version
parameter_list|)
block|{
name|in
operator|.
name|version
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seqID
specifier|public
name|SeqNoFieldMapper
operator|.
name|SequenceIDFields
name|seqID
parameter_list|()
block|{
return|return
name|in
operator|.
name|seqID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seqID
specifier|public
name|void
name|seqID
parameter_list|(
name|SeqNoFieldMapper
operator|.
name|SequenceIDFields
name|seqID
parameter_list|)
block|{
name|in
operator|.
name|seqID
argument_list|(
name|seqID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|allEntries
specifier|public
name|AllEntries
name|allEntries
parameter_list|()
block|{
return|return
name|in
operator|.
name|allEntries
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|externalValueSet
specifier|public
name|boolean
name|externalValueSet
parameter_list|()
block|{
return|return
name|in
operator|.
name|externalValueSet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|externalValue
specifier|public
name|Object
name|externalValue
parameter_list|()
block|{
return|return
name|in
operator|.
name|externalValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addDynamicMapper
specifier|public
name|void
name|addDynamicMapper
parameter_list|(
name|Mapper
name|update
parameter_list|)
block|{
name|in
operator|.
name|addDynamicMapper
argument_list|(
name|update
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDynamicMappers
specifier|public
name|List
argument_list|<
name|Mapper
argument_list|>
name|getDynamicMappers
parameter_list|()
block|{
return|return
name|in
operator|.
name|getDynamicMappers
argument_list|()
return|;
block|}
block|}
DECL|class|InternalParseContext
specifier|public
specifier|static
class|class
name|InternalParseContext
extends|extends
name|ParseContext
block|{
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
specifier|final
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
specifier|final
name|List
argument_list|<
name|Document
argument_list|>
name|documents
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
specifier|final
name|SourceToParse
name|sourceToParse
decl_stmt|;
DECL|field|version
specifier|private
name|Field
name|version
decl_stmt|;
DECL|field|seqID
specifier|private
name|SeqNoFieldMapper
operator|.
name|SequenceIDFields
name|seqID
decl_stmt|;
DECL|field|allEntries
specifier|private
specifier|final
name|AllEntries
name|allEntries
decl_stmt|;
DECL|field|dynamicMappers
specifier|private
specifier|final
name|List
argument_list|<
name|Mapper
argument_list|>
name|dynamicMappers
decl_stmt|;
DECL|method|InternalParseContext
specifier|public
name|InternalParseContext
parameter_list|(
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
name|SourceToParse
name|source
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
block|{
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
operator|new
name|ContentPath
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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
operator|new
name|Document
argument_list|()
expr_stmt|;
name|this
operator|.
name|documents
operator|=
operator|new
name|ArrayList
argument_list|<>
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
name|this
operator|.
name|version
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
name|allEntries
operator|=
operator|new
name|AllEntries
argument_list|()
expr_stmt|;
name|this
operator|.
name|dynamicMappers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|addDoc
specifier|protected
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|mapperService
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|docMapperParser
operator|.
name|mapperService
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|seqID
specifier|public
name|SeqNoFieldMapper
operator|.
name|SequenceIDFields
name|seqID
parameter_list|()
block|{
return|return
name|this
operator|.
name|seqID
return|;
block|}
annotation|@
name|Override
DECL|method|seqID
specifier|public
name|void
name|seqID
parameter_list|(
name|SeqNoFieldMapper
operator|.
name|SequenceIDFields
name|seqID
parameter_list|)
block|{
name|this
operator|.
name|seqID
operator|=
name|seqID
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|addDynamicMapper
specifier|public
name|void
name|addDynamicMapper
parameter_list|(
name|Mapper
name|mapper
parameter_list|)
block|{
name|dynamicMappers
operator|.
name|add
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDynamicMappers
specifier|public
name|List
argument_list|<
name|Mapper
argument_list|>
name|getDynamicMappers
parameter_list|()
block|{
return|return
name|dynamicMappers
return|;
block|}
block|}
DECL|method|docMapperParser
specifier|public
specifier|abstract
name|DocumentMapperParser
name|docMapperParser
parameter_list|()
function_decl|;
comment|/** Return a view of this {@link ParseContext} that changes the return      *  value of {@link #getIncludeInAllDefault()}. */
DECL|method|setIncludeInAllDefault
specifier|public
specifier|final
name|ParseContext
name|setIncludeInAllDefault
parameter_list|(
name|boolean
name|includeInAll
parameter_list|)
block|{
return|return
operator|new
name|FilterParseContext
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|getIncludeInAllDefault
parameter_list|()
block|{
return|return
name|includeInAll
return|;
block|}
block|}
return|;
block|}
comment|/** Whether field values should be added to the _all field by default. */
DECL|method|getIncludeInAllDefault
specifier|public
name|Boolean
name|getIncludeInAllDefault
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Return a new context that will be within a copy-to operation.      */
DECL|method|createCopyToContext
specifier|public
specifier|final
name|ParseContext
name|createCopyToContext
parameter_list|()
block|{
return|return
operator|new
name|FilterParseContext
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isWithinCopyTo
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
DECL|method|isWithinCopyTo
specifier|public
name|boolean
name|isWithinCopyTo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Return a new context that will be within multi-fields.      */
DECL|method|createMultiFieldContext
specifier|public
specifier|final
name|ParseContext
name|createMultiFieldContext
parameter_list|()
block|{
return|return
operator|new
name|FilterParseContext
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isWithinMultiFields
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
comment|/**      * Return a new context that will be used within a nested document.      */
DECL|method|createNestedContext
specifier|public
specifier|final
name|ParseContext
name|createNestedContext
parameter_list|(
name|String
name|fullPath
parameter_list|)
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|(
name|fullPath
argument_list|,
name|doc
argument_list|()
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|switchDoc
argument_list|(
name|doc
argument_list|)
return|;
block|}
comment|/**      * Return a new context that has the provided document as the current document.      */
DECL|method|switchDoc
specifier|public
specifier|final
name|ParseContext
name|switchDoc
parameter_list|(
specifier|final
name|Document
name|document
parameter_list|)
block|{
return|return
operator|new
name|FilterParseContext
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Document
name|doc
parameter_list|()
block|{
return|return
name|document
return|;
block|}
block|}
return|;
block|}
comment|/**      * Return a new context that will have the provided path.      */
DECL|method|overridePath
specifier|public
specifier|final
name|ParseContext
name|overridePath
parameter_list|(
specifier|final
name|ContentPath
name|path
parameter_list|)
block|{
return|return
operator|new
name|FilterParseContext
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|ContentPath
name|path
parameter_list|()
block|{
return|return
name|path
return|;
block|}
block|}
return|;
block|}
DECL|method|isWithinMultiFields
specifier|public
name|boolean
name|isWithinMultiFields
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nullable
DECL|method|indexSettings
specifier|public
specifier|abstract
name|Settings
name|indexSettings
parameter_list|()
function_decl|;
DECL|method|sourceToParse
specifier|public
specifier|abstract
name|SourceToParse
name|sourceToParse
parameter_list|()
function_decl|;
DECL|method|path
specifier|public
specifier|abstract
name|ContentPath
name|path
parameter_list|()
function_decl|;
DECL|method|parser
specifier|public
specifier|abstract
name|XContentParser
name|parser
parameter_list|()
function_decl|;
DECL|method|rootDoc
specifier|public
specifier|abstract
name|Document
name|rootDoc
parameter_list|()
function_decl|;
DECL|method|docs
specifier|public
specifier|abstract
name|List
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|()
function_decl|;
DECL|method|doc
specifier|public
specifier|abstract
name|Document
name|doc
parameter_list|()
function_decl|;
DECL|method|addDoc
specifier|protected
specifier|abstract
name|void
name|addDoc
parameter_list|(
name|Document
name|doc
parameter_list|)
function_decl|;
DECL|method|root
specifier|public
specifier|abstract
name|RootObjectMapper
name|root
parameter_list|()
function_decl|;
DECL|method|docMapper
specifier|public
specifier|abstract
name|DocumentMapper
name|docMapper
parameter_list|()
function_decl|;
DECL|method|mapperService
specifier|public
specifier|abstract
name|MapperService
name|mapperService
parameter_list|()
function_decl|;
DECL|method|version
specifier|public
specifier|abstract
name|Field
name|version
parameter_list|()
function_decl|;
DECL|method|version
specifier|public
specifier|abstract
name|void
name|version
parameter_list|(
name|Field
name|version
parameter_list|)
function_decl|;
DECL|method|seqID
specifier|public
specifier|abstract
name|SeqNoFieldMapper
operator|.
name|SequenceIDFields
name|seqID
parameter_list|()
function_decl|;
DECL|method|seqID
specifier|public
specifier|abstract
name|void
name|seqID
parameter_list|(
name|SeqNoFieldMapper
operator|.
name|SequenceIDFields
name|seqID
parameter_list|)
function_decl|;
DECL|method|includeInAll
specifier|public
specifier|final
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
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
argument_list|)
return|;
block|}
comment|/**      * Is all included or not. Will always disable it if {@link org.elasticsearch.index.mapper.AllFieldMapper#enabled()}      * is<tt>false</tt>. If its enabled, then will return<tt>true</tt> only if the specific flag is<tt>null</tt> or      * its actual value (so, if not set, defaults to "true") and the field is indexed.      */
DECL|method|includeInAll
specifier|private
name|boolean
name|includeInAll
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|,
name|boolean
name|indexed
parameter_list|)
block|{
if|if
condition|(
name|isWithinCopyTo
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|isWithinMultiFields
argument_list|()
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
argument_list|()
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
if|if
condition|(
name|includeInAll
operator|==
literal|null
condition|)
block|{
name|includeInAll
operator|=
name|getIncludeInAllDefault
argument_list|()
expr_stmt|;
block|}
comment|// not explicitly set
if|if
condition|(
name|includeInAll
operator|==
literal|null
condition|)
block|{
return|return
name|indexed
return|;
block|}
return|return
name|includeInAll
return|;
block|}
DECL|method|allEntries
specifier|public
specifier|abstract
name|AllEntries
name|allEntries
parameter_list|()
function_decl|;
comment|/**      * Return a new context that will have the external value set.      */
DECL|method|createExternalValueContext
specifier|public
specifier|final
name|ParseContext
name|createExternalValueContext
parameter_list|(
specifier|final
name|Object
name|externalValue
parameter_list|)
block|{
return|return
operator|new
name|FilterParseContext
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|externalValueSet
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|externalValue
parameter_list|()
block|{
return|return
name|externalValue
return|;
block|}
block|}
return|;
block|}
DECL|method|externalValueSet
specifier|public
name|boolean
name|externalValueSet
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|externalValue
specifier|public
name|Object
name|externalValue
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"External value is not set"
argument_list|)
throw|;
block|}
comment|/**      * Try to parse an externalValue if any      * @param clazz Expected class for external value      * @return null if no external value has been set or the value      */
DECL|method|parseExternalValue
specifier|public
specifier|final
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
name|IllegalArgumentException
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
name|clazz
operator|.
name|cast
argument_list|(
name|externalValue
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Add a new mapper dynamically created while parsing.      */
DECL|method|addDynamicMapper
specifier|public
specifier|abstract
name|void
name|addDynamicMapper
parameter_list|(
name|Mapper
name|update
parameter_list|)
function_decl|;
comment|/**      * Get dynamic mappers created while parsing.      */
DECL|method|getDynamicMappers
specifier|public
specifier|abstract
name|List
argument_list|<
name|Mapper
argument_list|>
name|getDynamicMappers
parameter_list|()
function_decl|;
block|}
end_class

end_unit

