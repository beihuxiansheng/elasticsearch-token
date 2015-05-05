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

begin_comment
comment|/**  * Base interface for all classes producing lucene queries.  * Supports conversion to BytesReference and creation of lucene Query objects.  */
end_comment

begin_interface
DECL|interface|QueryBuilder
specifier|public
interface|interface
name|QueryBuilder
extends|extends
name|ToXContent
block|{
DECL|method|buildAsBytes
name|BytesReference
name|buildAsBytes
parameter_list|()
function_decl|;
DECL|method|buildAsBytes
name|BytesReference
name|buildAsBytes
parameter_list|(
name|XContentType
name|contentType
parameter_list|)
function_decl|;
comment|/**      * Converts this QueryBuilder to a lucene {@link Query}      * @param parseContext additional information needed to construct the queries      * @return the {@link Query}      * @throws QueryParsingException      * @throws IOException      */
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
comment|/**      * Validate the query.      * @return a {@link QueryValidationException} containing error messages, {@code null} if query is valid.      * e.g. if fields that are needed to create the lucene query are missing.      */
DECL|method|validate
name|QueryValidationException
name|validate
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

