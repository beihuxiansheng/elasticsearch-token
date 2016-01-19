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
name|io
operator|.
name|stream
operator|.
name|NamedWriteable
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_interface
DECL|interface|QueryBuilder
specifier|public
interface|interface
name|QueryBuilder
parameter_list|<
name|QB
extends|extends
name|QueryBuilder
parameter_list|<
name|QB
parameter_list|>
parameter_list|>
extends|extends
name|NamedWriteable
argument_list|<
name|QB
argument_list|>
extends|,
name|ToXContent
block|{
comment|/**      * Converts this QueryBuilder to a lucene {@link Query}.      * Returns<tt>null</tt> if this query should be ignored in the context of      * parent queries.      *      * @param context additional information needed to construct the queries      * @return the {@link Query} or<tt>null</tt> if this query should be ignored upstream      */
DECL|method|toQuery
name|Query
name|toQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Converts this QueryBuilder to an unscored lucene {@link Query} that acts as a filter.      * Returns<tt>null</tt> if this query should be ignored in the context of      * parent queries.      *      * @param context additional information needed to construct the queries      * @return the {@link Query} or<tt>null</tt> if this query should be ignored upstream      */
DECL|method|toFilter
name|Query
name|toFilter
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Sets the arbitrary name to be assigned to the query (see named queries).      */
DECL|method|queryName
name|QB
name|queryName
parameter_list|(
name|String
name|queryName
parameter_list|)
function_decl|;
comment|/**      * Returns the arbitrary name assigned to the query (see named queries).      */
DECL|method|queryName
name|String
name|queryName
parameter_list|()
function_decl|;
comment|/**      * Returns the boost for this query.      */
DECL|method|boost
name|float
name|boost
parameter_list|()
function_decl|;
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
name|QB
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
function_decl|;
comment|/**      * Returns the name that identifies uniquely the query      */
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

