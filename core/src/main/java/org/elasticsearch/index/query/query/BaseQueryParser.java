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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Class used during the query parsers refactoring. Will be removed once we can parse search requests on the coordinating node.  * All query parsers that have a refactored "fromXContent" method can be changed to extend this instead of {@link BaseQueryParserTemp}.  * Keeps old {@link QueryParser#parse(QueryParseContext)} method as a stub delegating to  * {@link QueryParser#fromXContent(QueryParseContext)} and {@link QueryBuilder#toQuery(QueryParseContext)}}  */
end_comment

begin_comment
comment|//norelease needs to be removed once we parse search requests on the coordinating node, as the parse method is not needed anymore at that point.
end_comment

begin_class
DECL|class|BaseQueryParser
specifier|public
specifier|abstract
class|class
name|BaseQueryParser
implements|implements
name|QueryParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
specifier|final
name|Query
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
name|toQuery
argument_list|(
name|parseContext
argument_list|)
return|;
block|}
block|}
end_class

end_unit

