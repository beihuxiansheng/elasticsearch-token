begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|elasticsearch
operator|.
name|index
operator|.
name|AbstractIndexComponent
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
name|Index
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
name|analysis
operator|.
name|AnalysisService
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
name|cache
operator|.
name|IndexCache
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
name|mapper
operator|.
name|MapperService
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
name|xcontent
operator|.
name|XContentIndexQueryParser
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
name|settings
operator|.
name|IndexSettings
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
name|similarity
operator|.
name|SimilarityService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gcommon
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|guice
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|ImmutableSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gcommon
operator|.
name|collect
operator|.
name|Maps
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|IndexQueryParserService
specifier|public
class|class
name|IndexQueryParserService
extends|extends
name|AbstractIndexComponent
block|{
DECL|class|Defaults
specifier|public
specifier|static
specifier|final
class|class
name|Defaults
block|{
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT
init|=
literal|"default"
decl_stmt|;
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"index.queryparser.types"
decl_stmt|;
block|}
DECL|field|defaultIndexQueryParser
specifier|private
specifier|final
name|IndexQueryParser
name|defaultIndexQueryParser
decl_stmt|;
DECL|field|indexQueryParsers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexQueryParser
argument_list|>
name|indexQueryParsers
decl_stmt|;
DECL|method|IndexQueryParserService
specifier|public
name|IndexQueryParserService
parameter_list|(
name|Index
name|index
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|IndexCache
name|indexCache
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
name|mapperService
argument_list|,
name|indexCache
argument_list|,
name|analysisService
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|IndexQueryParserService
annotation|@
name|Inject
specifier|public
name|IndexQueryParserService
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|IndexCache
name|indexCache
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|,
annotation|@
name|Nullable
name|SimilarityService
name|similarityService
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|IndexQueryParserFactory
argument_list|>
name|indexQueryParsersFactories
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|queryParserGroupSettings
decl_stmt|;
if|if
condition|(
name|indexSettings
operator|!=
literal|null
condition|)
block|{
name|queryParserGroupSettings
operator|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
name|Defaults
operator|.
name|PREFIX
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryParserGroupSettings
operator|=
name|newHashMap
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|IndexQueryParser
argument_list|>
name|qparsers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexQueryParsersFactories
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|IndexQueryParserFactory
argument_list|>
name|entry
range|:
name|indexQueryParsersFactories
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|qparserName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Settings
name|qparserSettings
init|=
name|queryParserGroupSettings
operator|.
name|get
argument_list|(
name|qparserName
argument_list|)
decl_stmt|;
name|qparsers
operator|.
name|put
argument_list|(
name|qparserName
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|create
argument_list|(
name|qparserName
argument_list|,
name|qparserSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|qparsers
operator|.
name|containsKey
argument_list|(
name|Defaults
operator|.
name|DEFAULT
argument_list|)
condition|)
block|{
name|IndexQueryParser
name|defaultQueryParser
init|=
operator|new
name|XContentIndexQueryParser
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|mapperService
argument_list|,
name|indexCache
argument_list|,
name|analysisService
argument_list|,
name|similarityService
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Defaults
operator|.
name|DEFAULT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|qparsers
operator|.
name|put
argument_list|(
name|Defaults
operator|.
name|DEFAULT
argument_list|,
name|defaultQueryParser
argument_list|)
expr_stmt|;
block|}
name|indexQueryParsers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|qparsers
argument_list|)
expr_stmt|;
name|defaultIndexQueryParser
operator|=
name|indexQueryParser
argument_list|(
name|Defaults
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|indexQueryParser
specifier|public
name|IndexQueryParser
name|indexQueryParser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|indexQueryParsers
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|defaultIndexQueryParser
specifier|public
name|IndexQueryParser
name|defaultIndexQueryParser
parameter_list|()
block|{
return|return
name|defaultIndexQueryParser
return|;
block|}
block|}
end_class

end_unit

