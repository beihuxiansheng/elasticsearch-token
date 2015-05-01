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
name|Filter
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
name|Nullable
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
DECL|interface|FilterParser
specifier|public
interface|interface
name|FilterParser
block|{
comment|/**      * The names this filter is registered under.      */
DECL|method|names
name|String
index|[]
name|names
parameter_list|()
function_decl|;
comment|/**      * Parses the into a filter from the current parser location. Will be at "START_OBJECT" location,      * and should end when the token is at the matching "END_OBJECT".      *<p/>      * The parser should return null value when it should be ignored, regardless under which context      * it is. For example, an and filter with "and []" (no clauses), should be ignored regardless if      * it exists within a must clause or a must_not bool clause (that is why returning MATCH_ALL will      * not be good, since it will not match anything when returned within a must_not clause).      */
comment|//norelease can be removed in favour of fromXContent once search requests can be parsed on the coordinating node
annotation|@
name|Nullable
DECL|method|parse
name|Filter
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
function_decl|;
comment|/**      * Creates a new {@link FilterBuilder} from the filter held by the {@link QueryParseContext}      * in {@link org.elasticsearch.common.xcontent.XContent} format      *      * @param parseContext      *            the input parse context. The state on the parser contained in      *            this context will be changed as a side effect of this method      *            call      * @return the new FilterBuilder      * @throws IOException      * @throws QueryParsingException      */
DECL|method|fromXContent
name|FilterBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
function_decl|;
block|}
end_interface

end_unit

