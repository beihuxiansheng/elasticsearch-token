begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|internal
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
name|document
operator|.
name|Field
import|;
end_import

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
name|Strings
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
name|XContentBuilder
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
name|NamedAnalyzer
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
name|NumericIntegerAnalyzer
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
name|Mapper
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
name|MapperParsingException
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
name|index
operator|.
name|mapper
operator|.
name|MergeResult
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
name|ParseContext
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
name|RootMapper
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
name|core
operator|.
name|IntegerFieldMapper
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
name|core
operator|.
name|NumberFieldMapper
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
operator|.
name|TypeParsers
operator|.
name|parseStore
import|;
end_import

begin_class
DECL|class|SizeFieldMapper
specifier|public
class|class
name|SizeFieldMapper
extends|extends
name|IntegerFieldMapper
implements|implements
name|RootMapper
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"_size"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_size"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|IntegerFieldMapper
operator|.
name|Defaults
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|CONTENT_TYPE
decl_stmt|;
DECL|field|ENABLED_STATE
specifier|public
specifier|static
specifier|final
name|EnabledAttributeMapper
name|ENABLED_STATE
init|=
name|EnabledAttributeMapper
operator|.
name|UNSET_DISABLED
decl_stmt|;
DECL|field|SIZE_FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|SIZE_FIELD_TYPE
init|=
name|IntegerFieldMapper
operator|.
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|clone
argument_list|()
decl_stmt|;
static|static
block|{
name|SIZE_FIELD_TYPE
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SIZE_FIELD_TYPE
operator|.
name|setNumericPrecisionStep
argument_list|(
name|Defaults
operator|.
name|PRECISION_STEP_32_BIT
argument_list|)
expr_stmt|;
name|SIZE_FIELD_TYPE
operator|.
name|setNames
argument_list|(
operator|new
name|MappedFieldType
operator|.
name|Names
argument_list|(
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|SIZE_FIELD_TYPE
operator|.
name|setIndexAnalyzer
argument_list|(
name|NumericIntegerAnalyzer
operator|.
name|buildNamedAnalyzer
argument_list|(
name|Defaults
operator|.
name|PRECISION_STEP_32_BIT
argument_list|)
argument_list|)
expr_stmt|;
name|SIZE_FIELD_TYPE
operator|.
name|setSearchAnalyzer
argument_list|(
name|NumericIntegerAnalyzer
operator|.
name|buildNamedAnalyzer
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|SIZE_FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|NumberFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|IntegerFieldMapper
argument_list|>
block|{
DECL|field|enabledState
specifier|protected
name|EnabledAttributeMapper
name|enabledState
init|=
name|EnabledAttributeMapper
operator|.
name|UNSET_DISABLED
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|MappedFieldType
name|existing
parameter_list|)
block|{
name|super
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
name|existing
operator|==
literal|null
condition|?
name|Defaults
operator|.
name|SIZE_FIELD_TYPE
else|:
name|existing
argument_list|,
name|Defaults
operator|.
name|PRECISION_STEP_32_BIT
argument_list|)
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|Builder
name|enabled
parameter_list|(
name|EnabledAttributeMapper
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabledState
operator|=
name|enabled
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|SizeFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
operator|new
name|SizeFieldMapper
argument_list|(
name|enabledState
argument_list|,
name|fieldType
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeNumberAnalyzer
specifier|protected
name|NamedAnalyzer
name|makeNumberAnalyzer
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
return|return
name|NumericIntegerAnalyzer
operator|.
name|buildNamedAnalyzer
argument_list|(
name|precisionStep
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|maxPrecisionStep
specifier|protected
name|int
name|maxPrecisionStep
parameter_list|()
block|{
return|return
literal|32
return|;
block|}
block|}
DECL|class|TypeParser
specifier|public
specifier|static
class|class
name|TypeParser
implements|implements
name|Mapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
operator|.
name|Builder
name|parse
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|node
parameter_list|,
name|ParserContext
name|parserContext
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|(
name|parserContext
operator|.
name|mapperService
argument_list|()
operator|.
name|fullName
argument_list|(
name|NAME
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iterator
init|=
name|node
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
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
literal|"enabled"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|enabled
argument_list|(
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
condition|?
name|EnabledAttributeMapper
operator|.
name|ENABLED
else|:
name|EnabledAttributeMapper
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"store"
argument_list|)
operator|&&
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_0_0
argument_list|)
condition|)
block|{
name|builder
operator|.
name|store
argument_list|(
name|parseStore
argument_list|(
name|fieldName
argument_list|,
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|enabledState
specifier|private
name|EnabledAttributeMapper
name|enabledState
decl_stmt|;
DECL|method|SizeFieldMapper
specifier|public
name|SizeFieldMapper
parameter_list|(
name|Settings
name|indexSettings
parameter_list|,
name|MappedFieldType
name|existing
parameter_list|)
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|ENABLED_STATE
argument_list|,
name|existing
operator|==
literal|null
condition|?
name|Defaults
operator|.
name|SIZE_FIELD_TYPE
operator|.
name|clone
argument_list|()
else|:
name|existing
operator|.
name|clone
argument_list|()
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|SizeFieldMapper
specifier|public
name|SizeFieldMapper
parameter_list|(
name|EnabledAttributeMapper
name|enabled
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|fieldType
argument_list|,
literal|false
argument_list|,
name|Defaults
operator|.
name|IGNORE_MALFORMED
argument_list|,
name|Defaults
operator|.
name|COERCE
argument_list|,
literal|null
argument_list|,
name|indexSettings
argument_list|,
name|MultiFields
operator|.
name|empty
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabledState
operator|=
name|enabled
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|Defaults
operator|.
name|NAME
return|;
block|}
DECL|method|enabled
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|enabledState
operator|.
name|enabled
return|;
block|}
annotation|@
name|Override
DECL|method|preParse
specifier|public
name|void
name|preParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|postParse
specifier|public
name|void
name|postParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we post parse it so we get the size stored, possibly compressed (source will be preParse)
name|super
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing to do here, we call the parent in postParse
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|innerParseCreateField
specifier|protected
name|void
name|innerParseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|enabledState
operator|.
name|enabled
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|flyweight
argument_list|()
condition|)
block|{
return|return;
block|}
name|fields
operator|.
name|add
argument_list|(
operator|new
name|CustomIntegerNumericField
argument_list|(
name|this
argument_list|,
name|context
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
argument_list|,
name|fieldType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|includeDefaults
init|=
name|params
operator|.
name|paramAsBoolean
argument_list|(
literal|"include_defaults"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// all are defaults, no need to write it at all
if|if
condition|(
operator|!
name|includeDefaults
operator|&&
name|enabledState
operator|==
name|Defaults
operator|.
name|ENABLED_STATE
operator|&&
operator|(
name|indexCreatedBefore2x
operator|==
literal|false
operator|||
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
operator|==
literal|false
operator|)
condition|)
block|{
return|return
name|builder
return|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|contentType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeDefaults
operator|||
name|enabledState
operator|!=
name|Defaults
operator|.
name|ENABLED_STATE
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
name|enabledState
operator|.
name|enabled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexCreatedBefore2x
operator|&&
operator|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
operator|==
literal|true
operator|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|MergeResult
name|mergeResult
parameter_list|)
throws|throws
name|MergeMappingException
block|{
name|SizeFieldMapper
name|sizeFieldMapperMergeWith
init|=
operator|(
name|SizeFieldMapper
operator|)
name|mergeWith
decl_stmt|;
if|if
condition|(
operator|!
name|mergeResult
operator|.
name|simulate
argument_list|()
condition|)
block|{
if|if
condition|(
name|sizeFieldMapperMergeWith
operator|.
name|enabledState
operator|!=
name|enabledState
operator|&&
operator|!
name|sizeFieldMapperMergeWith
operator|.
name|enabledState
operator|.
name|unset
argument_list|()
condition|)
block|{
name|this
operator|.
name|enabledState
operator|=
name|sizeFieldMapperMergeWith
operator|.
name|enabledState
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

