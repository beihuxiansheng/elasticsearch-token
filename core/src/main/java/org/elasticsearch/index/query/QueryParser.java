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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Defines a query parser that is able to read and parse a query object in {@link org.elasticsearch.common.xcontent.XContent}  * format and create an internal object representing the query, implementing {@link QueryBuilder}, which can be streamed to other nodes.  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|QueryParser
specifier|public
interface|interface
name|QueryParser
parameter_list|<
name|QB
extends|extends
name|QueryBuilder
parameter_list|<
name|QB
parameter_list|>
parameter_list|>
block|{
comment|/**      * Creates a new {@link QueryBuilder} from the query held by the {@link QueryParseContext}      * in {@link org.elasticsearch.common.xcontent.XContent} format      *      * @param parseContext      *            the input parse context. The state on the parser contained in      *            this context will be changed as a side effect of this method      *            call      * @return the new QueryBuilder      */
DECL|method|fromXContent
name|QB
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @return an empty {@link QueryBuilder} instance for this parser that can be used for deserialization      */
DECL|method|getBuilderPrototype
specifier|default
name|QB
name|getBuilderPrototype
parameter_list|()
block|{
comment|// TODO remove this when nothing implements it
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_interface

end_unit

