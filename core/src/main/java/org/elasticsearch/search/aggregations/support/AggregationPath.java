begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
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
name|Strings
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
name|Aggregation
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
name|AggregationExecutionException
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
name|Aggregator
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
name|HasAggregations
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
name|SingleBucketAggregation
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
name|SingleBucketAggregator
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
name|metrics
operator|.
name|InternalNumericMetricsAggregation
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
name|metrics
operator|.
name|NumericMetricsAggregator
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

begin_comment
comment|/**  * A path that can be used to sort/order buckets (in some multi-bucket aggregations, eg terms&amp; histogram) based on  * sub-aggregations. The path may point to either a single-bucket aggregation or a metrics aggregation. If the path  * points to a single-bucket aggregation, the sort will be applied based on the {@code doc_count} of the bucket. If this  * path points to a metrics aggregation, if it's a single-value metrics (eg. avg, max, min, etc..) the sort will be  * applied on that single value. If it points to a multi-value metrics, the path should point out what metric should be  * the sort-by value.  *<p>  * The path has the following form:  *<center>{@code<aggregation_name>['>'<aggregation_name>*]['.'<metric_name>]}</center>  *<p>  * Examples:  *  *<ul>  *<li>  *         {@code agg1>agg2>agg3} - where agg1, agg2 and agg3 are all single-bucket aggs (eg filter, nested, missing, etc..). In  *                                  this case, the order will be based on the number of documents under {@code agg3}.  *</li>  *<li>  *         {@code agg1>agg2>agg3} - where agg1 and agg2 are both single-bucket aggs and agg3 is a single-value metrics agg (eg avg, max, min, etc..).  *                                  In this case, the order will be based on the value of {@code agg3}.  *</li>  *<li>  *         {@code agg1>agg2>agg3.avg} - where agg1 and agg2 are both single-bucket aggs and agg3 is a multi-value metrics agg (eg stats, extended_stats, etc...).  *                                  In this case, the order will be based on the avg value of {@code agg3}.  *</li>  *</ul>  *  */
end_comment

begin_class
DECL|class|AggregationPath
specifier|public
class|class
name|AggregationPath
block|{
DECL|field|AGG_DELIM
specifier|private
specifier|final
specifier|static
name|String
name|AGG_DELIM
init|=
literal|">"
decl_stmt|;
DECL|method|parse
specifier|public
specifier|static
name|AggregationPath
name|parse
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
index|[]
name|elements
init|=
name|Strings
operator|.
name|tokenizeToStringArray
argument_list|(
name|path
argument_list|,
name|AGG_DELIM
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PathElement
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|elements
operator|.
name|length
argument_list|)
decl_stmt|;
name|String
index|[]
name|tuple
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|elements
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|element
init|=
name|elements
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|elements
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|int
name|index
init|=
name|element
operator|.
name|lastIndexOf
argument_list|(
literal|'['
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|index
operator|==
literal|0
operator|||
name|index
operator|>
name|element
operator|.
name|length
argument_list|()
operator|-
literal|3
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid path element ["
operator|+
name|element
operator|+
literal|"] in path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|element
operator|.
name|charAt
argument_list|(
name|element
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|']'
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid path element ["
operator|+
name|element
operator|+
literal|"] in path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|PathElement
argument_list|(
name|element
argument_list|,
name|element
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
argument_list|,
name|element
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|,
name|element
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|index
operator|=
name|element
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|PathElement
argument_list|(
name|element
argument_list|,
name|element
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|index
operator|==
literal|0
operator|||
name|index
operator|>
name|element
operator|.
name|length
argument_list|()
operator|-
literal|2
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid path element ["
operator|+
name|element
operator|+
literal|"] in path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|tuple
operator|=
name|split
argument_list|(
name|element
argument_list|,
name|index
argument_list|,
name|tuple
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|PathElement
argument_list|(
name|element
argument_list|,
name|tuple
index|[
literal|0
index|]
argument_list|,
name|tuple
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|index
init|=
name|element
operator|.
name|lastIndexOf
argument_list|(
literal|'['
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|index
operator|==
literal|0
operator|||
name|index
operator|>
name|element
operator|.
name|length
argument_list|()
operator|-
literal|3
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid path element ["
operator|+
name|element
operator|+
literal|"] in path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|element
operator|.
name|charAt
argument_list|(
name|element
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|']'
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid path element ["
operator|+
name|element
operator|+
literal|"] in path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|PathElement
argument_list|(
name|element
argument_list|,
name|element
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
argument_list|,
name|element
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|,
name|element
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|PathElement
argument_list|(
name|element
argument_list|,
name|element
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|AggregationPath
argument_list|(
name|tokens
argument_list|)
return|;
block|}
DECL|class|PathElement
specifier|public
specifier|static
class|class
name|PathElement
block|{
DECL|field|fullName
specifier|private
specifier|final
name|String
name|fullName
decl_stmt|;
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|key
specifier|public
specifier|final
name|String
name|key
decl_stmt|;
DECL|method|PathElement
specifier|public
name|PathElement
parameter_list|(
name|String
name|fullName
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|fullName
operator|=
name|fullName
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
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
name|PathElement
name|token
init|=
operator|(
name|PathElement
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|?
operator|!
name|key
operator|.
name|equals
argument_list|(
name|token
operator|.
name|key
argument_list|)
else|:
name|token
operator|.
name|key
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|token
operator|.
name|name
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
name|name
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
operator|(
name|key
operator|!=
literal|null
condition|?
name|key
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|fullName
return|;
block|}
block|}
DECL|field|pathElements
specifier|private
specifier|final
name|List
argument_list|<
name|PathElement
argument_list|>
name|pathElements
decl_stmt|;
DECL|method|AggregationPath
specifier|public
name|AggregationPath
parameter_list|(
name|List
argument_list|<
name|PathElement
argument_list|>
name|tokens
parameter_list|)
block|{
name|this
operator|.
name|pathElements
operator|=
name|tokens
expr_stmt|;
if|if
condition|(
name|tokens
operator|==
literal|null
operator|||
name|tokens
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid path ["
operator|+
name|this
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Strings
operator|.
name|arrayToDelimitedString
argument_list|(
name|pathElements
operator|.
name|toArray
argument_list|()
argument_list|,
name|AGG_DELIM
argument_list|)
return|;
block|}
DECL|method|lastPathElement
specifier|public
name|PathElement
name|lastPathElement
parameter_list|()
block|{
return|return
name|pathElements
operator|.
name|get
argument_list|(
name|pathElements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|getPathElements
specifier|public
name|List
argument_list|<
name|PathElement
argument_list|>
name|getPathElements
parameter_list|()
block|{
return|return
name|this
operator|.
name|pathElements
return|;
block|}
DECL|method|getPathElementsAsStringList
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getPathElementsAsStringList
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|stringPathElements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PathElement
name|pathElement
range|:
name|this
operator|.
name|pathElements
control|)
block|{
name|stringPathElements
operator|.
name|add
argument_list|(
name|pathElement
operator|.
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|pathElement
operator|.
name|key
operator|!=
literal|null
condition|)
block|{
name|stringPathElements
operator|.
name|add
argument_list|(
name|pathElement
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|stringPathElements
return|;
block|}
DECL|method|subPath
specifier|public
name|AggregationPath
name|subPath
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|List
argument_list|<
name|PathElement
argument_list|>
name|subTokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pathElements
operator|.
name|subList
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|AggregationPath
argument_list|(
name|subTokens
argument_list|)
return|;
block|}
comment|/**      * Resolves the value pointed by this path given an aggregations root      *      * @param root  The root that serves as a point of reference for this path      * @return      The resolved value      */
DECL|method|resolveValue
specifier|public
name|double
name|resolveValue
parameter_list|(
name|HasAggregations
name|root
parameter_list|)
block|{
name|HasAggregations
name|parent
init|=
name|root
decl_stmt|;
name|double
name|value
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pathElements
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|AggregationPath
operator|.
name|PathElement
name|token
init|=
name|pathElements
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Aggregation
name|agg
init|=
name|parent
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
name|token
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|agg
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid order path ["
operator|+
name|this
operator|+
literal|"]. Cannot find aggregation named ["
operator|+
name|token
operator|.
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|agg
operator|instanceof
name|SingleBucketAggregation
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|key
operator|!=
literal|null
operator|&&
operator|!
name|token
operator|.
name|key
operator|.
name|equals
argument_list|(
literal|"doc_count"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid order path ["
operator|+
name|this
operator|+
literal|"]. Unknown value key ["
operator|+
name|token
operator|.
name|key
operator|+
literal|"] for single-bucket aggregation ["
operator|+
name|token
operator|.
name|name
operator|+
literal|"]. Either use [doc_count] as key or drop the key all together"
argument_list|)
throw|;
block|}
name|parent
operator|=
operator|(
name|SingleBucketAggregation
operator|)
name|agg
expr_stmt|;
name|value
operator|=
operator|(
operator|(
name|SingleBucketAggregation
operator|)
name|agg
operator|)
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
continue|continue;
block|}
comment|// the agg can only be a metrics agg, and a metrics agg must be at the end of the path
if|if
condition|(
name|i
operator|!=
name|pathElements
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid order path ["
operator|+
name|this
operator|+
literal|"]. Metrics aggregations cannot have sub-aggregations (at ["
operator|+
name|token
operator|+
literal|">"
operator|+
name|pathElements
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|agg
operator|instanceof
name|InternalNumericMetricsAggregation
operator|.
name|SingleValue
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|key
operator|!=
literal|null
operator|&&
operator|!
name|token
operator|.
name|key
operator|.
name|equals
argument_list|(
literal|"value"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid order path ["
operator|+
name|this
operator|+
literal|"]. Unknown value key ["
operator|+
name|token
operator|.
name|key
operator|+
literal|"] for single-value metric aggregation ["
operator|+
name|token
operator|.
name|name
operator|+
literal|"]. Either use [value] as key or drop the key all together"
argument_list|)
throw|;
block|}
name|parent
operator|=
literal|null
expr_stmt|;
name|value
operator|=
operator|(
operator|(
name|InternalNumericMetricsAggregation
operator|.
name|SingleValue
operator|)
name|agg
operator|)
operator|.
name|value
argument_list|()
expr_stmt|;
continue|continue;
block|}
comment|// we're left with a multi-value metric agg
if|if
condition|(
name|token
operator|.
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid order path ["
operator|+
name|this
operator|+
literal|"]. Missing value key in ["
operator|+
name|token
operator|+
literal|"] which refers to a multi-value metric aggregation"
argument_list|)
throw|;
block|}
name|parent
operator|=
literal|null
expr_stmt|;
name|value
operator|=
operator|(
operator|(
name|InternalNumericMetricsAggregation
operator|.
name|MultiValue
operator|)
name|agg
operator|)
operator|.
name|value
argument_list|(
name|token
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|/**      * Resolves the aggregator pointed by this path using the given root as a point of reference.      *      * @param root      The point of reference of this path      * @return          The aggregator pointed by this path starting from the given aggregator as a point of reference      */
DECL|method|resolveAggregator
specifier|public
name|Aggregator
name|resolveAggregator
parameter_list|(
name|Aggregator
name|root
parameter_list|)
block|{
name|Aggregator
name|aggregator
init|=
name|root
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pathElements
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|AggregationPath
operator|.
name|PathElement
name|token
init|=
name|pathElements
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|aggregator
operator|=
name|aggregator
operator|.
name|subAggregator
argument_list|(
name|token
operator|.
name|name
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|aggregator
operator|instanceof
name|SingleBucketAggregator
operator|&&
name|i
operator|<=
name|pathElements
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
operator|||
operator|(
name|aggregator
operator|instanceof
name|NumericMetricsAggregator
operator|&&
name|i
operator|==
name|pathElements
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
operator|:
literal|"this should be picked up before aggregation execution - on validate"
assert|;
block|}
return|return
name|aggregator
return|;
block|}
comment|/**      * Resolves the topmost aggregator pointed by this path using the given root as a point of reference.      *      * @param root      The point of reference of this path      * @return          The first child aggregator of the root pointed by this path      */
DECL|method|resolveTopmostAggregator
specifier|public
name|Aggregator
name|resolveTopmostAggregator
parameter_list|(
name|Aggregator
name|root
parameter_list|)
block|{
name|AggregationPath
operator|.
name|PathElement
name|token
init|=
name|pathElements
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Aggregator
name|aggregator
init|=
name|root
operator|.
name|subAggregator
argument_list|(
name|token
operator|.
name|name
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|aggregator
operator|instanceof
name|SingleBucketAggregator
operator|)
operator|||
operator|(
name|aggregator
operator|instanceof
name|NumericMetricsAggregator
operator|)
operator|:
literal|"this should be picked up before aggregation execution - on validate"
assert|;
return|return
name|aggregator
return|;
block|}
comment|/**      * Validates this path over the given aggregator as a point of reference.      *      * @param root  The point of reference of this path      */
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|(
name|Aggregator
name|root
parameter_list|)
block|{
name|Aggregator
name|aggregator
init|=
name|root
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pathElements
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|aggregator
operator|=
name|aggregator
operator|.
name|subAggregator
argument_list|(
name|pathElements
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|aggregator
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid term-aggregator order path ["
operator|+
name|this
operator|+
literal|"]. Unknown aggregation ["
operator|+
name|pathElements
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|i
operator|<
name|pathElements
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
comment|// we're in the middle of the path, so the aggregator can only be a single-bucket aggregator
if|if
condition|(
operator|!
operator|(
name|aggregator
operator|instanceof
name|SingleBucketAggregator
operator|)
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid terms aggregation order path ["
operator|+
name|this
operator|+
literal|"]. Terms buckets can only be sorted on a sub-aggregator path "
operator|+
literal|"that is built out of zero or more single-bucket aggregations within the path and a final "
operator|+
literal|"single-bucket or a metrics aggregation at the path end. Sub-path ["
operator|+
name|subPath
argument_list|(
literal|0
argument_list|,
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|"] points to non single-bucket aggregation"
argument_list|)
throw|;
block|}
if|if
condition|(
name|pathElements
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|key
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid terms aggregation order path ["
operator|+
name|this
operator|+
literal|"]. Terms buckets can only be sorted on a sub-aggregator path "
operator|+
literal|"that is built out of zero or more single-bucket aggregations within the path and a "
operator|+
literal|"final single-bucket or a metrics aggregation at the path end. Sub-path ["
operator|+
name|subPath
argument_list|(
literal|0
argument_list|,
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|"] points to non single-bucket aggregation"
argument_list|)
throw|;
block|}
block|}
block|}
name|boolean
name|singleBucket
init|=
name|aggregator
operator|instanceof
name|SingleBucketAggregator
decl_stmt|;
if|if
condition|(
operator|!
name|singleBucket
operator|&&
operator|!
operator|(
name|aggregator
operator|instanceof
name|NumericMetricsAggregator
operator|)
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid terms aggregation order path ["
operator|+
name|this
operator|+
literal|"]. Terms buckets can only be sorted on a sub-aggregator path "
operator|+
literal|"that is built out of zero or more single-bucket aggregations within the path and a final "
operator|+
literal|"single-bucket or a metrics aggregation at the path end."
argument_list|)
throw|;
block|}
name|AggregationPath
operator|.
name|PathElement
name|lastToken
init|=
name|lastPathElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|singleBucket
condition|)
block|{
if|if
condition|(
name|lastToken
operator|.
name|key
operator|!=
literal|null
operator|&&
operator|!
literal|"doc_count"
operator|.
name|equals
argument_list|(
name|lastToken
operator|.
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid terms aggregation order path ["
operator|+
name|this
operator|+
literal|"]. Ordering on a single-bucket aggregation can only be done on its doc_count. "
operator|+
literal|"Either drop the key (a la \""
operator|+
name|lastToken
operator|.
name|name
operator|+
literal|"\") or change it to \"doc_count\" (a la \""
operator|+
name|lastToken
operator|.
name|name
operator|+
literal|".doc_count\")"
argument_list|)
throw|;
block|}
return|return;
comment|// perfectly valid to sort on single-bucket aggregation (will be sored on its doc_count)
block|}
if|if
condition|(
name|aggregator
operator|instanceof
name|NumericMetricsAggregator
operator|.
name|SingleValue
condition|)
block|{
if|if
condition|(
name|lastToken
operator|.
name|key
operator|!=
literal|null
operator|&&
operator|!
literal|"value"
operator|.
name|equals
argument_list|(
name|lastToken
operator|.
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid terms aggregation order path ["
operator|+
name|this
operator|+
literal|"]. Ordering on a single-value metrics aggregation can only be done on its value. "
operator|+
literal|"Either drop the key (a la \""
operator|+
name|lastToken
operator|.
name|name
operator|+
literal|"\") or change it to \"value\" (a la \""
operator|+
name|lastToken
operator|.
name|name
operator|+
literal|".value\")"
argument_list|)
throw|;
block|}
return|return;
comment|// perfectly valid to sort on single metric aggregation (will be sorted on its associated value)
block|}
comment|// the aggregator must be of a multi-value metrics type
if|if
condition|(
name|lastToken
operator|.
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid terms aggregation order path ["
operator|+
name|this
operator|+
literal|"]. When ordering on a multi-value metrics aggregation a metric name must be specified"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
operator|(
name|NumericMetricsAggregator
operator|.
name|MultiValue
operator|)
name|aggregator
operator|)
operator|.
name|hasMetric
argument_list|(
name|lastToken
operator|.
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Invalid terms aggregation order path ["
operator|+
name|this
operator|+
literal|"]. Unknown metric name ["
operator|+
name|lastToken
operator|.
name|key
operator|+
literal|"] on multi-value metrics aggregation ["
operator|+
name|lastToken
operator|.
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|split
specifier|private
specifier|static
name|String
index|[]
name|split
parameter_list|(
name|String
name|toSplit
parameter_list|,
name|int
name|index
parameter_list|,
name|String
index|[]
name|result
parameter_list|)
block|{
name|result
index|[
literal|0
index|]
operator|=
name|toSplit
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|result
index|[
literal|1
index|]
operator|=
name|toSplit
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

