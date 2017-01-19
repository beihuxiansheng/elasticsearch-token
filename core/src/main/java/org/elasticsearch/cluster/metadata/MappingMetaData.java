begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|AbstractDiffable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|Diff
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
name|compress
operator|.
name|CompressedXContent
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|XContentBuilder
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
name|XContentFactory
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
name|index
operator|.
name|mapper
operator|.
name|DateFieldMapper
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
name|Map
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
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeBooleanValue
import|;
end_import

begin_comment
comment|/**  * Mapping configuration for a type.  */
end_comment

begin_class
DECL|class|MappingMetaData
specifier|public
class|class
name|MappingMetaData
extends|extends
name|AbstractDiffable
argument_list|<
name|MappingMetaData
argument_list|>
block|{
DECL|class|Routing
specifier|public
specifier|static
class|class
name|Routing
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|Routing
name|EMPTY
init|=
operator|new
name|Routing
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|required
specifier|private
specifier|final
name|boolean
name|required
decl_stmt|;
DECL|method|Routing
specifier|public
name|Routing
parameter_list|(
name|boolean
name|required
parameter_list|)
block|{
name|this
operator|.
name|required
operator|=
name|required
expr_stmt|;
block|}
DECL|method|required
specifier|public
name|boolean
name|required
parameter_list|()
block|{
return|return
name|required
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Routing
name|routing
init|=
operator|(
name|Routing
operator|)
name|o
decl_stmt|;
return|return
name|required
operator|==
name|routing
operator|.
name|required
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|required
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
block|}
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|CompressedXContent
name|source
decl_stmt|;
DECL|field|routing
specifier|private
name|Routing
name|routing
decl_stmt|;
DECL|field|hasParentField
specifier|private
name|boolean
name|hasParentField
decl_stmt|;
DECL|method|MappingMetaData
specifier|public
name|MappingMetaData
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|docMapper
operator|.
name|type
argument_list|()
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|docMapper
operator|.
name|mappingSource
argument_list|()
expr_stmt|;
name|this
operator|.
name|routing
operator|=
operator|new
name|Routing
argument_list|(
name|docMapper
operator|.
name|routingFieldMapper
argument_list|()
operator|.
name|required
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|hasParentField
operator|=
name|docMapper
operator|.
name|parentFieldMapper
argument_list|()
operator|.
name|active
argument_list|()
expr_stmt|;
block|}
DECL|method|MappingMetaData
specifier|public
name|MappingMetaData
parameter_list|(
name|CompressedXContent
name|mapping
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|source
operator|=
name|mapping
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mappingMap
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|mapping
operator|.
name|compressedReference
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|v2
argument_list|()
decl_stmt|;
if|if
condition|(
name|mappingMap
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't derive type from mapping, no root type: "
operator|+
name|mapping
operator|.
name|string
argument_list|()
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|mappingMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|initMappers
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|mappingMap
operator|.
name|get
argument_list|(
name|this
operator|.
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|MappingMetaData
specifier|public
name|MappingMetaData
parameter_list|(
name|String
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|XContentBuilder
name|mappingBuilder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|map
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|this
operator|.
name|source
operator|=
operator|new
name|CompressedXContent
argument_list|(
name|mappingBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|withoutType
init|=
name|mapping
decl_stmt|;
if|if
condition|(
name|mapping
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|mapping
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|withoutType
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|mapping
operator|.
name|get
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|initMappers
argument_list|(
name|withoutType
argument_list|)
expr_stmt|;
block|}
DECL|method|MappingMetaData
specifier|private
name|MappingMetaData
parameter_list|()
block|{
name|this
operator|.
name|type
operator|=
literal|""
expr_stmt|;
try|try
block|{
name|this
operator|.
name|source
operator|=
operator|new
name|CompressedXContent
argument_list|(
literal|"{}"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot create MappingMetaData prototype"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|initMappers
specifier|private
name|void
name|initMappers
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|withoutType
parameter_list|)
block|{
if|if
condition|(
name|withoutType
operator|.
name|containsKey
argument_list|(
literal|"_routing"
argument_list|)
condition|)
block|{
name|boolean
name|required
init|=
literal|false
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|routingNode
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|withoutType
operator|.
name|get
argument_list|(
literal|"_routing"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|routingNode
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|fieldName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|fieldNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"required"
argument_list|)
condition|)
block|{
try|try
block|{
name|required
operator|=
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to create mapping for type ["
operator|+
name|this
operator|.
name|type
argument_list|()
operator|+
literal|"]. "
operator|+
literal|"Illegal value in field [_routing.required]."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
name|this
operator|.
name|routing
operator|=
operator|new
name|Routing
argument_list|(
name|required
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|routing
operator|=
name|Routing
operator|.
name|EMPTY
expr_stmt|;
block|}
if|if
condition|(
name|withoutType
operator|.
name|containsKey
argument_list|(
literal|"_parent"
argument_list|)
condition|)
block|{
name|this
operator|.
name|hasParentField
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|hasParentField
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|MappingMetaData
specifier|public
name|MappingMetaData
parameter_list|(
name|String
name|type
parameter_list|,
name|CompressedXContent
name|source
parameter_list|,
name|Routing
name|routing
parameter_list|,
name|boolean
name|hasParentField
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|routing
expr_stmt|;
name|this
operator|.
name|hasParentField
operator|=
name|hasParentField
expr_stmt|;
block|}
DECL|method|updateDefaultMapping
name|void
name|updateDefaultMapping
parameter_list|(
name|MappingMetaData
name|defaultMapping
parameter_list|)
block|{
if|if
condition|(
name|routing
operator|==
name|Routing
operator|.
name|EMPTY
condition|)
block|{
name|routing
operator|=
name|defaultMapping
operator|.
name|routing
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
DECL|method|source
specifier|public
name|CompressedXContent
name|source
parameter_list|()
block|{
return|return
name|this
operator|.
name|source
return|;
block|}
DECL|method|hasParentField
specifier|public
name|boolean
name|hasParentField
parameter_list|()
block|{
return|return
name|hasParentField
return|;
block|}
comment|/**      * Converts the serialized compressed form of the mappings into a parsed map.      */
DECL|method|sourceAsMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAsMap
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|source
operator|.
name|compressedReference
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|v2
argument_list|()
decl_stmt|;
if|if
condition|(
name|mapping
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|mapping
operator|.
name|containsKey
argument_list|(
name|type
argument_list|()
argument_list|)
condition|)
block|{
comment|// the type name is the root value, reduce it
name|mapping
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|mapping
operator|.
name|get
argument_list|(
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|mapping
return|;
block|}
comment|/**      * Converts the serialized compressed form of the mappings into a parsed map.      */
DECL|method|getSourceAsMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getSourceAsMap
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|sourceAsMap
argument_list|()
return|;
block|}
DECL|method|routing
specifier|public
name|Routing
name|routing
parameter_list|()
block|{
return|return
name|this
operator|.
name|routing
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|source
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// routing
name|out
operator|.
name|writeBoolean
argument_list|(
name|routing
argument_list|()
operator|.
name|required
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha1_UNRELEASED
argument_list|)
condition|)
block|{
comment|// timestamp
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// enabled
name|out
operator|.
name|writeString
argument_list|(
name|DateFieldMapper
operator|.
name|DEFAULT_DATE_TIME_FORMATTER
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
literal|"now"
argument_list|)
expr_stmt|;
comment|// 5.x default
name|out
operator|.
name|writeOptionalBoolean
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasParentField
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MappingMetaData
name|that
init|=
operator|(
name|MappingMetaData
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|routing
operator|.
name|equals
argument_list|(
name|that
operator|.
name|routing
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|source
operator|.
name|equals
argument_list|(
name|that
operator|.
name|source
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|that
operator|.
name|type
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|type
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|source
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|routing
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|MappingMetaData
specifier|public
name|MappingMetaData
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|type
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|source
operator|=
name|CompressedXContent
operator|.
name|readCompressedString
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// routing
name|routing
operator|=
operator|new
name|Routing
argument_list|(
name|in
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha1_UNRELEASED
argument_list|)
condition|)
block|{
comment|// timestamp
name|boolean
name|enabled
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"_timestamp may not be enabled"
argument_list|)
throw|;
block|}
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
comment|// format
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
comment|// defaultTimestamp
name|in
operator|.
name|readOptionalBoolean
argument_list|()
expr_stmt|;
comment|// ignoreMissing
block|}
name|hasParentField
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
DECL|method|readDiffFrom
specifier|public
specifier|static
name|Diff
argument_list|<
name|MappingMetaData
argument_list|>
name|readDiffFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readDiffFrom
argument_list|(
name|MappingMetaData
operator|::
operator|new
argument_list|,
name|in
argument_list|)
return|;
block|}
block|}
end_class

end_unit

