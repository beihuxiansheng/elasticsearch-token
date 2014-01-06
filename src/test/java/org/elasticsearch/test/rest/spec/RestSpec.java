begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elasticsearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.spec
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|spec
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
name|Lists
import|;
end_import

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
name|Maps
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
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|support
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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

begin_comment
comment|/**  * Holds the elasticsearch REST spec  */
end_comment

begin_class
DECL|class|RestSpec
specifier|public
class|class
name|RestSpec
block|{
DECL|field|restApiMap
name|Map
argument_list|<
name|String
argument_list|,
name|RestApi
argument_list|>
name|restApiMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|RestSpec
specifier|private
name|RestSpec
parameter_list|()
block|{     }
DECL|method|addApi
name|void
name|addApi
parameter_list|(
name|RestApi
name|restApi
parameter_list|)
block|{
if|if
condition|(
literal|"info"
operator|.
name|equals
argument_list|(
name|restApi
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|//info and ping should really be two different api in the rest spec
comment|//info (GET|HEAD /) needs to be manually split into 1) info: GET /  2) ping: HEAD /
name|restApiMap
operator|.
name|put
argument_list|(
literal|"info"
argument_list|,
operator|new
name|RestApi
argument_list|(
name|restApi
argument_list|,
literal|"info"
argument_list|,
literal|"GET"
argument_list|)
argument_list|)
expr_stmt|;
name|restApiMap
operator|.
name|put
argument_list|(
literal|"ping"
argument_list|,
operator|new
name|RestApi
argument_list|(
name|restApi
argument_list|,
literal|"ping"
argument_list|,
literal|"HEAD"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"get"
operator|.
name|equals
argument_list|(
name|restApi
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|//get_source endpoint shouldn't be present in the rest spec for the get api
comment|//as get_source is already a separate api
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|restApi
operator|.
name|getPaths
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|endsWith
argument_list|(
literal|"/_source"
argument_list|)
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
name|restApiMap
operator|.
name|put
argument_list|(
name|restApi
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|RestApi
argument_list|(
name|restApi
argument_list|,
name|paths
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|restApiMap
operator|.
name|put
argument_list|(
name|restApi
operator|.
name|getName
argument_list|()
argument_list|,
name|restApi
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getApi
specifier|public
name|RestApi
name|getApi
parameter_list|(
name|String
name|api
parameter_list|)
block|{
return|return
name|restApiMap
operator|.
name|get
argument_list|(
name|api
argument_list|)
return|;
block|}
comment|/**      * Parses the complete set of REST spec available under the provided directories      */
DECL|method|parseFrom
specifier|public
specifier|static
name|RestSpec
name|parseFrom
parameter_list|(
name|String
name|optionalPathPrefix
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
name|RestSpec
name|restSpec
init|=
operator|new
name|RestSpec
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
for|for
control|(
name|File
name|jsonFile
range|:
name|FileUtils
operator|.
name|findJsonSpec
argument_list|(
name|optionalPathPrefix
argument_list|,
name|path
argument_list|)
control|)
block|{
name|XContentParser
name|parser
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|jsonFile
argument_list|)
argument_list|)
decl_stmt|;
name|RestApi
name|restApi
init|=
operator|new
name|RestApiParser
argument_list|()
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|restSpec
operator|.
name|addApi
argument_list|(
name|restApi
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|restSpec
return|;
block|}
block|}
end_class

end_unit

