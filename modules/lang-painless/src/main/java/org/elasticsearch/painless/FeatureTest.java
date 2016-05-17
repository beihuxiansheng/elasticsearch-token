begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_comment
comment|/** Currently just a dummy class for testing a few features not yet exposed by whitelist! */
end_comment

begin_class
DECL|class|FeatureTest
specifier|public
class|class
name|FeatureTest
block|{
DECL|field|x
specifier|private
name|int
name|x
decl_stmt|;
DECL|field|y
specifier|private
name|int
name|y
decl_stmt|;
comment|/** empty ctor */
DECL|method|FeatureTest
specifier|public
name|FeatureTest
parameter_list|()
block|{     }
comment|/** ctor with params */
DECL|method|FeatureTest
specifier|public
name|FeatureTest
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
block|}
comment|/** getter for x */
DECL|method|getX
specifier|public
name|int
name|getX
parameter_list|()
block|{
return|return
name|x
return|;
block|}
comment|/** setter for x */
DECL|method|setX
specifier|public
name|void
name|setX
parameter_list|(
name|int
name|x
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
block|}
comment|/** getter for y */
DECL|method|getY
specifier|public
name|int
name|getY
parameter_list|()
block|{
return|return
name|y
return|;
block|}
comment|/** setter for y */
DECL|method|setY
specifier|public
name|void
name|setY
parameter_list|(
name|int
name|y
parameter_list|)
block|{
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
block|}
comment|/** static method that returns true */
DECL|method|overloadedStatic
specifier|public
specifier|static
name|boolean
name|overloadedStatic
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** static method that returns what you ask it */
DECL|method|overloadedStatic
specifier|public
specifier|static
name|boolean
name|overloadedStatic
parameter_list|(
name|boolean
name|whatToReturn
parameter_list|)
block|{
return|return
name|whatToReturn
return|;
block|}
block|}
end_class

end_unit

