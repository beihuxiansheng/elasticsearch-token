begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.terms
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
name|terms
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
name|text
operator|.
name|Text
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
name|ToXContent
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
name|support
operator|.
name|ScriptValueType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|Terms
specifier|public
interface|interface
name|Terms
extends|extends
name|Aggregation
extends|,
name|Iterable
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
block|{
DECL|enum|ValueType
specifier|static
enum|enum
name|ValueType
block|{
DECL|enum constant|STRING
name|STRING
parameter_list|(
name|ScriptValueType
operator|.
name|STRING
parameter_list|)
operator|,
DECL|enum constant|LONG
constructor|LONG(ScriptValueType.LONG
block|)
enum|,
DECL|enum constant|DOUBLE
name|DOUBLE
parameter_list|(
name|ScriptValueType
operator|.
name|DOUBLE
parameter_list|)
constructor_decl|;
DECL|field|scriptValueType
specifier|final
name|ScriptValueType
name|scriptValueType
decl_stmt|;
DECL|method|ValueType
specifier|private
name|ValueType
parameter_list|(
name|ScriptValueType
name|scriptValueType
parameter_list|)
block|{
name|this
operator|.
name|scriptValueType
operator|=
name|scriptValueType
expr_stmt|;
block|}
DECL|method|resolveType
specifier|static
name|ValueType
name|resolveType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
literal|"string"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|STRING
return|;
block|}
if|if
condition|(
literal|"double"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"float"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|DOUBLE
return|;
block|}
if|if
condition|(
literal|"long"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"integer"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"short"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
literal|"byte"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|LONG
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_interface

begin_interface
DECL|interface|Bucket
specifier|static
interface|interface
name|Bucket
extends|extends
name|Comparable
argument_list|<
name|Bucket
argument_list|>
extends|,
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
name|Bucket
block|{
DECL|method|getKey
name|Text
name|getKey
parameter_list|()
function_decl|;
DECL|method|getKeyAsNumber
name|Number
name|getKeyAsNumber
parameter_list|()
function_decl|;
block|}
end_interface

begin_function_decl
DECL|method|buckets
name|Collection
argument_list|<
name|Bucket
argument_list|>
name|buckets
parameter_list|()
function_decl|;
end_function_decl

begin_function_decl
DECL|method|getByTerm
name|Bucket
name|getByTerm
parameter_list|(
name|String
name|term
parameter_list|)
function_decl|;
end_function_decl

begin_comment
comment|/**      *      */
end_comment

begin_class
DECL|class|Order
specifier|static
specifier|abstract
class|class
name|Order
implements|implements
name|ToXContent
block|{
comment|/**          * Order by the (higher) count of each term.          */
DECL|field|COUNT_DESC
specifier|public
specifier|static
specifier|final
name|Order
name|COUNT_DESC
init|=
operator|new
name|InternalOrder
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|,
literal|"_count"
argument_list|,
literal|false
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
name|long
name|i
init|=
name|o2
operator|.
name|getDocCount
argument_list|()
operator|-
name|o1
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|i
operator|=
name|o2
operator|.
name|compareTo
argument_list|(
name|o1
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|i
operator|=
name|System
operator|.
name|identityHashCode
argument_list|(
name|o2
argument_list|)
operator|-
name|System
operator|.
name|identityHashCode
argument_list|(
name|o1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|i
operator|>
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**          * Order by the (lower) count of each term.          */
DECL|field|COUNT_ASC
specifier|public
specifier|static
specifier|final
name|Order
name|COUNT_ASC
init|=
operator|new
name|InternalOrder
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|,
literal|"_count"
argument_list|,
literal|true
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
return|return
operator|-
name|COUNT_DESC
operator|.
name|comparator
argument_list|()
operator|.
name|compare
argument_list|(
name|o1
argument_list|,
name|o2
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**          * Order by the terms.          */
DECL|field|TERM_DESC
specifier|public
specifier|static
specifier|final
name|Order
name|TERM_DESC
init|=
operator|new
name|InternalOrder
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|,
literal|"_term"
argument_list|,
literal|false
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
return|return
name|o2
operator|.
name|compareTo
argument_list|(
name|o1
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**          * Order by the terms.          */
DECL|field|TERM_ASC
specifier|public
specifier|static
specifier|final
name|Order
name|TERM_ASC
init|=
operator|new
name|InternalOrder
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|,
literal|"_term"
argument_list|,
literal|true
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
return|return
operator|-
name|TERM_DESC
operator|.
name|comparator
argument_list|()
operator|.
name|compare
argument_list|(
name|o1
argument_list|,
name|o2
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**          * Creates a bucket ordering strategy which sorts buckets based on a single-valued calc get          *          * @param   aggregationName the name of the get          * @param   asc             The direction of the order (ascending or descending)          */
DECL|method|aggregation
specifier|public
specifier|static
name|InternalOrder
name|aggregation
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|boolean
name|asc
parameter_list|)
block|{
return|return
operator|new
name|InternalOrder
operator|.
name|Aggregation
argument_list|(
name|aggregationName
argument_list|,
literal|null
argument_list|,
name|asc
argument_list|)
return|;
block|}
comment|/**          * Creates a bucket ordering strategy which sorts buckets based on a multi-valued calc get          *          * @param   aggregationName the name of the get          * @param   valueName       The name of the value of the multi-value get by which the sorting will be applied          * @param   asc             The direction of the order (ascending or descending)          */
DECL|method|aggregation
specifier|public
specifier|static
name|InternalOrder
name|aggregation
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|String
name|valueName
parameter_list|,
name|boolean
name|asc
parameter_list|)
block|{
return|return
operator|new
name|InternalOrder
operator|.
name|Aggregation
argument_list|(
name|aggregationName
argument_list|,
name|valueName
argument_list|,
name|asc
argument_list|)
return|;
block|}
DECL|method|comparator
specifier|protected
specifier|abstract
name|Comparator
argument_list|<
name|Bucket
argument_list|>
name|comparator
parameter_list|()
function_decl|;
block|}
end_class

unit|}
end_unit

