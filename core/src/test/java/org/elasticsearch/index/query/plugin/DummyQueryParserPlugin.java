begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.plugin
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|plugin
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
name|IndexSearcher
import|;
end_import

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
name|MatchAllDocsQuery
import|;
end_import

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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Weight
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
name|inject
operator|.
name|Module
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
name|settings
operator|.
name|Settings
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
name|XContentBuilder
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
name|XContentParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|query
operator|.
name|IndicesQueriesModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|AbstractPlugin
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

begin_class
DECL|class|DummyQueryParserPlugin
specifier|public
class|class
name|DummyQueryParserPlugin
extends|extends
name|AbstractPlugin
block|{
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"dummy"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"dummy query"
return|;
block|}
annotation|@
name|Override
DECL|method|processModule
specifier|public
name|void
name|processModule
parameter_list|(
name|Module
name|module
parameter_list|)
block|{
if|if
condition|(
name|module
operator|instanceof
name|IndicesQueriesModule
condition|)
block|{
name|IndicesQueriesModule
name|indicesQueriesModule
init|=
operator|(
name|IndicesQueriesModule
operator|)
name|module
decl_stmt|;
name|indicesQueriesModule
operator|.
name|addQuery
argument_list|(
name|DummyQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|settings
specifier|public
name|Settings
name|settings
parameter_list|()
block|{
return|return
name|Settings
operator|.
name|EMPTY
return|;
block|}
DECL|class|DummyQueryBuilder
specifier|public
specifier|static
class|class
name|DummyQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|DummyQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"dummy"
decl_stmt|;
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|NAME
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
block|}
DECL|class|DummyQueryParser
specifier|public
specifier|static
class|class
name|DummyQueryParser
extends|extends
name|BaseQueryParserTemp
block|{
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|DummyQueryBuilder
operator|.
name|NAME
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryShardException
block|{
name|XContentParser
operator|.
name|Token
name|token
init|=
name|context
operator|.
name|parseContext
argument_list|()
operator|.
name|parser
argument_list|()
operator|.
name|nextToken
argument_list|()
decl_stmt|;
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
assert|;
return|return
operator|new
name|DummyQuery
argument_list|(
name|context
operator|.
name|isFilter
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|DummyQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
operator|new
name|DummyQueryBuilder
argument_list|()
return|;
block|}
block|}
DECL|class|DummyQuery
specifier|public
specifier|static
class|class
name|DummyQuery
extends|extends
name|Query
block|{
DECL|field|isFilter
specifier|public
specifier|final
name|boolean
name|isFilter
decl_stmt|;
DECL|field|matchAllDocsQuery
specifier|private
specifier|final
name|Query
name|matchAllDocsQuery
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
DECL|method|DummyQuery
specifier|private
name|DummyQuery
parameter_list|(
name|boolean
name|isFilter
parameter_list|)
block|{
name|this
operator|.
name|isFilter
operator|=
name|isFilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|matchAllDocsQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

