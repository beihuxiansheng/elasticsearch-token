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
comment|/**  * This class with method impl is an intermediate step in the filter parsers refactoring.  * Provides a fromXContent default implementation for filter parsers that don't have yet a  * specific fromXContent implementation that returns a FilterBuilder.  */
end_comment

begin_comment
comment|//norelease to be removed once all filters are moved over to extend BaseFilterParser
end_comment

begin_class
DECL|class|BaseFilterParserTemp
specifier|public
specifier|abstract
class|class
name|BaseFilterParserTemp
implements|implements
name|FilterParser
block|{
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
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
block|{
name|Filter
name|filter
init|=
name|parse
argument_list|(
name|parseContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterWrappingFilterBuilder
argument_list|(
name|filter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

