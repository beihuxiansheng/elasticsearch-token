begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch.source
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|source
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
name|XContentFactory
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
name|fetch
operator|.
name|FetchSubPhase
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
name|InternalSearchHit
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
DECL|class|FetchSourceSubPhase
specifier|public
class|class
name|FetchSourceSubPhase
implements|implements
name|FetchSubPhase
block|{
annotation|@
name|Inject
DECL|method|FetchSourceSubPhase
specifier|public
name|FetchSourceSubPhase
parameter_list|()
block|{      }
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
literal|"_source"
argument_list|,
operator|new
name|FetchSourceParseElement
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
DECL|method|hitsExecutionNeeded
specifier|public
name|boolean
name|hitsExecutionNeeded
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hitsExecute
specifier|public
name|void
name|hitsExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|InternalSearchHit
index|[]
name|hits
parameter_list|)
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|hitExecutionNeeded
specifier|public
name|boolean
name|hitExecutionNeeded
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|sourceRequested
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hitExecute
specifier|public
name|void
name|hitExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|HitContext
name|hitContext
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
name|FetchSourceContext
name|fetchSourceContext
init|=
name|context
operator|.
name|fetchSourceContext
argument_list|()
decl_stmt|;
assert|assert
name|fetchSourceContext
operator|.
name|fetchSource
argument_list|()
assert|;
if|if
condition|(
name|fetchSourceContext
operator|.
name|includes
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|&&
name|fetchSourceContext
operator|.
name|excludes
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|sourceRef
argument_list|(
name|context
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|internalSourceRef
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|Object
name|value
init|=
name|context
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|filter
argument_list|(
name|fetchSourceContext
operator|.
name|includes
argument_list|()
argument_list|,
name|fetchSourceContext
operator|.
name|excludes
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|context
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|sourceContentType
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|sourceRef
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
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
literal|"Error filtering source"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

