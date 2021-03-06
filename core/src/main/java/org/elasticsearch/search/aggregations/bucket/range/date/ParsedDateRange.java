begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range.date
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
operator|.
name|date
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
name|xcontent
operator|.
name|ObjectParser
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
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|range
operator|.
name|ParsedRange
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
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
DECL|class|ParsedDateRange
specifier|public
class|class
name|ParsedDateRange
extends|extends
name|ParsedRange
block|{
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|DateRangeAggregationBuilder
operator|.
name|NAME
return|;
block|}
DECL|field|PARSER
specifier|private
specifier|static
name|ObjectParser
argument_list|<
name|ParsedDateRange
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|ParsedDateRange
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|true
argument_list|,
name|ParsedDateRange
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|declareParsedRangeFields
argument_list|(
name|PARSER
argument_list|,
name|parser
lambda|->
name|ParsedBucket
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
literal|false
argument_list|)
argument_list|,
name|parser
lambda|->
name|ParsedBucket
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|ParsedDateRange
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ParsedDateRange
name|aggregation
init|=
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|aggregation
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|aggregation
return|;
block|}
DECL|class|ParsedBucket
specifier|public
specifier|static
class|class
name|ParsedBucket
extends|extends
name|ParsedRange
operator|.
name|ParsedBucket
block|{
annotation|@
name|Override
DECL|method|getFrom
specifier|public
name|Object
name|getFrom
parameter_list|()
block|{
return|return
name|doubleAsDateTime
argument_list|(
name|from
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTo
specifier|public
name|Object
name|getTo
parameter_list|()
block|{
return|return
name|doubleAsDateTime
argument_list|(
name|to
argument_list|)
return|;
block|}
DECL|method|doubleAsDateTime
specifier|private
specifier|static
name|DateTime
name|doubleAsDateTime
parameter_list|(
name|Double
name|d
parameter_list|)
block|{
if|if
condition|(
name|d
operator|==
literal|null
operator|||
name|Double
operator|.
name|isInfinite
argument_list|(
name|d
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|DateTime
argument_list|(
name|d
operator|.
name|longValue
argument_list|()
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|)
return|;
block|}
DECL|method|fromXContent
specifier|static
name|ParsedBucket
name|fromXContent
parameter_list|(
specifier|final
name|XContentParser
name|parser
parameter_list|,
specifier|final
name|boolean
name|keyed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parseRangeBucketXContent
argument_list|(
name|parser
argument_list|,
name|ParsedBucket
operator|::
operator|new
argument_list|,
name|keyed
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

