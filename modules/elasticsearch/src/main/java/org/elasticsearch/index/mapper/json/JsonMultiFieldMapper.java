begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|json
package|;
end_package

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
name|ImmutableMap
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
name|FieldMapper
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
name|FieldMapperListener
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
name|MergeMappingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JsonBuilder
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
name|HashMap
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|MapBuilder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JsonMultiFieldMapper
specifier|public
class|class
name|JsonMultiFieldMapper
implements|implements
name|JsonMapper
block|{
DECL|field|JSON_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|JSON_TYPE
init|=
literal|"multi_field"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
block|{
DECL|field|PATH_TYPE
specifier|public
specifier|static
specifier|final
name|JsonPath
operator|.
name|Type
name|PATH_TYPE
init|=
name|JsonPath
operator|.
name|Type
operator|.
name|FULL
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|JsonMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|JsonMultiFieldMapper
argument_list|>
block|{
DECL|field|pathType
specifier|private
name|JsonPath
operator|.
name|Type
name|pathType
init|=
name|Defaults
operator|.
name|PATH_TYPE
decl_stmt|;
DECL|field|mappersBuilders
specifier|private
specifier|final
name|List
argument_list|<
name|JsonMapper
operator|.
name|Builder
argument_list|>
name|mappersBuilders
init|=
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|defaultMapperBuilder
specifier|private
name|JsonMapper
operator|.
name|Builder
name|defaultMapperBuilder
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|this
expr_stmt|;
block|}
DECL|method|pathType
specifier|public
name|Builder
name|pathType
parameter_list|(
name|JsonPath
operator|.
name|Type
name|pathType
parameter_list|)
block|{
name|this
operator|.
name|pathType
operator|=
name|pathType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|JsonMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|.
name|name
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|defaultMapperBuilder
operator|=
name|builder
expr_stmt|;
block|}
else|else
block|{
name|mappersBuilders
operator|.
name|add
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|JsonMultiFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|JsonPath
operator|.
name|Type
name|origPathType
init|=
name|context
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|()
decl_stmt|;
name|context
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|(
name|pathType
argument_list|)
expr_stmt|;
name|JsonMapper
name|defaultMapper
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|defaultMapperBuilder
operator|!=
literal|null
condition|)
block|{
name|defaultMapper
operator|=
name|defaultMapperBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|path
argument_list|()
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|JsonMapper
argument_list|>
name|mappers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|JsonMapper
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|JsonMapper
operator|.
name|Builder
name|builder
range|:
name|mappersBuilders
control|)
block|{
name|JsonMapper
name|mapper
init|=
name|builder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|mappers
operator|.
name|put
argument_list|(
name|mapper
operator|.
name|name
argument_list|()
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|path
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
name|context
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|(
name|origPathType
argument_list|)
expr_stmt|;
return|return
operator|new
name|JsonMultiFieldMapper
argument_list|(
name|name
argument_list|,
name|pathType
argument_list|,
name|mappers
argument_list|,
name|defaultMapper
argument_list|)
return|;
block|}
block|}
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|pathType
specifier|private
specifier|final
name|JsonPath
operator|.
name|Type
name|pathType
decl_stmt|;
DECL|field|mutex
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|mappers
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|JsonMapper
argument_list|>
name|mappers
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|defaultMapper
specifier|private
specifier|volatile
name|JsonMapper
name|defaultMapper
decl_stmt|;
DECL|method|JsonMultiFieldMapper
specifier|public
name|JsonMultiFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|JsonPath
operator|.
name|Type
name|pathType
parameter_list|,
name|JsonMapper
name|defaultMapper
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|pathType
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|JsonMapper
argument_list|>
argument_list|()
argument_list|,
name|defaultMapper
argument_list|)
expr_stmt|;
block|}
DECL|method|JsonMultiFieldMapper
specifier|public
name|JsonMultiFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|JsonPath
operator|.
name|Type
name|pathType
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|JsonMapper
argument_list|>
name|mappers
parameter_list|,
name|JsonMapper
name|defaultMapper
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|pathType
operator|=
name|pathType
expr_stmt|;
name|this
operator|.
name|mappers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|mappers
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultMapper
operator|=
name|defaultMapper
expr_stmt|;
block|}
DECL|method|name
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|pathType
specifier|public
name|JsonPath
operator|.
name|Type
name|pathType
parameter_list|()
block|{
return|return
name|pathType
return|;
block|}
DECL|method|defaultMapper
specifier|public
name|JsonMapper
name|defaultMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|defaultMapper
return|;
block|}
DECL|method|mappers
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|JsonMapper
argument_list|>
name|mappers
parameter_list|()
block|{
return|return
name|this
operator|.
name|mappers
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|JsonParseContext
name|jsonContext
parameter_list|)
throws|throws
name|IOException
block|{
name|JsonPath
operator|.
name|Type
name|origPathType
init|=
name|jsonContext
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|()
decl_stmt|;
name|jsonContext
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|(
name|pathType
argument_list|)
expr_stmt|;
comment|// do the default mapper without adding the path
if|if
condition|(
name|defaultMapper
operator|!=
literal|null
condition|)
block|{
name|defaultMapper
operator|.
name|parse
argument_list|(
name|jsonContext
argument_list|)
expr_stmt|;
block|}
name|jsonContext
operator|.
name|path
argument_list|()
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|JsonMapper
name|mapper
range|:
name|mappers
operator|.
name|values
argument_list|()
control|)
block|{
name|mapper
operator|.
name|parse
argument_list|(
name|jsonContext
argument_list|)
expr_stmt|;
block|}
name|jsonContext
operator|.
name|path
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
name|jsonContext
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|(
name|origPathType
argument_list|)
expr_stmt|;
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|JsonMapper
name|mergeWith
parameter_list|,
name|JsonMergeContext
name|mergeContext
parameter_list|)
throws|throws
name|MergeMappingException
block|{
if|if
condition|(
operator|!
operator|(
name|mergeWith
operator|instanceof
name|JsonMultiFieldMapper
operator|)
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"Can't merge a non multi_field mapping ["
operator|+
name|mergeWith
operator|.
name|name
argument_list|()
operator|+
literal|"] with a multi_field mapping ["
operator|+
name|name
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|JsonMultiFieldMapper
name|mergeWithMultiField
init|=
operator|(
name|JsonMultiFieldMapper
operator|)
name|mergeWith
decl_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
comment|// merge the default mapper
if|if
condition|(
name|defaultMapper
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|mergeWithMultiField
operator|.
name|defaultMapper
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|mergeContext
operator|.
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|()
condition|)
block|{
name|defaultMapper
operator|=
name|mergeWithMultiField
operator|.
name|defaultMapper
expr_stmt|;
name|mergeContext
operator|.
name|docMapper
argument_list|()
operator|.
name|addFieldMapper
argument_list|(
operator|(
name|FieldMapper
operator|)
name|defaultMapper
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|mergeWithMultiField
operator|.
name|defaultMapper
operator|!=
literal|null
condition|)
block|{
name|defaultMapper
operator|.
name|merge
argument_list|(
name|mergeWithMultiField
operator|.
name|defaultMapper
argument_list|,
name|mergeContext
argument_list|)
expr_stmt|;
block|}
block|}
comment|// merge all the other mappers
for|for
control|(
name|JsonMapper
name|mergeWithMapper
range|:
name|mergeWithMultiField
operator|.
name|mappers
operator|.
name|values
argument_list|()
control|)
block|{
name|JsonMapper
name|mergeIntoMapper
init|=
name|mappers
operator|.
name|get
argument_list|(
name|mergeWithMapper
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeIntoMapper
operator|==
literal|null
condition|)
block|{
comment|// no mapping, simply add it if not simulating
if|if
condition|(
operator|!
name|mergeContext
operator|.
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|()
condition|)
block|{
name|mappers
operator|=
name|newMapBuilder
argument_list|(
name|mappers
argument_list|)
operator|.
name|put
argument_list|(
name|mergeWithMapper
operator|.
name|name
argument_list|()
argument_list|,
name|mergeWithMapper
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
if|if
condition|(
name|mergeWithMapper
operator|instanceof
name|JsonFieldMapper
condition|)
block|{
name|mergeContext
operator|.
name|docMapper
argument_list|()
operator|.
name|addFieldMapper
argument_list|(
operator|(
name|FieldMapper
operator|)
name|mergeWithMapper
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|mergeIntoMapper
operator|.
name|merge
argument_list|(
name|mergeWithMapper
argument_list|,
name|mergeContext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|traverse
annotation|@
name|Override
specifier|public
name|void
name|traverse
parameter_list|(
name|FieldMapperListener
name|fieldMapperListener
parameter_list|)
block|{
for|for
control|(
name|JsonMapper
name|mapper
range|:
name|mappers
operator|.
name|values
argument_list|()
control|)
block|{
name|mapper
operator|.
name|traverse
argument_list|(
name|fieldMapperListener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toJson
annotation|@
name|Override
specifier|public
name|void
name|toJson
parameter_list|(
name|JsonBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|JSON_TYPE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"pathType"
argument_list|,
name|pathType
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultMapper
operator|!=
literal|null
condition|)
block|{
name|defaultMapper
operator|.
name|toJson
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|JsonMapper
name|mapper
range|:
name|mappers
operator|.
name|values
argument_list|()
control|)
block|{
name|mapper
operator|.
name|toJson
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

