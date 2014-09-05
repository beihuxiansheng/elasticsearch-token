begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
operator|.
name|BytesReference
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
name|collect
operator|.
name|ImmutableOpenMap
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
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RestRequestTests
specifier|public
class|class
name|RestRequestTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testContext
specifier|public
name|void
name|testContext
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|count
init|=
name|randomInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|request
operator|.
name|putInContext
argument_list|(
literal|"key"
operator|+
name|i
argument_list|,
literal|"val"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|request
operator|.
name|isContextEmpty
argument_list|()
argument_list|,
name|is
argument_list|(
name|count
operator|==
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|request
operator|.
name|contextSize
argument_list|()
argument_list|,
name|is
argument_list|(
name|count
argument_list|)
argument_list|)
expr_stmt|;
name|ImmutableOpenMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|ctx
init|=
name|request
operator|.
name|getContext
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|request
operator|.
name|hasInContext
argument_list|(
literal|"key"
operator|+
name|i
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|request
operator|.
name|getFromContext
argument_list|(
literal|"key"
operator|+
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"val"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"key"
operator|+
name|i
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"val"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Request
specifier|public
specifier|static
class|class
name|Request
extends|extends
name|RestRequest
block|{
annotation|@
name|Override
DECL|method|method
specifier|public
name|Method
name|method
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|uri
specifier|public
name|String
name|uri
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|rawPath
specifier|public
name|String
name|rawPath
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasContent
specifier|public
name|boolean
name|hasContent
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|contentUnsafe
specifier|public
name|boolean
name|contentUnsafe
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|content
specifier|public
name|BytesReference
name|content
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|header
specifier|public
name|String
name|header
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|headers
specifier|public
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|headers
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasParam
specifier|public
name|boolean
name|hasParam
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|params
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

