begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.functionscore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|ParsingException
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|FieldValueFactorFunction
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
name|function
operator|.
name|ScoreFunction
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
name|fielddata
operator|.
name|IndexNumericFieldData
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
name|query
operator|.
name|QueryParseContext
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
name|query
operator|.
name|QueryShardContext
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
name|Locale
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

begin_comment
comment|/**  * Builder to construct {@code field_value_factor} functions for a function  * score query.  */
end_comment

begin_class
DECL|class|FieldValueFactorFunctionBuilder
specifier|public
class|class
name|FieldValueFactorFunctionBuilder
extends|extends
name|ScoreFunctionBuilder
argument_list|<
name|FieldValueFactorFunctionBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"field_value_factor"
decl_stmt|;
DECL|field|DEFAULT_MODIFIER
specifier|public
specifier|static
specifier|final
name|FieldValueFactorFunction
operator|.
name|Modifier
name|DEFAULT_MODIFIER
init|=
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|NONE
decl_stmt|;
DECL|field|DEFAULT_FACTOR
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_FACTOR
init|=
literal|1
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|factor
specifier|private
name|float
name|factor
init|=
name|DEFAULT_FACTOR
decl_stmt|;
DECL|field|missing
specifier|private
name|Double
name|missing
decl_stmt|;
DECL|field|modifier
specifier|private
name|FieldValueFactorFunction
operator|.
name|Modifier
name|modifier
init|=
name|DEFAULT_MODIFIER
decl_stmt|;
DECL|method|FieldValueFactorFunctionBuilder
specifier|public
name|FieldValueFactorFunctionBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field_value_factor: field must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|field
operator|=
name|fieldName
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|FieldValueFactorFunctionBuilder
specifier|public
name|FieldValueFactorFunctionBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|field
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|factor
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|missing
operator|=
name|in
operator|.
name|readOptionalDouble
argument_list|()
expr_stmt|;
name|modifier
operator|=
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|readFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
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
name|field
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|factor
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalDouble
argument_list|(
name|missing
argument_list|)
expr_stmt|;
name|modifier
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|this
operator|.
name|field
return|;
block|}
DECL|method|factor
specifier|public
name|FieldValueFactorFunctionBuilder
name|factor
parameter_list|(
name|float
name|boostFactor
parameter_list|)
block|{
name|this
operator|.
name|factor
operator|=
name|boostFactor
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|factor
specifier|public
name|float
name|factor
parameter_list|()
block|{
return|return
name|this
operator|.
name|factor
return|;
block|}
comment|/**      * Value used instead of the field value for documents that don't have that field defined.      */
DECL|method|missing
specifier|public
name|FieldValueFactorFunctionBuilder
name|missing
parameter_list|(
name|double
name|missing
parameter_list|)
block|{
name|this
operator|.
name|missing
operator|=
name|missing
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|missing
specifier|public
name|Double
name|missing
parameter_list|()
block|{
return|return
name|this
operator|.
name|missing
return|;
block|}
DECL|method|modifier
specifier|public
name|FieldValueFactorFunctionBuilder
name|modifier
parameter_list|(
name|FieldValueFactorFunction
operator|.
name|Modifier
name|modifier
parameter_list|)
block|{
if|if
condition|(
name|modifier
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field_value_factor: modifier must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|modifier
operator|=
name|modifier
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|modifier
specifier|public
name|FieldValueFactorFunction
operator|.
name|Modifier
name|modifier
parameter_list|()
block|{
return|return
name|this
operator|.
name|modifier
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|public
name|void
name|doXContent
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
name|builder
operator|.
name|startObject
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"factor"
argument_list|,
name|factor
argument_list|)
expr_stmt|;
if|if
condition|(
name|missing
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"missing"
argument_list|,
name|missing
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"modifier"
argument_list|,
name|modifier
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|FieldValueFactorFunctionBuilder
name|functionBuilder
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|field
argument_list|,
name|functionBuilder
operator|.
name|field
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|factor
argument_list|,
name|functionBuilder
operator|.
name|factor
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|missing
argument_list|,
name|functionBuilder
operator|.
name|missing
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|modifier
argument_list|,
name|functionBuilder
operator|.
name|modifier
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|this
operator|.
name|field
argument_list|,
name|this
operator|.
name|factor
argument_list|,
name|this
operator|.
name|missing
argument_list|,
name|this
operator|.
name|modifier
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doToFunction
specifier|protected
name|ScoreFunction
name|doToFunction
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
block|{
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|getMapperService
argument_list|()
operator|.
name|fullName
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|IndexNumericFieldData
name|fieldData
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|missing
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Unable to find a field mapper for field ["
operator|+
name|field
operator|+
literal|"]. No 'missing' value defined."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|fieldData
operator|=
name|context
operator|.
name|getForField
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FieldValueFactorFunction
argument_list|(
name|field
argument_list|,
name|factor
argument_list|,
name|modifier
argument_list|,
name|missing
argument_list|,
name|fieldData
argument_list|)
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|FieldValueFactorFunctionBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParsingException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
name|float
name|boostFactor
init|=
name|FieldValueFactorFunctionBuilder
operator|.
name|DEFAULT_FACTOR
decl_stmt|;
name|FieldValueFactorFunction
operator|.
name|Modifier
name|modifier
init|=
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|NONE
decl_stmt|;
name|Double
name|missing
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|field
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"factor"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boostFactor
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"modifier"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|modifier
operator|=
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"missing"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|missing
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
name|NAME
operator|+
literal|" query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"factor"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|&&
operator|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
operator|||
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|)
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|NAME
operator|+
literal|"] field 'factor' does not support lists or objects"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|NAME
operator|+
literal|"] required field 'field' missing"
argument_list|)
throw|;
block|}
name|FieldValueFactorFunctionBuilder
name|fieldValueFactorFunctionBuilder
init|=
operator|new
name|FieldValueFactorFunctionBuilder
argument_list|(
name|field
argument_list|)
operator|.
name|factor
argument_list|(
name|boostFactor
argument_list|)
operator|.
name|modifier
argument_list|(
name|modifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|missing
operator|!=
literal|null
condition|)
block|{
name|fieldValueFactorFunctionBuilder
operator|.
name|missing
argument_list|(
name|missing
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldValueFactorFunctionBuilder
return|;
block|}
block|}
end_class

end_unit
