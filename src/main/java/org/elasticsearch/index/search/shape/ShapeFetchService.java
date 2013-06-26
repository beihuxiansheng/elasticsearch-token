begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.shape
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|shape
package|;
end_package

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalStateException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
operator|.
name|GetRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
operator|.
name|GetResponse
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
name|geo
operator|.
name|GeoJSONShapeParser
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
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Service which retrieves pre-indexed Shapes from another index  */
end_comment

begin_class
DECL|class|ShapeFetchService
specifier|public
class|class
name|ShapeFetchService
extends|extends
name|AbstractComponent
block|{
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
annotation|@
name|Inject
DECL|method|ShapeFetchService
specifier|public
name|ShapeFetchService
parameter_list|(
name|Client
name|client
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
comment|/**      * Fetches the Shape with the given ID in the given type and index.      *      * @param id         ID of the Shape to fetch      * @param type       Index type where the Shape is indexed      * @param index      Index where the Shape is indexed      * @param shapeField Name of the field in the Shape Document where the Shape itself is located      * @return Shape with the given ID      * @throws IOException Can be thrown while parsing the Shape Document and extracting the Shape      */
DECL|method|fetch
specifier|public
name|Shape
name|fetch
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|shapeField
parameter_list|)
throws|throws
name|IOException
block|{
name|GetResponse
name|response
init|=
name|client
operator|.
name|get
argument_list|(
operator|new
name|GetRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|preference
argument_list|(
literal|"_local"
argument_list|)
operator|.
name|operationThreaded
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|response
operator|.
name|isExists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Shape with ID ["
operator|+
name|id
operator|+
literal|"] in type ["
operator|+
name|type
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|response
operator|.
name|getSourceAsBytesRef
argument_list|()
argument_list|)
expr_stmt|;
name|XContentParser
operator|.
name|Token
name|currentToken
decl_stmt|;
while|while
condition|(
operator|(
name|currentToken
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|currentToken
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
if|if
condition|(
name|shapeField
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
return|return
name|GeoJSONShapeParser
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
return|;
block|}
else|else
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
block|}
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Shape with name ["
operator|+
name|id
operator|+
literal|"] found but missing "
operator|+
name|shapeField
operator|+
literal|" field"
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

