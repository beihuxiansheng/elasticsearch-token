begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
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
name|search
operator|.
name|Query
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
name|fielddata
operator|.
name|IndexFieldData
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
name|fielddata
operator|.
name|plain
operator|.
name|PagedBytesIndexFieldData
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
name|DocumentMapperParser
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
name|internal
operator|.
name|AllFieldMapper
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|parseTextField
import|;
end_import

begin_comment
comment|/** A {@link FieldMapper} for full-text fields. */
end_comment

begin_class
DECL|class|TextFieldMapper
specifier|public
class|class
name|TextFieldMapper
extends|extends
name|FieldMapper
implements|implements
name|AllFieldMapper
operator|.
name|IncludeInAll
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"text"
decl_stmt|;
DECL|field|POSITION_INCREMENT_GAP_USE_ANALYZER
specifier|private
specifier|static
specifier|final
name|int
name|POSITION_INCREMENT_GAP_USE_ANALYZER
init|=
operator|-
literal|1
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
block|{
DECL|field|FIELDDATA_MIN_FREQUENCY
specifier|public
specifier|static
name|double
name|FIELDDATA_MIN_FREQUENCY
init|=
literal|0
decl_stmt|;
DECL|field|FIELDDATA_MAX_FREQUENCY
specifier|public
specifier|static
name|double
name|FIELDDATA_MAX_FREQUENCY
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|FIELDDATA_MIN_SEGMENT_SIZE
specifier|public
specifier|static
name|int
name|FIELDDATA_MIN_SEGMENT_SIZE
init|=
literal|0
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|TextFieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**          * The default position_increment_gap is set to 100 so that phrase          * queries of reasonably high slop will not match across field values.          */
DECL|field|POSITION_INCREMENT_GAP
specifier|public
specifier|static
specifier|final
name|int
name|POSITION_INCREMENT_GAP
init|=
literal|100
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|FieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|TextFieldMapper
argument_list|>
block|{
DECL|field|positionIncrementGap
specifier|private
name|int
name|positionIncrementGap
init|=
name|POSITION_INCREMENT_GAP_USE_ANALYZER
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
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|TextFieldType
name|fieldType
parameter_list|()
block|{
return|return
operator|(
name|TextFieldType
operator|)
name|super
operator|.
name|fieldType
argument_list|()
return|;
block|}
DECL|method|positionIncrementGap
specifier|public
name|Builder
name|positionIncrementGap
parameter_list|(
name|int
name|positionIncrementGap
parameter_list|)
block|{
if|if
condition|(
name|positionIncrementGap
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"[positions_increment_gap] must be positive, got "
operator|+
name|positionIncrementGap
argument_list|)
throw|;
block|}
name|this
operator|.
name|positionIncrementGap
operator|=
name|positionIncrementGap
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fielddata
specifier|public
name|Builder
name|fielddata
parameter_list|(
name|boolean
name|fielddata
parameter_list|)
block|{
name|fieldType
argument_list|()
operator|.
name|setFielddata
argument_list|(
name|fielddata
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|Builder
name|docValues
parameter_list|(
name|boolean
name|docValues
parameter_list|)
block|{
if|if
condition|(
name|docValues
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[text] fields do not support doc values"
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|docValues
argument_list|(
name|docValues
argument_list|)
return|;
block|}
DECL|method|eagerGlobalOrdinals
specifier|public
name|Builder
name|eagerGlobalOrdinals
parameter_list|(
name|boolean
name|eagerGlobalOrdinals
parameter_list|)
block|{
name|fieldType
argument_list|()
operator|.
name|setEagerGlobalOrdinals
argument_list|(
name|eagerGlobalOrdinals
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|fielddataFrequencyFilter
specifier|public
name|Builder
name|fielddataFrequencyFilter
parameter_list|(
name|double
name|minFreq
parameter_list|,
name|double
name|maxFreq
parameter_list|,
name|int
name|minSegmentSize
parameter_list|)
block|{
name|fieldType
argument_list|()
operator|.
name|setFielddataMinFrequency
argument_list|(
name|minFreq
argument_list|)
expr_stmt|;
name|fieldType
argument_list|()
operator|.
name|setFielddataMaxFrequency
argument_list|(
name|maxFreq
argument_list|)
expr_stmt|;
name|fieldType
argument_list|()
operator|.
name|setFielddataMinSegmentSize
argument_list|(
name|minSegmentSize
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|TextFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|positionIncrementGap
operator|!=
name|POSITION_INCREMENT_GAP_USE_ANALYZER
condition|)
block|{
name|fieldType
operator|.
name|setIndexAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
name|fieldType
operator|.
name|indexAnalyzer
argument_list|()
argument_list|,
name|positionIncrementGap
argument_list|)
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setSearchAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
name|fieldType
operator|.
name|searchAnalyzer
argument_list|()
argument_list|,
name|positionIncrementGap
argument_list|)
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setSearchQuoteAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
name|fieldType
operator|.
name|searchQuoteAnalyzer
argument_list|()
argument_list|,
name|positionIncrementGap
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|TextFieldMapper
name|fieldMapper
init|=
operator|new
name|TextFieldMapper
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|,
name|defaultFieldType
argument_list|,
name|positionIncrementGap
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|multiFieldsBuilder
operator|.
name|build
argument_list|(
name|this
argument_list|,
name|context
argument_list|)
argument_list|,
name|copyTo
argument_list|)
decl_stmt|;
return|return
name|fieldMapper
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|)
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
name|fieldName
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
name|TextFieldMapper
operator|.
name|Builder
name|builder
init|=
operator|new
name|TextFieldMapper
operator|.
name|Builder
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setIndexAnalyzer
argument_list|(
name|parserContext
operator|.
name|analysisService
argument_list|()
operator|.
name|defaultIndexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setSearchAnalyzer
argument_list|(
name|parserContext
operator|.
name|analysisService
argument_list|()
operator|.
name|defaultSearchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setSearchQuoteAnalyzer
argument_list|(
name|parserContext
operator|.
name|analysisService
argument_list|()
operator|.
name|defaultSearchQuoteAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|parseTextField
argument_list|(
name|builder
argument_list|,
name|fieldName
argument_list|,
name|node
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
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
name|propName
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
name|propNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"position_increment_gap"
argument_list|)
condition|)
block|{
name|int
name|newPositionIncrementGap
init|=
name|XContentMapValues
operator|.
name|nodeIntegerValue
argument_list|(
name|propNode
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|builder
operator|.
name|positionIncrementGap
argument_list|(
name|newPositionIncrementGap
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
name|propName
operator|.
name|equals
argument_list|(
literal|"fielddata"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fielddata
argument_list|(
name|XContentMapValues
operator|.
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
name|propName
operator|.
name|equals
argument_list|(
literal|"eager_global_ordinals"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|eagerGlobalOrdinals
argument_list|(
name|XContentMapValues
operator|.
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
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
name|propName
operator|.
name|equals
argument_list|(
literal|"fielddata_frequency_filter"
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|frequencyFilter
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|propNode
decl_stmt|;
name|double
name|minFrequency
init|=
name|XContentMapValues
operator|.
name|nodeDoubleValue
argument_list|(
name|frequencyFilter
operator|.
name|remove
argument_list|(
literal|"min"
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|maxFrequency
init|=
name|XContentMapValues
operator|.
name|nodeDoubleValue
argument_list|(
name|frequencyFilter
operator|.
name|remove
argument_list|(
literal|"max"
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|int
name|minSegmentSize
init|=
name|XContentMapValues
operator|.
name|nodeIntegerValue
argument_list|(
name|frequencyFilter
operator|.
name|remove
argument_list|(
literal|"min_segment_size"
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|builder
operator|.
name|fielddataFrequencyFilter
argument_list|(
name|minFrequency
argument_list|,
name|maxFrequency
argument_list|,
name|minSegmentSize
argument_list|)
expr_stmt|;
name|DocumentMapperParser
operator|.
name|checkNoRemainingFields
argument_list|(
name|propName
argument_list|,
name|frequencyFilter
argument_list|,
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
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
DECL|class|TextFieldType
specifier|public
specifier|static
specifier|final
class|class
name|TextFieldType
extends|extends
name|MappedFieldType
block|{
DECL|field|fielddata
specifier|private
name|boolean
name|fielddata
decl_stmt|;
DECL|field|fielddataMinFrequency
specifier|private
name|double
name|fielddataMinFrequency
decl_stmt|;
DECL|field|fielddataMaxFrequency
specifier|private
name|double
name|fielddataMaxFrequency
decl_stmt|;
DECL|field|fielddataMinSegmentSize
specifier|private
name|int
name|fielddataMinSegmentSize
decl_stmt|;
DECL|method|TextFieldType
specifier|public
name|TextFieldType
parameter_list|()
block|{
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fielddata
operator|=
literal|false
expr_stmt|;
name|fielddataMinFrequency
operator|=
name|Defaults
operator|.
name|FIELDDATA_MIN_FREQUENCY
expr_stmt|;
name|fielddataMaxFrequency
operator|=
name|Defaults
operator|.
name|FIELDDATA_MAX_FREQUENCY
expr_stmt|;
name|fielddataMinSegmentSize
operator|=
name|Defaults
operator|.
name|FIELDDATA_MIN_SEGMENT_SIZE
expr_stmt|;
block|}
DECL|method|TextFieldType
specifier|protected
name|TextFieldType
parameter_list|(
name|TextFieldType
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|this
operator|.
name|fielddata
operator|=
name|ref
operator|.
name|fielddata
expr_stmt|;
name|this
operator|.
name|fielddataMinFrequency
operator|=
name|ref
operator|.
name|fielddataMinFrequency
expr_stmt|;
name|this
operator|.
name|fielddataMaxFrequency
operator|=
name|ref
operator|.
name|fielddataMaxFrequency
expr_stmt|;
name|this
operator|.
name|fielddataMinSegmentSize
operator|=
name|ref
operator|.
name|fielddataMinSegmentSize
expr_stmt|;
block|}
DECL|method|clone
specifier|public
name|TextFieldType
name|clone
parameter_list|()
block|{
return|return
operator|new
name|TextFieldType
argument_list|(
name|this
argument_list|)
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
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TextFieldType
name|that
init|=
operator|(
name|TextFieldType
operator|)
name|o
decl_stmt|;
return|return
name|fielddata
operator|==
name|that
operator|.
name|fielddata
operator|&&
name|fielddataMinFrequency
operator|==
name|that
operator|.
name|fielddataMinFrequency
operator|&&
name|fielddataMaxFrequency
operator|==
name|that
operator|.
name|fielddataMaxFrequency
operator|&&
name|fielddataMinSegmentSize
operator|==
name|that
operator|.
name|fielddataMinSegmentSize
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
name|Objects
operator|.
name|hash
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|fielddata
argument_list|,
name|fielddataMinFrequency
argument_list|,
name|fielddataMaxFrequency
argument_list|,
name|fielddataMinSegmentSize
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkCompatibility
specifier|public
name|void
name|checkCompatibility
parameter_list|(
name|MappedFieldType
name|other
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|conflicts
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
name|super
operator|.
name|checkCompatibility
argument_list|(
name|other
argument_list|,
name|conflicts
argument_list|,
name|strict
argument_list|)
expr_stmt|;
name|TextFieldType
name|otherType
init|=
operator|(
name|TextFieldType
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|strict
condition|)
block|{
if|if
condition|(
name|fielddata
argument_list|()
operator|!=
name|otherType
operator|.
name|fielddata
argument_list|()
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|name
argument_list|()
operator|+
literal|"] is used by multiple types. Set update_all_types to true to update [fielddata] "
operator|+
literal|"across all types."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fielddataMinFrequency
argument_list|()
operator|!=
name|otherType
operator|.
name|fielddataMinFrequency
argument_list|()
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|name
argument_list|()
operator|+
literal|"] is used by multiple types. Set update_all_types to true to update "
operator|+
literal|"[fielddata_frequency_filter.min] across all types."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fielddataMaxFrequency
argument_list|()
operator|!=
name|otherType
operator|.
name|fielddataMaxFrequency
argument_list|()
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|name
argument_list|()
operator|+
literal|"] is used by multiple types. Set update_all_types to true to update "
operator|+
literal|"[fielddata_frequency_filter.max] across all types."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fielddataMinSegmentSize
argument_list|()
operator|!=
name|otherType
operator|.
name|fielddataMinSegmentSize
argument_list|()
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|name
argument_list|()
operator|+
literal|"] is used by multiple types. Set update_all_types to true to update "
operator|+
literal|"[fielddata_frequency_filter.min_segment_size] across all types."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|fielddata
specifier|public
name|boolean
name|fielddata
parameter_list|()
block|{
return|return
name|fielddata
return|;
block|}
DECL|method|setFielddata
specifier|public
name|void
name|setFielddata
parameter_list|(
name|boolean
name|fielddata
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|fielddata
operator|=
name|fielddata
expr_stmt|;
block|}
DECL|method|fielddataMinFrequency
specifier|public
name|double
name|fielddataMinFrequency
parameter_list|()
block|{
return|return
name|fielddataMinFrequency
return|;
block|}
DECL|method|setFielddataMinFrequency
specifier|public
name|void
name|setFielddataMinFrequency
parameter_list|(
name|double
name|fielddataMinFrequency
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|fielddataMinFrequency
operator|=
name|fielddataMinFrequency
expr_stmt|;
block|}
DECL|method|fielddataMaxFrequency
specifier|public
name|double
name|fielddataMaxFrequency
parameter_list|()
block|{
return|return
name|fielddataMaxFrequency
return|;
block|}
DECL|method|setFielddataMaxFrequency
specifier|public
name|void
name|setFielddataMaxFrequency
parameter_list|(
name|double
name|fielddataMaxFrequency
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|fielddataMaxFrequency
operator|=
name|fielddataMaxFrequency
expr_stmt|;
block|}
DECL|method|fielddataMinSegmentSize
specifier|public
name|int
name|fielddataMinSegmentSize
parameter_list|()
block|{
return|return
name|fielddataMinSegmentSize
return|;
block|}
DECL|method|setFielddataMinSegmentSize
specifier|public
name|void
name|setFielddataMinSegmentSize
parameter_list|(
name|int
name|fielddataMinSegmentSize
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|fielddataMinSegmentSize
operator|=
name|fielddataMinSegmentSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|typeName
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|nullValueQuery
specifier|public
name|Query
name|nullValueQuery
parameter_list|()
block|{
if|if
condition|(
name|nullValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|termQuery
argument_list|(
name|nullValue
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fielddataBuilder
specifier|public
name|IndexFieldData
operator|.
name|Builder
name|fielddataBuilder
parameter_list|()
block|{
if|if
condition|(
name|fielddata
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Fielddata is disabled on text fields by default. Set fielddata=true on ["
operator|+
name|name
argument_list|()
operator|+
literal|"] in order to load fielddata in memory by uninverting the inverted index. Note that this can however "
operator|+
literal|"use significant memory."
argument_list|)
throw|;
block|}
return|return
operator|new
name|PagedBytesIndexFieldData
operator|.
name|Builder
argument_list|(
name|fielddataMinFrequency
argument_list|,
name|fielddataMaxFrequency
argument_list|,
name|fielddataMinSegmentSize
argument_list|)
return|;
block|}
block|}
DECL|field|includeInAll
specifier|private
name|Boolean
name|includeInAll
decl_stmt|;
DECL|field|positionIncrementGap
specifier|private
name|int
name|positionIncrementGap
decl_stmt|;
DECL|method|TextFieldMapper
specifier|protected
name|TextFieldMapper
parameter_list|(
name|String
name|simpleName
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|MappedFieldType
name|defaultFieldType
parameter_list|,
name|int
name|positionIncrementGap
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|MultiFields
name|multiFields
parameter_list|,
name|CopyTo
name|copyTo
parameter_list|)
block|{
name|super
argument_list|(
name|simpleName
argument_list|,
name|fieldType
argument_list|,
name|defaultFieldType
argument_list|,
name|indexSettings
argument_list|,
name|multiFields
argument_list|,
name|copyTo
argument_list|)
expr_stmt|;
assert|assert
name|fieldType
operator|.
name|tokenized
argument_list|()
assert|;
assert|assert
name|fieldType
operator|.
name|hasDocValues
argument_list|()
operator|==
literal|false
assert|;
name|this
operator|.
name|positionIncrementGap
operator|=
name|positionIncrementGap
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|protected
name|TextFieldMapper
name|clone
parameter_list|()
block|{
return|return
operator|(
name|TextFieldMapper
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|includeInAll
specifier|public
name|TextFieldMapper
name|includeInAll
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
block|{
if|if
condition|(
name|includeInAll
operator|!=
literal|null
condition|)
block|{
name|TextFieldMapper
name|clone
init|=
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|includeInAll
operator|=
name|includeInAll
expr_stmt|;
return|return
name|clone
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|includeInAllIfNotSet
specifier|public
name|TextFieldMapper
name|includeInAllIfNotSet
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
block|{
if|if
condition|(
name|includeInAll
operator|!=
literal|null
operator|&&
name|this
operator|.
name|includeInAll
operator|==
literal|null
condition|)
block|{
name|TextFieldMapper
name|clone
init|=
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|includeInAll
operator|=
name|includeInAll
expr_stmt|;
return|return
name|clone
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|unsetIncludeInAll
specifier|public
name|TextFieldMapper
name|unsetIncludeInAll
parameter_list|()
block|{
if|if
condition|(
name|includeInAll
operator|!=
literal|null
condition|)
block|{
name|TextFieldMapper
name|clone
init|=
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|includeInAll
operator|=
literal|null
expr_stmt|;
return|return
name|clone
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|()
block|{
return|return
name|this
operator|.
name|positionIncrementGap
return|;
block|}
annotation|@
name|Override
DECL|method|parseCreateField
specifier|protected
name|void
name|parseCreateField
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
specifier|final
name|String
name|value
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|externalValueSet
argument_list|()
condition|)
block|{
name|value
operator|=
name|context
operator|.
name|externalValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|context
operator|.
name|parser
argument_list|()
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|,
name|this
argument_list|)
condition|)
block|{
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|addText
argument_list|(
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|value
argument_list|,
name|fieldType
argument_list|()
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|||
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|value
argument_list|,
name|fieldType
argument_list|()
argument_list|)
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
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
name|CONTENT_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|doMerge
specifier|protected
name|void
name|doMerge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|boolean
name|updateAllTypes
parameter_list|)
block|{
name|super
operator|.
name|doMerge
argument_list|(
name|mergeWith
argument_list|,
name|updateAllTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|includeInAll
operator|=
operator|(
operator|(
name|TextFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|includeInAll
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|TextFieldType
name|fieldType
parameter_list|()
block|{
return|return
operator|(
name|TextFieldType
operator|)
name|super
operator|.
name|fieldType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
name|void
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doXContentBody
argument_list|(
name|builder
argument_list|,
name|includeDefaults
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|doXContentAnalyzers
argument_list|(
name|builder
argument_list|,
name|includeDefaults
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeInAll
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include_in_all"
argument_list|,
name|includeInAll
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeDefaults
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include_in_all"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|positionIncrementGap
operator|!=
name|POSITION_INCREMENT_GAP_USE_ANALYZER
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"position_increment_gap"
argument_list|,
name|positionIncrementGap
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|fielddata
argument_list|()
operator|!=
operator|(
operator|(
name|TextFieldType
operator|)
name|defaultFieldType
operator|)
operator|.
name|fielddata
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fielddata"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|fielddata
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|fielddata
argument_list|()
condition|)
block|{
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|fielddataMinFrequency
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELDDATA_MIN_FREQUENCY
operator|||
name|fieldType
argument_list|()
operator|.
name|fielddataMaxFrequency
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELDDATA_MAX_FREQUENCY
operator|||
name|fieldType
argument_list|()
operator|.
name|fielddataMinSegmentSize
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELDDATA_MIN_SEGMENT_SIZE
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"fielddata_frequency_filter"
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|fielddataMinFrequency
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELDDATA_MIN_FREQUENCY
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"min"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|fielddataMinFrequency
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|fielddataMaxFrequency
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELDDATA_MAX_FREQUENCY
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"max"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|fielddataMaxFrequency
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|fielddataMinSegmentSize
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELDDATA_MIN_SEGMENT_SIZE
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"min_segment_size"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|fielddataMinSegmentSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

