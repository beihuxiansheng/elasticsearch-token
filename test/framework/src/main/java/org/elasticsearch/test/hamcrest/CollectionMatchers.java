begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.hamcrest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
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
name|collect
operator|.
name|ImmutableOpenMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|TypeSafeMatcher
import|;
end_import

begin_comment
comment|/**  * Matchers for easier handling of our custom collections,  * for example ImmutableOpenMap  */
end_comment

begin_class
DECL|class|CollectionMatchers
specifier|public
class|class
name|CollectionMatchers
block|{
DECL|class|ImmutableOpenMapHasKeyMatcher
specifier|public
specifier|static
class|class
name|ImmutableOpenMapHasKeyMatcher
extends|extends
name|TypeSafeMatcher
argument_list|<
name|ImmutableOpenMap
argument_list|>
block|{
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|method|ImmutableOpenMapHasKeyMatcher
specifier|public
name|ImmutableOpenMapHasKeyMatcher
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matchesSafely
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
name|ImmutableOpenMap
name|item
parameter_list|)
block|{
return|return
name|item
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|describeMismatchSafely
specifier|public
name|void
name|describeMismatchSafely
parameter_list|(
specifier|final
name|ImmutableOpenMap
name|map
parameter_list|,
specifier|final
name|Description
name|mismatchDescription
parameter_list|)
block|{
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|mismatchDescription
operator|.
name|appendText
argument_list|(
literal|"was empty"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mismatchDescription
operator|.
name|appendText
argument_list|(
literal|" was "
argument_list|)
operator|.
name|appendValue
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|describeTo
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
literal|"ImmutableOpenMap should contain key "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

