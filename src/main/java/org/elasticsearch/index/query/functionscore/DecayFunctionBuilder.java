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
name|xcontent
operator|.
name|XContentBuilder
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

begin_class
DECL|class|DecayFunctionBuilder
specifier|public
specifier|abstract
class|class
name|DecayFunctionBuilder
implements|implements
name|ScoreFunctionBuilder
block|{
DECL|field|ORIGIN
specifier|protected
specifier|static
specifier|final
name|String
name|ORIGIN
init|=
literal|"origin"
decl_stmt|;
DECL|field|SCALE
specifier|protected
specifier|static
specifier|final
name|String
name|SCALE
init|=
literal|"scale"
decl_stmt|;
DECL|field|DECAY
specifier|protected
specifier|static
specifier|final
name|String
name|DECAY
init|=
literal|"decay"
decl_stmt|;
DECL|field|OFFSET
specifier|protected
specifier|static
specifier|final
name|String
name|OFFSET
init|=
literal|"offset"
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|origin
specifier|private
name|Object
name|origin
decl_stmt|;
DECL|field|scale
specifier|private
name|Object
name|scale
decl_stmt|;
DECL|field|decay
specifier|private
name|double
name|decay
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|offset
specifier|private
name|Object
name|offset
decl_stmt|;
DECL|method|DecayFunctionBuilder
specifier|public
name|DecayFunctionBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|origin
parameter_list|,
name|Object
name|scale
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
name|this
operator|.
name|scale
operator|=
name|scale
expr_stmt|;
block|}
DECL|method|setDecay
specifier|public
name|DecayFunctionBuilder
name|setDecay
parameter_list|(
name|double
name|decay
parameter_list|)
block|{
if|if
condition|(
name|decay
operator|<=
literal|0
operator|||
name|decay
operator|>=
literal|1.0
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"scale weight parameter must be in range 0..1!"
argument_list|)
throw|;
block|}
name|this
operator|.
name|decay
operator|=
name|decay
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOffset
specifier|public
name|DecayFunctionBuilder
name|setOffset
parameter_list|(
name|Object
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
return|return
name|this
return|;
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
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|origin
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|ORIGIN
argument_list|,
name|origin
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|SCALE
argument_list|,
name|scale
argument_list|)
expr_stmt|;
if|if
condition|(
name|decay
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|DECAY
argument_list|,
name|decay
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|offset
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|OFFSET
argument_list|,
name|offset
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
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

