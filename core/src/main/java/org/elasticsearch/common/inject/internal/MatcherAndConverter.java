begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2007 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|internal
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
name|inject
operator|.
name|TypeLiteral
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
name|matcher
operator|.
name|Matcher
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
name|spi
operator|.
name|TypeConverter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|MatcherAndConverter
specifier|public
specifier|final
class|class
name|MatcherAndConverter
block|{
DECL|field|typeMatcher
specifier|private
specifier|final
name|Matcher
argument_list|<
name|?
super|super
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|>
name|typeMatcher
decl_stmt|;
DECL|field|typeConverter
specifier|private
specifier|final
name|TypeConverter
name|typeConverter
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|Object
name|source
decl_stmt|;
DECL|method|MatcherAndConverter
specifier|public
name|MatcherAndConverter
parameter_list|(
name|Matcher
argument_list|<
name|?
super|super
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|>
name|typeMatcher
parameter_list|,
name|TypeConverter
name|typeConverter
parameter_list|,
name|Object
name|source
parameter_list|)
block|{
name|this
operator|.
name|typeMatcher
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|typeMatcher
argument_list|,
literal|"type matcher"
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeConverter
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|typeConverter
argument_list|,
literal|"converter"
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
DECL|method|getTypeConverter
specifier|public
name|TypeConverter
name|getTypeConverter
parameter_list|()
block|{
return|return
name|typeConverter
return|;
block|}
DECL|method|getTypeMatcher
specifier|public
name|Matcher
argument_list|<
name|?
super|super
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTypeMatcher
parameter_list|()
block|{
return|return
name|typeMatcher
return|;
block|}
DECL|method|getSource
specifier|public
name|Object
name|getSource
parameter_list|()
block|{
return|return
name|source
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
name|typeConverter
operator|+
literal|" which matches "
operator|+
name|typeMatcher
operator|+
literal|" (bound at "
operator|+
name|source
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

