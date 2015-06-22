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
name|client
operator|.
name|Requests
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
name|bytes
operator|.
name|BytesReference
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
parameter_list|>
extends|extends
name|NamedWriteable
argument_list|<
name|QB
argument_list|>
extends|,
name|ToXContent
block|{
comment|/**      * Validate the query.      * @return a {@link QueryValidationException} containing error messages, {@code null} if query is valid.      * e.g. if fields that are needed to create the lucene query are missing.      */
DECL|method|validate
name|QueryValidationException
name|validate
parameter_list|()
function_decl|;
comment|/**      * Converts this QueryBuilder to a lucene {@link Query}.      * Returns<tt>null</tt> if this query should be ignored in the context of      * parent queries.      *      * @param parseContext additional information needed to construct the queries      * @return the {@link Query} or<tt>null</tt> if this query should be ignored upstream      * @throws QueryParsingException      * @throws IOException      */
DECL|method|toQuery
name|Query
name|toQuery
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|QueryParsingException
throws|,
name|IOException
function_decl|;
comment|/**      * Returns a {@link org.elasticsearch.common.bytes.BytesReference}      * containing the {@link ToXContent} output in binary format.      * Builds the request based on the default {@link XContentType}, either {@link Requests#CONTENT_TYPE} or provided as a constructor argument      */
comment|//norelease once we move to serializing queries over the wire in Streamable format, this method shouldn't be needed anymore
DECL|method|buildAsBytes
name|BytesReference
name|buildAsBytes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

