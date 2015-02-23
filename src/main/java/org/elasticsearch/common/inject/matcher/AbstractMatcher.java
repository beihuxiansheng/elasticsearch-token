begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.matcher
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|matcher
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Implements {@code and()} and {@code or()}.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|AbstractMatcher
specifier|public
specifier|abstract
class|class
name|AbstractMatcher
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Matcher
argument_list|<
name|T
argument_list|>
block|{
annotation|@
name|Override
DECL|method|and
specifier|public
name|Matcher
argument_list|<
name|T
argument_list|>
name|and
parameter_list|(
specifier|final
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|other
parameter_list|)
block|{
return|return
operator|new
name|AndMatcher
argument_list|<>
argument_list|(
name|this
argument_list|,
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|or
specifier|public
name|Matcher
argument_list|<
name|T
argument_list|>
name|or
parameter_list|(
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|other
parameter_list|)
block|{
return|return
operator|new
name|OrMatcher
argument_list|<>
argument_list|(
name|this
argument_list|,
name|other
argument_list|)
return|;
block|}
DECL|class|AndMatcher
specifier|private
specifier|static
class|class
name|AndMatcher
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractMatcher
argument_list|<
name|T
argument_list|>
implements|implements
name|Serializable
block|{
DECL|field|a
DECL|field|b
specifier|private
specifier|final
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|a
decl_stmt|,
name|b
decl_stmt|;
DECL|method|AndMatcher
specifier|public
name|AndMatcher
parameter_list|(
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|a
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|b
parameter_list|)
block|{
name|this
operator|.
name|a
operator|=
name|a
expr_stmt|;
name|this
operator|.
name|b
operator|=
name|b
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|(
name|T
name|t
parameter_list|)
block|{
return|return
name|a
operator|.
name|matches
argument_list|(
name|t
argument_list|)
operator|&&
name|b
operator|.
name|matches
argument_list|(
name|t
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|other
operator|instanceof
name|AndMatcher
operator|&&
operator|(
operator|(
name|AndMatcher
operator|)
name|other
operator|)
operator|.
name|a
operator|.
name|equals
argument_list|(
name|a
argument_list|)
operator|&&
operator|(
operator|(
name|AndMatcher
operator|)
name|other
operator|)
operator|.
name|b
operator|.
name|equals
argument_list|(
name|b
argument_list|)
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
literal|41
operator|*
operator|(
name|a
operator|.
name|hashCode
argument_list|()
operator|^
name|b
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"and("
operator|+
name|a
operator|+
literal|", "
operator|+
name|b
operator|+
literal|")"
return|;
block|}
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0
decl_stmt|;
block|}
DECL|class|OrMatcher
specifier|private
specifier|static
class|class
name|OrMatcher
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractMatcher
argument_list|<
name|T
argument_list|>
implements|implements
name|Serializable
block|{
DECL|field|a
DECL|field|b
specifier|private
specifier|final
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|a
decl_stmt|,
name|b
decl_stmt|;
DECL|method|OrMatcher
specifier|public
name|OrMatcher
parameter_list|(
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|a
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|b
parameter_list|)
block|{
name|this
operator|.
name|a
operator|=
name|a
expr_stmt|;
name|this
operator|.
name|b
operator|=
name|b
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|(
name|T
name|t
parameter_list|)
block|{
return|return
name|a
operator|.
name|matches
argument_list|(
name|t
argument_list|)
operator|||
name|b
operator|.
name|matches
argument_list|(
name|t
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|other
operator|instanceof
name|OrMatcher
operator|&&
operator|(
operator|(
name|OrMatcher
operator|)
name|other
operator|)
operator|.
name|a
operator|.
name|equals
argument_list|(
name|a
argument_list|)
operator|&&
operator|(
operator|(
name|OrMatcher
operator|)
name|other
operator|)
operator|.
name|b
operator|.
name|equals
argument_list|(
name|b
argument_list|)
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
literal|37
operator|*
operator|(
name|a
operator|.
name|hashCode
argument_list|()
operator|^
name|b
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"or("
operator|+
name|a
operator|+
literal|", "
operator|+
name|b
operator|+
literal|")"
return|;
block|}
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0
decl_stmt|;
block|}
block|}
end_class

end_unit

