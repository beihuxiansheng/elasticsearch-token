begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.section
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|section
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Represents a test section, which is composed of a skip section and multiple executable sections.  */
end_comment

begin_class
DECL|class|TestSection
specifier|public
class|class
name|TestSection
implements|implements
name|Comparable
argument_list|<
name|TestSection
argument_list|>
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|skipSection
specifier|private
name|SkipSection
name|skipSection
decl_stmt|;
DECL|field|executableSections
specifier|private
specifier|final
name|List
argument_list|<
name|ExecutableSection
argument_list|>
name|executableSections
decl_stmt|;
DECL|method|TestSection
specifier|public
name|TestSection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|executableSections
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getSkipSection
specifier|public
name|SkipSection
name|getSkipSection
parameter_list|()
block|{
return|return
name|skipSection
return|;
block|}
DECL|method|setSkipSection
specifier|public
name|void
name|setSkipSection
parameter_list|(
name|SkipSection
name|skipSection
parameter_list|)
block|{
name|this
operator|.
name|skipSection
operator|=
name|skipSection
expr_stmt|;
block|}
DECL|method|getExecutableSections
specifier|public
name|List
argument_list|<
name|ExecutableSection
argument_list|>
name|getExecutableSections
parameter_list|()
block|{
return|return
name|executableSections
return|;
block|}
DECL|method|addExecutableSection
specifier|public
name|void
name|addExecutableSection
parameter_list|(
name|ExecutableSection
name|executableSection
parameter_list|)
block|{
name|this
operator|.
name|executableSections
operator|.
name|add
argument_list|(
name|executableSection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TestSection
name|that
init|=
operator|(
name|TestSection
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|?
operator|!
name|name
operator|.
name|equals
argument_list|(
name|that
operator|.
name|name
argument_list|)
else|:
name|that
operator|.
name|name
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|!=
literal|null
condition|?
name|name
operator|.
name|hashCode
argument_list|()
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|TestSection
name|o
parameter_list|)
block|{
return|return
name|name
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

