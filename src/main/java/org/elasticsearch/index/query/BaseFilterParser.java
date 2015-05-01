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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Class used during the filter parsers refactoring.  * All filter parsers that have a refactored "fromXContent" method can be changed to extend this instead of {@link BaseFilterParserTemp}.  * Keeps old {@link FilterParser#parse(QueryParseContext)} method as a stub delegating to  * {@link FilterParser#fromXContent(QueryParseContext)} and {@link FilterBuilder#toFilter(QueryParseContext)}}  */
end_comment

begin_class
DECL|class|BaseFilterParser
specifier|public
specifier|abstract
class|class
name|BaseFilterParser
implements|implements
name|FilterParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
specifier|final
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
block|{
return|return
name|fromXContent
argument_list|(
name|parseContext
argument_list|)
operator|.
name|toFilter
argument_list|(
name|parseContext
argument_list|)
return|;
block|}
block|}
end_class

end_unit

