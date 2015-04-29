begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.rescore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|rescore
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|ScoreDoc
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
name|TopDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|component
operator|.
name|AbstractComponent
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
name|Inject
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
name|search
operator|.
name|SearchParseElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchPhase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|RescorePhase
specifier|public
class|class
name|RescorePhase
extends|extends
name|AbstractComponent
implements|implements
name|SearchPhase
block|{
annotation|@
name|Inject
DECL|method|RescorePhase
specifier|public
name|RescorePhase
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseElements
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchParseElement
argument_list|>
name|parseElements
parameter_list|()
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|SearchParseElement
argument_list|>
name|parseElements
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|parseElements
operator|.
name|put
argument_list|(
literal|"rescore"
argument_list|,
operator|new
name|RescoreParseElement
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parseElements
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|preProcess
specifier|public
name|void
name|preProcess
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
try|try
block|{
name|TopDocs
name|topDocs
init|=
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
decl_stmt|;
for|for
control|(
name|RescoreSearchContext
name|ctx
range|:
name|context
operator|.
name|rescore
argument_list|()
control|)
block|{
name|topDocs
operator|=
name|ctx
operator|.
name|rescorer
argument_list|()
operator|.
name|rescore
argument_list|(
name|topDocs
argument_list|,
name|context
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|size
argument_list|()
operator|<
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
name|ScoreDoc
index|[]
name|hits
init|=
operator|new
name|ScoreDoc
index|[
name|context
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|topDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|hits
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|topDocs
operator|=
operator|new
name|TopDocs
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|,
name|hits
argument_list|,
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|(
name|topDocs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Rescore Phase Failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

