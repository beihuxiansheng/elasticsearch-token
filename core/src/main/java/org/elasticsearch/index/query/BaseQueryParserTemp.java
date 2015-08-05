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
comment|/**  * This class with method impl is an intermediate step in the query parsers refactoring.  * Provides a fromXContent default implementation for query parsers that don't have yet a  * specific fromXContent implementation that returns a QueryBuilder.  */
end_comment

begin_comment
comment|//norelease to be removed once all queries are moved over to extend BaseQueryParser
end_comment

begin_class
DECL|class|BaseQueryParserTemp
specifier|public
specifier|abstract
class|class
name|BaseQueryParserTemp
implements|implements
name|QueryParser
block|{
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|QueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|Query
name|query
init|=
name|parse
argument_list|(
name|parseContext
operator|.
name|shardContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|QueryWrappingQueryBuilder
argument_list|(
name|query
argument_list|)
return|;
block|}
block|}
end_class

end_unit

