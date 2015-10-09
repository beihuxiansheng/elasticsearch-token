begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_comment
comment|/**  * The result of parsing a query.  */
end_comment

begin_class
DECL|class|ParsedQuery
specifier|public
class|class
name|ParsedQuery
block|{
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|namedFilters
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|namedFilters
decl_stmt|;
comment|/**      * Store the query and filters.      *      * @param query      *            the query      * @param namedFilters      *            an immutable Map containing the named filters. Good callers      *            use emptyMap or unmodifiableMap and copy the source to make      *            sure this is immutable.      */
DECL|method|ParsedQuery
specifier|public
name|ParsedQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|namedFilters
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|namedFilters
operator|=
name|namedFilters
expr_stmt|;
block|}
DECL|method|ParsedQuery
specifier|public
name|ParsedQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|ParsedQuery
name|parsedQuery
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|namedFilters
operator|=
name|parsedQuery
operator|.
name|namedFilters
expr_stmt|;
block|}
DECL|method|ParsedQuery
specifier|public
name|ParsedQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|namedFilters
operator|=
name|emptyMap
argument_list|()
expr_stmt|;
block|}
comment|/**      * The query parsed.      */
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|this
operator|.
name|query
return|;
block|}
DECL|method|namedFilters
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Query
argument_list|>
name|namedFilters
parameter_list|()
block|{
return|return
name|namedFilters
return|;
block|}
DECL|method|parsedMatchAllQuery
specifier|public
specifier|static
name|ParsedQuery
name|parsedMatchAllQuery
parameter_list|()
block|{
return|return
operator|new
name|ParsedQuery
argument_list|(
name|Queries
operator|.
name|newMatchAllQuery
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

