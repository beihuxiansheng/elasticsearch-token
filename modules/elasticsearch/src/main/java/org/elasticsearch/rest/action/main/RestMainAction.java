begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.main
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|main
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestXContentBuilder
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
name|Classes
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
name|concurrent
operator|.
name|jsr166y
operator|.
name|ThreadLocalRandom
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
name|Settings
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
name|util
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
name|util
operator|.
name|xcontent
operator|.
name|XContentType
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
name|xcontent
operator|.
name|builder
operator|.
name|XContentBuilder
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
name|List
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
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|RestMainAction
specifier|public
class|class
name|RestMainAction
extends|extends
name|BaseRestHandler
block|{
DECL|field|rootNode
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rootNode
decl_stmt|;
DECL|field|quotesSize
specifier|private
specifier|final
name|int
name|quotesSize
decl_stmt|;
DECL|method|RestMainAction
annotation|@
name|Inject
specifier|public
name|RestMainAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rootNode
decl_stmt|;
name|int
name|quotesSize
decl_stmt|;
try|try
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|Classes
operator|.
name|getDefaultClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"org/elasticsearch/rest/action/main/quotes.json"
argument_list|)
argument_list|)
decl_stmt|;
name|rootNode
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
name|List
name|arrayNode
init|=
operator|(
name|List
operator|)
name|rootNode
operator|.
name|get
argument_list|(
literal|"quotes"
argument_list|)
decl_stmt|;
name|quotesSize
operator|=
name|arrayNode
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|rootNode
operator|=
literal|null
expr_stmt|;
name|quotesSize
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|this
operator|.
name|rootNode
operator|=
name|rootNode
expr_stmt|;
name|this
operator|.
name|quotesSize
operator|=
name|quotesSize
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|handleRequest
annotation|@
name|Override
specifier|public
name|void
name|handleRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|RestXContentBuilder
operator|.
name|restContentBuilder
argument_list|(
name|request
argument_list|)
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"ok"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
literal|"version"
argument_list|)
operator|.
name|field
argument_list|(
literal|"number"
argument_list|,
name|Version
operator|.
name|number
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"date"
argument_list|,
name|Version
operator|.
name|date
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"snapshot_build"
argument_list|,
name|Version
operator|.
name|snapshotBuild
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"tagline"
argument_list|,
literal|"You Know, for Search"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"cover"
argument_list|,
literal|"DON'T PANIC"
argument_list|)
expr_stmt|;
if|if
condition|(
name|rootNode
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"quote"
argument_list|)
expr_stmt|;
name|List
name|arrayNode
init|=
operator|(
name|List
operator|)
name|rootNode
operator|.
name|get
argument_list|(
literal|"quotes"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|quoteNode
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|arrayNode
operator|.
name|get
argument_list|(
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|(
name|quotesSize
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"book"
argument_list|,
name|quoteNode
operator|.
name|get
argument_list|(
literal|"book"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"chapter"
argument_list|,
name|quoteNode
operator|.
name|get
argument_list|(
literal|"chapter"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|textNodes
init|=
operator|(
name|List
operator|)
name|quoteNode
operator|.
name|get
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
comment|//                builder.startArray("text");
comment|//                for (JsonNode textNode : textNodes) {
comment|//                    builder.value(textNode.getValueAsText());
comment|//                }
comment|//                builder.endArray();
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
name|textNode
range|:
name|textNodes
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"text"
operator|+
operator|(
operator|++
name|index
operator|)
argument_list|,
name|textNode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentRestResponse
argument_list|(
name|request
argument_list|,
name|RestResponse
operator|.
name|Status
operator|.
name|OK
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentThrowableRestResponse
argument_list|(
name|request
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send response"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

