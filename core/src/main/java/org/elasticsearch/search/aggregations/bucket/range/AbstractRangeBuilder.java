begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|range
package|;
end_package

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
name|StreamInputReader
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
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|range
operator|.
name|RangeAggregator
operator|.
name|Range
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
name|aggregations
operator|.
name|support
operator|.
name|ValuesSource
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
name|aggregations
operator|.
name|support
operator|.
name|ValuesSourceAggregationBuilder
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
name|List
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

begin_class
DECL|class|AbstractRangeBuilder
specifier|public
specifier|abstract
class|class
name|AbstractRangeBuilder
parameter_list|<
name|AB
extends|extends
name|AbstractRangeBuilder
parameter_list|<
name|AB
parameter_list|,
name|R
parameter_list|>
parameter_list|,
name|R
extends|extends
name|Range
parameter_list|>
extends|extends
name|ValuesSourceAggregationBuilder
argument_list|<
name|ValuesSource
operator|.
name|Numeric
argument_list|,
name|AB
argument_list|>
block|{
DECL|field|rangeFactory
specifier|protected
specifier|final
name|InternalRange
operator|.
name|Factory
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|rangeFactory
decl_stmt|;
DECL|field|ranges
specifier|protected
name|List
argument_list|<
name|R
argument_list|>
name|ranges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|keyed
specifier|protected
name|boolean
name|keyed
init|=
literal|false
decl_stmt|;
DECL|method|AbstractRangeBuilder
specifier|protected
name|AbstractRangeBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|InternalRange
operator|.
name|Factory
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|rangeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|rangeFactory
operator|.
name|type
argument_list|()
argument_list|,
name|rangeFactory
operator|.
name|getValueSourceType
argument_list|()
argument_list|,
name|rangeFactory
operator|.
name|getValueType
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|rangeFactory
operator|=
name|rangeFactory
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|AbstractRangeBuilder
specifier|protected
name|AbstractRangeBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|InternalRange
operator|.
name|Factory
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|rangeFactory
parameter_list|,
name|StreamInputReader
argument_list|<
name|R
argument_list|>
name|rangeReader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|rangeFactory
operator|.
name|type
argument_list|()
argument_list|,
name|rangeFactory
operator|.
name|getValueSourceType
argument_list|()
argument_list|,
name|rangeFactory
operator|.
name|getValueType
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|rangeFactory
operator|=
name|rangeFactory
expr_stmt|;
name|ranges
operator|=
name|in
operator|.
name|readList
argument_list|(
name|rangeReader
argument_list|)
expr_stmt|;
name|keyed
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|innerWriteTo
specifier|protected
name|void
name|innerWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|ranges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Range
name|range
range|:
name|ranges
control|)
block|{
name|range
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|keyed
argument_list|)
expr_stmt|;
block|}
DECL|method|addRange
specifier|public
name|AB
name|addRange
parameter_list|(
name|R
name|range
parameter_list|)
block|{
if|if
condition|(
name|range
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[range] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|ranges
operator|.
name|add
argument_list|(
name|range
argument_list|)
expr_stmt|;
return|return
operator|(
name|AB
operator|)
name|this
return|;
block|}
DECL|method|ranges
specifier|public
name|List
argument_list|<
name|R
argument_list|>
name|ranges
parameter_list|()
block|{
return|return
name|ranges
return|;
block|}
DECL|method|keyed
specifier|public
name|AB
name|keyed
parameter_list|(
name|boolean
name|keyed
parameter_list|)
block|{
name|this
operator|.
name|keyed
operator|=
name|keyed
expr_stmt|;
return|return
operator|(
name|AB
operator|)
name|this
return|;
block|}
DECL|method|keyed
specifier|public
name|boolean
name|keyed
parameter_list|()
block|{
return|return
name|keyed
return|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
name|XContentBuilder
name|doXContentBody
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
name|field
argument_list|(
name|RangeAggregator
operator|.
name|RANGES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|RangeAggregator
operator|.
name|KEYED_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|keyed
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|innerHashCode
specifier|protected
name|int
name|innerHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|ranges
argument_list|,
name|keyed
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|innerEquals
specifier|protected
name|boolean
name|innerEquals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|AbstractRangeBuilder
argument_list|<
name|AB
argument_list|,
name|R
argument_list|>
name|other
init|=
operator|(
name|AbstractRangeBuilder
argument_list|<
name|AB
argument_list|,
name|R
argument_list|>
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|ranges
argument_list|,
name|other
operator|.
name|ranges
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|keyed
argument_list|,
name|other
operator|.
name|keyed
argument_list|)
return|;
block|}
block|}
end_class

end_unit

