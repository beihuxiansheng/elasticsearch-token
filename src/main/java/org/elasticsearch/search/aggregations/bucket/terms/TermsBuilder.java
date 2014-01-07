begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * Builds a {@code terms} aggregation  */
end_comment

begin_class
DECL|class|TermsBuilder
specifier|public
class|class
name|TermsBuilder
extends|extends
name|ValuesSourceAggregationBuilder
argument_list|<
name|TermsBuilder
argument_list|>
block|{
DECL|field|size
specifier|private
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|shardSize
specifier|private
name|int
name|shardSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|valueType
specifier|private
name|Terms
operator|.
name|ValueType
name|valueType
decl_stmt|;
DECL|field|order
specifier|private
name|Terms
operator|.
name|Order
name|order
decl_stmt|;
DECL|field|includePattern
specifier|private
name|String
name|includePattern
decl_stmt|;
DECL|field|includeFlags
specifier|private
name|int
name|includeFlags
decl_stmt|;
DECL|field|excludePattern
specifier|private
name|String
name|excludePattern
decl_stmt|;
DECL|field|excludeFlags
specifier|private
name|int
name|excludeFlags
decl_stmt|;
DECL|field|executionHint
specifier|private
name|String
name|executionHint
decl_stmt|;
DECL|method|TermsBuilder
specifier|public
name|TermsBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
literal|"terms"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the size - indicating how many term buckets should be returned (defaults to 10)      */
DECL|method|size
specifier|public
name|TermsBuilder
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the shard_size - indicating the number of term buckets each shard will return to the coordinating node (the      * node that coordinates the search execution). The higher the shard size is, the more accurate the results are.      */
DECL|method|shardSize
specifier|public
name|TermsBuilder
name|shardSize
parameter_list|(
name|int
name|shardSize
parameter_list|)
block|{
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Define a regular expression that will determine what terms should be aggregated. The regular expression is based      * on the {@link java.util.regex.Pattern} class.      *      * @see #include(String, int)      */
DECL|method|include
specifier|public
name|TermsBuilder
name|include
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
return|return
name|include
argument_list|(
name|regex
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**      * Define a regular expression that will determine what terms should be aggregated. The regular expression is based      * on the {@link java.util.regex.Pattern} class.      *      * @see java.util.regex.Pattern#compile(String, int)      */
DECL|method|include
specifier|public
name|TermsBuilder
name|include
parameter_list|(
name|String
name|regex
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|includePattern
operator|=
name|regex
expr_stmt|;
name|this
operator|.
name|includeFlags
operator|=
name|flags
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Define a regular expression that will filter out terms that should be excluded from the aggregation. The regular      * expression is based on the {@link java.util.regex.Pattern} class.      *      * @see #exclude(String, int)      */
DECL|method|exclude
specifier|public
name|TermsBuilder
name|exclude
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
return|return
name|exclude
argument_list|(
name|regex
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**      * Define a regular expression that will filter out terms that should be excluded from the aggregation. The regular      * expression is based on the {@link java.util.regex.Pattern} class.      *      * @see java.util.regex.Pattern#compile(String, int)      */
DECL|method|exclude
specifier|public
name|TermsBuilder
name|exclude
parameter_list|(
name|String
name|regex
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|excludePattern
operator|=
name|regex
expr_stmt|;
name|this
operator|.
name|excludeFlags
operator|=
name|flags
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * When using scripts, the value type indicates the types of the values the script is generating.      */
DECL|method|valueType
specifier|public
name|TermsBuilder
name|valueType
parameter_list|(
name|Terms
operator|.
name|ValueType
name|valueType
parameter_list|)
block|{
name|this
operator|.
name|valueType
operator|=
name|valueType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Defines the order in which the buckets will be returned.      */
DECL|method|order
specifier|public
name|TermsBuilder
name|order
parameter_list|(
name|Terms
operator|.
name|Order
name|order
parameter_list|)
block|{
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|executionHint
specifier|public
name|TermsBuilder
name|executionHint
parameter_list|(
name|String
name|executionHint
parameter_list|)
block|{
name|this
operator|.
name|executionHint
operator|=
name|executionHint
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doInternalXContent
specifier|protected
name|XContentBuilder
name|doInternalXContent
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
if|if
condition|(
name|size
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"size"
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardSize
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"shard_size"
argument_list|,
name|shardSize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|valueType
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"value_type"
argument_list|,
name|valueType
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
block|}
if|if
condition|(
name|order
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"order"
argument_list|)
expr_stmt|;
name|order
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includePattern
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|includeFlags
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include"
argument_list|,
name|includePattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"include"
argument_list|)
operator|.
name|field
argument_list|(
literal|"pattern"
argument_list|,
name|includePattern
argument_list|)
operator|.
name|field
argument_list|(
literal|"flags"
argument_list|,
name|includeFlags
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|excludePattern
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|excludeFlags
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"exclude"
argument_list|,
name|excludePattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"exclude"
argument_list|)
operator|.
name|field
argument_list|(
literal|"pattern"
argument_list|,
name|excludePattern
argument_list|)
operator|.
name|field
argument_list|(
literal|"flags"
argument_list|,
name|excludeFlags
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|executionHint
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"execution_hint"
argument_list|,
name|executionHint
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

